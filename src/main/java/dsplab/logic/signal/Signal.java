package dsplab.logic.signal;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.scene.paint.Color;

import java.io.File;
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
    private final StringProperty dataFileNameProperty
        = new SimpleStringProperty(null);
    private final IntegerProperty dataFileChannelProperty
        = new SimpleIntegerProperty(0);
    private final BooleanProperty fileSourceProperty
        = new SimpleBooleanProperty(false);

    public Signal()
    {
        this("Unknown", Color.BLACK);
    }

    public Signal(String name, Color brushColor)
    {
        nameProperty.set(name);
        seriesColorProperty.set(brushColor);
    }

    public Signal(Signal prototype)
    {
        nameProperty.set(prototype.getNameProperty().getValue());
        seriesColorProperty.set(prototype.getSeriesColorProperty().getValue());
        dataFileNameProperty.set(prototype.getDataFileNameProperty().getValue());
        dataFileChannelProperty.set(prototype.getDataFileChannelProperty().getValue());
        fileSourceProperty.set(prototype.getFileSourceProperty().getValue());
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

    public
    StringProperty getDataFileNameProperty()
    {
        return this.dataFileNameProperty;
    }

    public
    IntegerProperty getDataFileChannelProperty()
    {
        return this.dataFileChannelProperty;
    }

    public
    BooleanProperty getFileSourceProperty()
    {
        return this.fileSourceProperty;
    }

    public
    boolean isSourcingFromFile()
    {
        return getFileSourceProperty().getValue();
    }

    public
    String getDataSourceFileName()
    {
        return getDataFileNameProperty().getValue();
    }

    public
    int getDataSourceChannel()
    {
        return getDataFileChannelProperty().getValue();
    }
}
