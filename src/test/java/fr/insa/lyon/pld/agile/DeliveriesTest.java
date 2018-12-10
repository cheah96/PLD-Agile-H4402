package fr.insa.lyon.pld.agile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.DeliveryMan;
import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.model.Node;
import fr.insa.lyon.pld.agile.model.Section;
import fr.insa.lyon.pld.agile.xml.XMLParser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

        for(Delivery delivery: map.getDeliveries().values()) {
            map.assignDelivery(delivery,map.getDeliveryMen().get(0));
        }

        map.unassignDelivery(3,map.getDeliveryMen().get(0));

        LocalTime last = LocalTime.of(0, 0, 1, 1);
        for(int i=0;i<map.getDeliveryMen().get(0).getRound().getItinerary().size();++i) {
            LocalTime curr = map.getDeliveryMen().get(0).getRound().getItinerary().get(i).getArrivalTime();
            System.out.println(last);
            System.out.println(curr);
            assertTrue(curr.compareTo(last)>0);
            last=curr;
        }
    }
}
