package dsplab.logic.algo.delegate;

import dsplab.architecture.callback.Delegate;

public interface StartFinishDelegates
{
    void setOnStart(Delegate delegate);
    void setOnSuccess(Delegate delegate);
}
