package dsplab.architecture.ctrl.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

public final class Utils
{
    Utils() {}

    /**
     * Loads components JavaFX node hierarchy from FXML document and associates it with
     * components C-object.
     *
     * @param fxmlUrl The location of components document.
     * @param ctrlObj A C-object or {@code null} if not required.
     * @param <T>
     * @return The top element of the loaded hierarchy.
     * @throws IOException
     */
    public static <T extends Parent> T fromFXML(URL fxmlUrl, Object ctrlObj)
        throws IOException
    {
        if (fxmlUrl == null)
            throw new IllegalArgumentException("fxmlUrl");
        FXMLLoader fxmlDoc = new FXMLLoader(fxmlUrl);
        fxmlDoc.setController(ctrlObj);
        return fxmlDoc.load();
    }

    public static void fromFXML(URL fxmlUrl, Object ctrlObj, Object fxRoot)
        throws IOException
    {
        if (fxmlUrl == null)
            throw new IllegalArgumentException("fxmlUrl");
        FXMLLoader fxmlDoc = new FXMLLoader(fxmlUrl);
        fxmlDoc.setRoot(fxRoot);
        fxmlDoc.setController(ctrlObj);
        fxmlDoc.load();
    }
}
