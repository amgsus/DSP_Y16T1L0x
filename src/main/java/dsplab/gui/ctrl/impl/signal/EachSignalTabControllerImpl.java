package dsplab.gui.ctrl.impl.signal;

import dsplab.architecture.ctrl.SimpleController;
import dsplab.architecture.ex.ControllerInitException;
import dsplab.common.Resources;
import dsplab.gui.Controllers;
import dsplab.gui.ctrl.EQController;
import dsplab.gui.ctrl.EachSignalTabController;
import dsplab.gui.ctrl.RMSChartController;
import dsplab.gui.ctrl.SignalRestoreController;
import dsplab.gui.ctrl.SmoothChartController;
import dsplab.gui.ctrl.SpectrumController;
import dsplab.gui.util.Hei;
import dsplab.logic.algo.production.AlgorithmResult;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.net.URL;

import static dsplab.common.Const.*;

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

        initTabStyles();

        /* *** */

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

        guiRMSTab.setOnSelectionChanged(event -> {
            Tab tab = Hei.cast(event.getTarget());
            if (rmsRenderPostponed && tab.isSelected()) {
                rmsChartController1.renderAll();
                rmsChartController2.renderAll();
                rmsRenderPostponed = false;
            }
        });

        /* *** */

        signalSpectrumCtrl = Controllers.getFactory()
            .giveMeSomethingLike(SPECTRUMCHARTS);

        guiSpectrumTab.setContent(signalSpectrumCtrl.getFxRoot());
        guiSpectrumTab.setOnSelectionChanged(event -> {
            Tab tab = Hei.cast(event.getTarget());
            if (spectrumRenderPostponed && tab.isSelected()) {
                signalSpectrumCtrl.renderAll();
                spectrumRenderPostponed = false;
            }
        });

        /* *** */

        smoothSignalChartCtrl = Controllers.getFactory()
            .giveMeSomethingLike(SMOOTHCHART);

        guiSmoothChartTab.setContent(smoothSignalChartCtrl.getFxRoot());
        guiSmoothChartTab.setOnSelectionChanged(event -> {
            Tab tab = Hei.cast(event.getTarget());
            if (smoothSignalRenderPostponed && tab.isSelected()) {
                smoothSignalChartCtrl.renderAll();
                smoothSignalRenderPostponed = false;
            }
        });

        /* *** */

        restoredSignalChartCtrl = Controllers.getFactory()
            .giveMeSomethingLike(RESTORATION);
        guiSignalRestoringTab.setContent(restoredSignalChartCtrl.getFxRoot());
        guiSignalRestoringTab.setOnSelectionChanged(event -> {
            Tab tab = Hei.cast(event.getTarget());
            if (restoredRenderPostponed && tab.isSelected()) {
                restoredSignalChartCtrl.renderAll();
                restoredRenderPostponed = false;
            }
        });

        /* *** */

        freqFilterCtrl = Controllers.getFactory()
            .giveMeSomethingLike(EQ);
        guiEQTab.setContent(freqFilterCtrl.getFxRoot());
        guiEQTab.setOnSelectionChanged(event -> {
            Tab tab = Hei.cast(event.getTarget());
            if (eqSignalRenderPostponed && tab.isSelected()) {
                freqFilterCtrl.renderAll();
                eqSignalRenderPostponed = false;
            }
        });
    }

    public static EachSignalTabController createInstance()
    {
        return new
            EachSignalTabControllerImpl();
    }

    // -------------------------------------------------------------------- //

    @Override
    public void setRenderingData(AlgorithmResult algoResult)
    {
        if (algoResult == null)
            throw new IllegalArgumentException("null");

        // ----- //

        int constK = algoResult.getSampleCount() / 4; // Variant

        rmsChartController1.setK(constK);
        rmsChartController1.setRMSValues(algoResult::getRMSByFormulaA);
        rmsChartController1.setAmplitudeValues(algoResult::getRMSAmplitudes);

        rmsChartController2.setK(constK);
        rmsChartController2.setRMSValues(algoResult::getRMSByFormulaB);
        rmsChartController2.setAmplitudeValues(algoResult::getRMSAmplitudes);

        rmsRenderPostponed = true;

        // ----- //

        signalSpectrumCtrl.setAmplitudeSpectrumData(
            algoResult::getAmplitudeSpectrum
        );
        signalSpectrumCtrl.setPhaseSpectrumData(
            algoResult::getPhaseSpectrum
        );

        spectrumRenderPostponed = true;

        // ----- //

        smoothSignalChartCtrl.setSignalSupplier(
            algoResult::getNoisySignal
        );
        smoothSignalChartCtrl.setAmplitudeSpectrumSupplier(
            algoResult::getNoisyAmplitudeSpectrum
        );
        smoothSignalChartCtrl.setPhaseSpectrumSupplier(
            algoResult::getNoisyPhaseSpectrum
        );
        smoothSignalChartCtrl.setSliSignalSupplier(
            algoResult::getSlidingWindowSmoothedSignal
        );
        smoothSignalChartCtrl.setSliAmplitudeSpectrumSupplier(
            algoResult::getSlidingWindowSmoothedSignalAmplitudeSpectrum
        );
        smoothSignalChartCtrl.setSliPhaseSpectrumSupplier(
            algoResult::getSlidingWindowSmoothedSignalPhaseSpectrum
        );
        smoothSignalChartCtrl.setMdnSignalSupplier(
            algoResult::getMedianSmoothedSignal
        );
        smoothSignalChartCtrl.setMdnAmplitudeSpectrumSupplier(
            algoResult::getMedianSmoothedSignalAmplitudeSpectrum
        );
        smoothSignalChartCtrl.setMdnPhaseSpectrumSupplier(
            algoResult::getMedianSmoothedSignalPhaseSpectrum
        );
        smoothSignalChartCtrl.setPblSignalSupplier(
            algoResult::getParabolicSmoothedSignal
        );
        smoothSignalChartCtrl.setPblAmplitudeSpectrumSupplier(
            algoResult::getParabolicSmoothedSignalAmplitudeSpectrum
        );
        smoothSignalChartCtrl.setPblPhaseSpectrumSupplier(
            algoResult::getParabolicSmoothedSignalPhaseSpectrum
        );

        smoothSignalRenderPostponed = true;

        // ----- //

        restoredSignalChartCtrl.setSignalSupplier(
            algoResult::getData
        );
        restoredSignalChartCtrl.setRestoredSignalSupplier(
            algoResult::getRestoredSignal
        );
        restoredSignalChartCtrl.setRestoredWithPhaseSignalSupplier(
            algoResult::getRestoredWithPhaseSignal
        );

        restoredRenderPostponed = true;

        // ----- //

        freqFilterCtrl.setSignal(algoResult::getData);
        freqFilterCtrl.setSrcAmplitudeSpectrum(algoResult::getAmplitudeSpectrum);
        freqFilterCtrl.setSrcPhaseSpectrum(algoResult::getPhaseSpectrum);
        freqFilterCtrl.setLPFilteredSignal(algoResult::getLPFSignal);
        freqFilterCtrl.setLPFAmplitudeSpectrum(algoResult::getLPFAmplitudeSpectrum);
        freqFilterCtrl.setLPFPhaseSpectrum(algoResult::getLPFPhaseSpectrum);
        freqFilterCtrl.setHPFilteredSignal(algoResult::getHPFSignal);
        freqFilterCtrl.setHPFAmplitudeSpectrum(algoResult::getHPFAmplitudeSpectrum);
        freqFilterCtrl.setHPFPhaseSpectrum(algoResult::getHPFPhaseSpectrum);
        freqFilterCtrl.setBPFilteredSignal(algoResult::getBPFSignal);
        freqFilterCtrl.setBPFAmplitudeSpectrum(algoResult::getBPFAmplitudeSpectrum);
        freqFilterCtrl.setBPFPhaseSpectrum(algoResult::getBPFPhaseSpectrum);

        eqSignalRenderPostponed = true;
    }

    @Override
    public void renderAll()
    {
        guiSpectrumTab.getOnSelectionChanged().handle(new Event(null,
            guiSpectrumTab, EventType.ROOT));
    }

    // -------------------------------------------------------------------- //

    private boolean rmsRenderPostponed          = false;
    private boolean spectrumRenderPostponed     = false; // Render lock...
    private boolean smoothSignalRenderPostponed = false;
    private boolean restoredRenderPostponed     = false;
    private boolean eqSignalRenderPostponed     = false;

    private RMSChartController rmsChartController1 = null;
    private RMSChartController rmsChartController2 = null;
    private SmoothChartController smoothSignalChartCtrl = null;
    private SpectrumController signalSpectrumCtrl = null;
    private SignalRestoreController restoredSignalChartCtrl = null;
    private EQController freqFilterCtrl = null;

    // -------------------------------------------------------------------- //

    private
    void initTabStyles()
    {
        guiTabPane.getStylesheets().add(
            Resources.TAB_PANE_BRIGHTER_HEADER_STYLESHEET);
        guiTabPane.getStylesheets().add(
            "/css/SignalTab.css"
        );

        guiSpectrumTab.setGraphic(
            new ImageView("/images/Tab/Tab.Spectrum.png")
        );
        guiRMSTab.setGraphic(
            new ImageView("/images/Tab/Tab.RMSe.png")
        );
        guiSmoothChartTab.setGraphic(
            new ImageView("/images/Tab/Tab.Noise.png")
        );
        guiSignalRestoringTab.setGraphic(
            new ImageView("/images/Tab/Tab.Restoring.png")
        );
        guiEQTab.setGraphic(
            new ImageView("/images/Tab/Tab.EQ.png")
        );
    }

    // -------------------------------------------------------------------- //

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

    @FXML
    private Tab guiSignalRestoringTab;

    @FXML
    private Tab guiEQTab;
}
