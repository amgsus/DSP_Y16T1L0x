package dsplab.logic.signal;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Represents components edit.
 * @see Harmonic
 */
public class Signal
{
    private final StringProperty nameProperty
        = new SimpleStringProperty();
    private final ObjectProperty<Color> seriesColorProperty
        = new SimpleObjectProperty<>(Color.BLACK);
    private final ListProperty<Harmonic> harmonicListProperty
        = new SimpleListProperty<>(FXCollections.observableArrayList());

    public Signal()
    {
        this("Unknown", Color.BLACK);
    }

    public Signal(String name, Color brushColor)
    {
        nameProperty.set(name);
        seriesColorProperty.set(brushColor);
    }

    public
    StringProperty getNameProperty()
    {
        return nameProperty;
    }

    public
    ObjectProperty<Color> getSeriesColorProperty()
    {
        return seriesColorProperty;
    }

    public
    ReadOnlyListProperty<Harmonic> getHarmonicsProperty()
    {
        return harmonicListProperty;
    }

    public
    String getName()
    {
        return nameProperty.get();
    }

    public
    Color  getBrushColor()
    {
        return seriesColorProperty.get();
    }

    public
    String setName(String newValue)
    {
        String o = getName();
        nameProperty.set(newValue);
        return o;
    }

    public
    Color  setBrushColor(Color newValue)
    {
        Color o = getBrushColor();
        seriesColorProperty.set(newValue);
        return o;
    }

    /**
     * @return {@code true} if the edit contains multiple harmonics.
     * @throws IllegalStateException if the edit does not contain any no
     * harmonic.
     */
    public
    boolean isPoly() throws IllegalStateException
    {
        if (getHarmonics().size() == 0)
            throw new IllegalStateException("No harmonics");
        return (getHarmonics().size() > 1);
    }

    public
    List<Harmonic> getHarmonics()
    {
        return harmonicListProperty.get();
    }
}
