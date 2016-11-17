package dsplab.logic.algo.impl;

import dsplab.architecture.callback.Delegate;
import dsplab.architecture.callback.DelegateWrapper;
import dsplab.common.Global;
import dsplab.logic.algo.AlgorithmThread;
import dsplab.logic.algo.production.AlgorithmResult;
import dsplab.logic.algo.production.AlgorithmResultBuilder;
import dsplab.logic.filter.band.BandPassFilter;
import dsplab.logic.filter.band.BandPassFilterFactory;
import dsplab.logic.filter.band.alg.FilterType;
import dsplab.logic.filter.smooth.SignalFilter;
import dsplab.logic.filter.smooth.alg.FilterAlgorithm;
import dsplab.logic.filter.smooth.fa.SignalFilterFactory;
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
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
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
        this.threadPool = Global.getContext().getThreadPool();
    }

    public static AlgorithmThreadImpl newInstance() { return
        new AlgorithmThreadImpl(); }

    // --------------------------------------------------------------------- //

    private List<Signal> signalList = null;
    private volatile List<AlgorithmResult> results = null;

    private int sampleCount = 0;

    private GenID generatorID = GenID.DEFAULT;
    private int periodCount = 1;

    private ValueModifier amplitudeModifier = null;
    private ValueModifier phaseModifier = null;
    private ValueModifier frequencyModifier = null;

    private boolean extended = true;

    private ExecutorService threadPool;

    // --------------------------------------------------------------------- //

    private Generator newGeneratorInstance()
    {
        Generator gen = GeneratorFactory.getFactory()
            .giveMeSomethingLike(generatorID);

        if (generatorID == GenID.WITH_VALUE_MODIFIERS) {
            GeneratorWithModifiers gMod = cast(gen);
            gMod.setAmplitudeModifier(amplitudeModifier);
            gMod.setPhaseModifier(phaseModifier);
            gMod.setFrequnecyModifier(frequencyModifier);
        }

        gen.setSampleCount(sampleCount); // T
        gen.setPeriodCount(periodCount); // n

        return gen;
    }

    @Override
    public void run()
    {
        List<Future<AlgorithmResult>> asyncResults =
            new ArrayList<>(signalList.size());

        try {
            final CountDownLatch latch =
                new CountDownLatch(signalList.size());

            // * Schedule all mathematic task for each signal * //

            signalList.forEach(signal -> {
                asyncResults.add(threadPool.submit(() -> {

                    AlgorithmResult result = null;

                    try {

                        Generator gen = newGeneratorInstance();

                        if (extended)
                            result = impl_DoExtendedMath(signal, gen);
                        else
                            result = impl_DoMath(signal, gen);

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
                    resultList.add(algorithmResultFuture.get(2000,
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
            e.printStackTrace();
        }

        Platform.runLater(onDone::execute);
    }

    @Override
    public synchronized void start()
    {
        if (signalList == null)
            throw new IllegalArgumentException("signalList:<null>");

        onBeforeStart.execute();
        super.start();
    }

    // --------------------------------------------------------------------- //

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
    AlgorithmResult impl_DoMath(Signal signal, Generator gen)
    {
        gen.setSignal(signal);

        double[] signalData = gen.run(); // Generate (length=T*n)

        return AlgorithmResultBuilder.newInstance()
            .setSignal(signal)
            .setData(signalData)
            .setSampleCount(sampleCount)
            .setPeriodCount(periodCount)
            .build();
    }

    /**
     * Does mathematical calculations. This method does tasks in the current
     * thread, so it should be called in a separate thread.
     * <p>
     * <b>Tasks:</b>
     * <ol>
     * <li>Calculate the amplitude and the phase spectrums of a signal.</li>
     * <li>Calculate RMS error.</li>
     * <li>Calculate amplitude error.</li>
     * <li>Restore a signal from the amplitude and the phase spectrums.</li>
     * <li>Apply a noise to the signal.</li>
     * <li>Apply a sliding filter to the noisy signal.</li>
     * <li>Apply a median filter to the noisy signal.</li>
     * <li>Apply a parabolic filter to the noisy signal.</li>
     * </ol>
     */
    protected
    AlgorithmResult impl_DoExtendedMath(Signal signal, Generator gen)
    {
        AlgorithmResult result = impl_DoMath(signal, gen);

        task_SignalSpectrum(result);            // Parallel
        task_RMSe(result);                      // Parallel
        task_Ae(result);                        // Parallel
        task_RestoreSignal(result);             // After task_SignalSpectrum
        task_NoisySignal(result, gen);          // Parallel
        task_SliFilterForNoisySignal(result);   // After task_NoisySignal
        task_MdnFilterForNoisySignal(result);   // After task_NoisySignal
        task_PblFilterForNoisySignal(result);   // After task_NoisySignal
        task_LPSignal(result);                  // Parallel
        task_HPSignal(result);                  // Parallel
        task_BPSignal(result);                  // Parallel

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
    void task_NoisySignal(AlgorithmResult result, Generator gen)
    {
        Signal signal = cloneSignal(result.getSignal());

        // ToDo: This should be refactored (not using noise harmonic)
        for (Harmonic h : signal.getHarmonics()) {
            h.setWaveform(Waveform.Noise);
        }

        double[] data;

        gen.setSignal(signal);
        gen.setSampleCount(result.getSampleCount());
        gen.setPeriodCount(result.getPeriodCount());

        data = gen.run();

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

    // May be invocated in a separate thread.
    protected
    void task_LPSignal(AlgorithmResult result)
    {
        BandPassFilter flt = BandPassFilterFactory.getFactory()
            .giveMe(FilterType.LOWPASS);

        double[] ampSpectrum = flt.apply(result.getAmplitudeSpectrum());
        double[] phsSpectrum = result.getPhaseSpectrum();

        SignalRestorer sigRest = new SignalRestorer(ampSpectrum, phsSpectrum);
        double[] signal = sigRest.restore();

        AlgorithmResultBuilder.instanceFor(result)
            .setLPFSignal(signal)
            .setLPFAmplitudeSpectrum(ampSpectrum)
            .setLPFPhaseSpectrum(phsSpectrum)
            .build();
    }

    // May be invocated in a separate thread.
    protected
    void task_HPSignal(AlgorithmResult result)
    {
        BandPassFilter flt = BandPassFilterFactory.getFactory()
            .giveMe(FilterType.HIGHPASS);

        double[] ampSpectrum = flt.apply(result.getAmplitudeSpectrum());
        double[] phsSpectrum = result.getPhaseSpectrum();

        SignalRestorer sigRest = new SignalRestorer(ampSpectrum, phsSpectrum);
        double[] signal = sigRest.restore();

        AlgorithmResultBuilder.instanceFor(result)
            .setHPFSignal(signal)
            .setHPFAmplitudeSpectrum(ampSpectrum)
            .setHPFPhaseSpectrum(phsSpectrum)
            .build();
    }

    // May be invocated in a separate thread.
    protected
    void task_BPSignal(AlgorithmResult result)
    {
        BandPassFilter flt = BandPassFilterFactory.getFactory()
            .giveMe(FilterType.BAND);

        double[] ampSpectrum = flt.apply(result.getAmplitudeSpectrum());
        double[] phsSpectrum = result.getPhaseSpectrum();

        SignalRestorer sigRest = new SignalRestorer(ampSpectrum, phsSpectrum);
        double[] signal = sigRest.restore();

        AlgorithmResultBuilder.instanceFor(result)
            .setBPFSignal(signal)
            .setBPFAmplitudeSpectrum(ampSpectrum)
            .setBPFPhaseSpectrum(phsSpectrum)
            .build();
    }

    // --------------------------------------------------------------------- //

    private final DelegateWrapper onBeforeStart = new DelegateWrapper();
    private final DelegateWrapper onDone = new DelegateWrapper();

    @Override
    public
    void setOnBeforeStart(Delegate delegate)
    {
        if (delegate != null) {
            onBeforeStart.wrapDelegate(delegate);
        } else {
            onBeforeStart.removeDelegate();
        }
    }

    @Override
    public
    void setOnSuccess(Delegate delegate)
    {
        if (delegate != null) {
            onDone.wrapDelegate(delegate);
        } else {
            onDone.removeDelegate();
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
