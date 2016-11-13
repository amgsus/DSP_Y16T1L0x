package dsplab.gui.ctrl.impl.signal;

import dsplab.architecture.ctrl.SimpleController;
import dsplab.architecture.ex.ControllerInitException;
import dsplab.common.Resources;
import dsplab.gui.Controllers;
import dsplab.gui.ctrl.EachSignalTabController;
import dsplab.gui.ctrl.RMSChartController;
import dsplab.gui.ctrl.SmoothChartController;
import dsplab.gui.ctrl.SpectrumController;
import dsplab.logic.algo.production.AlgorithmResult;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.net.URL;

import static dsplab.common.Const.RMSCHART;
import static dsplab.common.Const.SMOOTHCHART;
import static dsplab.common.Const.SPECTRUMCHARTS;

public class EachSignalTabControllerImpl extends SimpleController implements
    EachSignalTabController
{
    EachSignalTabControllerImpl()
    {
        try {
            URL u = Resources.EACH_SIGNAL_TAB_FXML;
            livenMe(u);
        } catch (Throwable cause) {
            throw new ControllerInitException(CTRL_INIT_FAILED_MSG, cause);
        }

        guiTabPane.getStylesheets().add(
            Resources.TAB_PANE_BRIGHTER_HEADER_STYLESHEET);


        /* RMS Chart */

        rmsChartController1 = Controllers.getFactory()
            .giveMeSomethingLike(RMSCHART);
        rmsChartController2 = Controllers.getFactory()
            .giveMeSomethingLike(RMSCHART);

        VBox.setVgrow(rmsChartController1.getFxRoot(), Priority.ALWAYS);
        VBox.setVgrow(rmsChartController2.getFxRoot(), Priority.ALWAYS);

        guiRMSBox.getChildren().addAll(rmsChartController1.getFxRoot(),
            rmsChartController2.getFxRoot());

        rmsChartController1.setCaption("A (2.21):");
        rmsChartController2.setCaption("B (2.22):");

        /* Smoothed Signal Chart */

        smoothChartController = Controllers.getFactory()
            .giveMeSomethingLike(SMOOTHCHART);

        guiSmoothChartTab.setContent(smoothChartController.getFxRoot());

        /* Source Signal Spectrums */

        spectrums = Controllers.getFactory()
            .giveMeSomethingLike(SPECTRUMCHARTS);
        guiSpectrumTab.setContent(spectrums.getFxRoot());
    }

    public static EachSignalTabController createInstance()
    {
        return new
            EachSignalTabControllerImpl();
    }

    // -------------------------------------------------------------------- //

    @Override
    public void renderAlgoResult(AlgorithmResult algoResult)
    {
        if (algoResult == null)
            throw new IllegalArgumentException("null");

        int constK = algoResult.getSampleCount() / 4; // Variant

        spectrums.setAmplitudeSpectrumData(algoResult::getAmplitudeSpectrum);
        spectrums.setPhaseSpectrumData(algoResult::getPhaseSpectrum);
        spectrums.renderAll();

        rmsChartController1.renderRMSValues(algoResult.getRMSByFormulaA());
        rmsChartController1.renderAmplitudeValues(algoResult.getFtAmplitudes());
        rmsChartController1.setK(constK);

        rmsChartController2.renderRMSValues(algoResult.getRMSByFormulaB());
        rmsChartController2.renderAmplitudeValues(algoResult.getFtAmplitudes());
        rmsChartController2.setK(constK);

        smoothChartController.setSignalSupplier(
            algoResult::getIV_NoisySignal
        );
        smoothChartController.setSliSignalSupplier(
            algoResult::getIV_SlidingWindowSmoothedSignal
        );
        smoothChartController.setSliAmplitudeSpectrumSupplier(
            algoResult::getIV_SlidingWindowSmoothedSignalAmplitudeSpectrum
        );
        smoothChartController.setSliPhaseSpectrumSupplier(
            algoResult::getIV_SlidingWindowSmoothedSignalPhaseSpectrum
        );
        smoothChartController.setMdnSignalSupplier(
            algoResult::getIV_MedianSmoothedSignal
        );
        smoothChartController.setMdnAmplitudeSpectrumSupplier(
            algoResult::getIV_MedianSmoothedSignalAmplitudeSpectrum
        );
        smoothChartController.setMdnPhaseSpectrumSupplier(
            algoResult::getIV_MedianSmoothedSignalPhaseSpectrum
        );

        smoothChartController.renderAll();
    }

    // -------------------------------------------------------------------- //

    private RMSChartController rmsChartController1 = null;
    private RMSChartController rmsChartController2 = null;

    private SmoothChartController smoothChartController = null;

    private SpectrumController spectrums = null;

    @FXML
    private TabPane guiTabPane;

    @FXML
    private Tab guiRMSTab;

    @FXML
    private Tab guiSpectrumTab;

    @FXML
    private Tab guiSmoothChartTab;

    @FXML
    private VBox guiRMSBox;
}
