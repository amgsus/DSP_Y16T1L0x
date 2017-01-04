package dsplab.gui.ctrl.impl.signal;

import dsplab.architecture.ctrl.SimpleController;
import dsplab.architecture.ex.ControllerInitException;
import dsplab.common.Global;
import dsplab.common.Resources;
import dsplab.gui.component.common.OverlayPane;
import dsplab.gui.ctrl.EQController;
import dsplab.gui.util.Percentage;
import dsplab.gui.util.SpectrumRender;
import dsplab.logic.ft.FourierTransform;
import dsplab.logic.ft.alg.FFTImpl;
import dsplab.logic.ft.fa.FourierTransformFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;

import static dsplab.gui.util.Hei.cast;
import static dsplab.gui.util.SpectrumRender.SPECTRUM_RENDER_SIZE;

public class EQCtrlImpl extends SimpleController implements EQController
{
    public EQCtrlImpl()
    {
        try {
            URL u = Resources.EQ_CONTROLLER_FXML;
            livenMe(u);
        } catch (Exception cause) {
            throw new ControllerInitException(CTRL_INIT_FAILED_MSG, cause);
        }

        initChartEventListeners();
        initRadioBtnEventListeners();
        initComponentsHandlers();

        /*
         * Add OverlayPane for the signal chart.
         */

        List<Node> children = guiChartStackPane.getChildren();

        children.remove(guiChartAnchorPane);
        children.remove(guiChartWaitBox);

        chartOverlay = new OverlayPane(guiChartAnchorPane, guiChartWaitBox);

        children.add(chartOverlay);

        chartOverlay.getOverlayVisibleProperty().set(false);

        /*
         * Add OverlayPane for each the amplitude and the
         * spectrum charts.
         */

        children = guiAmplitudeSpectrumStackPane.getChildren();

        children.remove(guiAmplitudeSpectrumChart);
        children.remove(guiAmplitudeSpectrumWaitBox);

        ampSpectrumOverlayPane = new OverlayPane(guiAmplitudeSpectrumChart,
            guiAmplitudeSpectrumWaitBox);

        children.add(ampSpectrumOverlayPane);

        ampSpectrumOverlayPane.getOverlayVisibleProperty().set(false);

        children = guiPhaseSpectrumStackPane.getChildren();

        children.remove(guiPhaseSpectrumChart);
        children.remove(guiPhaseSpectrumWaitBox);

        phsSpectrumOverlayPane = new OverlayPane(guiPhaseSpectrumChart,
            guiPhaseSpectrumWaitBox);

        children.add(phsSpectrumOverlayPane);

        phsSpectrumOverlayPane.getOverlayVisibleProperty().set(false);

        /*
         * Create series for each chart.
         */

        signalSeries = new Series<>();
        signalSeries.setName("Source");

        eqSeries = new Series<>();
        eqSeries.setName("Filtered");

        ampSpectrumSeries = new Series<>();
        phsSpectrumSeries = new Series<>();

        /*
         * Do some stuff.
         */

        guiScaleComboBox.getItems().addAll(
            new Percentage(0.125), new Percentage(0.250),
            new Percentage(0.500), new Percentage(1.000),
            new Percentage(2.000), new Percentage(4.000)
        );
        guiScaleComboBox.getSelectionModel().select(3); // 100%

        guiAmplitudeSpectrumChart.widthProperty().addListener(o -> {
            //impl_renderAmplitudeSpectrum(); // Kostyl
        });

        guiPhaseSpectrumChart.widthProperty().addListener(o -> {
            //impl_renderPhaseSpectrum(); // Kostyl
        });
    }

    public static EQCtrlImpl newInstance() { return new EQCtrlImpl(); }

    // -------------------------------------------------------------------- //

    private Series<Integer, Double> signalSeries;
    private Series<Integer, Double> eqSeries;
    private Series<Integer, Double> ampSpectrumSeries;
    private Series<Integer, Double> phsSpectrumSeries;

