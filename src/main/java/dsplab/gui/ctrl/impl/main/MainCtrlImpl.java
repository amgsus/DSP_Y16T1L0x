package dsplab.gui.ctrl.impl.main;

import dsplab.architecture.util.MessageBox;
import dsplab.architecture.callback.Delegate;
import dsplab.architecture.ctrl.SimpleController;
import dsplab.architecture.ex.ControllerInitException;
import dsplab.common.Resources;
import dsplab.gui.Controllers;
import dsplab.gui.Stages;
import dsplab.gui.prop.GeneratorWithValueModifiersProperties;
import dsplab.gui.prop.TimelineProperties;
import dsplab.gui.anim.LineChartRenderAnimation;
import dsplab.gui.component.common.OverlayPane;
import dsplab.gui.component.common.WaitIndicatorBox;
import dsplab.gui.ctrl.EachSignalTabController;
import dsplab.gui.ctrl.MainController;
import dsplab.gui.ctrl.MainStatusBarController;
import dsplab.gui.stage.main.SignalListEditorStage;
import dsplab.gui.stage.settings.GeneratorSetupStage;
import dsplab.gui.stage.settings.TimelineSetupStage;
import dsplab.gui.util.Percentage;
import dsplab.logic.algo.AlgorithmThread;
import dsplab.logic.algo.AlgorithmThreadBuilder;
import dsplab.logic.algo.production.AlgorithmResult;
import dsplab.logic.gen.alg.GenID;
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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

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
            new Percentage(0.125), new Percentage(0.25), new Percentage(0.50),
            new Percentage(0.750), new Percentage(1.00), new Percentage(1.50),
            new Percentage(2.000), new Percentage(2.50), new Percentage(3.00)
        );
        guiScaleComboBox.getSelectionModel().select(4); // 1.0

        initCrossOverChart();
        initGenList();
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
        ctrl.renderAlgoResult(algoRes);

        Tab tab = new Tab("Details: " + sigName, ctrl.getFxRoot());
        guiTabPane.getTabs().add(tab);
        algoResultTabs.add(tab);
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
                    p.setStroke(results.get(i).getSignal().getBrushColor());
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
            resultList.get(seriesIndex).getAmplitudes()[dataIndex]);
        animation.setOnStop(onRenderAnimationDone);

        int sampleCount = resultList.get(0).getSampleCount();

        period = resultList.get(0).getSampleCount();
        totalSamples = sampleCount * resultList.get(0).getPeriodCount();

        int chartSize = (int) (sampleCount *
            this.guiScaleComboBox.getValue().getValue());

        NumberAxis axisX = cast(this.guiChart.getXAxis());
        axisX.setLowerBound(0);
        axisX.setUpperBound(chartSize);

        updateTickCount();
        updateHScrollProperties();

