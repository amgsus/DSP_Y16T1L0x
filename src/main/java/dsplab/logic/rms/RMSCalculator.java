package dsplab.logic.rms;

public interface RMSCalculator
{
    void setSpectrum(double[] spectrum);
    void setRange(int range);

    double calculateRMS();

    static double calculateRMSError(double rms)
    {
        return 0.707 - rms;
    }
}
