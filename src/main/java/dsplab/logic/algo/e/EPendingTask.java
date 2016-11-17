package dsplab.logic.algo.e;

public class EPendingTask extends Exception
{
    public EPendingTask()
    {
    }

    public EPendingTask(String message)
    {
        super(message);
    }

    public EPendingTask(String message, Throwable cause)
    {
        super(message, cause);
    }

    public EPendingTask(Throwable cause)
    {
        super(cause);
    }
}
