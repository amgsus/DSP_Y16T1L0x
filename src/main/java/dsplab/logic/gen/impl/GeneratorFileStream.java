package dsplab.logic.gen.impl;

import dsplab.io.signal.TembrFileReader;
import dsplab.io.signal.fa.SignalListIO;
import dsplab.logic.signal.Signal;

import java.io.File;

public class GeneratorFileStream extends GeneratorImpl
{
    protected String fileName;

    @Override
    public double[] run() throws Exception
    {
        Signal s = this.signal;

        if (!(s.isSourcingFromFile()))
            return new double[0];

        File file = new File(s.getDataSourceFileName());
        TembrFileReader reader = SignalListIO.newTembrFileReader(file);

        return reader.readChannel(s.getDataSourceChannel(), 0, 8192);
    }
}
