package dsplab.gui.stage.settings.impl;

import dsplab.architecture.ex.StageInitException;
import dsplab.architecture.stage.StageCommImpl;
import dsplab.gui.Controllers;
import dsplab.gui.ctrl.GeneratorSetupController;
import dsplab.gui.prop.GeneratorWithValueModifiersProperties;
import dsplab.gui.stage.settings.GeneratorSetupStage;
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

        // ...
    }

    public static GeneratorSetupStage createInstance() { return new
        GeneratorSetupStageImpl(); }

    // -------------------------------------------------------------------- //

    protected boolean okClosed = false;

    // -------------------------------------------------------------------- //

    @Override
    public boolean showModal(GeneratorWithValueModifiersProperties properties)
    {
        this.okClosed = false;
        showAndWait();
        return okClosed;
    }

    // ...
}
