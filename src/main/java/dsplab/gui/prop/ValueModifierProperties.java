package dsplab.gui.prop;

import dsplab.logic.gen.modifier.alg.ValueModifierAlgorithm;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ValueModifierProperties
{
    public ValueModifierProperties()
    {
        enabledProperty.set(false);
    }

    public final BooleanProperty enabledProperty
        = new SimpleBooleanProperty();
    public final ObjectProperty<ValueModifierAlgorithm> algoProperty
        = new SimpleObjectProperty<>();
    public final DoubleProperty valueProperty
        = new SimpleDoubleProperty();

}
