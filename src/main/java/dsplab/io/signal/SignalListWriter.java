package dsplab.io.signal;

import dsplab.io.util.GetSetFileName;
import dsplab.logic.signal.Signal;

import java.io.IOException;
import java.util.List;

public interface SignalListWriter extends GetSetFileName
{
    void writeAll(List<Signal> signalList) throws IOException;
}
