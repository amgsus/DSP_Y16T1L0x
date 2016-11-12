package dsplab.gui.anim;

import dsplab.architecture.callback.Delegate;
import dsplab.architecture.callback.DelegateWrapper;
import javafx.animation.AnimationTimer;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class LineChartRenderAnimation<X extends Number, Y extends Number>
    extends AnimationTimer
{
    public LineChartRenderAnimation(LineChart<X, Y> lineChart)
    {
        this.lineChart = lineChart;
    }

    // -------------------------------------------------------------------- //

    LineChart<X, Y> lineChart;

    int skipFramesReload = 0;
    int framesToSkip = 0;

    final int pointsPerTick = 128;

    int index = 0;
    public int count = 0; // ToDo: How to find out this value? Samples!

    private ValueSupplier<X> valueXSupplier; // Not used
    private ValueSupplier<Y> valueYSupplier;

    private DelegateWrapper onStop = new DelegateWrapper();

    // -------------------------------------------------------------------- //

    @Override
    public void start()
    {
        if (lineChart == null)
            throw new IllegalArgumentException("lineChart");

        index = 0;

        super.start();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handle(long now)
    {
        try {
            if (index < count) {

                if (framesToSkip > 0) {
                    framesToSkip = framesToSkip - 1;
                    return;
                }

                int remain = (index + pointsPerTick < count ?
                    pointsPerTick : count - index);

                for (int i = 0; i < remain; i++) {

                    // Interrate through all series at X (index) value //

                    for (int j = 0; j < lineChart.getData().size(); j++) {

                        Number x = index;
                        Number a = valueYSupplier.getValue(j, index); // Value

                        lineChart.getData().get(j).getData().add(
                            new XYChart.Data<>(
                                (X) x,
                                (Y) a
                            ));
                    }

                    index++;
                }

                framesToSkip = skipFramesReload;

            } else {
                stop();
                onStop.execute();
            }
        } catch (Exception cause) {
            stop();
            onStop.execute();
            throw cause;
        }
    }

    // -------------------------------------------------------------------- //

    /**
     * <b>Note:</b> This supplier should return a value as quick as possible
     * as it called from animation tick.
     *
     * @param supplier The supplier for the X-axis value.
     */
    public
    void setValueXSupplier(ValueSupplier<X> supplier)
    {
        this.valueXSupplier = supplier;
    }

    /**
     * <b>Note:</b> This supplier should return a value as quick as possible
     * as it called from animation tick.
     *
     * @param supplier The supplier for the X-axis value.
     */
    public
    void setValueYSupplier(ValueSupplier<Y> supplier)
    {
        this.valueYSupplier = supplier;
    }

    public
    void setThrottling(int ticks)
    {
        if (ticks < 0)
            throw new IllegalArgumentException("ticks");

        this.skipFramesReload = ticks;
    }

    public
    void setDataCount(int count)
    {
        if (count < 0)
            throw new IllegalArgumentException("'count' should be positive");

        this.count = count;
    }

    public
    void setOnStop(Delegate delegate)
    {
        if (delegate != null)
            onStop.wrapDelegate(delegate);
        else
            onStop.removeDelegate();
    }

    public
    void setOffset(int offset)
    {
        this.index = offset;
    }
}
