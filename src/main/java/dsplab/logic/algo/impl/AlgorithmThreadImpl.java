package dsplab.logic.algo.impl;

import dsplab.architecture.callback.Delegate;
import dsplab.architecture.callback.DelegateWrapper;
import dsplab.logic.algo.AlgorithmThread;
import dsplab.logic.algo.e.MathInvocationException;
import dsplab.logic.algo.production.AlgorithmResult;
import dsplab.logic.algo.production.AlgorithmResultBuilder;
import dsplab.logic.filter.SignalFilter;
import dsplab.logic.filter.alg.FilterAlgorithm;
import dsplab.logic.filter.fa.SignalFilterFactory;
import dsplab.logic.filter.impl.MedianFilter;
import dsplab.logic.filter.impl.SlidingFilter;
import dsplab.logic.ft.FourierTransform;
import dsplab.logic.ft.SignalRestorer;
import dsplab.logic.ft.alg.FFTImpl;
import dsplab.logic.ft.fa.FourierTransformFactory;
import dsplab.logic.gen.Generator;
import dsplab.logic.gen.GeneratorWithModifiers;
import dsplab.logic.gen.alg.GenID;
import dsplab.logic.gen.fa.GeneratorFactory;
import dsplab.logic.gen.modifier.ValueModifier;
import dsplab.logic.rms.RMSCalculator;
import dsplab.logic.rms.alg.RMSFormula;
import dsplab.logic.rms.fa.RMSCalculatorFactory;
import dsplab.logic.signal.Harmonic;
import dsplab.logic.signal.Signal;
import dsplab.logic.signal.enums.Waveform;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static dsplab.gui.util.Hei.assertThreadNotRunning;
import static dsplab.gui.util.Hei.cast;
import static dsplab.logic.signal.util.SigUtils.cloneSignal;

public class AlgorithmThreadImpl extends Thread implements AlgorithmThread
{
    AlgorithmThreadImpl()
    {
        setDaemon(true);
    }

    public static AlgorithmThread newInstance() { return
        new AlgorithmThreadImpl(); }

    // --------------------------------------------------------------------- //

    private final int THREAD_POOL_SIZE = 4; // Four

    private List<Signal> signalList = null;
    private volatile List<AlgorithmResult> results = null;

    private static final boolean stopOnDelegateException = false;

    private int sampleCount = 0;

    private GenID generatorID = GenID.DEFAULT;
    private int periodCount = 1;

    private ValueModifier amplitudeModifier = null;
    private ValueModifier phaseModifier = null;
    private ValueModifier frequencyModifier = null;

    private boolean extended = true;

    // --------------------------------------------------------------------- //

    @Override
    public void run()
    {
        ensureFieldsValid(); // Will never fail if start() called before...

        // * Call a delegate on the thread started * //

        try {

            this.onStart.execute();

        } catch (Exception e) {

            // Log about fail and continue...

            if (stopOnDelegateException) {
                return;
            }
        }

        // * Main * //

        ExecutorService pool = null;

        try {

            pool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            final ExecutorService _pool_ = pool;

            List<Future<AlgorithmResult>> asyncResults =
                new ArrayList<>(signalList.size());

            final CountDownLatch latch =
                new CountDownLatch(signalList.size());

            // * Schedule all mathematic task for each signal * //

            signalList.forEach(signal -> {

                asyncResults.add(_pool_.submit(() -> {

                    AlgorithmResult result = null;

                    try {

                        if (extended)
                            result = impl_DoExtendedMath(signal);
                        else
                            result = impl_DoMath(signal);

                    } catch (Exception cause) {

                        // ToDo: Handle the exception...
                        cause.printStackTrace();
                        throw cause; // ToDo: ???

                    }

                    latch.countDown();
                    return result;
                }));

            });

            // * Wait until all calculations are done * //

            latch.await();

            // * Collect calculation results into a single list  * //

            List<AlgorithmResult> resultList =
                new ArrayList<>(asyncResults.size());

            asyncResults.forEach(algorithmResultFuture -> {

                try {
                    resultList.add(algorithmResultFuture.get(1000,
                        TimeUnit.MILLISECONDS));
                } catch (TimeoutException e) {
                    System.out.println("Timeout for" +
                        asyncResults.indexOf(algorithmResultFuture));
                } catch (InterruptedException e) {
                    final String msg
                        = "Worker thread has been interrupted";
                    System.err.println(msg);
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    final String msg
                        = "Worker thread has been interrupted by exception";
                    System.err.println(msg);
                    e.printStackTrace();
                }

            });

            this.results = resultList;

        } catch (Exception e) {
            // ...
        } finally {
            if (pool != null)
                pool.shutdown();
        }

        // * Execute the delegate before stopping the thread * //

        try {

            this.onFinish.execute();

        } catch (Exception e) {

            // Log about fail and continue...

            if (stopOnDelegateException) {
                return;
            }
        }

        final String msg = "Thread '%s' is being safely stopped...";
    }

