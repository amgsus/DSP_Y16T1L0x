package dsplab.gui.ctrl.impl.gen;

import dsplab.architecture.callback.Delegate;
import dsplab.architecture.callback.DelegateWrapper;
import dsplab.architecture.ctrl.SimpleController;
import dsplab.architecture.ex.ControllerInitException;
import dsplab.architecture.util.MessageBox;
import dsplab.common.Resources;
import dsplab.gui.Controllers;
import dsplab.gui.ctrl.GeneratorSetupController;
import dsplab.gui.ctrl.GeneratorValueSetupController;
import dsplab.logic.gen.modifier.ValueModifier;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.net.URL;

import static dsplab.architecture.util.MessageBox.getErroBox;
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

        this.ampModifierCtrl = Controllers.getFactory()
            .giveMeSomethingLike(GENERATORVALUESETUP);
        this.phsModifierCtrl = Controllers.getFactory()
            .giveMeSomethingLike(GENERATORVALUESETUP);
        this.frqModifierCtrl = Controllers.getFactory()
            .giveMeSomethingLike(GENERATORVALUESETUP);

        this.ampModifierCtrl.setCaption("Amplitude");
        this.phsModifierCtrl.setCaption("Phase");
        this.frqModifierCtrl.setCaption("Frequency");

        HBox.setHgrow(this.ampModifierCtrl.getFxRoot(), Priority.ALWAYS);
        HBox.setHgrow(this.phsModifierCtrl.getFxRoot(), Priority.ALWAYS);
        HBox.setHgrow(this.frqModifierCtrl.getFxRoot(), Priority.ALWAYS);

        guiCenterBox.getChildren().addAll(
            this.ampModifierCtrl.getFxRoot(),
            this.phsModifierCtrl.getFxRoot(),
            this.frqModifierCtrl.getFxRoot()
        );

        // ...
    }

    public static GeneratorSetupController createInstance() { return new
        GenSetupCtrlImpl(); }

    // -------------------------------------------------------------------- //

    private ValueModifier ampValueModifier = null;
    private ValueModifier phsValueModifier = null;
    private ValueModifier frqValueModifier = null;

    @Override
    public
    ValueModifier getNewAmplitudeModifierInstance()
    {
        return ampValueModifier;
    }

    @Override
    public
    ValueModifier getNewPhaseModifierInstance()
    {
        return phsValueModifier;
    }

    @Override
    public
    ValueModifier getNewFrequencyModifierInstance()
    {
        return frqValueModifier;
    }

    @Override
    public
    void setAmplitudeModifierInstance(ValueModifier modifier)
    {
        ampModifierCtrl.peekValuesFrom(modifier);
    }

    @Override
    public
    void setPhaseModifierInstance(ValueModifier modifier)
    {
        phsModifierCtrl.peekValuesFrom(modifier);
    }

    @Override
    public
    void setFrequencyModifierInstance(ValueModifier modifier)
    {
        frqModifierCtrl.peekValuesFrom(modifier);
    }

    // -------------------------------------------------------------------- //

    protected
    void initComponentHandlers()
    {
        guiOKButton.setOnAction(event -> {
            try {
                ampValueModifier = ampModifierCtrl.buildInstance();
                phsValueModifier = phsModifierCtrl.buildInstance();
                frqValueModifier = frqModifierCtrl.buildInstance();
            } catch (IllegalArgumentException cause) {
                getErroBox(cause.getCause().getLocalizedMessage(),
                    cause.getMessage()).show();
                return;
            }

            okDelegate.execute();
        });

        guiCancelButton.setOnAction(event -> {
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

    private GeneratorValueSetupController ampModifierCtrl;
    private GeneratorValueSetupController phsModifierCtrl;
    private GeneratorValueSetupController frqModifierCtrl;

    private final DelegateWrapper okDelegate = new DelegateWrapper();
    private final DelegateWrapper cancelDelegate = new DelegateWrapper();

    @FXML
    private Button guiOKButton;

    @FXML
    private Button guiCancelButton;

    @FXML
    private HBox guiCenterBox;
}
