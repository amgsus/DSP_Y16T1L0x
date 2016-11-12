package dsplab.io.signal.impl;

import dsplab.io.signal.SignalListReader;
import dsplab.logic.signal.Signal;
import javafx.scene.paint.Color;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SignalListReaderImpl implements SignalListReader
{
    SignalListReaderImpl()
    {
    }

    public static SignalListReader newInstance() { return new
        SignalListReaderImpl(); }

    // -------------------------------------------------------------------- //

    private File file;

    // -------------------------------------------------------------------- //

    @Override
    public List<Signal> fetchAll() throws IOException
    {
        ArrayList<Signal> signalList = new ArrayList<>();
        this.fetchAll(signalList);
        return signalList;
    }

    @Override
    public int fetchAll(List<Signal> existingList) throws IOException
    {
        if (existingList == null)
            throw new IllegalArgumentException("existingList is null");

        try {
            DocumentBuilderFactory docBuilderFactory
                = DocumentBuilderFactory.newInstance();

            DocumentBuilder docBuilder
                = docBuilderFactory.newDocumentBuilder(); // ParserConfig...Ex.

            Document doc = docBuilder.parse(this.file);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("Signal");

            for (int i = 0; i < nodeList.getLength(); i++) {

                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element e = (Element) node;

                    String signalName = e.getAttribute("name");
                    Color SignalColor = Color.valueOf(signalName);

                }
            }

            return existingList.size();

        } catch (ParserConfigurationException pConfEx) {
            throw new IOException("Failed to init XML-parser", pConfEx);
        } catch (SAXException saxE) {
            throw new IOException("Failed to parser XML-document", saxE);
        }
    }

    @Override
    public void setFile(File file)
    {
        this.file = file;
    }

    @Override
    public File getFile()
    {
        return file;
    }
}
