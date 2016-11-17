package dsplab.gui.context.impl;

import dsplab.gui.Stages;
import dsplab.gui.context.AppContext;
import dsplab.gui.stage.main.MainStage;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static dsplab.common.Const.MAIN;

public class WTFContext implements AppContext
{
    WTFContext() {}
    public static AppContext newInstance() { return new WTFContext(); }

    @Override
    public
    void init()
    {
        mainStage = Stages.getFactory().giveMeSomethingLike(MAIN);

        System.out.println("Initializing thread pool with 16 workers.");

        final int coreThreads = 16;
        final int maxSize = Integer.MAX_VALUE;
        final int keepAliveTime = 60; // 5 seconds

        threadPool = new ThreadPoolExecutor(coreThreads, maxSize,
            keepAliveTime, TimeUnit.SECONDS, new SynchronousQueue<>());
    }

    private MainStage mainStage;
    private ExecutorService threadPool;

    @Override
    public
    void show()
    {
        mainStage.show();
    }

    @Override
    public
    void shutdown()
    {
        List<?> queuedTasks = threadPool.shutdownNow();

        if (queuedTasks.size() > 0) {

            System.out.println("Thread pool: " + queuedTasks.size() +
                "tasks have been canceled.\nAwaiting others to complete...");

            try {
                if (threadPool.awaitTermination(2000, TimeUnit.MILLISECONDS)) {
                    System.out.println("All task completed successfully!");
                } else {
                    System.err.println("Thread pool: Timeout.");
                }
            } catch (InterruptedException intE) {
                System.err.println("Thread pool: awaitTermination() failed");
                intE.printStackTrace();
            }
        }
    }

    @Override
    public ExecutorService getThreadPool()
    {
        return this.threadPool;
    }
}
