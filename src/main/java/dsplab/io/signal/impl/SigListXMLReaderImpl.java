package dsplab.io.signal.impl;

import dsplab.io.signal.SignalListReader;
import dsplab.io.util.AbstractGetSetFileName;
import dsplab.logic.signal.Signal;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SigListXMLReaderImpl extends AbstractGetSetFileName implements
    SignalListReader
{
    public SigListXMLReaderImpl()
    {
    }

    public SigListXMLReaderImpl(File file)
    {
        super(file);
    }

    @Override
    public List<Signal> fetchAll() throws IOException
    {
        return null;
    }

    @Override
    public void fetchAll(List<Signal> signalList) throws IOException
    {

    }
}
