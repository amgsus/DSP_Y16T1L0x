package dsplab.gui.ctrl.impl.signal;

import dsplab.architecture.ctrl.SimpleController;
import dsplab.architecture.ex.ControllerInitException;
import dsplab.common.Resources;
import dsplab.gui.ctrl.SignalRestoreController;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.function.Supplier;

import static dsplab.architecture.stage.StageCommImpl.ERR_INIT_FAILED;
import static dsplab.gui.util.Hei.cast;

public class SignalRestoreCtrlImpl extends SimpleController implements
    SignalRestoreController
{
    public SignalRestoreCtrlImpl()
    {
        try {
            URL u = Resources.SIGNAL_RESTORE_FXML;
            livenMe(u);
        } catch (Exception cause) {
            throw new ControllerInitException(ERR_INIT_FAILED, cause);
        }

        this.signalSeries = new Series<>();
        this.signalSeries.setName("Signal");

        this.restoredSignalSeries = new Series<>();
        this.restoredSignalSeries.setName("Restored");

        this.restoredWithPhaseSeries = new Series<>();
        this.restoredWithPhaseSeries.setName("Restored with phase");

        initToggleGroupListeners();

        guiSingleViewRadioBtn.setSelected(true);

        initEventHandlers();

        guiNormalModeCheckBox.setSelected(true);
        guiModeWithPhaseCheckBox.setSelected(true);

        renderLock = false;
    }

    public static SignalRestoreController createInstance() { return new
        SignalRestoreCtrlImpl(); }

    // -------------------------------------------------------------------- //

    private Series<Integer, Double> signalSeries;
    private Series<Integer, Double> restoredSignalSeries;
    private Series<Integer, Double> restoredWithPhaseSeries;

    /*
     * This will lock renders to run on the object construction, preventing
     * throw of IllegalStateException, 'cause suppliers not set.
     */
    private boolean renderLock = true; // ... or for perfomance improvements.

    @Override
    public
    void renderAll()
    {
        this.renderSignal();
        this.renderRestoredSignal();
    }

    @Override
    public
    void renderSignal()
    {
        if (renderLock)
            return;

        if (this.signalSupplier == null)
            throw new IllegalStateException("supplier:<null>");

        double[] data = signalSupplier.get();
        int dataSize = data.length;

        NumberAxis axisX = cast(guiRestoredSignalChart.getXAxis());
        axisX.setUpperBound(dataSize);

        axisX = cast(guiSignalChart.getXAxis());
        axisX.setUpperBound(dataSize);

        guiSignalChart.getData().remove(signalSeries);
        signalSeries.getData().clear();

        for (int i = 0; i < dataSize; i++) {
            signalSeries.getData().add(
                new XYChart.Data<>(i, data[i])
            );
        }

        guiSignalChart.getData().add(signalSeries);
    }

    @Override
    public
    void renderRestoredSignal()
    {
        this.impl_renderRestoredSignal();
        this.impl_renderRestoredWithPhaseSignal();
    }

    public
    void impl_renderRestoredSignal()
    {
        if (renderLock)
            return;

        LineChart<Integer, Double> chart = (guiViewRadioBtnGroup
            .getSelectedToggle() == guiSingleViewRadioBtn ? guiSignalChart :
            guiRestoredSignalChart);

        guiSignalChart.getData().remove(restoredSignalSeries);
        guiRestoredSignalChart.getData().remove(restoredSignalSeries);

        if (guiNormalModeCheckBox.isSelected()) {

            if (restoredSignalSupplier == null)
                throw new IllegalStateException("supplier:<null>");

            double[] data = restoredSignalSupplier.get();

            chart.getData().remove(restoredSignalSeries);
            restoredSignalSeries.getData().clear();

            for (int i = 0; i < data.length; i++) {
                restoredSignalSeries.getData().add(
                    new XYChart.Data<>(2 * i, data[i])
                );
                restoredSignalSeries.getData().add(
                    new XYChart.Data<>(2 * i + 1, data[i])
                );
            }

            chart.getData().add(restoredSignalSeries);
        }
    }

    public
    void impl_renderRestoredWithPhaseSignal()
    {
        if (renderLock)
            return;

        LineChart<Integer, Double> chart = (guiViewRadioBtnGroup
            .getSelectedToggle() == guiSingleViewRadioBtn ? guiSignalChart :
            guiRestoredSignalChart);

        guiSignalChart.getData().remove(restoredWithPhaseSeries);
        guiRestoredSignalChart.getData().remove(restoredWithPhaseSeries);

        if (guiModeWithPhaseCheckBox.isSelected()) {

            if (restoredWithPhaseSignalSupplier == null)
                throw new IllegalStateException("supplier:<null>");

            double[] data = restoredWithPhaseSignalSupplier.get();

            chart.getData().remove(restoredWithPhaseSeries);
            restoredWithPhaseSeries.getData().clear();

            for (int i = 0; i < data.length; i++) {
                restoredWithPhaseSeries.getData().add(
                    new XYChart.Data<>(2 * i, data[i])
                );
                restoredWithPhaseSeries.getData().add(
                    new XYChart.Data<>(2 * i + 1, data[i])
                );
            }

            chart.getData().add(restoredWithPhaseSeries);
        }
    }

    // -------------------------------------------------------------------- //

    private Supplier<double[]> signalSupplier;
    private Supplier<double[]> restoredSignalSupplier;
    private Supplier<double[]> restoredWithPhaseSignalSupplier;

    @Override
    public
    void setSignalSupplier(Supplier<double[]> supplier)
    {
        this.signalSupplier = supplier;
    }

    @Override
    public
    void setRestoredSignalSupplier(Supplier<double[]> supplier)
    {
        this.restoredSignalSupplier = supplier;
    }

    @Override
    public
    void setRestoredWithPhaseSignalSupplier(Supplier<double[]> supplier)
    {
        this.restoredWithPhaseSignalSupplier = supplier;
    }

    // -------------------------------------------------------------------- //

    protected
    void initToggleGroupListeners()
    {
        guiViewRadioBtnGroup.selectedToggleProperty()
            .addListener(o -> {
                if (guiViewRadioBtnGroup.getSelectedToggle() ==
                    guiSeparatedViewRadioBtn) {
                    guiChartsBox.getChildren().add(guiRestoredSignalBox);
                } else {
                    guiChartsBox.getChildren().remove(guiRestoredSignalBox);
                }

                renderRestoredSignal();
            });

        guiSignalChart.legendVisibleProperty().bind(guiSingleViewRadioBtn
            .selectedProperty());

        guiRestoredSignalChart.legendVisibleProperty().bind(
            guiNormalModeCheckBox.selectedProperty().and(
                guiModeWithPhaseCheckBox.selectedProperty()
            )
        );
    }

    protected
    void initEventHandlers()
    {
        guiNormalModeCheckBox.selectedProperty().addListener(o -> {
            impl_renderRestoredSignal();
        });

        guiModeWithPhaseCheckBox.selectedProperty().addListener(o -> {
            impl_renderRestoredWithPhaseSignal();
        });
    }

    // -------------------------------------------------------------------- //

    @FXML
    private RadioButton guiSingleViewRadioBtn;

    @FXML
    private ToggleGroup guiViewRadioBtnGroup;

    @FXML
    private RadioButton guiSeparatedViewRadioBtn;

    @FXML
    private CheckBox guiNormalModeCheckBox;

    @FXML
    private CheckBox guiModeWithPhaseCheckBox;

    @FXML
    private LineChart<Integer, Double> guiSignalChart;

    @FXML
    private LineChart<Integer, Double> guiRestoredSignalChart;

    @FXML
    private HBox guiRestoredSignalBox;

    @FXML
    private VBox guiChartsBox;
}
