package dsplab.gui.ctrl.impl.signal;

import dsplab.architecture.ctrl.SimpleController;
import dsplab.architecture.ex.ControllerInitException;
import dsplab.common.Resources;
import dsplab.gui.ctrl.SmoothChartController;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;
import java.net.URL;
import java.util.function.Supplier;

import static dsplab.gui.util.Hei.cast;

public class SmoothChartCtrlImpl extends SimpleController implements
    SmoothChartController
{
    SmoothChartCtrlImpl()
    {
        try {
            URL u = Resources.SMOOTH_CHART_FXML;
            livenMe(u);
        } catch (IOException cause) {
            throw new ControllerInitException(CTRL_INIT_FAILED_MSG, cause);
        }

        // ... //

        signalSeries = new XYChart.Series<>();
        signalSeries.setName("Signal");

        smoothedSignalSeries = new XYChart.Series<>();
        smoothedSignalSeries.setName("Filtered");

        srcAmplitudeSpectrumSeries = new XYChart.Series<>();
        srcPhaseSpectrumSeries = new XYChart.Series<>();

        this.initDataSupplierSwitch();
    }

    public static SmoothChartCtrlImpl createInstance() { return new
        SmoothChartCtrlImpl(); }

    // -------------------------------------------------------------------- //

    XYChart.Series<Integer, Double> signalSeries;
    XYChart.Series<Integer, Double> smoothedSignalSeries;

    XYChart.Series<String , Double> srcAmplitudeSpectrumSeries;
    XYChart.Series<String , Double> srcPhaseSpectrumSeries;

    Supplier<double[]> signalDataSupplier = null;

    Supplier<double[]> sliSmoothSignalDataSupplier = null;
    Supplier<double[]> sliSmoothSignalAmplitudeSpectrumDataSupplier = null;
    Supplier<double[]> sliSmoothSignalPhaseSpectrumDataSupplier = null;

    Supplier<double[]> mdnSmoothSignalDataSupplier = null;
    Supplier<double[]> mdnSmoothSignalAmplitudeSpectrumDataSupplier = null;
    Supplier<double[]> mdnSmoothSignalPhaseSpectrumDataSupplier = null;

    Supplier<double[]> pblSmoothSignalDataSupplier = null;
    Supplier<double[]> pblSmoothSignalAmplitudeSpectrumDataSupplier = null;
    Supplier<double[]> pblSmoothSignalPhaseSpectrumDataSupplier = null;

    @Override
    public
    void setSignalSupplier(Supplier<double[]> supplier)
    {
        this.signalDataSupplier = supplier;
    }

    @Override
    public void setSliAmplitudeSpectrumSupplier(Supplier<double[]> supplier)
    {
        this.sliSmoothSignalAmplitudeSpectrumDataSupplier = supplier;
    }

    @Override
    public void setSliPhaseSpectrumSupplier(Supplier<double[]> supplier)
    {
        this.sliSmoothSignalPhaseSpectrumDataSupplier = supplier;
    }

    @Override
    public void setSliSignalSupplier(Supplier<double[]> supplier)
    {
        this.sliSmoothSignalDataSupplier = supplier;
    }

    @Override
    public
    void setMdnSignalSupplier(Supplier<double[]> supplier)
    {
        this.mdnSmoothSignalDataSupplier = supplier;
    }

    @Override
    public
    void setMdnAmplitudeSpectrumSupplier(Supplier<double[]> supplier)
    {
        this.mdnSmoothSignalAmplitudeSpectrumDataSupplier = supplier;
    }

    @Override
    public
    void setMdnPhaseSpectrumSupplier(Supplier<double[]> supplier)
    {
        this.mdnSmoothSignalPhaseSpectrumDataSupplier = supplier;
    }

    @Override
    public
    void setPblSignalSupplier(Supplier<double[]> supplier)
    {
        this.pblSmoothSignalDataSupplier = supplier;
    }

    @Override
    public
    void setPblAmplitudeSpectrumSupplier(Supplier<double[]> supplier)
    {
        this.pblSmoothSignalAmplitudeSpectrumDataSupplier = supplier;
    }

    @Override
    public
    void setPblPhaseSpectrumSupplier(Supplier<double[]> supplier)
    {
        this.pblSmoothSignalPhaseSpectrumDataSupplier = supplier;
    }

    // -------------------------------------------------------------------- //

    @Override
    public
    void renderAll()
    {
        this.renderSignal();
        this.renderSmoothedSignal();
        this.renderAmplitudeSpectrum();
        this.renderPhaseSpectrum();
    }

    @Override
    public
    void renderSignal()
    {
        if (this.signalDataSupplier == null)
            throw new IllegalStateException("signalDataSupplier:<null>");

        double[] data = this.signalDataSupplier.get();

        NumberAxis axisX = cast(this.guiSignalChart.getXAxis());
        axisX.setUpperBound(data.length);

        this.guiSignalChart.getData().remove(signalSeries);
        signalSeries.getData().clear();

        for (int i = 0; i < data.length; i++) {
            signalSeries.getData().add(
                new XYChart.Data<>(i, data[i])
            );
        }

        this.guiSignalChart.getData().add(signalSeries);
    }

    @Override
    public
    void renderSmoothedSignal()
    {
        RadioButton rb = cast(guiFilterRadioBtnGroup.getSelectedToggle());

        Supplier<double[]> supplier = null;

        if (rb == guiSlidingRadioBtn) {
            supplier
                = this.sliSmoothSignalDataSupplier;
        } else {
            if (rb == guiMedianRadioBtn) {
                supplier
                    = this.mdnSmoothSignalDataSupplier;
            } else {
                if (rb == guiParabolicRadioBtn) {
                    supplier
                        = this.pblSmoothSignalDataSupplier;
                }
            }
        }

        if (supplier == null)
            throw new IllegalStateException("supplier:<null>");

        double[] data = supplier.get();

        this.guiSignalChart.getData()
            .remove(smoothedSignalSeries);
        smoothedSignalSeries.getData().clear();

        for (int i = 0; i < data.length; i++) {
            smoothedSignalSeries.getData().add(
                new XYChart.Data<>(i, data[i])
            );
        }

        this.guiSignalChart.getData()
            .add(smoothedSignalSeries);
    }

    @Override
    public
    void renderAmplitudeSpectrum()
    {
        RadioButton rb = cast(guiFilterRadioBtnGroup.getSelectedToggle());

        Supplier<double[]> supplier = null;

        if (rb == guiSlidingRadioBtn) {
            supplier
                = this.sliSmoothSignalAmplitudeSpectrumDataSupplier;
        } else {
            if (rb == guiMedianRadioBtn) {
                supplier
                    = this.mdnSmoothSignalAmplitudeSpectrumDataSupplier;
            } else {
                if (rb == guiParabolicRadioBtn) {
                    supplier
                        = this.pblSmoothSignalAmplitudeSpectrumDataSupplier;
                }
            }
        }

        if (supplier == null)
            throw new IllegalStateException("supplier:<null>");

        double[] data = supplier.get();

        /*
        NumberAxis axisX = cast(this.guiAmplitudeSpectrumChart.getXAxis());
        axisX.setUpperBound(data.length);
        */

        this.guiAmplitudeSpectrumChart.getData()
            .remove(srcAmplitudeSpectrumSeries);
        srcAmplitudeSpectrumSeries.getData().clear();

        for (int i = 0; i < data.length; i++) {
            srcAmplitudeSpectrumSeries.getData().add(
                new XYChart.Data<>(Integer.toString(i), data[i])
            );
        }

        this.guiAmplitudeSpectrumChart.getData()
            .add(srcAmplitudeSpectrumSeries);
    }

    @Override
    public
    void renderPhaseSpectrum()
    {
        RadioButton rb = cast(guiFilterRadioBtnGroup.getSelectedToggle());

        Supplier<double[]> supplier = null;

        if (rb == guiSlidingRadioBtn) {
            supplier
                = this.sliSmoothSignalPhaseSpectrumDataSupplier;
        } else {
            if (rb == guiMedianRadioBtn) {
                supplier
                    = this.mdnSmoothSignalPhaseSpectrumDataSupplier;
            } else {
                if (rb == guiParabolicRadioBtn) {
                    supplier
                        = this.pblSmoothSignalPhaseSpectrumDataSupplier;
                }
            }
        }

        if (supplier == null)
            throw new IllegalStateException("supplier:<null>");

        double[] data = supplier.get();

        /*
        NumberAxis axisX = cast(this.guiPhaseSpectrumChart.getXAxis());
        axisX.setUpperBound(data.length);
        */

        this.guiPhaseSpectrumChart.getData()
            .remove(srcPhaseSpectrumSeries);
        srcPhaseSpectrumSeries.getData().clear();

        for (int i = 0; i < data.length; i++) {
            srcPhaseSpectrumSeries.getData().add(
                new XYChart.Data<>(Integer.toString(i), data[i])
            );
        }

        this.guiPhaseSpectrumChart.getData()
            .add(srcPhaseSpectrumSeries);
    }

    // -------------------------------------------------------------------- //

    protected
    void initDataSupplierSwitch()
    {
        guiFilterRadioBtnGroup.selectedToggleProperty().addListener(o -> {
            this.renderSmoothedSignal();
            this.renderAmplitudeSpectrum();
            this.renderPhaseSpectrum();
        });
    }

    // -------------------------------------------------------------------- //

    @FXML
    private LineChart<Integer, Double> guiSignalChart;

    @FXML
    private RadioButton guiSlidingRadioBtn;

    @FXML
    private RadioButton guiMedianRadioBtn;

    @FXML
    private RadioButton guiParabolicRadioBtn;

    @FXML
    private BarChart<String, Double> guiAmplitudeSpectrumChart;

    @FXML
    private BarChart<String, Double> guiPhaseSpectrumChart;

    @FXML
    private ToggleGroup guiFilterRadioBtnGroup;
}
