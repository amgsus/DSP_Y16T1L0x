package dsplab.logic.ft;

public class SinTable
{
    public SinTable()
    {
        // ...
    }

    public SinTable(int size)
    {
        this(size, 1.0);
    }

    public SinTable(int size, double amplitude)
    {
        init(size, amplitude);
    }

    double[] sinTable;

    public void init(int size, double amplitude)
    {
        double sinTable[] = new double[size];

        for (int i = 0; i < sinTable.length; i++) {
            sinTable[i] = amplitude * Math.sin(2 * Math.PI * i / size);
        }

        this.sinTable = sinTable;
    }

    public double[] getTable()
    {
        return sinTable;
    }

    public double getSinFromTable(int nR)
    {
        /*
        if (sinTable == null)
            throw new IllegalStateException("SinTable not initialized");

        if (nR > sinTable.length)
            throw new IllegalArgumentException("Index out of bounds: " + nR);

        return sinTable[nR];
        */

        return Math.sin(2 * Math.PI * nR / sinTable.length);
    }

    public double getCosFromTable(int nR)
    {
        /*
        if (sinTable == null)
            throw new IllegalStateException("SinTable not initialized");

        int nBolshoe = sinTable.length;
        int i = (nR + nBolshoe / 4) % nBolshoe;

        if (i > sinTable.length)
            throw new IllegalArgumentException("Index out of bounds: " + i);

        return sinTable[i];
        */

        return Math.cos(2 * Math.PI * nR / sinTable.length);
    }
}
