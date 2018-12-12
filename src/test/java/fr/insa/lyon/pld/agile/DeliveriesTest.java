package fr.insa.lyon.pld.agile;

import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.xml.XMLParser;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.Collection;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

public class DeliveriesTest {

    @Test
    public void testAssignUnassign() throws IOException, SAXException, ParserConfigurationException{
        Map map = new Map();
        String pathStringMap = "src/test/res/map/mapSuccessSmall.xml";

        Path pathMap = Paths.get(pathStringMap);

        XMLParser.loadMap(map, pathMap);

        String pathStringDeliveries = "src/test/res/delivery/deliveriesSuccess6.xml";

        Path pathDeliveries = Paths.get(pathStringDeliveries);
        XMLParser.loadDeliveries(map, pathDeliveries);

        map.setDeliveryManCount(1);
        
        Collection<Delivery> deliveriesColl = map.getDeliveries().values();
        
        Object[] deliveries = deliveriesColl.toArray();
   
        map.assignDelivery((Delivery)deliveries[4],map.getDeliveryMen().get(0));
        map.assignDelivery((Delivery)deliveries[3],map.getDeliveryMen().get(0));
        map.assignDelivery((Delivery)deliveries[0],map.getDeliveryMen().get(0));
        map.assignDelivery((Delivery)deliveries[2],map.getDeliveryMen().get(0));
        
        Delivery del0 = map.getDeliveryMen().get(0).getDeliveries().get(0);
        assertTrue(del0.getNode().getId()==((Delivery)deliveries[4]).getNode().getId());
        
        Delivery del1 = map.getDeliveryMen().get(0).getDeliveries().get(1);
        assertTrue(del1.getNode().getId()==((Delivery)deliveries[3]).getNode().getId());
        
        Delivery del2 = map.getDeliveryMen().get(0).getDeliveries().get(2);
        assertTrue(del2.getNode().getId()==((Delivery)deliveries[0]).getNode().getId());
        
        Delivery del3 = map.getDeliveryMen().get(0).getDeliveries().get(3);
        assertTrue(del3.getNode().getId()==((Delivery)deliveries[2]).getNode().getId());
        
        map.unassignDelivery(del1);
        assertTrue(del2.getNode().getId()==map.getDeliveryMen().get(0).getDeliveries().get(1).getNode().getId());
        
        map.assignDelivery(2, del1, map.getDeliveryMen().get(0));
        assertTrue(del1.getNode().getId()==map.getDeliveryMen().get(0).getDeliveries().get(2).getNode().getId());
        
        LocalTime last = LocalTime.of(0, 0, 1, 1);
        for(int i=0;i<map.getDeliveryMen().get(0).getRound().getItinerary().size();++i) {
            LocalTime curr = map.getDeliveryMen().get(0).getRound().getItinerary().get(i).getArrivalTime();
            assertTrue(curr.compareTo(last)>0);
            last=curr;
        }
    }
}
