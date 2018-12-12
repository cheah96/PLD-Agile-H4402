package fr.insa.lyon.pld.agile.xml;

import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.model.Node;
import fr.insa.lyon.pld.agile.model.Section;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLParser {
    private static final SAXParserFactory spf = SAXParserFactory.newInstance();
    
    static public void loadMap(Map map, Path path) throws IOException, SAXException, ParserConfigurationException {
        SAXParser saxParser = spf.newSAXParser();
        saxParser.parse(Files.newInputStream(path), new MapNodesHandler(map));
        saxParser.parse(Files.newInputStream(path), new MapSectionsHandler(map));
    }

    private static class MapNodesHandler extends DefaultHandler {
        private final Map map;
        private final HashSet<String> expectedElements;
        
        public MapNodesHandler(Map map) {
            this.map = map;
            expectedElements = new HashSet<String>();
            expectedElements.add("noeud");
            expectedElements.add("troncon");
            expectedElements.add("reseau");
        }
        
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException, XMLMissingAttributeException, XMLDuplicateNodeException {
            if(!expectedElements.contains(qName)) {
                throw new XMLUnexpectedElementException(qName);
            }
            
            if(!"noeud".equals(qName)) {
                return;
            }
            
            if(attributes.getValue("id") == null) {
                throw new XMLMissingAttributeException("id");
            }
            if(attributes.getValue("latitude") == null) {
                throw new XMLMissingAttributeException("latitude");
            }
            if(attributes.getValue("longitude") == null) {
                throw new XMLMissingAttributeException("longitude");
            }
            
            long id = 0;
            try {
                id = Long.parseLong(attributes.getValue("id"));
            } catch (NumberFormatException e) {
                throw new XMLAttributeFormatException("id", attributes.getValue("id"));
            }
            
            double latitude = 0;
            try {
                latitude = Double.parseDouble(attributes.getValue("latitude"));
            } catch (NumberFormatException e) {
                throw new XMLAttributeFormatException("latitude", attributes.getValue("latitude"));
            }
            
            double longitude = 0;
            try {
                longitude = Double.parseDouble(attributes.getValue("longitude"));
            } catch (NumberFormatException e) {
                throw new XMLAttributeFormatException("longitude", attributes.getValue("longitude"));
            }

            if (!map.addNode(new Node(id, latitude, longitude))) {
                throw new XMLDuplicateNodeException(id);
            }
        }
    }
    
    private static class MapSectionsHandler extends DefaultHandler {
        private final Map map;
        private final HashSet<String> expectedElements;
        
        public MapSectionsHandler(Map map) {
            this.map = map;
            expectedElements = new HashSet<String>();
            expectedElements.add("noeud");
            expectedElements.add("troncon");
            expectedElements.add("reseau");
        }
        
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if(!expectedElements.contains(qName)) {
                throw new XMLUnexpectedElementException(qName);
            }
            
            if(!"troncon".equals(qName)) {
                return;
            }
            
            if(attributes.getValue("origine") == null) {
                throw new XMLMissingAttributeException("origine");
            }
            if(attributes.getValue("destination") == null) {
                throw new XMLMissingAttributeException("destination");
            }
            if(attributes.getValue("longueur") == null) {
                throw new XMLMissingAttributeException("longueur");
            }
            if(attributes.getValue("nomRue") == null) {
                throw new XMLMissingAttributeException("nomRue");
            }
            
            long originId = 0;
            try {
                originId = Long.parseLong(attributes.getValue("origine"));
            } catch (NumberFormatException e) {
                throw new XMLAttributeFormatException("origine", attributes.getValue("origine"));
            }
            
            long destinationId = 0;
            try {
                destinationId = Long.parseLong(attributes.getValue("destination"));
            } catch (NumberFormatException e) {
                throw new XMLAttributeFormatException("destination", attributes.getValue("destination"));
            }
            
            double length = 0;
            try {
                length = Double.parseDouble(attributes.getValue("longueur"));
            } catch (NumberFormatException e) {
                throw new XMLAttributeFormatException("longueur", attributes.getValue("longueur"));
            }
            
            String name = attributes.getValue("nomRue");

            Node origin = map.getNode(originId);
            if (origin != null) {
                Node destination = map.getNode(destinationId);
                if (destination != null) {
                    Section section = new Section(name, length, destination);
                    origin.addOutgoingSection(section);
                } else {
                    throw new XMLUndefinedNodeReferenceException(destinationId);
                }
            }
            else {
                throw new XMLUndefinedNodeReferenceException(originId);
            }
        }
    }
    
    static public void loadDeliveries(Map map, Path path) throws IOException, SAXException, ParserConfigurationException {
        SAXParser saxParser = spf.newSAXParser();
        saxParser.parse(Files.newInputStream(path), new DeliveriesHandler(map));
    }

    private static class DeliveriesHandler extends DefaultHandler {
        private final Map map;
        private final HashSet<String> expectedElements;
        
        public DeliveriesHandler(Map map) {
            this.map = map;
            expectedElements = new HashSet<String>();
            expectedElements.add("entrepot");
            expectedElements.add("livraison");
            expectedElements.add("demandeDeLivraisons");
        }
        
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if(!expectedElements.contains(qName)) {
                throw new XMLUnexpectedElementException(qName);
            }
            
            switch (qName) {
                case "entrepot":
                    if (map.getWarehouse() != null) {
                        throw new XMLMultipleDefinitionOfWarehouseException();
            }
                    
                    if(attributes.getValue("adresse") == null) {
                        throw new XMLMissingAttributeException("adresse");
            }
                    
                    long warehouseId = 0;
                    try {
                        warehouseId = Long.parseLong(attributes.getValue("adresse"));
                    } catch(NumberFormatException e) {
                        throw new XMLAttributeFormatException("adresse", attributes.getValue("adresse"));
                    }
                    
                    if (!map.setWarehouse(warehouseId)) {
                        throw new XMLUndefinedNodeReferenceException(warehouseId);
            }
                    
                    String startingHourString = attributes.getValue("heureDepart");
                    map.setStartingHour(LocalTime.parse(startingHourString, DateTimeFormatter.ofPattern("H:m:s")));
                    break;
                case "livraison":
                    if(attributes.getValue("adresse") == null) {
                        throw new XMLMissingAttributeException("adresse");
            }
                    if(attributes.getValue("duree") == null) {
                        throw new XMLMissingAttributeException("duree");
            }
                    
                    long addressId = 0;
                    try {
                        addressId = Long.parseLong(attributes.getValue("adresse"));
                    } catch(NumberFormatException e) {
                        throw new XMLAttributeFormatException("adresse", attributes.getValue("adresse"));
                    }
                    
                    int duration = 0;
                    try {
                        duration = Integer.parseInt(attributes.getValue("duree"));
                    } catch(NumberFormatException e) {
                        throw new XMLAttributeFormatException("duree", attributes.getValue("duree"));
                    }
                    
                    Node address = map.getNode(addressId);
                    if (address == null) {
                        throw new XMLUndefinedNodeReferenceException(addressId);
            }
                    
                    map.addDelivery(new Delivery(address, duration));
                    break;
            }
        }
    }
    
    private XMLParser() {
        
    }
    
    public static void main(String args[]) throws IOException, SAXException, ParserConfigurationException {
        Path mapPath = Paths.get("grandPlan.xml");
        Path deliveriesPath = Paths.get("dl-grand-12.xml");
        
        Map map = new Map();
        XMLParser.loadMap(map, mapPath);
        XMLParser.loadDeliveries(map, deliveriesPath);
        System.out.println(map);
    }
}
