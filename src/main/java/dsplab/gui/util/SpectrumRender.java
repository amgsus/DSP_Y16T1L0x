package dsplab.gui.util;

import dsplab.architecture.Callback;
import dsplab.logic.MathUtils;
import javafx.scene.chart.XYChart;

public final class SpectrumRender
{
    SpectrumRender() {}

    public static final int SPECTRUM_RENDER_SIZE = 256;
    public static final int CHART_RENDER_SIZE = 1024;

    public static int renderByMaxValue(double[] data, int maxRenderPoints,
        Callback<XYChart.Data<String, Double>> callback)
    {
        if (data == null)
            throw new IllegalArgumentException("data:null");

        final int maxCycle = Math.min(data.length, maxRenderPoints);
        final int scale = data.length / maxRenderPoints;

        for (int i = 0; i < maxCycle; i++) {

            int real = scale == 0 ? i : scale * i; // Unscaled index
            double maxAmplitude = data[real];

            if (scale > 1) {
                for (int j = 1 + real; j < scale + real; j++)
                    maxAmplitude = Math.max(maxAmplitude, data[j]);
            }

            callback.call(new XYChart.Data<>(Integer.toString(i),
                maxAmplitude));
        }

        return maxCycle;
    }

    public static int renderByAvgValue(double[] data, int maxRenderPoints,
        Callback<XYChart.Data<String, Double>> callback)
    {
        if (data == null)
            throw new IllegalArgumentException("data:null");

        final int maxCycle = Math.min(data.length, maxRenderPoints);
        final int scale = data.length / maxRenderPoints;

        for (int i = 0; i < maxCycle; i++) {

            int real = scale == 0 ? i : scale * i; // Unscaled index
            double avgAmplitude;

            if (scale > 1) {
                avgAmplitude = MathUtils.avg(data, real, scale);
            } else {
                avgAmplitude = data[real];
            }

            callback.call(new XYChart.Data<>(Integer.toString(i),
                avgAmplitude));
        }

        return maxCycle;
    }
}
