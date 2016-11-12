package dsplab.gui.ctrl.impl.signal;

import dsplab.architecture.ctrl.SimpleController;
import dsplab.architecture.ex.ControllerInitException;
import dsplab.architecture.stage.StageCommImpl;
import dsplab.common.Resources;
import dsplab.gui.ctrl.SpectrumController;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.function.Supplier;

import static dsplab.architecture.stage.StageCommImpl.ERR_INIT_FAILED;

public class SpectrumCtrlImpl extends SimpleController implements
    SpectrumController
{
    SpectrumCtrlImpl()
    {
        try {
            URL u = Resources.SPECTRUM_CHARTS_FXML;
            livenMe(u);
        } catch (Exception cause) {
            throw new ControllerInitException(ERR_INIT_FAILED, cause);
        }

        // ...
    }

    public static SpectrumController createInstance() { return new
        SpectrumCtrlImpl(); }

    // -------------------------------------------------------------------- //

    Supplier<double[]> ampSpectrumSupplier;
    Supplier<double[]> phsSpectrumSupplier;

    @Override
    public void setAmplitudeSpectrumData(Supplier<double[]> s)
    {
        this.ampSpectrumSupplier = s;
    }

    @Override
    public void setPhaseSpectrumData(Supplier<double[]> s)
    {
        this.phsSpectrumSupplier = s;
    }

    @Override
    public void renderAll()
    {
        this.renderAmplitudeSpectrum();
        this.renderPhaseSpectrum();
    }

    @Override
    public void renderAmplitudeSpectrum()
    {
        if (this.ampSpectrumSupplier == null)
            throw new IllegalStateException("supplier:<null>");

        double[] data = ampSpectrumSupplier.get();

        this.guiAmplitudeSpectrumChart.getData()
            .remove(amplitudeSpectrumSeries);
        amplitudeSpectrumSeries.getData().clear();

        for (int i = 0; i < data.length; i++) {
            amplitudeSpectrumSeries.getData().add(
                new XYChart.Data<>(Integer.toString(i), data[i])
            );
        }

        this.guiAmplitudeSpectrumChart.getData()
            .add(amplitudeSpectrumSeries);
    }

    @Override
    public void renderPhaseSpectrum()
    {
        if (this.phsSpectrumSupplier == null)
            throw new IllegalStateException("supplier:<null>");

        double[] data = phsSpectrumSupplier.get();

        this.guiPhaseSpectrumChart.getData()
            .remove(phaseSpectrumSeries);
        phaseSpectrumSeries.getData().clear();

        for (int i = 0; i < data.length; i++) {
            phaseSpectrumSeries.getData().add(
                new XYChart.Data<>(Integer.toString(i), data[i])
            );
        }

        this.guiPhaseSpectrumChart.getData()
            .add(phaseSpectrumSeries);
    }

    // -------------------------------------------------------------------- //

    @FXML
    private BarChart<String, Double> guiAmplitudeSpectrumChart;
    private XYChart.Series<String, Double> amplitudeSpectrumSeries =
        new XYChart.Series<>();

    @FXML
    private BarChart<String, Double> guiPhaseSpectrumChart;
    private XYChart.Series<String, Double> phaseSpectrumSeries =
        new XYChart.Series<>();
}
