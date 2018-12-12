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
    
    /**
     * constructs a new delivery man 
     * @param an id number 
     */

    public DeliveryMan(int id) {
        this.id = id;
        this.round = new Round();
        this.deliveries = new ArrayList<>();
    }

    /**
     * gets the id number for the delivery man 
     */
    public int getId() {
        return id;
    }
    
    /**
     * gets the list of routes from round
     * @return the round
     */

    public Round getRound() {
        return round;
    }

    /**
     * gets the deliveries 
     * @return the list of deliveries
     */
    
    public List<Delivery> getDeliveries() {
        return Collections.unmodifiableList(deliveries);
    }
    
    /**
     * adds a delivery
     * @param index the index of a delivery man 
     * @param delivery the delivery 
     * @param map the map object   
     */
    
    void addDelivery(int index, Delivery delivery, Map map) {
        if (deliveries.contains(delivery))
            throw new RuntimeException("DeliveryMan already deliver there"); //TODO : Better error handling
        
        round.addNode(index, delivery.getNode(), true, map);
        deliveries.add(index, delivery);
    }
    
    /**
     * adds a delivery
     * @param delivery the delivery
     * @param map the map 
     */
    
    void addDelivery(Delivery delivery, Map map) {
        int index = !round.getItinerary().isEmpty() ? round.getItinerary().size()-1 : 0;
        addDelivery(index, delivery, map);
    }
    /**
     * removes a delivery based on the index of a delivery man
     * @param index an index of a delivery man
     * @param map the map  
     */
    
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
    
    /**
     * removes a delivery based on selected delivery
     * @param delivery a delivery 
     * @param map the map 
     */
    
    void removeDelivery(Delivery delivery, Map map) {
        int index = deliveries.indexOf(delivery);
        if (index == -1)
            throw new RuntimeException("Delivery not found");
        removeDelivery(index, map);
    }
    
    /**
     * clears the list of round and deliveries 
     */
    
    void clear() {
        round.clear();
        deliveries.clear();
    }
    
    /**
     * updates the starting hour of the deliveries
     * @param map the map 
     */
    
    void updateStartingHour(Map map) {
        round.updateStartingHour(map);
    }
}
