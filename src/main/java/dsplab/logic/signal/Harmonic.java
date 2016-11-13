package dsplab.logic.signal;

import dsplab.logic.signal.enums.Waveform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents components harmonic part of components signal.
 * @see Signal
 */
public class Harmonic
{
    private final DoubleProperty amplitudeProperty
        = new SimpleDoubleProperty(1.0);
    private final DoubleProperty phaseProperty
        = new SimpleDoubleProperty(0);
    private final DoubleProperty frequencyProperty
        = new SimpleDoubleProperty(1.0);
    private final ObjectProperty<Waveform> waveformProperty
        = new SimpleObjectProperty<>(Waveform.Sine);

    public Harmonic()
    {
        this(1.0, 0, 1.0);
    }

    public Harmonic(double amplitude, double phase, double frequency)
    {
        this(amplitude, phase, frequency, Waveform.Sine);
    }

    public Harmonic(double amplitude, double phase, double frequency,
        Waveform waveform)
    {
        amplitudeProperty.set(amplitude);
        phaseProperty.set(phase);
        frequencyProperty.set(frequency);
        waveformProperty.set(waveform);
    }

    public
    DoubleProperty getAmplitudeProperty()
    {
        return amplitudeProperty;
    }

    public
    DoubleProperty getPhaseProperty()
    {
        return phaseProperty;
    }

    public
    DoubleProperty getFrequencyProperty()
    {
        return frequencyProperty;
    }

    public
    ObjectProperty getWaveformProperty()
    {
        return waveformProperty;
    }

    public
    double getAmplitude()
    {
        return amplitudeProperty.get();
    }

    public
    double getPhase()
    {
        return phaseProperty.get();
    }

    public
    double getFrequency()
    {
        return frequencyProperty.get();
    }

    public
    Waveform getWaveform()
    {
        return waveformProperty.get();
    }

    /**
     * @return A previous value.
     */
    public
    double setAmplitude(double newValue)
    {
        double o = getAmplitude();
        amplitudeProperty.set(newValue);
        return o;
    }

    /**
     * @return A previous value.
     */
    public
    double setPhase(double newValue)
    {
        double o = getPhase();
        phaseProperty.set(newValue);
        return o;
    }

    /**
     * @return A previous value.
     */
    public
    double setFrequency(double newValue)
    {
        double o = getFrequency();
        frequencyProperty.set(newValue);
        return o;
    }

    /**
     * @return A previous value.
     */
    public Waveform setWaveform(Waveform newValue)
    {
        Waveform o = getWaveform();
        waveformProperty.set(newValue);
        return o;
    }
}
