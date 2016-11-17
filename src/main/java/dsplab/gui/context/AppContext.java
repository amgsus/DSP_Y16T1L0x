package dsplab.gui.context;

import java.util.concurrent.ExecutorService;

public interface AppContext
{
    void init();
    void show();
    void shutdown();

    ExecutorService getThreadPool();
}
