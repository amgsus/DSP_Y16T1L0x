package dsplab.gui.ctrl.impl.signal;

import dsplab.architecture.ctrl.SimpleController;
import dsplab.architecture.ex.ControllerInitException;
import dsplab.common.Resources;
import dsplab.gui.util.SpectrumRender;
import dsplab.gui.ctrl.SpectrumController;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.function.Supplier;

import static dsplab.architecture.stage.StageCommImpl.ERR_INIT_FAILED;
import static dsplab.gui.util.SpectrumRender.SPECTRUM_RENDER_SIZE;

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

        guiAmplitudeSpectrumChart.getData().remove(ampSpectrumSeries);
        ampSpectrumSeries.getData().clear();

        SpectrumRender.renderByMaxValue(data, SPECTRUM_RENDER_SIZE * 2, obj ->
            ampSpectrumSeries.getData().add(obj));

        guiAmplitudeSpectrumChart.getData().add(ampSpectrumSeries);
    }

    @Override
    public void renderPhaseSpectrum()
    {
        if (this.phsSpectrumSupplier == null)
            throw new IllegalStateException("supplier:<null>");

        double[] data = phsSpectrumSupplier.get();

        guiPhaseSpectrumChart.getData().remove(phsSpectrumSeries);
        phsSpectrumSeries.getData().clear();

        SpectrumRender.renderByAvgValue(data, SPECTRUM_RENDER_SIZE * 2, obj ->
            phsSpectrumSeries.getData().add(obj));

        guiPhaseSpectrumChart.getData().add(phsSpectrumSeries);
    }

    // -------------------------------------------------------------------- //

    @FXML
    private BarChart<String, Double> guiAmplitudeSpectrumChart;
    private XYChart.Series<String, Double> ampSpectrumSeries =
        new XYChart.Series<>();

    @FXML
    private BarChart<String, Double> guiPhaseSpectrumChart;
    private XYChart.Series<String, Double> phsSpectrumSeries =
        new XYChart.Series<>();
}
