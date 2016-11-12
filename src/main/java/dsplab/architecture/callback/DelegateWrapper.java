package dsplab.architecture.callback;

public class DelegateWrapper implements Delegate
{
    public DelegateWrapper()
    {
    }

    public DelegateWrapper(Delegate delegateToWrap)
    {
        wrapDelegate(delegateToWrap);
    }

    private Delegate delegate;

    public void wrapDelegate(Delegate delegateToWrap)
    {
        if (delegateToWrap == null)
            throw new IllegalArgumentException("<delegateToWrap> is null");

        this.delegate = delegateToWrap;
    }

    public void removeDelegate()
    {
        this.delegate = null;
    }

    public boolean isSet()
    {
        return (this.delegate != null);
    }

    @Override
    public void execute()
    {
        if (delegate != null) {
            delegate.execute();
        }
    }
}
