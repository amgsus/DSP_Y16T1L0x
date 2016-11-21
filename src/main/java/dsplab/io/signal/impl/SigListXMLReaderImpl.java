package dsplab.io.signal.impl;

import dsplab.gui.util.Hei;
import dsplab.io.signal.SignalListReader;
import dsplab.io.signal.fa.SignalListIO;
import dsplab.io.util.AbstractGetSetFileName;
import dsplab.io.util.xml.XMLUtils;
import dsplab.logic.signal.Harmonic;
import dsplab.logic.signal.Signal;
import dsplab.logic.signal.enums.Waveform;
import javafx.scene.paint.Color;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static dsplab.gui.util.Hei.cast;
import static dsplab.io.signal.fa.SignalListIO.*;

public class SigListXMLReaderImpl extends AbstractGetSetFileName implements
    SignalListReader
{
    public SigListXMLReaderImpl()
    {
    }

    public SigListXMLReaderImpl(File file)
    {
        super(file);
    }

    @Override
    public List<Signal> fetchAll() throws IOException
    {
        List<Signal> list = new ArrayList<>();
        this.fetchAll(list);
        return list;
    }

    @Override
    public void fetchAll(List<Signal> signalList) throws IOException
    {
        if (signalList == null)
            throw new IllegalArgumentException("signalList is not specified");

        try {
            Document doc = XMLUtils.loadDocumentFromFile(getFile());

            List<Signal> list = new LinkedList<>();

            NodeList signalNodes = doc.getElementsByTagName(SIGNAL_ELEMENT);

            for (int i = 0; i < signalNodes.getLength(); i++) {

                Node sigNode = signalNodes.item(i);

                if (sigNode.getNodeType() != Node.ELEMENT_NODE)
                    continue;

                Element sigElement = cast(sigNode);

                Signal signal = new Signal();
                signal.setName(sigElement.getAttribute(SIGNAL_NAME_ATTRIBUTE));
                signal.setBrushColor(Color.valueOf(sigElement.getAttribute(
                    SIGNAL_COLOR_ATTRIBUTE)));

                NodeList harmonicNodes =
                    sigElement.getElementsByTagName(HARMONIC_ELEMENT);

                for (int j = 0; j < harmonicNodes.getLength(); j++) {

                    Node harmNode = harmonicNodes.item(j);

                    if (harmNode.getNodeType() != Node.ELEMENT_NODE)
                        continue;

                    Element harmonicElement = cast(harmNode);

                    Harmonic h = new Harmonic();

                    h.setAmplitude(Double.parseDouble(harmonicElement
                        .getAttribute(HARMONIC_AMPLITUDE_ATTRIBUTE)));
                    h.setPhase(Double.parseDouble(harmonicElement
                        .getAttribute(HARMONIC_PHASE_ATTRIBUTE)));
                    h.setFrequency(Double.parseDouble(harmonicElement
                        .getAttribute(HARMONIC_FREQUENCY_ATTRIBUTE)));

                    h.setWaveform(Waveform.valueOf(harmonicElement
                        .getAttribute(HARMONIC_WAVEFORM_ATTRIBUTE)));

                    signal.getHarmonics().add(h);
                }

                list.add(signal);
            }

            signalList.addAll(list);

        } catch (Exception cause) {
            throw new IOException(String.format("Failed to load a file: '%s'",
                getFile().getAbsolutePath()), cause);
        }
    }
}