    @Override
    public synchronized void start()
    {
        ensureFieldsValid(); // Allows to fail on a calling thread...
        super.start();
    }

    // --------------------------------------------------------------------- //

    private Generator prefGenrt;
    private final Object prefGenrtLock = new Object();

    /**
     * Does mathematical calculations. This method does tasks in the current
     * thread, so it should be called in a separate thread.
     * <p>
     * <b>Tasks:</b>
     * <ol>
     * <li>Generate a signal using apropriate generator implementation.</li>
     * </ol>
     */
    protected
    AlgorithmResult impl_DoMath(Signal signal)
        throws MathInvocationException
    {
        synchronized (prefGenrtLock) {
            prefGenrt = GeneratorFactory.getFactory()
                .giveMeSomethingLike(generatorID);

            if (generatorID == GenID.WITH_VALUE_MODIFIERS) {
                GeneratorWithModifiers gMod = cast(prefGenrt);
                gMod.setAmplitudeModifier(amplitudeModifier);
                gMod.setPhaseModifier(phaseModifier);
                gMod.setFrequnecyModifier(frequencyModifier);
            }

            prefGenrt.setSignal(signal);
            prefGenrt.setSampleCount(sampleCount); // T
            prefGenrt.setPeriodCount(periodCount); // n

            double[] signalData = prefGenrt.run(); // Generate (length=T*n)

            return AlgorithmResultBuilder.newInstance()
                .setSignal(signal)
                .setData(signalData)
                .setSampleCount(sampleCount)
                .setPeriodCount(periodCount)
                .build();
        }
    }

    /**
     * Does mathematical calculations. This method does tasks in the current
     * thread, so it should be called in a separate thread.
     * <p>
     * <b>Tasks:</b>
     * <ol>
     * <li>Calculate the amplitude and the phase spectrums of a signal.</li>
     * </ol>
     */
    protected
    AlgorithmResult impl_DoExtendedMath(Signal signal)
        throws MathInvocationException
    {
        AlgorithmResult result = impl_DoMath(signal);

        task_SignalSpectrum(result);
        task_RMSe(result);
        task_Ae(result);
        task_RestoreSignal(result);
        task_NoisySignal(result);
        task_SliFilterForNoisySignal(result);
        task_MdnFilterForNoisySignal(result);
        task_PblFilterForNoisySignal(result);

        return result;
    }

    // May be invocated in a separate thread.
    protected
    void task_SignalSpectrum(AlgorithmResult algoResult)
    {
        FourierTransform ft = FourierTransformFactory.getFactory()
            .newFFTImplementation(FFTImpl.DISCRETE);

        double[] signal = algoResult.getData();

        ft.setSpectrum(signal);
        ft.setRange(signal.length);

        double[] ampSpectrum = ft.calculateAmplitudeSpectrum();
        double[] phsSpectrum = ft.calculatePhaseSpectrum();

        AlgorithmResultBuilder.instanceFor(algoResult)
            .setAmplitudeSpectrum(ampSpectrum)
            .setPhaseSpectrum(phsSpectrum)
            .build();
    }

    // May be invocated in a separate thread.
    protected
    void task_RMSe(AlgorithmResult algoResult)
    {
        double[] signalData = algoResult.getData();

        RMSCalculator rmsACalc = RMSCalculatorFactory.getFactory()
            .giveMeSomethingLike(RMSFormula.A);
        RMSCalculator rmsBCalc = RMSCalculatorFactory.getFactory()
            .giveMeSomethingLike(RMSFormula.B);

        rmsACalc.setSpectrum(signalData);
        rmsBCalc.setSpectrum(signalData);

        double[] rmsA = new double[signalData.length];
        double[] rmsB = new double[signalData.length];

        // ToDo: Iterate i -> 1..length?
        for (int i = 0; i < rmsA.length; i++) {
            rmsACalc.setRange(i);
            rmsA[i] = rmsACalc.calculateRMS();
            rmsBCalc.setRange(i);
            rmsB[i] = rmsBCalc.calculateRMS();
        }

        AlgorithmResultBuilder.instanceFor(algoResult)
            .setRMSByFormulaA(rmsA)
            .setRMSByFormulaB(rmsB)
            .build();
    }

