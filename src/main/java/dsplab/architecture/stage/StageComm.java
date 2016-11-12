package dsplab.architecture.stage;

import dsplab.architecture.ctrl.Controller;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

public interface StageComm<CtrlType extends Controller>
{
    Stage getStage();
    Scene getScene();
    CtrlType getController();
    void show();
    void close();
    void initOwner(Window owner);
}
