package dsplab.gui.stage.main.impl;

import dsplab.architecture.ex.StageInitException;
import dsplab.architecture.stage.StageCommImpl;
import dsplab.gui.Controllers;
import dsplab.gui.ctrl.SignalListEditorController;
import dsplab.gui.stage.main.SignalListEditorStage;
import dsplab.logic.signal.Signal;
import javafx.stage.Modality;

import java.util.List;

import static dsplab.common.Const.SIGNALLISTEDITOR;

public class SignalListEditorStageImpl extends
    StageCommImpl<SignalListEditorController> implements
    SignalListEditorStage
{
    public SignalListEditorStageImpl()
    {
        try {
            SignalListEditorController sigListEditCtrl =
                Controllers.getFactory().giveMeSomethingLike(SIGNALLISTEDITOR);
            init(sigListEditCtrl);
            setTitle("Signal List Editor");
            setResizable(false);
            initModality(Modality.APPLICATION_MODAL);
        } catch (Throwable cause) {
            throw new StageInitException(ERR_INIT_FAILED, cause);
        }

        getController().setOnOK(() -> {
            oKIssued = true;
            this.close();
        });

        getController().setOnCancel(this::close);
    }

    private volatile
    boolean oKIssued;

    @Override
    public
    boolean show(List<Signal> signals)
    {
        this.oKIssued = false;
        getController().setEditableList(signals);
        showAndWait();
        return oKIssued;
    }

    @Override
    public
    List<Signal> getModifiedList()
    {
        if (oKIssued)
            return getController().getModifiedList();
        else
            return null; // :)
    }
}
