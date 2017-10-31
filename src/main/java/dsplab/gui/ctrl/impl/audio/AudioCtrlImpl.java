package dsplab.gui.ctrl.impl.audio;

import dsplab.architecture.ctrl.SimpleController;
import dsplab.architecture.ex.ControllerInitException;
import dsplab.gui.ctrl.AudioController;

import java.net.URL;

public class AudioCtrlImpl extends SimpleController implements
    AudioController
{
    public AudioCtrlImpl()
    {
        try {
            URL u = null;
            livenMe(u);
        } catch (Exception cause) {
            throw new ControllerInitException(CTRL_INIT_FAILED_MSG, cause);
        }

        // ...
    }

    public static AudioController newInstance() { return new AudioCtrlImpl(); }

    // -------------------------------------------------------------------- //

    // ...

    // -------------------------------------------------------------------- //

    // ...
}
