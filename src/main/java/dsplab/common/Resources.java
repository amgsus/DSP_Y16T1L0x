package dsplab.common;

import java.net.URL;

public final class Resources
{
    Resources() {}

    /* Window Scene Controllers */

    public static final String MAINCTRL_FXML_PATH
        = "/fxml/window/main/DSP.Main.fxml";
    public static final String SIGNALEDITOR_FXML_PATH
        = "/fxml/window/editor/DSP.SignalListEditor.fxml";

    public static final URL MAINCTRL_FXML
        = Resources.class.getResource(MAINCTRL_FXML_PATH);
    public static final URL SIGNALEDITOR_FXML
        = Resources.class.getResource(SIGNALEDITOR_FXML_PATH);

    /* Main Components */

    public static final String OVERLAYPANE_FXML_PATH
        = "/fxml/components/DSP.OverlayPane.fxml";

    public static final URL OVERLAYPANE_FXML
        = Resources.class.getResource(OVERLAYPANE_FXML_PATH);

    public static final String WAITINDICATOR_FXML_PATH
        = "/fxml/components/DSP.WaitIndicator.fxml";

    public static final URL WAITINDICATOR_FXML
        = Resources.class.getResource(WAITINDICATOR_FXML_PATH);

    /* Signal Editor Components */

    public static final String HARMONICEDITOR_FIELDS_FXML_PATH
        = "/fxml/window/editor/HarmonicEditorFields.fxml";

    public static final URL HARMONICEDITOR_FIELDS_FXML
        = Resources.class.getResource(HARMONICEDITOR_FIELDS_FXML_PATH);

    public static final String SIGNALEDITOR_FIELDS_FXML_PATH
        = "/fxml/window/editor/SignalEditorFields.fxml";

    public static final URL SIGNALEDITOR_FIELDS_FXML
        = Resources.class.getResource(SIGNALEDITOR_FIELDS_FXML_PATH);

    public static final String STCV_FXML_PATH
        = "/fxml/window/editor/li/DSP.SignalList.Item.fxml";
    public static final String HTCV_FXML_PATH
        = "/fxml/window/editor/li/DSP.SignalList.SubItem.fxml";

    public static final URL STCV_FXML
        = Resources.class.getResource(STCV_FXML_PATH);
    public static final URL HTCV_FXML
        = Resources.class.getResource(HTCV_FXML_PATH);

    public static final String SINE_WAVEFORM_ICON_PATH
        = "/images/SignalList.SubItem.Sine.16px.png";
    public static final String COSINE_WAVEFORM_ICON_PATH
        = "/images/SignalList.SubItem.Cosine.16px.png";

    /*
    public static final URL SINE_WAVEFORM_ICON
        = Resources.class.getResource(SINE_WAVEFORM_ICON_PATH);
    public static final URL COSINE_WAVEFORM_ICON
        = Resources.class.getResource(COSINE_WAVEFORM_ICON_PATH);
    */

    /* Tabs (Main Window) */

    public static final String EACH_SIGNAL_TAB_FXML_PATH
        = "/fxml/window/main/DSP.EachSignalTab.fxml";
    public static final URL EACH_SIGNAL_TAB_FXML
        = Resources.class.getResource(EACH_SIGNAL_TAB_FXML_PATH);

    public static final String TIMELINE_SETUP_SCENE_FXML_PATH
        = "/fxml/window/DSP.TimelineSetup.fxml";
    public static final URL TIMELINE_SETUP_SCENE_FXML
        = Resources.class.getResource(TIMELINE_SETUP_SCENE_FXML_PATH);

    public static final String MAIN_STATUS_BAR_FXML_PATH
        = "/fxml/window/main/DSP.Main.StatusBar.fxml";
    public static final URL MAIN_STATUS_BAR_FXML
        = Resources.class.getResource(MAIN_STATUS_BAR_FXML_PATH);

    public static final String CHART_SYMBOL_STYLESHEET
        = "/css/ChartSymbol.css";

    public static final String RMS_CHART_FXML_PATH
        = "/fxml/signal/DSP.RMSChart.fxml";
    public static final URL RMS_CHART_FXML
        = Resources.class.getResource(RMS_CHART_FXML_PATH);

    public static final String TAB_PANE_BRIGHTER_HEADER_STYLESHEET
        = "/css/TabPaneBrighterHeader.css";

    public static final String GENERATOR_SETUP_FXML_PATH
        = "/fxml/window/gen/DSP.GenSetup.fxml";
    public static final URL GENERATOR_SETUP_FXML
        = Resources.class.getResource(GENERATOR_SETUP_FXML_PATH);

    public static final String GENERATOR_VALUE_SETUP_FXML_PATH
        = "/fxml/window/gen/DSP.GenValueSetup.fxml";
    public static final URL GENERATOR_VALUE_SETUP_FXML
        = Resources.class.getResource(GENERATOR_VALUE_SETUP_FXML_PATH);

    public static final String SMOOTH_CHART_FXML_PATH
        = "/fxml/signal/DSP.SignalSmoothing.fxml";
    public static final URL SMOOTH_CHART_FXML
        = Resources.class.getResource(SMOOTH_CHART_FXML_PATH);

    public static final String SPECTRUM_CHARTS_FXML_PATH
        = "/fxml/signal/DSP.Spectrum.fxml";
    public static final URL SPECTRUM_CHARTS_FXML
        = Resources.class.getResource(SPECTRUM_CHARTS_FXML_PATH);

    public static final String SIGNAL_RESTORE_FXML_PATH
        = "/fxml/signal/DSP.Restoration.fxml";
    public static final URL SIGNAL_RESTORE_FXML
        = Resources.class.getResource(SIGNAL_RESTORE_FXML_PATH);
}
