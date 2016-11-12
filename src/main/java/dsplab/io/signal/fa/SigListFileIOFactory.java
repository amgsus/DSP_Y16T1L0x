package dsplab.io.signal.fa;

import dsplab.io.signal.SignalListReader;
import dsplab.io.signal.SignalListWritter;
import dsplab.io.signal.impl.SignalListReaderImpl;
import dsplab.io.signal.impl.SignalListWritterImpl;

public final class SigListFileIOFactory
{
    SigListFileIOFactory() {}
    static SigListFileIOFactory factory = new SigListFileIOFactory();

    public static SigListFileIOFactory getFactory() { return factory; }

    public static SignalListReader newReader() { return
        SignalListReaderImpl.newInstance(); }

    public static SignalListWritter newWritter() { return
        SignalListWritterImpl.newInstance(); }
}
