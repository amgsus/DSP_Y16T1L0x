package dsplab.architecture;

public interface MultiFactory<T> extends Factory<T>
{
    T giveMeSomethingLike(String id);
}
