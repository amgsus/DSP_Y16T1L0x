package dsplab.io.signal.impl;

import dsplab.io.signal.SignalListWriter;
import dsplab.io.signal.fa.SignalListIO;
import dsplab.io.util.AbstractGetSetFileName;
import dsplab.io.util.xml.XMLUtils;
import dsplab.logic.signal.Harmonic;
import dsplab.logic.signal.Signal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static dsplab.io.signal.fa.SignalListIO.*;

public class SigListXMLWriterImpl extends AbstractGetSetFileName implements
    SignalListWriter
{
    public SigListXMLWriterImpl()
    {
    }

    public SigListXMLWriterImpl(File file)
    {
        super(file);
    }

    @Override
    public void writeAll(List<Signal> signalList) throws IOException
    {
        if (signalList == null)
            throw new IllegalArgumentException("null");

        try {

            Document doc = XMLUtils.createEmptyDocument();

            Element signalListElement = doc.createElement(ROOT_ELEMENT);
            doc.appendChild(signalListElement);

            for (Signal signal : signalList) {

                Element signalElement = doc.createElement(SIGNAL_ELEMENT);
                signalListElement.appendChild(signalElement);

                signalElement.setAttribute(SIGNAL_NAME_ATTRIBUTE,
                    signal.getName());
                signalElement.setAttribute(SIGNAL_COLOR_ATTRIBUTE,
                    signal.getBrushColor().toString());

                for (Harmonic h : signal.getHarmonics()) {

                    Element harmonicElement =
                        doc.createElement(HARMONIC_ELEMENT);
                    signalElement.appendChild(harmonicElement);

                    harmonicElement.setAttribute(HARMONIC_AMPLITUDE_ATTRIBUTE,
                        Double.toString(h.getAmplitude()));
                    harmonicElement.setAttribute(HARMONIC_PHASE_ATTRIBUTE,
                        Double.toString(h.getPhase()));
                    harmonicElement.setAttribute(HARMONIC_FREQUENCY_ATTRIBUTE,
                        Double.toString(h.getFrequency()));
                    harmonicElement.setAttribute(HARMONIC_WAVEFORM_ATTRIBUTE,
                        h.getWaveform().name()); // Do not use toString()!
                }
            }

            XMLUtils.saveDocumentToFile(doc, getFile());

        } catch (Exception cause) {
            throw new IOException(String.format("Failed to write file: '%s'",
                getFile().getAbsolutePath()), cause);
        }
    }
}
