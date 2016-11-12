package dsplab.architecture.util;

import javafx.scene.control.Alert;

public final class MessageBox
{
    MessageBox() {}

    public static void showInformation(String message)
    {
        Alert a = new Alert(Alert.AlertType.INFORMATION, message);
        a.setHeaderText(null);
        a.show();
    }
}
