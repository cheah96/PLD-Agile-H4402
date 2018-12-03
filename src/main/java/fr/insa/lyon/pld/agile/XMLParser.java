package fr.insa.lyon.pld.agile;

import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.model.Node;
import fr.insa.lyon.pld.agile.model.Section;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    private static final SAXParserFactory spf = SAXParserFactory.newInstance();
    
    static public void loadNodes(Map map, Path path) throws IOException, SAXException, ParserConfigurationException {
        loadMap(map, Files.newInputStream(path));
    }
    
    static public void loadMap(Map map, InputStream stream) throws IOException, SAXException, ParserConfigurationException {
        SAXParser saxParser = spf.newSAXParser();
        saxParser.parse(stream, new MapHandler(map));
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
                    
                    Node origin = map.getNode(originId);
                    if (origin != null) {
                        Node destination = map.getNode(destinationId);
                        if (destination != null) {
                            Section section = new Section(name, length, destination);
                            origin.addOutgoingSection(section);
                        } else
                            throw new RuntimeException("Destination node doesn't exist"); //TODO : Better error handling
                    }
                    else
                        throw new RuntimeException("Origin node doesn't exist"); //TODO : Better error handling
                    break;
            }
        }
    }
    
    static public void loadDeliveries(Map map, Path path) throws IOException, SAXException, ParserConfigurationException {
        loadDeliveries(map, Files.newInputStream(path));
    }
    
    static public void loadDeliveries(Map map, InputStream stream) throws IOException, SAXException, ParserConfigurationException {
        SAXParser saxParser = spf.newSAXParser();
        saxParser.parse(stream, new DeliveriesHandler(map));
    }

    private static class DeliveriesHandler extends DefaultHandler {
        private final Map map;
        
        public DeliveriesHandler(Map map) {
            this.map = map;
        }
        
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            //TODO : handing missing attributes
            switch (qName)
            {
                case "entrepot":
                    long warehouseId = Long.parseLong(attributes.getValue("adresse"));
                    //if (map.getWarehouse() != null)
                    //    throw new RuntimeException("Warehouse already set"); //TODO : Better error handling
                    
                    if (!map.setWarehouse(warehouseId))
                        throw new RuntimeException("Warehouse node not found"); //TODO : Better error handling
                    
                    String startingHourString = attributes.getValue("heureDepart");
                    map.setStartingHour(LocalTime.parse(startingHourString, DateTimeFormatter.ofPattern("H:m:s")));
                    break;
                case "livraison":
                    long addressId = Long.parseLong(attributes.getValue("adresse"));
                    int duration = Integer.parseInt(attributes.getValue("duree"));
                    
                    Node address = map.getNode(addressId);
                    if (address == null)
                        throw new RuntimeException("Address node not found"); //TODO : Better error handling
                    
                    map.addDelivery(new Delivery(address, duration));
                    break;
            }
        }
    }
    
    public static void main(String args[]) throws IOException, SAXException, ParserConfigurationException {
        Path mapPath = Paths.get("grandPlan.xml");
        Path deliveriesPath = Paths.get("dl-grand-12.xml");
        
        Map map = new Map();
        XMLParser.loadNodes(map, mapPath);
        XMLParser.loadDeliveries(map, deliveriesPath);
        System.out.println(map);
    }
}
