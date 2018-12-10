package fr.insa.lyon.pld.agile;

import fr.insa.lyon.pld.agile.xml.XMLParser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.insa.lyon.pld.agile.model.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParserMapTest {
    @Test
    public void testLoadMapSuccess() throws IOException, SAXException, ParserConfigurationException{
        String pathString = "src/test/res/map/mapSuccess.xml";
        Path path = Paths.get(pathString);
        Map map = new Map();
        XMLParser.loadMap(map, path);
        assertEquals(false,map.getNodes().isEmpty());
        assertEquals(3,map.getNodes().size());
        Long idNode1  = new Long(25175791);
        double latNode1 = 45.75406;
        double epsilon = 0.0001;
        double longNode1 = 4.857418;
        
        assertTrue(map.getNodes().get(idNode1)!=null);
        assertEquals(latNode1,map.getNodes().get(idNode1).getLatitude(), epsilon);
        assertEquals(longNode1,map.getNodes().get(idNode1).getLongitude(), epsilon);
        assertEquals(2, map.getNodes().get(idNode1).getOutgoingSections().size());
        
        assertEquals("Rue Danton", map.getNodes().get(idNode1).getOutgoingSections().get(0).getName());
        assertEquals(69.979805, map.getNodes().get(idNode1).getOutgoingSections().get(0).getLength(), epsilon);
        assertEquals(new Long(2129259178L), new Long(map.getNodes().get(idNode1).getOutgoingSections().get(0).getDestination().getId()));
        
        assertEquals("Rue de l'Abondance", map.getNodes().get(idNode1).getOutgoingSections().get(1).getName());
        assertEquals(136.00636, map.getNodes().get(idNode1).getOutgoingSections().get(1).getLength(), epsilon);
        assertEquals(new Long(26086130L), new Long(map.getNodes().get(idNode1).getOutgoingSections().get(1).getDestination().getId()));
    }
    
    @Test
    public void testLoadMapFailFileNotFound() throws IOException, SAXException, ParserConfigurationException{
        String pathString = "src/test/res/map/mapFailFileNotFound.xml";
        Path path = Paths.get(pathString);
        Assertions.assertThrows(IOException.class, () -> XMLParser.loadMap(new Map(), path));
    }
    
    @Test
    public void testLoadMapFailNodeSameID() throws IOException, SAXException, ParserConfigurationException{
        String pathString = "src/test/res/map/mapFailNodesSameID.xml";
        Path path = Paths.get(pathString);
        Assertions.assertThrows(RuntimeException.class, () -> XMLParser.loadMap(new Map(), path));
    }
    
    @Test
    public void testLoadMapFailDestinationNodeNotExist() throws IOException, SAXException, ParserConfigurationException{
        String pathString = "src/test/res/map/mapFailDestinationNodeNotExist.xml";
        Path path = Paths.get(pathString);
        Assertions.assertThrows(RuntimeException.class, () -> XMLParser.loadMap(new Map(), path));
    }
    
    @Test
    public void testLoadMapFailOriginNodeNotExist() throws IOException, SAXException, ParserConfigurationException{
        String pathString = "src/test/res/map/mapFailOriginNodeNotExist.xml";
        Path path = Paths.get(pathString);
        Assertions.assertThrows(RuntimeException.class, () -> XMLParser.loadMap(new Map(), path));
    }
}
