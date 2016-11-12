package dsplab.architecture.ctrl;

import javafx.scene.Parent;

public interface Controller
{
    /**
     * @return The node associated with this C-object or {@code null} if not
     * initialized.
     */
    Parent getFxRoot();
}
