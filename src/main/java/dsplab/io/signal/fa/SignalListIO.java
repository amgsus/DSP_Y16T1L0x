package dsplab.io.signal.fa;

import dsplab.io.signal.SignalListReader;
import dsplab.io.signal.SignalListWriter;
import dsplab.io.signal.impl.SigListXMLReaderImpl;
import dsplab.io.signal.impl.SigListXMLWriterImpl;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public final class SignalListIO
{
    private SignalListIO() {}

    // -------------------------------------------------------------------- //

    /* XML */

    public static final String ROOT_ELEMENT                 = "SignalList";

    public static final String SIGNAL_ELEMENT               = "Signal";
    public static final String SIGNAL_NAME_ATTRIBUTE        = "Name";
    public static final String SIGNAL_COLOR_ATTRIBUTE       = "Color";

    public static final String HARMONIC_ELEMENT             = "Harmonic";
    public static final String HARMONIC_AMPLITUDE_ATTRIBUTE = "Amplitude";
    public static final String HARMONIC_PHASE_ATTRIBUTE     = "Phase";
    public static final String HARMONIC_FREQUENCY_ATTRIBUTE = "Frequency";
    public static final String HARMONIC_WAVEFORM_ATTRIBUTE  = "Waveform";

    // -------------------------------------------------------------------- //

    public static SignalListReader newFileReader(File fileToRead)
    {
        return new SigListXMLReaderImpl(fileToRead);
    }

    public static SignalListWriter newFileWriter(File fileToRewrite)
    {
        return new SigListXMLWriterImpl(fileToRewrite);
    }

    public static FileChooser newFileChooser()
    {
        FileChooser dlg = new FileChooser();
        List<FileChooser.ExtensionFilter> filters = dlg.getExtensionFilters();

        filters.add(new FileChooser.ExtensionFilter(
            "Signals (*.siglist)",
            "*.siglist"
        ));
        filters.add(new FileChooser.ExtensionFilter(
            "XML Documents (*.xml)",
            "*.xml"
        ));
        filters.add(new FileChooser.ExtensionFilter(
            "All files (*.*)",
            "*.*"
        ));

        return dlg;
    }

    public static List<File> getDefaultSignalTemplates() throws IOException
    {
        final String TEMPLATE_PATH = "/templates";

        List<File> fileList = new ArrayList<>();

        try {

            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            InputStream in = cl.getResourceAsStream(TEMPLATE_PATH);

            if (in == null) {
                in = SignalListIO.class.getResourceAsStream(TEMPLATE_PATH);
            }

            try (BufferedReader buff =
                     new BufferedReader(new InputStreamReader(in))) {

                String resourceName;

                while ((resourceName = buff.readLine()) != null) {
                    fileList.add(new File(SignalListIO.class
                        .getResource(TEMPLATE_PATH + "/" + resourceName)
                        .toURI()));
                }

            }

        } catch (Exception cause) {
            throw new IOException(cause);
        }

        return fileList;
    }
}
