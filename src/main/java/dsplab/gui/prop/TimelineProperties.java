package dsplab.gui.prop;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class TimelineProperties
{
    public TimelineProperties()
    {
        periodCountProperty.set(1);
        sampleCountProperty.set(512);
    }

    private IntegerProperty periodCountProperty = new SimpleIntegerProperty();
    private IntegerProperty sampleCountProperty = new SimpleIntegerProperty();

    public
    IntegerProperty getPeriodsProperty()
    {
        return this.periodCountProperty;
    }

    public
    IntegerProperty getSamplesProperty()
    {
        return this.sampleCountProperty;
    }
}
