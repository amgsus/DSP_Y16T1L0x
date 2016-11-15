package dsplab.architecture.pattern;

public abstract class BuilderEx<T>
{
    protected BuilderEx(T existingObject)
    {
        if (existingObject == null)
            throw new IllegalArgumentException("null");

        this.object = existingObject;
    }

    private T object;

    protected void ensureBuilderValid()
    {
        if (object == null)
            throw new IllegalStateException("Object has been already built");
    }

    protected T getObject()
    {
        ensureBuilderValid();
        return object;
    }

    public T build()
    {
        T built = this.object;
        this.object = null;
        return built;
    }
}
