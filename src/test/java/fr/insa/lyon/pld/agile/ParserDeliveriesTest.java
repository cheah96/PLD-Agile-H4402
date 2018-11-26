package fr.insa.lyon.pld.agile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.model.Node;
import fr.insa.lyon.pld.agile.model.Section;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ParserDeliveriesTest {
    private static Map map;
    
    @BeforeEach
    public void beforeEachTest() {
        map = new Map();
        
        map.addNode(new Node(25175791L, 45.75406, 4.857418));
        map.addNode(new Node(2129259178L, 45.750404, 4.8744674));
        
        Node origin = map.getNode(25175791L);
        Node destination = map.getNode(2129259178);
        
        Section section1 = new Section("Rue Danton", 69.979805, destination);
        origin.addOutgoingSection(section1);
        
        Section section2 = new Section("Rue de l'Abondance", 136.00636, origin);
        destination.addOutgoingSection(section2);
    }
    
    @Test
    public void testLoadDeliveriesSuccess() throws IOException, SAXException, ParserConfigurationException{
        String pathString = "src/test/res/delivery/deliveriesSuccess.xml";
        Path path = Paths.get(pathString);
        XMLParser.loadDeliveries(map, path);
        
        assertTrue(map.getWarehouse()!=null);
        assertEquals(25175791L,map.getWarehouse().getId());
        assertEquals(1, map.getDeliveries().size());
        assertEquals(2129259178L, map.getDeliveries().get(0).getNode().getId());
        assertEquals(60, map.getDeliveries().get(0).getDuration());
        assertEquals(8, map.getStartingHour().getHour());
        assertEquals(0, map.getStartingHour().getMinute());
        assertEquals(0, map.getStartingHour().getSecond());
    }
    
    @Test
    public void testLoadDeliveriesFailFileNotFound() throws IOException, SAXException, ParserConfigurationException{
        String pathString = "src/test/res/delivery/deliveriesFailFileNotFound.xml";
        Path path = Paths.get(pathString);
        Assertions.assertThrows(IOException.class, () -> XMLParser.loadDeliveries(map,path));
    }
    
    @Test
    public void testLoadDeliveriesFailTwoWarehouses() throws IOException, SAXException, ParserConfigurationException{
        String pathString = "src/test/res/delivery/deliveriesFailTwoWarehouses.xml";
        Path path = Paths.get(pathString);
        Assertions.assertThrows(RuntimeException.class, () -> XMLParser.loadDeliveries(map,path));
    }
    
    @Test
    public void testLoadDeliveriesFailWarehouseNotExist() throws IOException, SAXException, ParserConfigurationException{
        String pathString = "src/test/res/delivery/deliveriesFailWarehouseNotExist.xml";
        Path path = Paths.get(pathString);
        Assertions.assertThrows(RuntimeException.class, () -> XMLParser.loadDeliveries(map,path));
    }
    
    @Test
    public void testLoadDeliveriesFailDeliveryAddressNotExist() throws IOException, SAXException, ParserConfigurationException{
        String pathString = "src/test/res/delivery/deliveriesFailDeliveryAddressNotExist.xml";
        Path path = Paths.get(pathString);
        Assertions.assertThrows(RuntimeException.class, () -> XMLParser.loadDeliveries(map,path));
    }
}

