package dsplab.io.util.xml;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public final class XMLUtils
{
    private XMLUtils() {}

    public static Document createEmptyDocument()
        throws Exception
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        return builder.newDocument();
    }

    public static void saveDocumentToFile(Document doc, File file)
        throws Exception
    {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer t = factory.newTransformer();

            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(file);

            t.transform(domSource, streamResult);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
