package dsplab.gui.context;

import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;

public interface AppContext
{
    void init();

    /**
     * Shows the main stage.
     */
    void show();

    void releaseStage(Stage stage);

    ExecutorService getThreadPool();
}
