package dsplab.architecture;

public interface Builder<T, B extends Builder>
{
    B newObject();
    T build();
}
