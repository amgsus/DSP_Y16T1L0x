package dsplab.logic.algo.fa;

import dsplab.logic.algo.AlgorithmThread;
import dsplab.logic.algo.impl.AlgorithmThreadImpl;

public final class AlgorithmThreadFactory
{
    AlgorithmThreadFactory() {}
    static final AlgorithmThreadFactory factory
        = new AlgorithmThreadFactory();

    public static AlgorithmThreadFactory getFactory() { return factory; }

    public AlgorithmThread newThread()
    {
        return AlgorithmThreadImpl.newInstance();
    }
}
