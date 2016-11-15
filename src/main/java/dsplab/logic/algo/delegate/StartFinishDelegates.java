package dsplab.logic.algo.delegate;

import dsplab.architecture.callback.Delegate;

public interface StartFinishDelegates
{
    void setOnBeforeStart(Delegate delegate);
    void setOnSuccess(Delegate delegate);
}