    // May be invocated in a separate thread.
    protected
    void task_Ae(AlgorithmResult algoResult)
    {
        double[] signalData = algoResult.getData();

        double[] aeData = new double[signalData.length];

        FourierTransform ft = FourierTransformFactory.getFactory()
            .newFFTImplementation(FFTImpl.DISCRETE);

        ft.setSpectrum(signalData);

        // ToDo: Iterate i -> 1..length?
        for (int i = 1; i < aeData.length; i++) {
            ft.setRange(i);
            aeData[i] = ft.calculateAmplitude();
        }

        AlgorithmResultBuilder.instanceFor(algoResult)
            .setAeData(aeData)
            .build();
    }

    // Must be invocated [in a separate thread] ONLY after
    // task_SignalSpectrum() is done.
    protected
    void task_RestoreSignal(AlgorithmResult result)
    {
        double[] ampSpectrum = result.getAmplitudeSpectrum();
        double[] phsSpectrum = result.getPhaseSpectrum();

        SignalRestorer signalRestorer =
            new SignalRestorer(ampSpectrum, phsSpectrum);

        signalRestorer.setGain(0);
        signalRestorer.setSampleCount(ampSpectrum.length);

        double[] restored =
            signalRestorer.restore();
        double[] restWithoutPhase =
            signalRestorer.restoreWithoutPhase();

        AlgorithmResultBuilder.instanceFor(result)
            .setRestoredSignal(restored)
            .setRestoredWithoutPhaseSignal(restWithoutPhase)
            .build();
    }

    // Must be invocated [in a separate thread] ONLY after
    // impl_DoMath() is done. Locks the generator instance.
    protected
    void task_NoisySignal(AlgorithmResult result)
    {
        Signal signal = cloneSignal(result.getSignal());

        // ToDo: This should be refactored (not using noise harmonic)
        for (Harmonic h : signal.getHarmonics()) {
            h.setWaveform(Waveform.Noise);
        }

        double[] data;

        synchronized (prefGenrtLock) {

            prefGenrt.setSignal(signal);
            prefGenrt.setSampleCount(result.getSampleCount());
            prefGenrt.setPeriodCount(result.getPeriodCount());

            data = prefGenrt.run();
        }

        FourierTransform ft = FourierTransformFactory.getFactory()
            .newFFTImplementation(FFTImpl.DISCRETE);

        ft.setSpectrum(data);
        ft.setRange(data.length);

        double[] ampSpectrum = ft.calculateAmplitudeSpectrum();
        double[] phsSpectrum = ft.calculatePhaseSpectrum();

        AlgorithmResultBuilder.instanceFor(result)
            .setNoisySignal(data)
            .setNoisyAmplitudeSpectrum(ampSpectrum)
            .setNoisyPhaseSpectrum(phsSpectrum)
            .build();
    }

    // Must be invocated [in a separate thread] ONLY after task_NoisySignal()
    // is done.
    protected
    void task_SliFilterForNoisySignal(AlgorithmResult result)
    {
        SignalFilter flt = SignalFilterFactory.getFactory()
            .newFilter(FilterAlgorithm.SLIDING);

        double[] data = flt.apply(result.getNoisySignal());

        FourierTransform ft = FourierTransformFactory.getFactory()
            .newFFTImplementation(FFTImpl.DISCRETE);

        ft.setSpectrum(data);
        ft.setRange(data.length);

        double[] ampSpectrum = ft.calculateAmplitudeSpectrum();
        double[] phsSpectrum = ft.calculatePhaseSpectrum();

        AlgorithmResultBuilder.instanceFor(result)
            .setSliSignal(data)
            .setSliAmplitudeSpectrum(ampSpectrum)
            .setSliPhaseSpectrum(phsSpectrum)
            .build();
    }

