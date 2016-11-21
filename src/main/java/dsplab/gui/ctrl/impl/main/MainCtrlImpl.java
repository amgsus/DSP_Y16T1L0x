package dsplab.gui.ctrl.impl.main;

import dsplab.architecture.callback.Delegate;
import dsplab.architecture.ctrl.SimpleController;
import dsplab.architecture.ex.ControllerInitException;
import dsplab.architecture.util.MessageBox;
import dsplab.common.Global;
import dsplab.common.Resources;
import dsplab.gui.Controllers;
import dsplab.gui.Stages;
import dsplab.gui.anim.LineChartRenderAnimation;
import dsplab.gui.component.common.OverlayPane;
import dsplab.gui.component.common.WaitIndicatorBox;
import dsplab.gui.ctrl.EachSignalTabController;
import dsplab.gui.ctrl.MainController;
import dsplab.gui.ctrl.MainStatusBarController;
import dsplab.gui.prop.TimelineProperties;
import dsplab.gui.stage.main.SignalListEditorStage;
import dsplab.gui.stage.settings.GeneratorSetupStage;
import dsplab.gui.stage.settings.TimelineSetupStage;
import dsplab.gui.util.Percentage;
import dsplab.io.signal.SignalListReader;
import dsplab.io.signal.SignalListWriter;
import dsplab.io.signal.fa.SignalListIO;
import dsplab.logic.algo.AlgorithmThread;
import dsplab.logic.algo.AlgorithmThreadBuilder;
import dsplab.logic.algo.production.AlgorithmResult;
import dsplab.logic.gen.alg.GenID;
import dsplab.logic.gen.modifier.ValueModifier;
import dsplab.logic.signal.Harmonic;
import dsplab.logic.signal.Signal;
import dsplab.logic.signal.enums.Waveform;
import dsplab.logic.signal.util.SigUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static dsplab.architecture.util.MessageBox.*;
import static dsplab.common.Const.*;
import static dsplab.gui.util.Hei.cast;

