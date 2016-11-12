package dsplab.architecture.ex;

import dsplab.architecture.ex.UIException;

public class StageInitException extends UIException
{
    public StageInitException()
    {
    }

    public StageInitException(String message)
    {
        super(message);
    }

    public StageInitException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public StageInitException(Throwable cause)
    {
        super(cause);
    }

    public StageInitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
