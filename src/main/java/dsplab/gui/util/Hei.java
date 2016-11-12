package dsplab.gui.util;

public class Hei
{
    Hei() {}

    @SuppressWarnings("unchecked")
    public
    static <T> T cast(Object objectToCast)
    {
        return (T) objectToCast;
    }

    public
    static void assertSingleton(Object object)
    {
        if (object != null) {
            final String s = "'%s' could not be constructed (singleton)";
            throw new IllegalStateException(String.format(s,
                object.getClass().getSimpleName()));
        }
    }

    public
    static void assertThreadNotRunning(Thread thread)
    {
        if (thread == null)
            throw new IllegalArgumentException("<thread> is null");
        if (thread.isAlive()) {
            throw new IllegalStateException("This operation is not allowed " +
                "on the running thread");
        }
    }
}
