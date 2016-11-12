package dsplab.gui.stage.settings.impl;

import dsplab.architecture.ex.StageInitException;
import dsplab.architecture.stage.StageCommImpl;
import dsplab.gui.Controllers;
import dsplab.gui.prop.TimelineProperties;
import dsplab.gui.ctrl.TimelineSetupController;
import dsplab.gui.stage.settings.TimelineSetupStage;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import static dsplab.common.Const.TIMELINESETUP;

public class TimelineSetupStageImpl extends
    StageCommImpl<TimelineSetupController> implements TimelineSetupStage
{
    public TimelineSetupStageImpl()
    {
        try {
            TimelineSetupController ctrl =
                Controllers.getFactory().giveMeSomethingLike(TIMELINESETUP);
            init(ctrl);
            initModality(Modality.APPLICATION_MODAL);
            setTitle("Timeline Setup");
            initStyle(StageStyle.DECORATED);
            setResizable(false);
        } catch (Exception cause) {
            throw new StageInitException(ERR_INIT_FAILED, cause);
        }

        getController().setOnOK(() -> {
            okClosed = true;
            close();
        });

        getController().setOnCancel(this::close);
    }

    public static TimelineSetupStageImpl createInstance() { return new
        TimelineSetupStageImpl(); }

    // -------------------------------------------------------------------- //

    protected
    boolean okClosed = false;

    // -------------------------------------------------------------------- //

    @Override
    public boolean showModal(TimelineProperties properties)
    {
        if (properties == null)
            throw new IllegalArgumentException("Properties not specified");

        this.okClosed = false;
        this.getController().setEditableProps(properties);
        showAndWait();
        return okClosed;
    }
}
