package dsplab.architecture.ctrl;

import dsplab.architecture.ctrl.util.Utils;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

/**
 * Simple & Small.
 */
public class SimpleController implements Controller
{
    /* SURPRISE MOTHERFUCKER */
    protected Parent o_O;

    /**
     * @param url The location of components FXML document.
     * @see Utils#fromFXML(URL, Object)
     */
    protected void livenMe(URL url) throws IOException
    {
        this.o_O = Utils.fromFXML(url, this);
    }

    /**
     * @return The top element of an UI component tree associated with this
     * C-object.
     */
    @Override
    public
    Parent getFxRoot()
    {
        return o_O;
    }

    public static final String CTRL_INIT_FAILED_MSG
        = "Failed to initialize a controller object";
}