    // -------------------------------------------------------------------- //

    private int totalSamples = 0;
    private int period = 0;

    private
    void updateTickCount()
    {
        NumberAxis axisX = cast(guiSignalChart.getXAxis());

        int w = (int) axisX.getUpperBound() - (int) axisX.getLowerBound();
        int tickUnit = (w != 0 ? /*w /*/ Math.max(2, w / 8) : 4);

        axisX.setTickUnit(tickUnit);
    }

    private int scrollAmount;

    private
    void updateHScrollProperties()
    {
        NumberAxis axisX = cast(guiSignalChart.getXAxis());

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

    @Override
    public
    void renderAll()
    {
        this.renderSourceSignal();
        this.renderEQSignal();
        this.renderAmplitudeSpectrum();
        this.renderPhaseSpectrum();
    }

    @Override
    public
    void renderSourceSignal()
    {
        Supplier<double[]> supplier = signalSupplier;

        if (supplier == null)
            throw new IllegalStateException("supplier:null");

        double[] data = supplier.get();

        NumberAxis axisX = cast(guiSignalChart.getXAxis());
        axisX.setLowerBound(0);
        axisX.setUpperBound(Math.min(data.length, 512));

        period = (int) axisX.getUpperBound();
        totalSamples = data.length;

        updateTickCount();
        updateHScrollProperties();

        guiSignalChart.getData().remove(signalSeries);
        signalSeries.getData().clear();

        for (int i = 0; i < data.length; i += 2) {
            signalSeries.getData().add(new Data<>(i, data[i]));
        }

        guiSignalChart.getData().add(signalSeries);
    }

    @Override
    public
    void renderEQSignal()
    {
        Supplier<double[]> supplier = getActiveEQSignalSupplier();

        if (supplier == null)
            throw new IllegalStateException("supplier:null");

        double[] data = supplier.get();

        guiSignalChart.getData().remove(eqSeries);
        eqSeries.getData().clear();

        for (int i = 0; i < data.length; i++) {
            eqSeries.getData().add(new Data<>(i * 2, data[i]));
        }

        guiSignalChart.getData().add(eqSeries);
    }

    @Override
    public
    void renderAmplitudeSpectrum()
    {
        guiAmplitudeSpectrumChart.getData().remove(ampSpectrumSeries);

        if (guiWholeSpectrumRadioBtn.isSelected()) {

            Supplier<double[]> supplier =
                getActiveAmplitudeSpectrumSupplier();

            if (supplier == null)
                return;

            ampSpectrumData.set(supplier.get());
            impl_renderAmplitudeSpectrum();

        } else {
            scheduleSpectrumsUpdate();
        }
    }

    @Override
    public
    void renderPhaseSpectrum()
    {
        guiPhaseSpectrumChart.getData().remove(phsSpectrumSeries);

        if (guiWholeSpectrumRadioBtn.isSelected()) {

            Supplier<double[]> supplier = getActivePhaseSpectrumSupplier();

            if (supplier == null)
                return;

            phsSpectrumData.set(supplier.get());
            impl_renderPhaseSpectrum();

        } else {
            scheduleSpectrumsUpdate();
        }
    }

    // -------------------------------------------------------------------- //

    private final Timer rangedUpdateTimer = new Timer(true);
    private volatile long lastUpdateTick = 0;
    private volatile boolean rangeUpdated = false;

    private
    void scheduleRangeUpdate()
    {
        rangedUpdateTimer.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                if (rangeUpdated) {
                    if (System.currentTimeMillis() - lastUpdateTick >= 1000) {
                        rangeUpdated = false;
                        scheduleSpectrumsUpdate();
                    }
                }
            }
        }, 0, 500);
    }

    private final AtomicReference<double[]> ampSpectrumData =
        new AtomicReference<>();
    private final AtomicReference<double[]> phsSpectrumData =
        new AtomicReference<>();

    private volatile Future bgTaskFuture;

    private final Runnable fourierTransformBackgroundTask = () -> {

        if (Thread.interrupted())
            return;

        double[] data = this.signalRangeSupplier.get();

        if (Thread.interrupted() || data == null)
            return;

        FourierTransform ft = FourierTransformFactory.getFactory()
            .newFFTImplementation(FFTImpl.DISCRETE);

        ft.setSpectrum(data);
        ft.setRange(data.length);

        double[] ampSpectrum = ft.calculateAmplitudeSpectrum();
        double[] phsSpectrum = ft.calculatePhaseSpectrum();

        if (Thread.interrupted())
            return;

        ampSpectrumData.set(ampSpectrum);
        Platform.runLater(this::impl_renderAmplitudeSpectrum);

        if (Thread.interrupted())
            return;

        phsSpectrumData.set(phsSpectrum);
        Platform.runLater(this::impl_renderPhaseSpectrum);
    };

    private
    void scheduleSpectrumsUpdate()
    {
        ampSpectrumOverlayPane.showOverlay();
        phsSpectrumOverlayPane.showOverlay();

        if (bgTaskFuture != null) {
            bgTaskFuture.cancel(true);
        }

        bgTaskFuture = Global.getContext().getThreadPool()
            .submit(fourierTransformBackgroundTask);
    }

    private
    Supplier<double[]> getActiveEQSignalSupplier()
    {
        Toggle toggle = guiFreqFilterGroup.getSelectedToggle();

        if (toggle == guiLFRadioBtn) {
            return this.lpFilteredSignalSupplier;
        } else if (toggle == guiHFRadioBtn) {
            return this.hpFilteredSignalSupplier;
        } else if (toggle == guiBandFilterRadioBtn) {
            return this.bpFilteredSignalSupplier;
        }

        System.err.println("getActiveEQSignalSupplier(): Oops!");
        return null;
    }

    /**
     * <b>Note:</b> Can be invoked from non-FX thread.
     */
    private
    Supplier<double[]> getActiveSignalSupplier()
    {
        Supplier<double[]> supplier;

        Callable<Supplier<double[]>> callable = () -> {
            if (this.guiSourceSpectrumRadioBtn.isSelected()) {
                return this.signalSupplier;
            } else {
                return this.getActiveEQSignalSupplier();
            }
        };

        try {

            if (Platform.isFxApplicationThread()) {
                supplier = callable.call();
            } else {
                FutureTask<Supplier<double[]>> future =
                    new FutureTask<>(callable);
                Platform.runLater(future);
                supplier = future.get(5000, TimeUnit.MILLISECONDS);
            }

        } catch (Exception cause) {
            cause.printStackTrace();
            return null;
        }

        return supplier;
    }

    /**
     * <b>Note:</b> Can be invoked from non-FX thread.
     */
    private
    Supplier<double[]> getActiveAmplitudeSpectrumSupplier()
    {
        Supplier<double[]> supplier;

        Callable<Supplier<double[]>> callable = () -> {
            Toggle srcToggle = this.guiSpectrumSourceGroup.getSelectedToggle();

            if (srcToggle == this.guiSourceSpectrumRadioBtn) {
                return this.srcAmplitudeSpectrumSupplier;
            } else {
                Toggle frqToggle = this.guiFreqFilterGroup.getSelectedToggle();

                if (frqToggle == this.guiLFRadioBtn) {
                    return this.lpfAmplitudeSpectrumSupplier;
                } else if (frqToggle == this.guiHFRadioBtn) {
                    return this.hpfAmplitudeSpectrumSupplier;
                } else if (frqToggle == this.guiBandFilterRadioBtn) {
                    return this.bpfAmplitudeSpectrumSupplier;
                }
            }

            System.err.println("getActiveAmplitudeSpectrumSupplier(): Oops!");
            return null;
        };

        try {

            if (Platform.isFxApplicationThread()) {
                supplier = callable.call();
            } else {
                FutureTask<Supplier<double[]>> future =
                    new FutureTask<>(callable);
                Platform.runLater(future);
                supplier = future.get(5000, TimeUnit.MILLISECONDS);
            }

        } catch (Exception cause) {
            cause.printStackTrace();
            return null;
        }

        return supplier;
    }

    /**
     * <b>Note:</b> Can be invoked from non-FX thread.
     */
    private
    Supplier<double[]> getActivePhaseSpectrumSupplier()
    {
        Supplier<double[]> supplier;

        Callable<Supplier<double[]>> callable = () -> {
            Toggle srcToggle = this.guiSpectrumSourceGroup.getSelectedToggle();

            if (srcToggle == this.guiSourceSpectrumRadioBtn) {
                return this.srcPhaseSpectrumSupplier;
            } else {
                Toggle frqToggle = this.guiFreqFilterGroup.getSelectedToggle();

                if (frqToggle == this.guiLFRadioBtn) {
                    return this.lpfPhaseSpectrumSupplier;
                } else if (frqToggle == this.guiHFRadioBtn) {
                    return this.hpfPhaseSpectrumSupplier;
                } else if (frqToggle == this.guiBandFilterRadioBtn) {
                    return this.bpfPhaseSpectrumSupplier;
                }
            }

            System.err.println("getActivePhaseSpectrumSupplier(): Oops!");
            return null;
        };

        try {

            if (Platform.isFxApplicationThread()) {
                supplier = callable.call();
            } else {
                FutureTask<Supplier<double[]>> future =
                    new FutureTask<>(callable);
                Platform.runLater(future);
                supplier = future.get(5000, TimeUnit.MILLISECONDS);
            }

        } catch (Exception cause) {
            cause.printStackTrace();
            return null;
        }

        return supplier;
    }

    /**
     * <b>Note:</b> Can be invoked from non-FX thread.
     */
    private final Supplier<double[]> signalRangeSupplier = () -> {

        double[] data;

        Callable<int[]> callable = () -> {
            NumberAxis axisX = cast(this.guiSignalChart.getXAxis());

            int l = (int)(axisX.getLowerBound());
            int r = (int)(axisX.getUpperBound());

            return new int[] { l, r };
        };

        try {

            int[] bounds;

            if (Platform.isFxApplicationThread()) {
                bounds = callable.call();
            } else {
                FutureTask<int[]> future = new FutureTask<>(callable);
                Platform.runLater(future);
                bounds = future.get(5000, TimeUnit.MILLISECONDS);
            }

            Supplier<double[]> supplier = this.getActiveSignalSupplier();

            if (supplier == null)
                return null;

            double[] srcData = supplier.get();
            data = Arrays.copyOfRange(srcData, bounds[0], bounds[1]);

        } catch (Exception cause) {
            return null;
        }

        return data;
    };

    private
    void impl_renderAmplitudeSpectrum()
    {
        guiAmplitudeSpectrumChart.getData().remove(ampSpectrumSeries);
        ampSpectrumSeries.getData().clear();

        double[] data = ampSpectrumData.get();

        if (data == null)
            return;

        // ...

        Supplier<double[]> sup_1 = getActiveSignalSupplier();
        Supplier<double[]> sup_2 = getActiveEQSignalSupplier();

        double maxAmplitude;

        if (sup_1 != sup_2) {
            OptionalDouble maxAmplitude_1 = DoubleStream.of(sup_1.get()).max();
            OptionalDouble maxAmplitude_2 = DoubleStream.of(sup_2.get()).max();
            maxAmplitude = Math.max(maxAmplitude_1.getAsDouble(),
                maxAmplitude_2.getAsDouble());
        } else {
            maxAmplitude = DoubleStream.of(sup_1.get()).max().getAsDouble();
        }

        NumberAxis axisY = cast(guiAmplitudeSpectrumChart.getYAxis());

        double tickUnit = maxAmplitude / axisY.getMinorTickCount();

        axisY.setTickUnit(tickUnit);
        axisY.setUpperBound(Math.max(1.0, maxAmplitude + tickUnit));

        SpectrumRender.render(data, SPECTRUM_RENDER_SIZE, obj ->
            ampSpectrumSeries.getData().add(cast(obj)));

        guiAmplitudeSpectrumChart.getData().add(ampSpectrumSeries);
        ampSpectrumOverlayPane.hideOverlay();
    }

    private
    void impl_renderPhaseSpectrum()
    {
        guiPhaseSpectrumChart.getData().remove(phsSpectrumSeries);
        phsSpectrumSeries.getData().clear();

        double[] data = phsSpectrumData.get();

        if (data == null) {
            System.out.println("EQ: data is null. Rendering is skipped.");
            return;
        }

        SpectrumRender.render(data, SPECTRUM_RENDER_SIZE, obj ->
            phsSpectrumSeries.getData().add(cast(obj)));

        guiPhaseSpectrumChart.getData().add(phsSpectrumSeries);
        phsSpectrumOverlayPane.hideOverlay();
        guiPhaseSpectrumChart.requestLayout();
    }

    // -------------------------------------------------------------------- //

    private
    void initRadioBtnEventListeners()
    {
        guiRangeGroup.selectedToggleProperty().addListener(o -> {
            boolean wholeRange = guiWholeSpectrumRadioBtn.isSelected();
            renderAmplitudeSpectrum(); // This will also update PS if range...
            if (wholeRange) {
                renderPhaseSpectrum();
            }
        });

        guiFreqFilterGroup.selectedToggleProperty().addListener(o -> {
            renderAll();
        });

        guiSpectrumSourceGroup.selectedToggleProperty().addListener(o -> {
            // Copy-paste ^^
            boolean wholeRange = guiWholeSpectrumRadioBtn.isSelected();
            renderAmplitudeSpectrum(); // This will also update PS if range...
            if (wholeRange) {
                renderPhaseSpectrum();
            }
        });
    }

    private
    void initChartEventListeners()
    {
        guiScaleComboBox.valueProperty().addListener(o -> {
            // ...
        });

        guiChartHScrollBar.valueProperty().addListener(o -> {
            // ...
        });
    }

    // -------------------------------------------------------------------- //

    private Supplier<double[]> signalSupplier;
    private Supplier<double[]> lpFilteredSignalSupplier;
    private Supplier<double[]> hpFilteredSignalSupplier;
    private Supplier<double[]> bpFilteredSignalSupplier;

    private Supplier<double[]> srcAmplitudeSpectrumSupplier;
    private Supplier<double[]> srcPhaseSpectrumSupplier;
    private Supplier<double[]> lpfAmplitudeSpectrumSupplier;
    private Supplier<double[]> lpfPhaseSpectrumSupplier;
    private Supplier<double[]> hpfAmplitudeSpectrumSupplier;
    private Supplier<double[]> hpfPhaseSpectrumSupplier;
    private Supplier<double[]> bpfAmplitudeSpectrumSupplier;
    private Supplier<double[]> bpfPhaseSpectrumSupplier;

    @Override
    public
    void setSignal(Supplier<double[]> supplier)
    {
        signalSupplier = supplier;
    }

    @Override
    public
    void setLPFilteredSignal(Supplier<double[]> supplier)
    {
        lpFilteredSignalSupplier = supplier;
    }

    @Override
    public
    void setHPFilteredSignal(Supplier<double[]> supplier)
    {
        hpFilteredSignalSupplier = supplier;
    }

    @Override
    public
    void setBPFilteredSignal(Supplier<double[]> supplier)
    {
        bpFilteredSignalSupplier = supplier;
    }

    @Override
    public
    void setSrcAmplitudeSpectrum(Supplier<double[]> supplier)
    {
        srcAmplitudeSpectrumSupplier = supplier;
    }

    @Override
    public
    void setSrcPhaseSpectrum(Supplier<double[]> supplier)
    {
        srcPhaseSpectrumSupplier = supplier;
    }

    @Override
    public
    void setLPFAmplitudeSpectrum(Supplier<double[]> supplier)
    {
        lpfAmplitudeSpectrumSupplier = supplier;
    }

    @Override
    public
    void setLPFPhaseSpectrum(Supplier<double[]> supplier)
    {
        lpfPhaseSpectrumSupplier = supplier;
    }

    @Override
    public
    void setHPFAmplitudeSpectrum(Supplier<double[]> supplier)
    {
        hpfAmplitudeSpectrumSupplier = supplier;
    }

    @Override
    public
    void setHPFPhaseSpectrum(Supplier<double[]> supplier)
    {
        hpfPhaseSpectrumSupplier = supplier;
    }

    @Override
    public
    void setBPFAmplitudeSpectrum(Supplier<double[]> supplier)
    {
        bpfAmplitudeSpectrumSupplier = supplier;
    }

    @Override
    public
    void setBPFPhaseSpectrum(Supplier<double[]> supplier)
    {
        bpfPhaseSpectrumSupplier = supplier;
    }

    // -------------------------------------------------------------------- //

    protected
    void initComponentsHandlers()
    {
        NumberAxis axisX = cast(guiSignalChart.getXAxis());

        guiChartHScrollBar.valueProperty().addListener(l -> {
            int offset = (int) guiChartHScrollBar.getValue();
            int viewportSize = (int)(axisX.getUpperBound() -
                axisX.getLowerBound());

            axisX.setLowerBound(offset * scrollAmount);
            axisX.setUpperBound(offset * scrollAmount + viewportSize);

            if (guiRangedSpectrumRadioBtn.isSelected()) {
                lastUpdateTick = System.currentTimeMillis();
                rangeUpdated = true;
                scheduleRangeUpdate();
            }
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

            updateTickCount();
            updateHScrollProperties();

            if (guiRangedSpectrumRadioBtn.isSelected()) {
                lastUpdateTick = System.currentTimeMillis();
                rangeUpdated = true;
                scheduleRangeUpdate();
            }
        });
    }

    private OverlayPane chartOverlay;
    private OverlayPane ampSpectrumOverlayPane;
    private OverlayPane phsSpectrumOverlayPane;

    @FXML
    private RadioButton guiWholeSpectrumRadioBtn;

    @FXML
    private ToggleGroup guiRangeGroup;

    @FXML
    private RadioButton guiRangedSpectrumRadioBtn;

    @FXML
    private Label guiRangedSpectrumLabel;

    @FXML
    private RadioButton guiLFRadioBtn;

    @FXML
    private ToggleGroup guiFreqFilterGroup;

    @FXML
    private RadioButton guiHFRadioBtn;

    @FXML
    private RadioButton guiBandFilterRadioBtn;

    @FXML
    private RadioButton guiCustomFilterRadioBtn;

    @FXML
    private StackPane guiChartStackPane;

    @FXML
    private AnchorPane guiChartAnchorPane;

    @FXML
    private LineChart<Integer, Double> guiSignalChart;

    @FXML
    private ScrollBar guiChartHScrollBar;

    @FXML
    private ComboBox<Percentage> guiScaleComboBox;

    @FXML
    private VBox guiChartWaitBox;

    @FXML
    private StackPane guiAmplitudeSpectrumStackPane;

    @FXML
    private BarChart<Integer, Double> guiAmplitudeSpectrumChart;

    @FXML
    private VBox guiAmplitudeSpectrumWaitBox;

    @FXML
    private StackPane guiPhaseSpectrumStackPane;

    @FXML
    private BarChart<Integer, Double> guiPhaseSpectrumChart;

    @FXML
    private VBox guiPhaseSpectrumWaitBox;

    @FXML
    private RadioButton guiSourceSpectrumRadioBtn;

    @FXML
    private ToggleGroup guiSpectrumSourceGroup;

    @FXML
    private RadioButton guiEQSpectrumRadioBtn;

}
