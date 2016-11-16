package dsplab.gui;

import dsplab.architecture.stage.StageComm;
import dsplab.gui.stage.main.impl.MainStageImpl;
import dsplab.gui.stage.main.impl.SignalListEditorStageImpl;
import dsplab.gui.stage.settings.impl.GeneratorSetupStageImpl;
import dsplab.gui.stage.settings.impl.TimelineSetupStageImpl;

import static dsplab.common.Const.*;

/**
 * Stages.
 */
public final class Stages
{
    Stages() {}
    static final Stages factory = new Stages();

    public static Stages getFactory() { return factory; }

    @SuppressWarnings("unchecked")
    public <T extends StageComm> T giveMeSomethingLike(String stageID)
    {
        if (stageID == null || stageID.isEmpty()) {
            final String msg = "'%s' not a valid <stageID> value";
            throw new IllegalArgumentException(String.format(msg, stageID));
        }

        switch (stageID.toUpperCase()) {

            case MAIN:
                return (T) new MainStageImpl();
            case SIGNALLISTEDITOR:
                return (T) new SignalListEditorStageImpl();
            case TIMELINESETUP:
                return (T) TimelineSetupStageImpl.createInstance();
            case GENERATORSETUP:
                return (T) GeneratorSetupStageImpl.createInstance();

            default:
                final String s = "'%s' is not supported by the factory";
                throw new IllegalArgumentException(String.format(s, stageID));

        }
    }
}
