package dsplab.io.signal;

import dsplab.io.util.GetSetFileName;
import dsplab.logic.signal.Signal;

import java.io.IOException;
import java.util.List;

public interface SignalListReader extends GetSetFileName
{
    List<Signal> fetchAll() throws IOException;
    void fetchAll(List<Signal> signalList) throws IOException;
}
