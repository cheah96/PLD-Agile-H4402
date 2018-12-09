package fr.insa.lyon.pld.agile.model;

import fr.insa.lyon.pld.agile.tsp.Dijkstra;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author scheah
 */
public class Round {
    private final List<Route> itinerary;

    public Round() {
        this.itinerary = new ArrayList<>();
    }

    public List<Route> getItinerary() {
        return Collections.unmodifiableList(itinerary);
    }
    
    void addNode(int index, Node node, boolean delivering, Map map) {
        Route before = null;
        Route after = null;
        if (!itinerary.isEmpty()) {
            if (itinerary.size() == 1)
                throw new RuntimeException("Round has only one passage");
            before = itinerary.get(index-1);
            after = itinerary.get(index);
        }
        
        Node startingNode;
        Node destinationNode;
        LocalTime departureTime;
        boolean afterDelivering;
        if (before != null && after != null) {
            startingNode = before.getDestination();
            destinationNode = after.getDestination();
            departureTime = before.getArrivalTime();
            afterDelivering = true;
            if (before.isDelivering()) {
                Delivery delivery = map.getDeliveries().get(before.getDestination().getId());
                departureTime.plusSeconds(delivery.getDuration());
            }
        } else {
            startingNode = map.getWarehouse();
            destinationNode = startingNode;
            departureTime = map.getStartingHour();
            afterDelivering = false;
            itinerary.add(null); //For itinary.set to work
        }
        
        Route route = new Route(departureTime, delivering);
        route.addPassages(Dijkstra.getPath(map.getNodes(), startingNode, node));
        itinerary.add(index, route);
        
        route = new Route(route.getArrivalTime(), afterDelivering);
        route.addPassages(Dijkstra.getPath(map.getNodes(), node, destinationNode));
        itinerary.set(index+1, route); //The old route index became index+1 due to the add
    }
    
    void removeNode(int index, Map map) {
        if (index == itinerary.size()-1)
            throw new RuntimeException("Cannot remove the last route to the warehouse");
        
        Route before = null;
        if (index-1 >= 0)
            before = itinerary.get(index-1);
        Route after = itinerary.get(index+1);
        
        Route route;
        Node startingNode;
        LocalTime departureTime;
        if (before != null) {
            departureTime = before.getArrivalTime();
            if (before.isDelivering()) {
                Delivery delivery = map.getDeliveries().get(before.getDestination().getId());
                departureTime.plusSeconds(delivery.getDuration());
            }
            startingNode = before.getDestination();
        } else {
            departureTime = map.getStartingHour();
            startingNode = map.getWarehouse();
        }
        
        route = new Route(departureTime, after.isDelivering());
        route.addPassages(Dijkstra.getPath(map.getNodes(), startingNode, after.getDestination()));
        itinerary.set(index+1, route);
        itinerary.remove(index);
    }
    
    void clear() {
        itinerary.clear();
    }
}
