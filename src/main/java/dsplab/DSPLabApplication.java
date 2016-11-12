package dsplab;

import dsplab.common.Global;
import dsplab.gui.context.AppContext;
import dsplab.gui.context.impl.WTFContext;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import static dsplab.gui.util.Hei.assertSingleton;

/**
 * The application context.
 */
public class DSPLabApplication extends Application
{
    private static DSPLabApplication instance;
    public  static DSPLabApplication getInstance() { return instance; }

    private AppContext cxt = WTFContext.newInstance();
    public  AppContext getContext() { return cxt; }

    // --------------------------------------------------------------------- //

    /**
     * @throws IllegalStateException if the application class is being
     * constructed more than one time.
     */
    public DSPLabApplication() throws IllegalStateException
    {
        assertSingleton(instance);
        instance = this;
    }

    @Override
    public void start(Stage embeddedStage)
    {
        try {
            cxt.init();
        } catch (Throwable cs) {
            System.err.println("[FATAL] Failed to init context");
            cs.printStackTrace();
            Platform.exit();
            return;
        }

        Global.getContext().show();
    }
}
