package dsplab.gui.component.edit.com;

import dsplab.architecture.ctrl.Controller;

public interface TreeCellGraphic extends Controller
{
    /**
     * Passing components non-{@code null} value will bind components tree treecell to it,
     * otherwise the tree treecell is unbinded.
     */
    void setData(Object item);
}
