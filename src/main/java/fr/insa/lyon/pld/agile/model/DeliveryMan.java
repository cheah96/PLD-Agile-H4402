package fr.insa.lyon.pld.agile.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author scheah
 */
public class DeliveryMan {
    private final int id;
    private final Round round;
    private final List<Delivery> deliveries;

    public DeliveryMan(int id) {
        this.id = id;
        this.round = new Round();
        this.deliveries = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public Round getRound() {
        return round;
    }

    public List<Delivery> getDeliveries() {
        return Collections.unmodifiableList(deliveries);
    }
    
    void addDelivery(int index, Delivery delivery, Map map) {
        if (deliveries.contains(delivery))
            throw new RuntimeException("DeliveryMan already deliver there"); //TODO : Better error handling
        
        round.addNode(index, delivery.getNode(), true, map);
        deliveries.add(index, delivery);
    }
    
    void addDelivery(Delivery delivery, Map map) {
        int index = !round.getItinerary().isEmpty() ? round.getItinerary().size()-1 : 0;
        addDelivery(index, delivery, map);
    }
    
    void removeDelivery(int index, Map map) {
        Node node = deliveries.get(index).getNode();
        
        int routeIndex = 0;
        for (Route route : round.getItinerary()) {
            if (route.getDestination() == node)
                break;
            
            routeIndex++;
        }
        
        round.removeNode(routeIndex, map);
        deliveries.remove(index);
    }
    
    void removeDelivery(Delivery delivery, Map map) {
        int index = deliveries.indexOf(delivery);
        if (index == -1)
            throw new RuntimeException("Delivery not found");
        removeDelivery(index, map);
    }
    
    void clear() {
        round.clear();
        deliveries.clear();
    }
    
    void updateStartingHour(Map map) {
        round.updateStartingHour(map);
    }
}
