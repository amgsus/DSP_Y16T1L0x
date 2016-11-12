package dsplab.io.signal;

import dsplab.io.GetSetFileName;
import dsplab.logic.signal.Signal;

import java.io.IOException;
import java.util.List;

public interface SignalListReader extends GetSetFileName
{
    List<Signal> fetchAll() throws IOException;
    int fetchAll(List<Signal> existingList) throws IOException;
}
