package dsplab.gui.component.edit.impl;

import dsplab.gui.component.edit.com.TreeCellGraphic;
import dsplab.gui.component.edit.fa.SECells;
import dsplab.gui.util.Hei;
import dsplab.logic.signal.Harmonic;
import dsplab.logic.signal.Signal;
import javafx.scene.control.TreeCell;

public class SETreeViewCell<T> extends TreeCell<T>
{
    /**
     * @param item data or {@code null} if {@code noItem} is true.
     * @param bgItem determines if components treecell is components background.
     */
    @Override
    protected
    void updateItem(T item, boolean bgItem)
    {
        super.updateItem(item, bgItem);

        if (!bgItem) {

            TreeCellGraphic tc = null;

            if (item instanceof Signal) {
                tc = SECells.getFactory().make("Signal");
            } else {
                if (item instanceof Harmonic) {
                    tc = SECells.getFactory().make("Harmonic");
                }
            }

            if (tc != null) {
                tc.setData(item);

                this.setGraphic(tc.getFxRoot());
                this.setText("");

                return;
            }
        }

        /* Unbind if treecell has been cleared? */
        if (this.getGraphic() != null) {
            if (this.getGraphic() instanceof TreeCellGraphic) {

                TreeCellGraphic cell = Hei.cast(this.getGraphic());
                cell.setData(null); // Unbind

            }
        }

        this.setGraphic(null);
        this.setText(null);
    }
}
