package dsplab.common;

import dsplab.DSPLabApplication;
import dsplab.gui.context.AppContext;

public final class Global
{
    Global() {}

    public static DSPLabApplication getApplication()
    {
        return DSPLabApplication.getInstance();
    }

    public static AppContext getContext()
    {
        return DSPLabApplication.getInstance().getContext();
    }
}
