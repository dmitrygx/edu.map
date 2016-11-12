package com.itmm.map.xml_handler;

import android.os.Environment;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Дмитрий on 11/12/2016.
 */

public class DomXmlWriter {

    Result output;
    String outputXmlFile;

    public DomXmlWriter(String xmlFile) {
        outputXmlFile = xmlFile;
        output =  new StreamResult(new File(Environment.getExternalStorageDirectory(), outputXmlFile));
    }

    public void createNewFile(String titleVal, double latitudeVal, double longitudeVal) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("locations");
            doc.appendChild(rootElement);

            Element location = doc.createElement("location");
            rootElement.appendChild(location);

            // latitude elements
            Element title = doc.createElement("title");
            title.appendChild(doc.createTextNode(titleVal));
            location.appendChild(title);

            // latitude elements
            Element latitude = doc.createElement("latitude");
            latitude.appendChild(doc.createTextNode(Double.toString(latitudeVal)));
            location.appendChild(latitude);

            // longitude elements
            Element longitude = doc.createElement("longitude");
            longitude.appendChild(doc.createTextNode(Double.toString(longitudeVal)));
            location.appendChild(longitude);

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(Environment.getExternalStorageDirectory(), outputXmlFile));

            transformer.transform(source, result);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }

    }

    public void addNewEntry(String titleVal, double latitudeVal, double longitudeVal) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(Environment.getExternalStorageDirectory(), outputXmlFile));

            Node locations = doc.getElementsByTagName("locations").item(0);

            Element location = doc.createElement("location");
            locations.appendChild(location);

            // latitude elements
            Element title = doc.createElement("title");
            title.appendChild(doc.createTextNode(titleVal));
            location.appendChild(title);

            // latitude elements
            Element latitude = doc.createElement("latitude");
            latitude.appendChild(doc.createTextNode(Double.toString(latitudeVal)));
            location.appendChild(latitude);

            // longitude elements
            Element longitude = doc.createElement("longitude");
            longitude.appendChild(doc.createTextNode(Double.toString(longitudeVal)));
            location.appendChild(longitude);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(Environment.getExternalStorageDirectory(), outputXmlFile));
            transformer.transform(source, result);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        } catch (SAXException sae) {
            sae.printStackTrace();
        } catch (IOException ioe) {
            createNewFile(titleVal, latitudeVal, longitudeVal);
        }
    }
}
