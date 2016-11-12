package dsplab.logic.ft;

public interface FourierTransform
{
    void setSpectrum(double[] spectrum);
    void setRange(int range);

    double calculateAmplitude();
    double[] calculateAmplitudeSpectrum();
    double[] calculatePhaseSpectrum();

    static double calculateAmplitudeError(double amplitude)
    {
        return 1 - amplitude;
    }
}
