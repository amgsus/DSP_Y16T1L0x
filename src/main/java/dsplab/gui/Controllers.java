package dsplab.gui;

import dsplab.architecture.ctrl.Controller;
import dsplab.gui.ctrl.impl.TimelineSetupControllerImpl;
import dsplab.gui.ctrl.impl.edit.SignalListEditorControllerImpl;
import dsplab.gui.ctrl.impl.edit.field.HarmonicEditorControllerImpl;
import dsplab.gui.ctrl.impl.edit.field.SignalEditorControllerImpl;
import dsplab.gui.ctrl.impl.gen.GenSetupCtrlImpl;
import dsplab.gui.ctrl.impl.gen.GeneratorValueSetupControllerImpl;
import dsplab.gui.ctrl.impl.main.MainCtrlImpl;
import dsplab.gui.ctrl.impl.main.MainStatusBarCtrlImpl;
import dsplab.gui.ctrl.impl.signal.EQCtrlImpl;
import dsplab.gui.ctrl.impl.signal.EachSignalTabControllerImpl;
import dsplab.gui.ctrl.impl.signal.RMSChartCtrlImpl;
import dsplab.gui.ctrl.impl.signal.SignalRestoreCtrlImpl;
import dsplab.gui.ctrl.impl.signal.SmoothChartCtrlImpl;
import dsplab.gui.ctrl.impl.signal.SpectrumCtrlImpl;

import static dsplab.common.Const.*;

/**
 * Controllers.
 */
public final class Controllers
{
    Controllers() {}
    static Controllers factory = new Controllers();

    public static Controllers getFactory() { return factory; }

    /**
     * @param ctrlID The ID of a controller to build. Case-<b>insensitive</b>.
     */
    @SuppressWarnings("unchecked")
    public <T extends Controller> T giveMeSomethingLike(String ctrlID)
    {
        if (ctrlID == null || ctrlID.isEmpty()) {
            final String msg = "'%s' not a valid <ctrlID> value";
            throw new IllegalArgumentException(String.format(msg, ctrlID));
        }

        switch (ctrlID.toUpperCase()) {

            case MAIN:
                return (T) MainCtrlImpl.createInstance();
            case SIGNALLISTEDITOR:
                return (T) new SignalListEditorControllerImpl();
            case HARMONICEDITOR:
                return (T) new HarmonicEditorControllerImpl();
            case SIGNALEDITOR:
                return (T) new SignalEditorControllerImpl();
            case EACHSIGNALTAB:
                return (T) EachSignalTabControllerImpl.createInstance();
            case TIMELINESETUP:
                return (T) TimelineSetupControllerImpl.createInstance();
            case MAINSTATUSBAR:
                return (T) MainStatusBarCtrlImpl.createInstance();
            case RMSCHART:
                return (T) RMSChartCtrlImpl.createInstance();
            case GENERATORSETUP:
                return (T) GenSetupCtrlImpl.createInstance();
            case GENERATORVALUESETUP:
                return (T) GeneratorValueSetupControllerImpl.createInstance();
            case SMOOTHCHART:
                return (T) SmoothChartCtrlImpl.createInstance();
            case SPECTRUMCHARTS:
                return (T) SpectrumCtrlImpl.createInstance();
            case RESTORATION:
                return (T) SignalRestoreCtrlImpl.createInstance();
            case EQ:
                return (T) EQCtrlImpl.newInstance();

            default:
                final String s = "'%s' is not supported by the factory";
                throw new IllegalArgumentException(String.format(s, ctrlID));

        }
    }
}
