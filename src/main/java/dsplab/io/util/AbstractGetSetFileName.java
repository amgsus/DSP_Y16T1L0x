package dsplab.io.util;

import java.io.File;

public class AbstractGetSetFileName implements GetSetFileName
{
    public AbstractGetSetFileName()
    {
        setFile(null);
    }

    public AbstractGetSetFileName(File file)
    {
        setFile(file);
    }

    File file;

    @Override
    public File getFile()
    {
        return file;
    }

    @Override
    public void setFile(File file)
    {
        this.file = file;
    }
}
