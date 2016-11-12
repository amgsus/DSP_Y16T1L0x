package dsplab.architecture.stage;

import dsplab.architecture.ctrl.Controller;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StageCommImpl<CtrlType extends Controller> extends
    Stage implements StageComm<CtrlType>
{
    private CtrlType sceneController;
    private Scene scene;

    @Override
    public Stage getStage()
    {
        return this;
    }

    @Override
    public CtrlType getController()
    {
        return sceneController;
    }

    protected void init(CtrlType ctrl)
    {
        if (ctrl == null)
            throw new IllegalArgumentException("<ctrl> could not be null");
        if (ctrl.getFxRoot() == null)
            throw new IllegalStateException("<ctrl> is not initialized");

        sceneController = ctrl;
        scene = new Scene(sceneController.getFxRoot());
        this.setScene(scene);
    }

    public static final String ERR_INIT_FAILED
        = "Failed to initialize a stage";
}
