package dsplab.architecture.ex;

import dsplab.architecture.ex.UIException;

public class ControllerInitException extends UIException
{
    public ControllerInitException()
    {
    }

    public ControllerInitException(String message)
    {
        super(message);
    }

    public ControllerInitException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ControllerInitException(Throwable cause)
    {
        super(cause);
    }

    public ControllerInitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
