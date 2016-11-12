package dsplab.gui.util;

public
interface AcceptsBinding<T>
{
    void bind(T data);
    void unbind();
}
