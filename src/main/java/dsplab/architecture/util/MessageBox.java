package dsplab.architecture.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import static dsplab.common.Const.APPTITLE;

public final class MessageBox
{
    MessageBox() {}

    /* COMMON */

    public static Alert get(String msgText, String hdrText, String dlgText,
        AlertType alertType)
    {
        Alert a = new Alert(alertType);

        a.setTitle(dlgText);
        a.setHeaderText(hdrText);
        a.setContentText(msgText);
        a.setResizable(true);

        return a;
    }

    /* INFORMATION */

    public static Alert getInfoBox(String msgText)
    {
        return getInfoBox(msgText, null);
    }

    public static Alert getInfoBox(String msgText, String hdrText)
    {
        return getInfoBox(msgText, hdrText, APPTITLE);
    }

    public static Alert getInfoBox(String msgText, String hdrText,
        String dlgText)
    {
        return get(msgText, hdrText, dlgText, AlertType.INFORMATION);
    }

    /* WARNING */

    public static Alert getWarnBox(String msgText)
    {
        return getWarnBox(msgText, null);
    }

    public static Alert getWarnBox(String msgText, String hdrText)
    {
        return getWarnBox(msgText, hdrText, APPTITLE);
    }

    public static Alert getWarnBox(String msgText, String hdrText,
        String dlgText)
    {
        return get(msgText, hdrText, dlgText, AlertType.WARNING);
    }

    /* ERROR */

    public static Alert getErroBox(String msgText)
    {
        return getErroBox(msgText, null);
    }

    public static Alert getErroBox(String msgText, String hdrText)
    {
        return getErroBox(msgText, hdrText, APPTITLE);
    }

    public static Alert getErroBox(String msgText, String hdrText,
        String dlgText)
    {
        return get(msgText, hdrText, dlgText, AlertType.ERROR);
    }

    /* CONFIRMATION */

    public static Alert getCnfmBox(String msgText)
    {
        return getCnfmBox(msgText, null);
    }

    public static Alert getCnfmBox(String msgText, String hdrText)
    {
        return getCnfmBox(msgText, hdrText, APPTITLE);
    }

    public static Alert getCnfmBox(String msgText, String hdrText,
        String dlgText)
    {
        return get(msgText, hdrText, dlgText, AlertType.CONFIRMATION);
    }
}
