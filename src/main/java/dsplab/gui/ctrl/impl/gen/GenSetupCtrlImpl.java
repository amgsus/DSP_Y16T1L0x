package dsplab.gui.ctrl.impl.gen;

import dsplab.architecture.callback.Delegate;
import dsplab.architecture.callback.DelegateWrapper;
import dsplab.architecture.ctrl.SimpleController;
import dsplab.architecture.ex.ControllerInitException;
import dsplab.common.Resources;
import dsplab.gui.Controllers;
import dsplab.gui.ctrl.GeneratorSetupController;
import dsplab.gui.ctrl.GeneratorValueSetupController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.net.URL;

import static dsplab.common.Const.GENERATORVALUESETUP;

public class GenSetupCtrlImpl extends SimpleController implements
    GeneratorSetupController
{
    public GenSetupCtrlImpl()
    {
        try {
            URL u = Resources.GENERATOR_SETUP_FXML;
            livenMe(u);
        } catch (Exception cause) {
            throw new ControllerInitException(CTRL_INIT_FAILED_MSG, cause);
        }

        initComponentHandlers();

        /* Values Modifier Controllers */

        this.amplitudeModifierCtrl = Controllers.getFactory()
            .giveMeSomethingLike(GENERATORVALUESETUP);
        this.phaseModifierCtrl = Controllers.getFactory()
            .giveMeSomethingLike(GENERATORVALUESETUP);
        this.frequencyModifierCtrl = Controllers.getFactory()
            .giveMeSomethingLike(GENERATORVALUESETUP);

        this.amplitudeModifierCtrl.setCaption("Amplitude");
        this.phaseModifierCtrl.setCaption("Phase");
        this.frequencyModifierCtrl.setCaption("Frequency");

        HBox.setHgrow(this.amplitudeModifierCtrl.getFxRoot(), Priority.ALWAYS);
        HBox.setHgrow(this.phaseModifierCtrl.getFxRoot(), Priority.ALWAYS);
        HBox.setHgrow(this.frequencyModifierCtrl.getFxRoot(), Priority.ALWAYS);

        guiCenterBox.getChildren().addAll(
            this.amplitudeModifierCtrl.getFxRoot(),
            this.phaseModifierCtrl.getFxRoot(),
            this.frequencyModifierCtrl.getFxRoot()
        );

        // ...
    }

    public static GeneratorSetupController createInstance() { return new
        GenSetupCtrlImpl(); }

    // -------------------------------------------------------------------- //

    // ...

    protected
    void initComponentHandlers()
    {
        guiOKButton.setOnAction(event -> {
            // ...
            okDelegate.execute();
        });

        guiCancelButton.setOnAction(event -> {
            // ...
            cancelDelegate.execute();
        });
    }

    // -------------------------------------------------------------------- //

    @Override
    public void setOnOK(Delegate delegate)
    {
        if (delegate != null)
            this.okDelegate.wrapDelegate(delegate);
        else
            this.okDelegate.removeDelegate();
    }

    @Override
    public void setOnCancel(Delegate delegate)
    {
        if (delegate != null)
            this.cancelDelegate.wrapDelegate(delegate);
        else
            this.cancelDelegate.removeDelegate();
    }

    // -------------------------------------------------------------------- //

    private GeneratorValueSetupController amplitudeModifierCtrl;
    private GeneratorValueSetupController phaseModifierCtrl;
    private GeneratorValueSetupController frequencyModifierCtrl;

    private final DelegateWrapper okDelegate = new DelegateWrapper();
    private final DelegateWrapper cancelDelegate = new DelegateWrapper();

    @FXML
    private Button guiOKButton;

    @FXML
    private Button guiCancelButton;

    @FXML
    private HBox guiCenterBox;
}
