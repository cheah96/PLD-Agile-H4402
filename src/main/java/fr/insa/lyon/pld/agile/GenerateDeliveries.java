package fr.insa.lyon.pld.agile;

import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.model.Node;
import fr.insa.lyon.pld.agile.model.UnreachableDeliveryException;
import fr.insa.lyon.pld.agile.xml.XMLParser;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GenerateDeliveries {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage : java fr.insa.lyon.pld.agile.GenerateDeliveries >XMLMapFilepath> <number of deliveries>");
        }
        
        int nb = Integer.parseInt(args[1]);
        
        Map map = new Map();
        XMLParser.loadMap(map, Paths.get(args[0]));
        
        List<Node> nodes = new ArrayList<>(map.getNodes().values());
        Collections.shuffle(nodes);
        
        Node warehouse = nodes.get(0);
        map.setWarehouse(warehouse.getId());
        
        List<Delivery> deliveries = new ArrayList<>();
        
        for (Node node : nodes) {
            if (node == warehouse) {
                continue;
            }
            
            if (deliveries.size() >= nb) {
                break;
            }
            
            try {
                Delivery delivery = new Delivery(node, (int) (Math.random()*360+1));
                map.addDelivery(delivery);
                deliveries.add(delivery);
            } catch (UnreachableDeliveryException e) {
                
            }
        }
        
        System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
        System.out.println("<demandeDeLivraisons>");
        System.out.println("<entrepot adresse=\""+warehouse.getId()+"\" heureDepart=\"8:0:0\"/>");
        for (Delivery delivery : deliveries) {
            System.out.println("<livraison adresse=\""+delivery.getNode().getId()+"\" duree=\""+delivery.getDuration()+"\"/>");
        }
        System.out.println("</demandeDeLivraisons>");
    }
}