public class MainCtrlImpl extends SimpleController implements
    MainController
{
    public MainCtrlImpl()
    {
        try {
            URL u = Resources.MAINCTRL_FXML;
            livenMe(u);
        } catch (Throwable cause) {
            throw new ControllerInitException(CTRL_INIT_FAILED_MSG, cause);
        }

        // Continue initialization //

        this.signalList = FXCollections.observableList(this.signalListBack);

        initComponentsHandlers();
        initBindings();

        initStatusBar();

        this.waitIndicator = new WaitIndicatorBox();

        this.guiBorderPane.getChildren().remove(guiTabPane);
        this.overlayPane = new OverlayPane(guiTabPane, waitIndicator);
        this.guiBorderPane.setCenter(overlayPane);

        guiChart.setCreateSymbols(false);
        guiChart.getStylesheets().add(Resources.CHART_SYMBOL_STYLESHEET);

        guiScaleComboBox.getItems().addAll(
            new Percentage(0.125), new Percentage(0.250),
            new Percentage(0.500), new Percentage(1.000),
            new Percentage(2.000), new Percentage(4.000)
        );
        guiScaleComboBox.getSelectionModel().select(3); // 100%

        initCrossOverChart();
        initGenList();
        initDefaultSignalListItems();
    }

    /**
     * @return The new instance of {@link MainCtrlImpl}.
     */
    public static MainCtrlImpl createInstance() { return new
        MainCtrlImpl(); }

    // -------------------------------------------------------------------- //

    private final TimelineProperties timelineProperties
        = new TimelineProperties();

    // -------------------------------------------------------------------- //

    private final List<Signal> signalListBack = new ArrayList<>();
    private final ObservableList<Signal> signalList;

    // -------------------------------------------------------------------- //

    private volatile AlgorithmThread algoThread = null;
    private final AtomicLong algoStartTime = new AtomicLong(0);
    private List<AlgorithmResult> resultList; // Returned by AlgorithmThread.

    // -------------------------------------------------------------------- //

    @Override
    public
    void loadSignalList(File file)
    {
        statusBarController.setStatusText("Loading from a file...");

        Runnable loadTask = () -> {

            SignalListReader slr = SignalListIO.newFileReader(file);

            try {
                slr.fetchAll(signalListBack);
                Platform.runLater(this::start);

                Platform.runLater(() -> {
                    statusBarController.setStatusText("Successfull load!");
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    getErroBox(e.getCause().getLocalizedMessage(),
                        e.getMessage()).show();
                });
            }
        };

        Global.getContext().getThreadPool().submit(loadTask);
    }

    @Override
    public
    void saveSignalList(File file)
    {
        statusBarController.setStatusText("Saving to a file...");

        Runnable saveTask = () -> {

            SignalListWriter slw = SignalListIO.newFileWriter(file);

            try {
                slw.writeAll(signalList);

                Platform.runLater(() -> {
                    statusBarController.setStatusText("Successfull save!");
                });
            } catch (IOException e) {
                Platform.runLater(() -> {
                    getErroBox(e.getCause().getLocalizedMessage(),
                        e.getMessage()).show();
                });
            }
        };

        Global.getContext().getThreadPool().submit(saveTask);
    }

    // -------------------------------------------------------------------- //

    private final List<Tab> algoResultTabs = new ArrayList<>();

    private
    void removeAllAlgoResultTabs()
    {
        guiTabPane.getTabs().removeAll(algoResultTabs);
        algoResultTabs.clear();
    }

    private
    void createAlgoResultTab(AlgorithmResult algoRes)
    {
        EachSignalTabController ctrl = Controllers.getFactory()
            .giveMeSomethingLike(EACHSIGNALTAB);
        String sigName = algoRes.getSignal().getName();
        ctrl.setRenderingData(algoRes);

        Tab tab = new Tab("Details: " + sigName, ctrl.getFxRoot());
        guiTabPane.getTabs().add(tab);
        algoResultTabs.add(tab);

        tab.setOnSelectionChanged(event -> {
            ctrl.renderAll(); // Kostyl
        });
    }

    private
    void createTabsForEachAlgoResult(List<AlgorithmResult> resultList)
    {
        if (resultList == null)
            throw new IllegalArgumentException("resultList");
        removeAllAlgoResultTabs();
        resultList.forEach(this::createAlgoResultTab);
    }

    // -------------------------------------------------------------------- //

    private
    List<Series<Number, Number>> initChartSeries(List<AlgorithmResult> results)
    {
        guiChart.getData().clear();

        if (results != null) {

            results.forEach(algo -> {
                Series<Number, Number> dataSeries = new Series<>();

                dataSeries.setName(algo.getSignal().getName());
                guiChart.getData().add(dataSeries);
            });

            guiChart.applyCss();

            List<Node> chartNodes = guiChart.getChildrenUnmodifiable();

            Pane pane = (Pane) chartNodes.get(1);
            Group group = (Group) pane.getChildren().get(1);

            Group plotContent = (Group) group.getChildrenUnmodifiable().get
                (group.getChildrenUnmodifiable().size() - 3); // !!!

            if (plotContent != null) {

                List<Node> paths = plotContent.getChildrenUnmodifiable();

                for (int i = 0; i < results.size(); i++) {
                    Path p = (Path) paths.get(i);
                    Color color = results.get(i).getSignal().getBrushColor();
                    p.setStroke(new Color(color.getRed(), color.getGreen(),
                        color.getBlue(), 0.50));
                }
            }
        }

        return guiChart.getData();
    }

    private int totalSamples = 0;
    private int period = 0;

    /**
     * <b>Note:</b> This method returns immediately after the animation
     * has been started. {@code onRenderAnimationDone} is called after the
     * animation has been stopped.
     *
     * @param resultList The list of calculated data.
     */
    private
    void renderSeries(List<AlgorithmResult> resultList)
    {
        if (resultList == null)
            throw new IllegalArgumentException("resultList");

        if (resultList.isEmpty())
            return;

        initChartSeries(resultList);

        LineChartRenderAnimation<Number, Number> animation =
            new LineChartRenderAnimation<>(guiChart);

        animation.setValueYSupplier((seriesIndex, dataIndex) ->
            resultList.get(seriesIndex).getData()[dataIndex]);
        animation.setOnStop(onRenderAnimationDone);

        int sampleCount = resultList.get(0).getSampleCount();

        period = resultList.get(0).getSampleCount();
        totalSamples = sampleCount * resultList.get(0).getPeriodCount();

        guiScaleComboBox.getSelectionModel().select(3); // 100%

        NumberAxis axisX = cast(this.guiChart.getXAxis());
        axisX.setLowerBound(0);
        axisX.setUpperBound(sampleCount);

        updateTickCount();
        updateHScrollProperties();

        animation.setDataCount(totalSamples);
        animation.setOffset(0); // Always
        animation.start();
    }

    private
    void updateTickCount()
    {
        NumberAxis axisX = cast(this.guiChart.getXAxis());

        int w = (int) axisX.getUpperBound() - (int) axisX.getLowerBound();
        int tickUnit = (w != 0 ? /*w /*/ Math.max(2, w / 8) : 4);

        axisX.setTickUnit(tickUnit);
    }

    private int scrollAmount;

    private
    void updateHScrollProperties()
    {
        NumberAxis axisX = cast(this.guiChart.getXAxis());

        int viewportSize = (int) (axisX.getUpperBound() -
            axisX.getLowerBound());

        scrollAmount = (int) axisX.getTickUnit();

        guiChartHScrollBar.setMax((totalSamples - viewportSize) /
            scrollAmount);
        guiChartHScrollBar.setValue((int)
            axisX.getLowerBound() / scrollAmount);
        guiChartHScrollBar.setBlockIncrement((int) axisX.getTickUnit());
    }

    // -------------------------------------------------------------------- //

    private
    void refreshChart(boolean forceMath)
    {
        if (forceMath || this.resultList == null) {
            start();
        } else {
            showWaitOverlay();
            updateWaitOverlay("Refreshing...");
            algoStartTime.set(System.currentTimeMillis());
            renderSeries(this.resultList);
        }
    }

    // -------------------------------------------------------------------- //

    private
    final Delegate onRenderAnimationDone = () -> {

        hideWaitOverlay();

        /* Update all components showing the information, depending on the
        current render state. */

        this.statusBarController.setStatusText(
            String.format("Ready! Done in %.3f seconds.",
                (System.currentTimeMillis() - algoStartTime.get()) / 1000.0)
        );

        this.statusBarController.setRenderedPercentage(this.guiScaleComboBox
            .getValue().getValue());
    };

    /*
     * This delegate is called by the algorithm (on it's thread) before
     * it starts doing its main task.
     */
    private
    final Delegate algoStartDelegate = () -> {

        this.algoStartTime.set(System.currentTimeMillis());

        Platform.runLater(() -> {

            showWaitOverlay();
            updateWaitOverlay("The calculations are in progress.");
        });
    };

    /*
     * This delegate is called by the algorithm (on it's thread) when it
     * successfully done.
     */
    private
    final Delegate algoSuccessDelegate = () -> {

        if (algoThread == null) {
            final String msg = "Algorithm thread is not running";
            throw new IllegalStateException(msg);
        }

        // * Fetch results * //

        resultList = algoThread.getResults();

        if (algoThread.hasFailed()) {

            updateWaitOverlay("Oops! Something has broken...");

            StringWriter sw = new StringWriter();
            algoThread.getException().printStackTrace(new PrintWriter(sw));

            getErroBox(sw.toString(), "Fatal: Algorithm Thread").show();
            onRenderAnimationDone.execute();

        } else {

            if (algoThread.hasFailedTasks()) {

                StringWriter sw = new StringWriter();

                for (AlgorithmResult failed : algoThread.getFailedTasks()) {
                    failed.getException().printStackTrace(new PrintWriter(sw));
                }

                getErroBox(sw.toString(), String.format("%d tasks failed",
                    algoThread.getFailedTasks().size())).show();
            }

            this.statusBarController.setPeriod(algoThread.getSampleCount());
            this.statusBarController.setNumberOfSamples(
                algoThread.getSampleCount() * algoThread.getPeriodCount()
            );

            if (resultList.size() > 0) {

                this.updateWaitOverlay("Starting the renderer...");

                renderSeries(resultList); // Render only successfull tasks

                if (this.extended) {
                    updateWaitOverlay("Creating tabs...");
                    createTabsForEachAlgoResult(resultList);
                }

                this.updateWaitOverlay("Rendering...");

            } else {
                onRenderAnimationDone.execute();
            }
        }

        // * Cleanup: Allow thread to be created again * //

        this.algoThread = null;
    };

    private boolean extended;

    private
    void clearChart()
    {
        guiChart.getData().clear();

        NumberAxis xAxis = cast(guiChart.getXAxis());
        xAxis.setTickUnit(64);
        xAxis.setUpperBound(0);
        xAxis.setUpperBound(1024);
    }

    private static final String MSG_THREAD_INSTANCE =
        "Another task is not finished yet! Please, wait until it done.";
    private static final String MSG_EMPTY_SIGNAL_LIST =
        "The signal list is empty. Do you want to open the editor?";

    private
    void start()
    {
        if (algoThread != null) {
            getWarnBox(MSG_THREAD_INSTANCE).show();
            return;
        }

        removeAllAlgoResultTabs();
        clearChart();
        resultList = null;

        guiOutdatedLabel.setVisible(false);

        if (this.signalList.size() == 0) {
            Alert a = getCnfmBox(MSG_EMPTY_SIGNAL_LIST);
            Optional<ButtonType> clicked = a.showAndWait();

            if (clicked.get() == ButtonType.OK) {
                guiSignalListEditButton.fire();
            }

            return;
        }

        extended = guiExtendedCalculationsMenuItem.isSelected();

        int samples = timelineProperties.getSamplesProperty().get();
        int periods = timelineProperties.getPeriodsProperty().get();

        GenID genAlgo
            = cast(genRadioBtnGroup.getSelectedToggle().getUserData());

        statusBarController.setStatusText("Working...");
        updateWaitOverlay("Preparing...");

        algoThread = AlgorithmThreadBuilder.newInstance()
            .setSignalList(signalList)
            .setOnBeforeStart(algoStartDelegate)
            .setOnDone(algoSuccessDelegate)
            .setSampleCount(samples)
            .setPeriodCount(periods)
            .setGeneratorID(genAlgo)
            .setMathEx(extended)
            .setAmpModifier(amplitudeValueModifier)
            .setPhsModifier(phaseValueModifier)
            .setFrqModifier(frequencyValueModifier)
            .build();

        algoThread.start();
    }

    private
    void updateWaitOverlay(String message)
    {
        if (Platform.isFxApplicationThread()) {
            impl_updateText(message);
        } else {
            Platform.runLater(() -> impl_updateText(message));
        }
    }

    private
    void showWaitOverlay()
    {
        if (Platform.isFxApplicationThread()) {
            impl_updateOverlayVisiblity(true);
        } else {
            Platform.runLater(() -> impl_updateOverlayVisiblity(true));
        }
    }

    private
    void hideWaitOverlay()
    {
        if (Platform.isFxApplicationThread()) {
            impl_updateOverlayVisiblity(false);
        } else {
            Platform.runLater(() -> impl_updateOverlayVisiblity(false));
        }
    }

    private
    void impl_updateText(String text)
    {
        waitIndicator.setText(text);
    }

    private
    void impl_updateOverlayVisiblity(boolean shown)
    {
        overlayPane.getOverlayVisibleProperty().set(shown);
    }

    // -------------------------------------------------------------------- //

    private ValueModifier amplitudeValueModifier = null;
    private ValueModifier phaseValueModifier = null;
    private ValueModifier frequencyValueModifier = null;

    private
    void initGenList()
    {
        genRadioBtnGroup = new ToggleGroup();
        guiGenSelectButton.getItems().clear();

        genRadioBtnGroup.selectedToggleProperty().addListener(o -> {
            Toggle toggle = genRadioBtnGroup.getSelectedToggle();

            if (toggle == null)
                return;

            RadioMenuItem rmi = cast(toggle);
            String btnText = "Gen.: %s";
            guiGenSelectButton.setText(String.format(btnText,
                rmi.getText()));
        });

        for (GenID gid : GenID.values()) {

            RadioMenuItem mi = new RadioMenuItem(gid.toString());
            mi.setUserData(gid);
            mi.setToggleGroup(genRadioBtnGroup);

            guiGenSelectButton.getItems().add(mi);
        }

        RadioMenuItem first = cast(guiGenSelectButton.getItems().get(0));
        first.setSelected(true);
    }

    private
    void showGenSetupDialog()
    {
        Toggle selected = this.genRadioBtnGroup.getSelectedToggle();

        if (selected != null) {

            GenID genID = cast(selected.getUserData());

            switch (genID)
            {
                case WITH_VALUE_MODIFIERS:

                    GeneratorSetupStage s = Stages.getFactory()
                        .giveMeSomethingLike(GENERATORSETUP);

                    boolean okIssued = s.showModal(amplitudeValueModifier,
                        phaseValueModifier, frequencyValueModifier);

                    if (okIssued) {

                        /*
                         * Next methods return 'null' if the appropriate
                         * options are disabled
                         */

                        amplitudeValueModifier =
                            s.getNewAmplitudeModifierInstance();
                        phaseValueModifier = s.getNewPhaseModifierInstance();
                        frequencyValueModifier =
                            s.getNewFrequencyModifierInstance();

                        this.toggleOutdated();
                    }

                    break;

                default:
                    String msg = "No configuration available for '%s'.";
                    MessageBox.getInfoBox(String.format(msg, genID)).show();
            }
        }
    }

    // -------------------------------------------------------------------- //

    private
    void toggleOutdated()
    {
        this.guiOutdatedLabel.setVisible(true);

        final String h = null;
        final String c = "Would u like apply changes and refresh " +
            "the chart?";

        Alert a = new Alert(AlertType.CONFIRMATION);
        a.setHeaderText(h);
        a.setContentText(c);

        Optional<ButtonType> result = a.showAndWait();

        if (result.get() == ButtonType.OK) {
            this.refreshChart(true);
        }
    }

    private static final String MSG_SIGLIST_NOT_EMPTY_APPEND =
        "The signal is NOT empty.\n" +
            "Do you want to clear it before loading the file?";

    private static final String MSG_REPLACE_WITH_DEFAULT =
        "Replace current signal(s) with the default?";

    private
    void initComponentsHandlers()
    {
        /*
         * Recalculates values and renders the graphic.
         */
        guiRefreshAllMenuItem.setOnAction(event -> refreshChart(true));

        /*
         * Shows a Signal List Editor stage.
         */
        guiSignalListEditMenuItem.setOnAction(event -> {

            SignalListEditorStage stage =
                Stages.getFactory().giveMeSomethingLike(SIGNALLISTEDITOR);

            boolean success = stage.show(signalList);

            if (success) {

                List<Signal> modlist = stage.getModifiedList();

                this.signalList.clear();
                SigUtils.cloneSignalList(modlist, this.signalList);

                toggleOutdated();
            }
        });

        guiFileNewMenuItem .setOnAction(event -> {
            removeAllAlgoResultTabs();
            guiChart.getData().clear();
            signalList.clear();
            guiOutdatedLabel.setVisible(false);
            statusBarController.setStatusText("'New File' has been issued.");
        });

        guiFileOpenMenuItem.setOnAction(event -> {
            FileChooser dlg = SignalListIO.newFileChooser();
            dlg.setTitle("Load signals from a file...");

            File selected;

            if ((selected = dlg.showOpenDialog(null)) != null) {

                if (signalList.size() > 0) {

                    Optional<ButtonType> btn = getCnfmBox
                        (MSG_SIGLIST_NOT_EMPTY_APPEND).showAndWait();

                    if (btn.get() == ButtonType.OK) {
                        guiFileNewButton.getOnAction().handle(event);
                    }
                }

                loadSignalList(selected);
            }
        });

        guiFileSaveMenuItem.setOnAction(event -> {
            FileChooser dlg = SignalListIO.newFileChooser();
            dlg.setTitle("Save signals to a file...");

            File selected;

            if ((selected = dlg.showSaveDialog(null)) != null) {
                saveSignalList(selected);
            }
        });

        guiGenSetupButton.setOnAction(event -> showGenSetupDialog());

        guiTimelineSetupMenuItem.setOnAction(e -> {
            TimelineSetupStage setup
                = Stages.getFactory().giveMeSomethingLike(TIMELINESETUP);

            if (setup.showModal(this.timelineProperties)) {
                toggleOutdated();
            }
        });

        NumberAxis axisX = cast(guiChart.getXAxis());

        guiChartHScrollBar.valueProperty().addListener(o -> {
            int offset = (int) guiChartHScrollBar.getValue();
            int viewportSize = (int)(axisX.getUpperBound() -
                axisX.getLowerBound());

            axisX.setLowerBound(offset * scrollAmount);
            axisX.setUpperBound(offset * scrollAmount + viewportSize);
        });

        guiScaleComboBox.valueProperty().addListener(o -> {

            double scale = guiScaleComboBox.getValue().getValue();
            int window = (int) (this.period * scale);
            int prefBound = (int) (axisX.getLowerBound() + window);

            if (window <= this.totalSamples) {
                axisX.setLowerBound(Math.max(Math.min(totalSamples - window,
                    (int) axisX.getLowerBound()), 0));
                axisX.setUpperBound(Math.min(totalSamples, prefBound));
            } else {
                axisX.setUpperBound(prefBound);
                axisX.setLowerBound(0);
            }

            statusBarController.setRenderedPercentage(scale);

            updateTickCount();
            updateHScrollProperties();
        });

        guiExitMenuItem.setOnAction(event -> {
            Platform.exit();
        });

        guiRestoreDefaultMenuItem.setOnAction(event -> {
            if (signalList.size() > 0) {
                Optional<ButtonType> btn = getCnfmBox(MSG_REPLACE_WITH_DEFAULT)
                    .showAndWait();

                if (btn.get() != ButtonType.OK) {
                    event.consume();
                    return;
                }
            }

            signalList.clear();
            initDefaultSignalListItems();
        });
    }

    private
    void initBindings()
    {
        guiSignalListEditButton.onActionProperty().bind(
            guiSignalListEditMenuItem.onActionProperty()
        );

        guiChart.createSymbolsProperty().bindBidirectional(
            ui_PointsVisibleCheck.selectedProperty()
        );

        signalList.addListener((ListChangeListener<Signal>) c -> {
            statusBarController.setNumberOfSignals(signalList.size());
            guiOutdatedLabel.setVisible(true);
        });

        timelineProperties.getSamplesProperty().addListener(o ->
            statusBarController.setNumberOfSamples(timelineProperties
                .getSamplesProperty().get()));

        guiTimelineSetupButton.onActionProperty().bind(
            guiTimelineSetupMenuItem.onActionProperty()
        );

        guiRefreshButton.onActionProperty().bind(
            guiRefreshAllMenuItem.onActionProperty()
        );

        guiFileNewButton.onActionProperty().bind(
            guiFileNewMenuItem.onActionProperty()
        );

        guiFileOpenButton.onActionProperty().bind(
            guiFileOpenMenuItem.onActionProperty()
        );

        guiFileSaveButton.onActionProperty().bind(
            guiFileSaveMenuItem.onActionProperty()
        );
    }

    protected
    void initDefaultSignalListItems()
    {
        Signal sig;

        sig = new Signal("Sine", Color.CORNFLOWERBLUE);
        sig.getHarmonics().add(new Harmonic(5.0, 0, 10.0, Waveform.Sine));

        signalList.add(sig);

        sig = new Signal("Cosine", Color.LIGHTSTEELBLUE);
        sig.getHarmonics().add(new Harmonic(5.0, 0, 10.0, Waveform.Cosine));

        signalList.add(sig);

        sig = new Signal("Polyharmonic", Color.YELLOWGREEN);
        sig.getHarmonics().add(new Harmonic(2.5, 0, 20.0, Waveform.Digital));
        sig.getHarmonics().add(new Harmonic(0.2, 120, 50.0, Waveform.Sine));

        signalList.add(sig);
    }

    // -------------------------------------------------------------------- //

    private
    void initCrossOverChart()
    {
        crosslineHrz.setCursor(Cursor.CROSSHAIR);
        crosslineHrz.setStrokeWidth(0.5);
        crosslineHrz.setStroke(Color.color(0, 0, 1, 0.35));

        crosslineVrt.setCursor(Cursor.CROSSHAIR);
        crosslineVrt.setStrokeWidth(0.5);
        crosslineVrt.setStroke(Color.color(0, 0, 1, 0.35));

        List<Node> chartNodes = guiChart.getChildrenUnmodifiable();

        Pane pane = (Pane) chartNodes.get(1);
        Region region = (Region) pane.getChildren().get(0);
        Group g = cast(pane.getChildren().get(1));

        region.setCursor(Cursor.CROSSHAIR);

        g.getChildren().addAll(crosslineHrz, crosslineVrt);

        final NumberAxis axisX = cast(guiChart.getXAxis());
        final NumberAxis axisY = cast(guiChart.getYAxis());

        region.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> {
            crosslineHrz.setVisible(true);
            crosslineVrt.setVisible(true);
        });

        region.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
            double x = event.getX();
            double y = event.getY();

            Point2D mouseInRegion = region.localToParent(x, y);

            crosslineVrt.setStartX(mouseInRegion.getX());
            crosslineVrt.setEndX(mouseInRegion.getX());

            crosslineHrz.setStartY(mouseInRegion.getY());
            crosslineHrz.setEndY(mouseInRegion.getY());

            Number xn = axisY.getValueForDisplay(event.getY());
            Number n = axisX.getValueForDisplay(event.getX());

            final String chartFormat = "X:%.1f  Y:%.1f  n=%.3f  x(n)=%.3f";

            guiChart.setTitle(String.format(chartFormat, x, y,
                n.doubleValue(), xn.doubleValue()));

            event.consume();
        });

        pane.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
            crosslineHrz.setVisible(false);
            crosslineVrt.setVisible(false);
        });

        region.widthProperty().addListener(observable -> {
            Bounds plotBounds = region.getBoundsInParent();

            crosslineHrz.setStartX(plotBounds.getMinX());
            crosslineHrz.setEndX(plotBounds.getMaxX());
        });

        region.heightProperty().addListener(observable -> {
            Bounds plotBounds = region.getBoundsInParent();

            crosslineVrt.setStartY(plotBounds.getMinY());
            crosslineVrt.setEndY(plotBounds.getMaxY());
        });
    }

    private
    void initStatusBar()
    {
        this.statusBarController = Controllers.getFactory()
            .giveMeSomethingLike(MAINSTATUSBAR);
        Parent statusBarNode = statusBarController.getFxRoot();
        guiStatusBarBox.getChildren().add(statusBarNode);
        HBox.setHgrow(statusBarNode, Priority.ALWAYS);

        statusBarController.setNumberOfSamples(NOT_AVAILABLE_VAL);
        statusBarController.setRenderedPercentage(NOT_AVAILABLE_VAL);
        statusBarController.setStatusText("Ready");
    }

    // -------------------------------------------------------------------- //

    private final Line crosslineHrz = new Line();
    private final Line crosslineVrt = new Line();

    private WaitIndicatorBox waitIndicator;
    private OverlayPane overlayPane;
    private MainStatusBarController statusBarController;

    @FXML
    private BorderPane guiBorderPane;

    @FXML
    private LineChart<Number, Number> guiChart;

    @FXML
    private MenuButton guiGenSelectButton;

    @FXML
    private ToggleGroup genRadioBtnGroup;

    @FXML
    private Button guiSignalListEditButton;

    @FXML
    private Button guiRefreshButton;

    @FXML
    private MenuItem guiSignalListEditMenuItem;

    @FXML
    private Button guiFileNewButton;

    @FXML
    private Button guiFileOpenButton;

    @FXML
    private Button guiFileSaveButton;

    @FXML
    private TabPane guiTabPane;

    @FXML
    private Tab ui_SignalMapTab;

    @FXML
    private CheckBox ui_PointsVisibleCheck;

    @FXML
    private HBox guiStatusBarBox;

    @FXML
    private ComboBox<Percentage> guiScaleComboBox;

    @FXML
    private Label guiOutdatedLabel;

    @FXML
    private Button guiGenSetupButton;

    @FXML
    private MenuItem guiTimelineSetupMenuItem;

    @FXML
    private Menu guiGenListMenuItem;

    @FXML
    private ScrollBar guiChartHScrollBar;

    @FXML
    private Button guiTimelineSetupButton;

    @FXML
    private CheckMenuItem guiExtendedCalculationsMenuItem;

    @FXML
    private MenuItem guiRefreshAllMenuItem;

    @FXML
    private MenuItem guiFileNewMenuItem;

    @FXML
    private MenuItem guiRestoreDefaultMenuItem;

    @FXML
    private MenuItem guiFileOpenMenuItem;

    @FXML
    private Menu guiTemplatesMenu;

    @FXML
    private MenuItem guiFileSaveMenuItem;

    @FXML
    private MenuItem guiExitMenuItem;
}
