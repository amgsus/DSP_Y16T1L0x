package dsplab.gui.component.edit.impl.custom;

import dsplab.architecture.ctrl.SimpleController;
import dsplab.common.Resources;
import dsplab.gui.component.edit.SignalTreeCellGraphic;
import dsplab.gui.util.Hei;
import dsplab.logic.signal.Signal;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;

import java.net.URL;

/**
 * A tree treecell graphic for item of {@link Signal} class.
 */
public class Signal_TCV extends SimpleController implements
    SignalTreeCellGraphic
{
    public Signal_TCV()
    {
        try {
            URL u = Resources.STCV_FXML;
            livenMe(u);
        } catch (Throwable cause) {
            throw new RuntimeException("Failed to init STCV treecell", cause);
        }
    }

    @Override
    public void setData(Object item)
    {
        Signal data = Hei.cast(item);

        if (item != null) {

            this.guiNameLabel.textProperty().bind(data.getNameProperty());
            this.guiBrushColorIndicatorShape.fillProperty().bind(data
                .getSeriesColorProperty());

        } else {

            this.guiNameLabel.textProperty().unbind();
            this.guiBrushColorIndicatorShape.fillProperty().unbind();

        }
    }

    @FXML
    protected
    Rectangle guiBrushColorIndicatorShape;

    @FXML
    protected
    Label guiNameLabel;
}
