package dsplab.logic.algo.impl;

import dsplab.architecture.callback.Delegate;
import dsplab.architecture.callback.DelegateWrapper;
import dsplab.logic.algo.AlgorithmThread;
import dsplab.logic.algo.production.AlgorithmResult;
import dsplab.logic.algo.production.AlgorithmResultBuilder;
import dsplab.logic.filter.SignalFilter;
import dsplab.logic.filter.alg.FilterAlgorithm;
import dsplab.logic.filter.fa.SignalFilterFactory;
import dsplab.logic.filter.impl.SlidingFilter;
import dsplab.logic.ft.FourierTransform;
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

                    // * Make calculations for each 'n' * //

                    // Task I

                    Generator g = GeneratorFactory.getFactory()
                        .giveMeSomethingLike(generatorID);

                    if (generatorID == GenID.WITH_VALUE_MODIFIERS) {
                        GeneratorWithModifiers gEx = cast(g);
                        gEx.setAmplitudeModifier(this.amplitudeModifier);
                        gEx.setPhaseModifier(this.phaseModifier);
                        gEx.setFrequnecyModifier(this.frequencyModifier);
                    }

                    g.setSignal(signal);
                    g.setSampleCount(this.sampleCount);
                    g.setPeriodCount(this.periodCount);

                    double[] srcSignalData = g.run(); // Generate all points

                    // Task II

                    RMSCalculator rmsACalc = RMSCalculatorFactory.getFactory()
                        .giveMeSomethingLike(RMSFormula.A);
                    RMSCalculator rmsBCalc = RMSCalculatorFactory.getFactory()
                        .giveMeSomethingLike(RMSFormula.B);

                    rmsACalc.setSpectrum(srcSignalData);
                    rmsBCalc.setSpectrum(srcSignalData);

                    double[] rmsA = new double[srcSignalData.length];
                    double[] rmsB = new double[srcSignalData.length];

                    for (int i = 0; i < rmsA.length; i++) {
                        rmsACalc.setRange(i);
                        rmsA[i] = rmsACalc.calculateRMS();
                        rmsBCalc.setRange(i);
                        rmsB[i] = rmsBCalc.calculateRMS();
                    }

                    double[] ftAmplitudes = new double[srcSignalData.length];

                    FourierTransform ft = FourierTransformFactory.getFactory()
                        .newFFTImplementation(FFTImpl.DISCRETE);

                    ft.setSpectrum(srcSignalData);

                    for (int i = 1; i < ftAmplitudes.length; i++) {
                        ft.setRange(i);
                        ftAmplitudes[i] = ft.calculateAmplitude();
                    }

                    // Spectrums

                    double[] ampSpectrum
                        = ft.calculateAmplitudeSpectrum();
                    double[] phsSpectrum
                        = ft.calculatePhaseSpectrum();

                    // Task IV

                    Signal noisy = cloneSignal(signal);

                    for (Harmonic h : noisy.getHarmonics()) {
                        h.setWaveform(Waveform.Noise);
                    }

                    g.setSignal(noisy);

                    double[] noisySignal = g.run();

                    SignalFilter flt = SignalFilterFactory.getFactory()
                        .newFilter(FilterAlgorithm.SLIDING);

                    double[] sli = flt.apply(noisySignal);

                    ft.setSpectrum(sli);
                    ft.setRange(sli.length);

                    double[] sliAmplitudeSpectrum
                        = ft.calculateAmplitudeSpectrum();
                    double[] sliPhaseSpectrum
                        = ft.calculatePhaseSpectrum();

                    // * OK * //

                    AlgorithmResultBuilder resultBuilder =
                        AlgorithmResultBuilder.newInstance().newObject();

                    AlgorithmResult result = resultBuilder
                        .setAmplitudes(srcSignalData)
                        .setSignal(signal)
                        .setRMSByFormulaA(rmsA)
                        .setRMSByFormulaB(rmsB)
                        .setSampleCount(this.sampleCount)
                        .setFtAmplitudes(ftAmplitudes)
                        .setNoisySignal(noisySignal)
                        .setSliSignal(sli)
                        .setSliSignalAmplitudeSpectrum(sliAmplitudeSpectrum)
                        .setSliSignalPhaseSpectrum(sliPhaseSpectrum)
                        .setAmplitudeSpectrum(ampSpectrum)
                        .setPhaseSpectrum(phsSpectrum)
                        .build();

                    latch.countDown();

                    return result;
                }));

            });

            // * Wait until all calculations are done * //

            latch.await(/* 5, TimeUnit.SECONDS */);

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

        } catch (InterruptedException intEx) {
            // Ignore
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
//        LOG.d(msg, Thread.currentThread().getName());
    }

    @Override
    public synchronized void start()
    {
        ensureFieldsValid(); // Allows to fail on a calling thread...
        super.start();
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
    void setGenerator(GenID id)
    {

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
}
