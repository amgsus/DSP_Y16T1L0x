package dsplab.io.signal.impl.tmb;

import dsplab.io.signal.TembrFileReader;
import dsplab.io.util.AbstractGetSetFileName;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class TembrFileReaderImpl extends AbstractGetSetFileName implements TembrFileReader
{
    public TembrFileReaderImpl()
    {
    }

    public TembrFileReaderImpl(File file)
    {
        this();
        this.setFile(file);
    }

    @Override
    public TembrInfoHeader readInfo()
        throws IOException
    {
        try (FileInputStream s = new FileInputStream(getFile()))
        {
            return readInfo(s);
        }
    }

    protected static TembrInfoHeader readInfo(InputStream stream)
        throws IOException
    {
        TembrInfoHeader info = new TembrInfoHeader();

        DataInputStream s = new DataInputStream(stream);
        byte[] signature = new byte[4];

        if (s.read(signature) < 4)
            throw new IOException("Signature read failed");

        if (!(signature[0] == 'T' && signature[1] == 'M'
            && signature[2] == 'B' && signature[3] == '1'))
            throw new IOException("Invalid signature");

        info.channels = Integer.reverseBytes(s.readInt());
        info.samplingRate = Integer.reverseBytes(s.readInt());

        s.readInt(); // spectreLinesCount
        s.readInt(); // cutoffFrequencies
        s.readInt(); // frequencyResolution
        s.readInt(); // dataBlockRecieveTime
        s.readInt(); // collectionTime
        s.readInt(); // countCollectedBlocks

        info.dataSize = Integer.reverseBytes(s.readInt());

        s.readInt(); // totalBlocksRecieved
        s.readInt(); // upperDataLimit
        s.readInt(); // lowerDataLimit

        return info;
    }

    @Override
    public double[] readChannel(int channel, int offset, int count)
        throws IOException
    {
        double data[];

        try (FileInputStream s = new FileInputStream(getFile()))
        {
            TembrInfoHeader info = readInfo(s);
//            float channelData[]  = new float[info.channels];
            data = new double[info.dataSize];

            DataInputStream ds = new DataInputStream(s);

            for ( int i = 0; i < Math.min(info.dataSize, count); i++ )
            {
//                for ( int j = 0; j < info.channels; j++ )
//                    channelData[j] =
//                        Float.intBitsToFloat(Integer.reverseBytes(ds.readInt()));
//
//                data[i] = channelData[channel];

                data[i] =
                    Float.intBitsToFloat(Integer.reverseBytes(ds.readInt()));
            }
        }

        return data;
    }
}