    // Must be invocated [in a separate thread] ONLY after task_NoisySignal()
    // is done.
    protected
    void task_MdnFilterForNoisySignal(AlgorithmResult result)
    {
        SignalFilter flt = SignalFilterFactory.getFactory()
            .newFilter(FilterAlgorithm.MEDIAN);

        double[] data = flt.apply(result.getNoisySignal());

        FourierTransform ft = FourierTransformFactory.getFactory()
            .newFFTImplementation(FFTImpl.DISCRETE);

        ft.setSpectrum(data);
        ft.setRange(data.length);

        double[] ampSpectrum = ft.calculateAmplitudeSpectrum();
        double[] phsSpectrum = ft.calculatePhaseSpectrum();

        AlgorithmResultBuilder.instanceFor(result)
            .setMdnSignal(data)
            .setMdnAmplitudeSpectrum(ampSpectrum)
            .setMdnPhaseSpectrum(phsSpectrum)
            .build();
    }

    // Must be invocated [in a separate thread] ONLY after task_NoisySignal()
    // is done.
    protected
    void task_PblFilterForNoisySignal(AlgorithmResult result)
    {
        SignalFilter flt = SignalFilterFactory.getFactory()
            .newFilter(FilterAlgorithm.PARABOLIC);

        double[] data = flt.apply(result.getNoisySignal());

        FourierTransform ft = FourierTransformFactory.getFactory()
            .newFFTImplementation(FFTImpl.DISCRETE);

        ft.setSpectrum(data);
        ft.setRange(data.length);

        double[] ampSpectrum = ft.calculateAmplitudeSpectrum();
        double[] phsSpectrum = ft.calculatePhaseSpectrum();

        AlgorithmResultBuilder.instanceFor(result)
            .setPblSignal(data)
            .setPblAmplitudeSpectrum(ampSpectrum)
            .setPblPhaseSpectrum(phsSpectrum)
            .build();
    }

    // --------------------------------------------------------------------- //

    private
    void ensureFieldsValid()
    {
        if (signalList == null)
            throw new IllegalArgumentException("Signal list is not set");
    }

    // --------------------------------------------------------------------- //

    private final DelegateWrapper onStart = new DelegateWrapper();
    private final DelegateWrapper onFinish = new DelegateWrapper();

    // --------------------------------------------------------------------- //

    /**
     * <b>Note:</b> This delegate will be run on different thread, not on
     * the calling.
     *
     * @param delegate A delegate to call on the algorithm has been started.
     */
    @Override
    public
    void setOnStart(Delegate delegate)
    {
        if (delegate != null) {
            onStart.wrapDelegate(delegate);
        } else {
            onStart.removeDelegate();
        }
    }

    @Override
    public
    void setOnSuccess(Delegate delegate)
    {
        if (delegate != null) {
            onFinish.wrapDelegate(delegate);
        } else {
            onFinish.removeDelegate();
        }
    }

    // --------------------------------------------------------------------- //


    @Override
    public int getSampleCount()
    {
        return sampleCount;
    }

    @Override
    public int getPeriodCount()
    {
        return periodCount;
    }

    /**
     * <b>Note:</b> This method makes a "true"-copy of a {@code signalList}.
     * So, the thread has its own copy of the data.
     *
     * @param signalList The list of signal to process by this thread.
     */
    @Override
    public
    void setSignalList(List<Signal> signalList)
    {
        assertThreadNotRunning(this);

        if (signalList == null)
            throw new IllegalArgumentException("null");

        List<Signal> list = new ArrayList<>();
        signalList.forEach(signal -> list.add(cloneSignal(signal)));

        this.signalList = list;
    }

    @Override
    public
    List<AlgorithmResult> getResults()
    {
        if (results == null)
            throw new IllegalStateException("No results");

        return results;
    }

    @Override
    public
    void setSampleCount(int samples)
    {
        if (samples < 1)
            throw new IllegalArgumentException("Value must be 2^x (x > 0)");

        // ToDo: 2^X?

        this.sampleCount = samples;
    }

    @Override
    public
    void setPeriodCount(int periods)
    {
        this.periodCount = periods;
    }

    @Override
    public
    void setGeneratorID(GenID id)
    {
        generatorID = id;
    }

    @Override
    public
    void setAmplitudeModifier(ValueModifier modifier)
    {
        this.amplitudeModifier = modifier;
    }

    @Override
    public
    void setPhaseModifier(ValueModifier modifier)
    {
        this.phaseModifier = modifier;
    }

    @Override
    public
    void setFrequencyModifier(ValueModifier modifier)
    {
        this.frequencyModifier = modifier;
    }

    @Override
    public
    void setExtendedCalculationEnabled(boolean enabled)
    {
        this.extended = enabled;
    }
}
