package fr.insa.lyon.pld.agile;

import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.model.Node;
import fr.insa.lyon.pld.agile.model.Section;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author paul
 */
public class XMLParser {
    static private SAXParserFactory spf = SAXParserFactory.newInstance();
    
    static public Map loadMap(Path path) throws IOException, SAXException, ParserConfigurationException {
        return loadMap(Files.newInputStream(path));
    }
    
    static public Map loadMap(InputStream stream) throws IOException, SAXException, ParserConfigurationException {
        Map map = new Map();
        
        SAXParser saxParser = spf.newSAXParser();
        saxParser.parse(stream, new MapHandler(map));
        
        return map;
    }

    private static class MapHandler extends DefaultHandler {
        private final Map map;
        
        public MapHandler(Map map) {
            this.map = map;
        }
        
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            //TODO : handing missing attributes
            switch (qName)
            {
                case "noeud":
                    long id = Long.parseLong(attributes.getValue("id"));
                    double latitude = Double.parseDouble(attributes.getValue("latitude"));
                    double longitude = Double.parseDouble(attributes.getValue("longitude"));
                    if (!map.addNode(new Node(id, latitude, longitude)))
                        throw new RuntimeException("Node already exists"); //TODO : Better error handling
                    break;
                case "troncon":
                    long originId = Long.parseLong(attributes.getValue("origine"));
                    long destinationId = Long.parseLong(attributes.getValue("destination"));
                    double length = Double.parseDouble(attributes.getValue("longueur"));
                    String name = attributes.getValue("nomRue");
                    
                    Node origin = map.getNodes().get(originId);
                    if (origin != null) {
                        Node destination = map.getNodes().get(destinationId);
                        if (destination != null)
                        {
                            Section section = new Section(name, length, destination);
                            origin.addOutgoingSection(section);
                        }
                        else
                            throw new RuntimeException("Destination node doesn't exist"); //TODO : Better error handling
                    }
                    else
                        throw new RuntimeException("Origin node doesn't exist"); //TODO : Better error handling
                    break;
            }
        }
    }
    
    public static void main(String args[]) throws IOException, SAXException, ParserConfigurationException
    {
        Path path = Paths.get("grandPlan.xml");
        
        Map map = XMLParser.loadMap(path);
        System.out.println(map);
    }
}
