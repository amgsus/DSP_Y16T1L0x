package dsplab.gui.stage.settings.impl;

import dsplab.architecture.ex.StageInitException;
import dsplab.architecture.stage.StageCommImpl;
import dsplab.gui.Controllers;
import dsplab.gui.ctrl.GeneratorSetupController;
import dsplab.gui.stage.settings.GeneratorSetupStage;
import dsplab.logic.gen.modifier.ValueModifier;
import javafx.stage.Modality;

import static dsplab.common.Const.GENERATORSETUP;

public class GeneratorSetupStageImpl extends
    StageCommImpl<GeneratorSetupController> implements
    GeneratorSetupStage
{
    public GeneratorSetupStageImpl()
    {
        try {
            GeneratorSetupController ctrl = Controllers.getFactory()
                .giveMeSomethingLike(GENERATORSETUP);
            init(ctrl);
        } catch (Exception cause) {
            throw new StageInitException(ERR_INIT_FAILED, cause);
        }

        this.initModality(Modality.APPLICATION_MODAL);
        this.setTitle("Generator Setup");
        this.setResizable(false);

        getController().setOnOK(() -> {
            // ...
            okClosed = true;
            this.close();
        });

        getController().setOnCancel(this::close);
    }

    public static GeneratorSetupStage createInstance() { return new
        GeneratorSetupStageImpl(); }

    // -------------------------------------------------------------------- //

    protected boolean okClosed = false;

    // -------------------------------------------------------------------- //

    @Override
    public
    boolean showModal(ValueModifier ampModifier, ValueModifier phsModifier,
        ValueModifier frqModifier)
    {
        this.okClosed = false;
        getController().setAmplitudeModifierInstance(ampModifier);
        getController().setPhaseModifierInstance(phsModifier);
        getController().setFrequencyModifierInstance(frqModifier);
        showAndWait();
        return okClosed;
    }

    @Override
    public ValueModifier getNewAmplitudeModifierInstance()
    {
        if (okClosed)
            return getController().getNewAmplitudeModifierInstance();
        else
            return null;
    }

    @Override
    public ValueModifier getNewPhaseModifierInstance()
    {
        if (okClosed)
            return getController().getNewPhaseModifierInstance();
        else
            return null;
    }

    @Override
    public ValueModifier getNewFrequencyModifierInstance()
    {
        if (okClosed)
            return getController().getNewFrequencyModifierInstance();
        else
            return null;
    }
}
