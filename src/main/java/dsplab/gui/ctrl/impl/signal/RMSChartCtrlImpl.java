package dsplab.gui.ctrl.impl.signal;

import dsplab.architecture.ctrl.SimpleController;
import dsplab.architecture.ex.ControllerInitException;
import dsplab.common.Const;
import dsplab.common.Resources;
import dsplab.gui.ctrl.RMSChartController;
import dsplab.gui.util.Hei;
import dsplab.logic.ft.FourierTransform;
import dsplab.logic.rms.RMSCalculator;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.List;
import java.util.function.Supplier;

public class RMSChartCtrlImpl extends SimpleController implements
    RMSChartController
{
    public RMSChartCtrlImpl()
    {
        try {
            URL u = Resources.RMS_CHART_FXML;
            livenMe(u);
        } catch (Exception cause) {
            throw new ControllerInitException(CTRL_INIT_FAILED_MSG, cause);
        }

        setCaption("");
    }

    public static RMSChartController createInstance() { return new
        RMSChartCtrlImpl(); }

    // -------------------------------------------------------------------- //

    @Override
    public
    void renderAll()
    {
        this.renderAmplitudeValues();
        this.renderRMSValues();
    }

    @Override
    public
    void renderRMSValues()
    {
        double[] values = rmsValuesSupplier.get();

        NumberAxis axisX = Hei.cast(this.guiRMSeChart.getXAxis());
        axisX.setUpperBound(values.length);
        axisX.setLowerBound(this.K);

        int tickUnit = values.length - this.K;
        tickUnit = tickUnit / Math.min(8, tickUnit / 8);

        axisX.setTickUnit(tickUnit);

        List<XYChart.Series<Integer, Double>> data
            = this.guiRMSeChart.getData();

        XYChart.Series<Integer, Double> s = new XYChart.Series<>();

        for (int i = K; i < values.length; i++) {
            double e = RMSCalculator.calculateRMSError(values[i]);
            s.getData().add(new XYChart.Data<>(i, e));
        }

        data.clear();
        data.add(s);

        this.guiRMSValueLabel.setText(String.format("%.3f",
            values[values.length - 1]));
        this.guiRMSeValueLabel.setText(String.format("%.3f",
            RMSCalculator.calculateRMSError(values[values.length - 1])));
    }

    @Override
    public
    void renderAmplitudeValues()
    {
        double[] values = amplitudeValuesSupplier.get();

        NumberAxis axisX = Hei.cast(this.guiAeChart.getXAxis());
        axisX.setUpperBound(values.length);
        axisX.setLowerBound(this.K);

        int tickUnit = values.length - this.K;
        tickUnit = tickUnit / Math.min(8, tickUnit / 8);

        axisX.setTickUnit(tickUnit);

        List<XYChart.Series<Integer, Double>> data
            = this.guiAeChart.getData();

        XYChart.Series<Integer, Double> s = new XYChart.Series<>();

        for (int i = K; i < values.length; i++) {
            double e = FourierTransform.calculateAmplitudeError(values[i]);
            s.getData().add(new XYChart.Data<>(i, e));
        }

        data.clear();
        data.add(s);
    }

    // -------------------------------------------------------------------- //

    private int K; // Variant

    private Supplier<double[]> rmsValuesSupplier = null;
    private Supplier<double[]> amplitudeValuesSupplier = null;

    @Override
    public
    void setCaption(String s)
    {
        if (s != null)
            this.guiCaptionLabel.setText(s);
        else
            this.guiCaptionLabel.setText(Const.NOT_AVAILABLE_STR);
    }

    @Override
    public
    void setK(int value)
    {
        this.K = value;
        this.guiKConstValueLabel.setText(String.valueOf(value));
    }

    @Override
    public
    void setRMSValues(Supplier<double[]> supplier)
    {
        rmsValuesSupplier = supplier;
    }

    @Override
    public
    void setAmplitudeValues(Supplier<double[]> supplier)
    {
        amplitudeValuesSupplier = supplier;
    }

    // -------------------------------------------------------------------- //

    @FXML
    private Label guiCaptionLabel;

    @FXML
    private Label guiRMSValueLabel;

    @FXML
    private Label guiRMSeValueLabel;

    @FXML
    private LineChart<Integer, Double> guiRMSeChart;

    @FXML
    private LineChart<Integer, Double> guiAeChart;

    @FXML
    private Label guiKConstValueLabel;
}
