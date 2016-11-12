package dsplab.gui.anim;

public interface ValueSupplier<T>
{
    T getValue(int seriesIndex, int dataIndex);
}
