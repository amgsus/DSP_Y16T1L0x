package dsplab.logic.algo;

import dsplab.logic.algo.delegate.StartFinishDelegates;
import dsplab.logic.algo.production.AlgorithmResult;
import dsplab.logic.gen.alg.GenID;
import dsplab.logic.gen.modifier.ValueModifier;
import dsplab.logic.signal.Signal;

import java.util.List;

/**
 * Does all math calculations.
 */
public interface AlgorithmThread extends StartFinishDelegates
{
    /*
     * Input.
     */

    void setSignalList(List<Signal> signalList);
    void setSampleCount(int samples);
    void setPeriodCount(int periods);
    void setGenerator(GenID id);

    void setAmplitudeModifier(ValueModifier modifier);
    void setPhaseModifier(ValueModifier modifier);
    void setFrequencyModifier(ValueModifier modifier);

    /*
     * Output.
     */

    //List<double[]> getAmplitudes();
    List<AlgorithmResult> getResults();

    void start();

    //void setProcessingRegion(double fromX, double toX);
}
