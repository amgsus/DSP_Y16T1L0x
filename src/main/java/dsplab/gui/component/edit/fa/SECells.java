package dsplab.gui.component.edit.fa;

import dsplab.gui.component.edit.com.TreeCellGraphic;
import dsplab.gui.component.edit.impl.custom.Harmonic_TCV;
import dsplab.gui.component.edit.impl.custom.Signal_TCV;

public final class SECells
{
    SECells() {}
    private static SECells factory = new SECells();
    public  static SECells getFactory() { return factory; }

    public TreeCellGraphic make(String cell) throws IllegalArgumentException
    {
        switch (cell.toUpperCase()) {

            case "SIGNAL":
                return new Signal_TCV();
            case "HARMONIC":
                return new Harmonic_TCV();

            default:
                final String s = "'%s' is not valid cell class";
                throw new IllegalArgumentException(String.format(s, cell));

        }
    }
}
