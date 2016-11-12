package com.itmm.map.xml_handler;

import android.os.Environment;

import com.itmm.map.location.Location;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Дмитрий on 11/12/2016.
 */

public class DomXmlParser {


    // names of the XML tags
    static final  String LAT = "latitude";
    static final  String LONG = "longitude";
    static final  String TITLE = "title";
    static final  String LOCATION = "location";

    String outputXmlFile;

    public DomXmlParser(String xmlFile) {
        outputXmlFile = xmlFile;
    }

    public List<Location> parse() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        List<Location> locations = new ArrayList<>();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document dom = builder.parse(new File(Environment.getExternalStorageDirectory(), outputXmlFile));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            Element root = dom.getDocumentElement();
            NodeList items = root.getElementsByTagName(LOCATION);

            // Save parsed xml from InputStream in the appropriate xml file
            Result output = new StreamResult(new File(Environment.getExternalStorageDirectory(), outputXmlFile));
            Source input = new DOMSource(dom);
            transformer.transform(input, output);

            for (int i = 0; i < items.getLength(); i++){
                Location location = new Location();
                Node item = items.item(i);
                NodeList properties = item.getChildNodes();

                for (int j = 0;j < properties.getLength(); j++){
                    Node property = properties.item(j);
                    String name = property.getNodeName();

                    if (name.equalsIgnoreCase(TITLE)){
                        location.setTitle(property.getFirstChild().getNodeValue());
                    } else if (name.equalsIgnoreCase(LAT)){
                        location.setLatitude(Double.valueOf(property.getFirstChild().getNodeValue()));
                    } else if (name.equalsIgnoreCase(LONG)){
                        location.setLongitude(Double.valueOf(property.getFirstChild().getNodeValue()));
                    }
                }
                locations.add(location);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return locations;
    }
}
