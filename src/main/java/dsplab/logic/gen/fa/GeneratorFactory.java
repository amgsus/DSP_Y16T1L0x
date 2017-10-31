package dsplab.logic.gen.fa;

import dsplab.logic.gen.Generator;
import dsplab.logic.gen.alg.GenID;
import dsplab.logic.gen.impl.GeneratorFileStream;
import dsplab.logic.gen.impl.GeneratorImpl;
import dsplab.logic.gen.impl.GeneratorWithModifiersImpl;

public final class GeneratorFactory
{
    private GeneratorFactory() {}

    private static GeneratorFactory factory = new GeneratorFactory();
    public  static GeneratorFactory getFactory() { return factory; }

    // -------------------------------------------------------------------- //

    @SuppressWarnings("unchecked")
    public <T extends Generator> T newGenerator(GenID genID)
    {
        switch (genID)
        {
            case DEFAULT:
                return (T) new GeneratorImpl();
            case WITH_VALUE_MODIFIERS:
                return (T) new GeneratorWithModifiersImpl();
            case FILE_STREAM:
                return (T) new GeneratorFileStream();

            default:
                final String s = "'%s' is not supported by the factory";
                throw new IllegalArgumentException(String.format(s, genID));
        }
    }
}
