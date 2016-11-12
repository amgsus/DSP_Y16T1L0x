package dsplab.gui.component.common;

import dsplab.architecture.ctrl.util.Utils;
import dsplab.common.Resources;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.net.URL;

public class OverlayPane extends StackPane
{
    public OverlayPane() { this(null, null); }

    public OverlayPane(Node content, Node overlayContent)
    {
        try {
            URL u = Resources.OVERLAYPANE_FXML;
            Utils.fromFXML(u, this, this); // FX:ROOT
        } catch (Throwable cause) {
            throw new RuntimeException("Failed to load a FXML", cause);
        }

        guiOverlayPane.visibleProperty().bindBidirectional(
            guiOverlayContentPane.visibleProperty()
        );
        guiOverlayContentPane.visibleProperty().addListener(observable -> {

            if (guiOverlayContentPane.isVisible()) {
                guiContentPane.setEffect(new GaussianBlur(8.0));
            } else {
                guiContentPane.setEffect(null);
            }

        });

        setContent(content);
        setOverlayContent(overlayContent);

        getOverlayVisibleProperty().set(false); // Hide overlay by default
    }

    public Node getContent()
    {
        return guiContentPane.getCenter();
    }

    public void setContent(Node content)
    {
        guiContentPane.setCenter(content);
    }

    public Node getOverlayContent()
    {
        return guiOverlayContentPane.getCenter();
    }

    public void setOverlayContent(Node content)
    {
        guiOverlayContentPane.setCenter(content);
    }

    /**
     * @see OverlayPane#getOverlayVisibleProperty()
     */
    public void showOverlay()
    {
        guiOverlayContentPane.setVisible(true);
    }

    /**
     * @see OverlayPane#getOverlayVisibleProperty()
     */
    public void hideOverlay()
    {
        guiOverlayContentPane.setVisible(false);
    }

    public BooleanProperty getOverlayVisibleProperty()
    {
        return guiOverlayContentPane.visibleProperty();
    }

    @FXML
    protected StackPane guiStackPane;

    @FXML
    protected BorderPane guiContentPane;

    @FXML
    protected Pane guiOverlayPane;

    @FXML
    protected BorderPane guiOverlayContentPane;
}
