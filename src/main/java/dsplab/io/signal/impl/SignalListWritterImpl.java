package dsplab.io.signal.impl;

import dsplab.io.signal.SignalListWritter;
import dsplab.logic.signal.Signal;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SignalListWritterImpl implements SignalListWritter
{
    SignalListWritterImpl()
    {
    }

    public static SignalListWritter newInstance() { return new
        SignalListWritterImpl(); }

    // -------------------------------------------------------------------- //

    @Override
    public void writeAll(List<Signal> signalList) throws IOException
    {

    }

    @Override
    public void setFile(File file)
    {

    }

    @Override
    public File getFile()
    {
        return null;
    }
}