//      animation.setDataCount(Math.min(chartSize, sampleCount * periodCount));
        animation.setDataCount(totalSamples);
        animation.setOffset(0); // Always
        animation.start();
    }

    private
    void updateTickCount()
    {
        NumberAxis axisX = cast(this.guiChart.getXAxis());

        int w = (int) axisX.getUpperBound() - (int) axisX.getLowerBound();
        int tickUnit = w != 0 ? w / Math.min(8, w / 8) : 64;

        axisX.setTickUnit(tickUnit);
    }

    private
    void updateHScrollProperties()
    {
        NumberAxis axisX = cast(this.guiChart.getXAxis());

        int chartSize = (int) axisX.getUpperBound() -
            (int) axisX.getLowerBound();

        guiChartHScrollBar.setMax((totalSamples - chartSize) /
            (int) axisX.getTickUnit());
        guiChartHScrollBar.setBlockIncrement(chartSize /
            (int) axisX.getTickUnit());
        guiChartHScrollBar.setValue((int) axisX.getLowerBound() /
            (int) axisX.getTickUnit());
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

        Platform.runLater(() -> {

            // * Fetch results * //

            List<AlgorithmResult> results = algoThread.getResults();

            this.statusBarController.setPeriod(algoThread.getSampleCount());
            this.statusBarController.setNumberOfSamples(
                algoThread.getSampleCount() * algoThread.getPeriodCount()
            );

            this.updateWaitOverlay("Starting the renderer...");

            renderSeries(results);

            this.updateWaitOverlay("Initializing tabs for each signal...");

            createTabsForEachAlgoResult(results);

            this.updateWaitOverlay("Rendering...");

            // * Cleanup: Allow algorithm thread to be created again * //

            this.resultList = results;
            this.algoThread = null;
        });
    };

    private
    void start()
    {
        if (algoThread != null)
            throw new IllegalStateException("Algorithm is still running");

        removeAllAlgoResultTabs();
        guiChart.getData().clear();
        this.resultList = null;

        final int samples = timelineProperties.getSamplesProperty().get();
        final int periods = timelineProperties.getPeriodsProperty().get();

        final GenID genAlgo = cast(genRadioBtnGroup.getSelectedToggle()
            .getUserData());

        NumberAxis xAxis = cast(guiChart.getXAxis());
        xAxis.setUpperBound(samples);

        guiOutdatedLabel.setVisible(false);

        if (this.signalList.size() == 0) {

            Alert a = new Alert(AlertType.INFORMATION);
            a.setHeaderText(null);
            a.setContentText("Hei! Signal list is empty...");
            a.show();

            return; // Cleaned up. Exit if no signals are specified.
        }

        statusBarController.setStatusText("Working...");
        updateWaitOverlay("Preparing!");

        algoThread = AlgorithmThreadBuilder.newInstance().newObject()
            .setSignalList(signalList)
            .setOnStart(algoStartDelegate)
            .setOnSuccess(algoSuccessDelegate)
            .setSampleCount(samples)
            .setPeriodCount(periods)
            .setGenerator(genAlgo)
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
    void lockControls(boolean lock)
    {
        // ...
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

    private GeneratorWithValueModifiersProperties genWithVMProperties =
        new GeneratorWithValueModifiersProperties();

    private
    void initGenList()
    {
        if (genRadioBtnGroup != null)
            throw new IllegalStateException("Already initialized");

        genRadioBtnGroup = new ToggleGroup();
        genRadioBtnGroup.selectedToggleProperty().addListener(
            (o, oldValue, newValue) -> {

                if (newValue instanceof RadioMenuItem) {

                    RadioMenuItem rmi = cast(newValue);
                    String btnText = "Gen.: %s";
                    this.guiGenSelectButton.setText(
                        String.format(btnText, rmi.getText())
                    );
                }
            }
        );

        for (GenID id : GenID.values()) {

            RadioMenuItem mi = new RadioMenuItem(id.toString());
            mi.setUserData(id);
            mi.setToggleGroup(genRadioBtnGroup);

            guiGenSelectButton.getItems().add(mi);
        }

        // Select the first item in the list...

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

                    boolean okIssued = s.showModal(this.genWithVMProperties);

                    if (okIssued) {
                        this.toggleOutdated();
                    }

                    break;

                default:
                    String msg = "No configuration available for '%s'.";
                    MessageBox.showInformation(String.format(msg, genID));
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

    private
    void initComponentsHandlers()
    {
        /*
         * Recalculates values and renders the graphic.
         */
        guiRefreshButton.setOnAction(event -> refreshChart(true));

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

        guiFileNewButton.setOnAction(event -> {
            // ...
        });

        guiFileOpenButton.setOnAction(event -> {
            // ...
        });

        guiFileSaveButton.setOnAction(event -> {
            // ...
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
            int window = (int) axisX.getUpperBound() -
                (int) axisX.getLowerBound();

            axisX.setLowerBound(offset * (int) axisX.getTickUnit());
            axisX.setUpperBound(offset * (int) axisX.getTickUnit() + window);
        });

        guiScaleComboBox.valueProperty().addListener(o -> {

            double scale = guiScaleComboBox.getValue().getValue();
            int window = (int) (this.period * scale);
            int up = (int) (axisX.getLowerBound() + window);

            axisX.setLowerBound(Math.min(totalSamples - window,
                (int) axisX.getLowerBound()));
            axisX.setUpperBound(Math.min(up, totalSamples));

            updateTickCount();
            updateHScrollProperties();
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

        // ...
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

            Number xn = axisY.getValueForDisplay(y);
            Number n = axisX.getValueForDisplay(x);

            final String chartFormat = "X:%.0f  Y:%.0f  n=%.3f  x(n)=%.3f";

            guiChart.setTitle(String.format(chartFormat, x, y,
                n.doubleValue(), xn.doubleValue()));

            Point2D mouseInRegion = region.localToParent(x, y);

            crosslineHrz.setStartY(mouseInRegion.getY());
            crosslineHrz.setEndY(mouseInRegion.getY());

            crosslineVrt.setStartX(mouseInRegion.getX());
            crosslineVrt.setEndX(mouseInRegion.getX());

            event.consume(); // ???
        });

        region.addEventHandler(MouseEvent.MOUSE_EXITED, event -> {
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
}
