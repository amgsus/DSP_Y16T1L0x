package dsplab.logic.algo.fa;

import dsplab.architecture.Factory;
import dsplab.logic.algo.AlgorithmThread;
import dsplab.logic.algo.impl.AlgorithmThreadImpl;

public final class AlgorithmThreadFactory implements Factory<AlgorithmThread>
{
    AlgorithmThreadFactory() {}
    static final AlgorithmThreadFactory factory
        = new AlgorithmThreadFactory();

    public static AlgorithmThreadFactory getFactory() { return factory; }

    @Override
    public AlgorithmThread giveMeDefault()
    {
        return AlgorithmThreadImpl.newInstance();
    }
}
