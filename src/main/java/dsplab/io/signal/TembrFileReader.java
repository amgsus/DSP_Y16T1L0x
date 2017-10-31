package dsplab.io.signal;

import dsplab.io.signal.impl.tmb.TembrInfoHeader;

import java.io.IOException;

public interface TembrFileReader
{
    TembrInfoHeader readInfo() throws IOException;
    double[] readChannel(int channel, int offset, int count) throws IOException;
}
