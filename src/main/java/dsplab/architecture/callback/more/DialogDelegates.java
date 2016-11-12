package dsplab.architecture.callback.more;

import dsplab.architecture.callback.Delegate;

public interface DialogDelegates
{
    void setOnOK(Delegate okDelegate);
    void setOnCancel(Delegate caDelegate);
}
