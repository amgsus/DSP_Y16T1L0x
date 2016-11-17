package dsplab.logic.algo.impl;

import dsplab.architecture.callback.Delegate;
import dsplab.architecture.callback.DelegateWrapper;
import dsplab.common.Global;
import dsplab.logic.algo.AlgorithmThread;
import dsplab.logic.algo.e.EMathInvocation;
import dsplab.logic.algo.e.EPendingTask;
import dsplab.logic.algo.e.MultipleCauseException;
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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

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

    private final List<AlgorithmResult> results = new ArrayList<>();
    private final List<AlgorithmResult> failedTasks = new ArrayList<>();

    private int sampleCount = 0;

    private GenID generatorID = GenID.DEFAULT;
    private int periodCount = 1;

    private ValueModifier amplitudeModifier = null;
    private ValueModifier phaseModifier = null;
    private ValueModifier frequencyModifier = null;

    private boolean extended = true;

    private Exception majorCause;

    private final ExecutorService threadPool;
    private static final int MAX_CONCURRENT_TASKS = 8;

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

    private Future<AlgorithmResult> submitTask(final Signal signal)
        throws RejectedExecutionException
    {
        return threadPool.submit(() -> {

            AlgorithmResult result = null;
            Exception e = null;
            Semaphore maxActives = this.maxActiveTaskSemaphoreRef.get();
            CountDownLatch latch = this.latchRef.get();

            try {
                maxActives.acquire();

                Generator gen = newGeneratorInstance();

                if (extended)
                    result = impl_DoExtendedMath(signal, gen);
                else
                    result = impl_DoMath(signal, gen);

            } catch (EMathInvocation | MultipleCauseException eMath) {
                e = eMath;
            } catch (Exception eUnknown) {
                e = eUnknown;
            } finally {
                maxActives.release();
                latch.countDown();
            }

            if (e != null) {
                throw e;
            }

            return result;
        });
    }

    private final AtomicReference<Semaphore> maxActiveTaskSemaphoreRef =
        new AtomicReference<>();
    private final AtomicReference<CountDownLatch> latchRef =
        new AtomicReference<>();

    @Override
    public
    void run()
    {
        majorCause = null;

        results.clear();
        failedTasks.clear();

        final int signalCount = signalList.size();
        List<Future<AlgorithmResult>> futures = new ArrayList<>(signalCount);

        CountDownLatch latch = new CountDownLatch(signalCount);
        Semaphore maxActives = new Semaphore(MAX_CONCURRENT_TASKS);

        latchRef.set(latch);
        maxActiveTaskSemaphoreRef.set(maxActives);

        try {
            // * Do math for each signal in a separate thread * //

            signalList.forEach(s -> futures.add(submitTask(s)));

            // * Collect all results * //

            latchRef.get().await();

            for (int i = 0; i < futures.size(); i++) {

                Future<AlgorithmResult> f = futures.get(i);
                AlgorithmResult result;

                try {
                    result = f.get();
                    results.add(result);
                } catch (Exception cause) {
                    failedTasks.add(AlgorithmResultBuilder.newInstance()
                            .setSignal(signalList.get(i))
                            .setException(cast(cause.getCause()))
                            .build()
                    );
                }
            }

        } catch (Exception e) {
            majorCause = e;
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
        throws Exception
    {
        try {
            gen.setSignal(signal);

            double[] signalData = gen.run(); // Generate (length=T*n)

            return AlgorithmResultBuilder.newInstance()
                .setSignal(signal)
                .setData(signalData)
                .setSampleCount(sampleCount)
                .setPeriodCount(periodCount)
                .build();

        } catch (Exception cause) {
            throw new EMathInvocation(cause);
        }
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
        throws Exception
    {
        /*
         * Calculate signal form.
         */

        AlgorithmResult algoResult = impl_DoMath(signal, gen);

        /*
         * Sumbiting all tasks (including depending ones)
         * to the thread pool.
         */

        final Callable<Void> callableSigSpectum = () -> {
            try {
                task_SignalSpectrum(algoResult);
            } catch (Exception cause) {
                final String s = "FT has failed";
                throw new EMathInvocation(s, cause);
            }

            return null;
        };

        final Future asyncSigSpectrum = threadPool.submit(callableSigSpectum);

        final Callable<Void> callableRMSeAe = () -> {
            try {
                task_RMSe(algoResult);
            } catch (Exception cause) {
                final String s = "RMSe calculation has failed";
                throw new EMathInvocation(s, cause);
            }

            try {
                task_Ae(algoResult);
            } catch (Exception cause) {
                final String s = "Ae calculation has failed";
                throw new EMathInvocation(s, cause);
            }

            return null;
        };

        final Future asyncRMSeAe = threadPool.submit(callableRMSeAe);

        // This will actually block a worker from the thread pool
        final Callable<Void> callableRestoreSig = () -> {
            try {
                asyncSigSpectrum.get();
            } catch (Exception e) {
                throw new EPendingTask(e);
            }

            try {
                task_RestoreSignal(algoResult);
            } catch (Exception cause) {
                final String s = "Reversed FT has failed";
                throw new EMathInvocation(s, cause);
            }

            return null;
        };

        final Future asyncRestoreSig = threadPool.submit(callableRestoreSig);

        final Callable<Void> callableNoise = () -> {
            try {
                task_NoisySignal(algoResult, gen);
            } catch (Exception cause) {
                final String s = "Noise generator has failed";
                throw new EMathInvocation(s, cause);
            }

            return null;
        };

        final Future asyncNoise = threadPool.submit(callableNoise);

        // This will actually block a worker from the thread pool
        final Callable<Void> callableSliFilter = () -> {
            try {
                asyncNoise.get();
            } catch (Exception e) {
                throw new EPendingTask(e);
            }

            try {
                task_SliFilterForNoisySignal(algoResult);
            } catch (Exception cause) {
                final String s = "Sliding filter has failed";
                throw new EMathInvocation(s, cause);
            }

            return null;
        };

        final Future asyncSliFilter = threadPool.submit(callableSliFilter);

        // This will actually block a worker from the thread pool
        final Callable<Void> callableMdnFilter = () -> {
            try {
                asyncNoise.get();
            } catch (Exception e) {
                throw new EPendingTask(e);
            }

            try {
                task_MdnFilterForNoisySignal(algoResult);
            } catch (Exception cause) {
                final String s = "Median filter has failed";
                throw new EMathInvocation(s, cause);
            }

            return null;
        };

        final Future asyncMdnFilter = threadPool.submit(callableMdnFilter);

        // This will actually block a worker from the thread pool
        final Callable<Void> callablePblFilter = () -> {
            try {
                asyncNoise.get();
            } catch (Exception e) {
                throw new EPendingTask(e);
            }

            try {
                task_PblFilterForNoisySignal(algoResult);
            } catch (Exception cause) {
                final String s = "Parabolic filter has failed";
                throw new EMathInvocation(s, cause);
            }

            return null;
        };

        final Future asyncPblFilter = threadPool.submit(callablePblFilter);

        // This will actually block a worker from the thread pool
        final Callable<Void> callableLPSig = () -> {
            try {
                asyncSigSpectrum.get();
            } catch (Exception e) {
                throw new EPendingTask(e);
            }

            try {
                task_LPSignal(algoResult);
            } catch (Exception cause) {
                final String s = "Low-pass filter has failed";
                throw new EMathInvocation(s, cause);
            }

            return null;
        };

        final Future asyncLPSig = threadPool.submit(callableLPSig);

        // This will actually block a worker from the thread pool
        final Callable<Void> callableHPSig = () -> {
            try {
                asyncSigSpectrum.get();
            } catch (Exception e) {
                throw new EPendingTask(e);
            }

            try {
                task_HPSignal(algoResult);
            } catch (Exception cause) {
                final String s = "High-pass filter has failed";
                throw new EMathInvocation(s, cause);
            }

            return null;
        };

        final Future asyncHPSig = threadPool.submit(callableHPSig);

        // This will actually block a worker from the thread pool
        final Callable<Void> callableBPSig = () -> {
            try {
                asyncSigSpectrum.get();
            } catch (Exception e) {
                throw new EPendingTask(e);
            }

            try {
                task_BPSignal(algoResult);
            } catch (Exception cause) {
                final String s = "Band-pass filter has failed";
                throw new EMathInvocation(s, cause);
            }

            return null;
        };

        final Future asyncBPSig = threadPool.submit(callableBPSig);

        /*
         * Awaiting all task to complete. No guarantee that all tasks have
         * done without exceptions. So, each task is handled separately.
         */

        List<Throwable> eList = new ArrayList<>(10); // Max num. of exceptions

        try {
            asyncSigSpectrum.get();
        } catch (Exception cause) {
            eList.add(cause);
            cause.printStackTrace();
        }

        try {
            asyncRMSeAe.get();
        } catch (Exception cause) {
            eList.add(cause);
            cause.printStackTrace();
        }

        try {
            asyncRestoreSig.get();
        } catch (Exception cause) {
            eList.add(cause);
            cause.printStackTrace();
        }

        try {
            asyncNoise.get();
        } catch (Exception cause) {
            eList.add(cause);
            cause.printStackTrace();
        }

        try {
            asyncSliFilter.get();
        } catch (Exception cause) {
            eList.add(cause);
            cause.printStackTrace();
        }

        try {
            asyncMdnFilter.get();
        } catch (Exception cause) {
            eList.add(cause);
            cause.printStackTrace();
        }

        try {
            asyncPblFilter.get();
        } catch (Exception cause) {
            eList.add(cause);
            cause.printStackTrace();
        }

        try {
            asyncLPSig.get();
        } catch (Exception cause) {
            eList.add(cause);
            cause.printStackTrace();
        }

        try {
            asyncHPSig.get();
        } catch (Exception cause) {
            eList.add(cause);
            cause.printStackTrace();
        }

        try {
            asyncBPSig.get();
        } catch (Exception cause) {
            eList.add(cause);
            cause.printStackTrace();
        }

        if (eList.size() > 0) { //  Something has failed...

            String s = String.format("Algo [%s:%s]: Execution has failed " +
                "with %d exceptions",
                signal.getName(), signal.getBrushColor(), eList.size());
            throw new MultipleCauseException(s, eList);
        }

        return algoResult;
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

    // Must be invocated [in a separate thread] ONLY after
    // task_SignalSpectrum() is done.
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

    // Must be invocated [in a separate thread] ONLY after
    // task_SignalSpectrum() is done.
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

    // Must be invocated [in a separate thread] ONLY after
    // task_SignalSpectrum() is done.
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

    @Override
    public Exception getException()
    {
        return majorCause;
    }

    @Override
    public List<AlgorithmResult> getFailedTasks()
    {
        return failedTasks;
    }

    @Override
    public boolean hasFailedTasks()
    {
        return failedTasks.size() > 0;
    }

    @Override
    public boolean hasFailed()
    {
        return majorCause != null;
    }
}
