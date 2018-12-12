package fr.insa.lyon.pld.agile.model;

import fr.insa.lyon.pld.agile.tsp.Dijkstra;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

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
            if (itinerary.size() == 1) //Consistency check : Should never happen
                throw new RuntimeException("Round has only one route");
            
            if (index-1 >= 0)
                before = itinerary.get(index-1);
            after = itinerary.get(index);
        }
        
        Node startingNode;
        Node destinationNode;
        LocalTime departureTime;
        boolean afterDelivering;
        
        if (before != null) {
            startingNode = before.getDestination();
            departureTime = getNextDepartureTime(before, map);
        } else {
            startingNode = map.getWarehouse();
            departureTime = map.getStartingHour();
        }
        
        if (after != null) {
            destinationNode = after.getDestination();
            afterDelivering = after.isDelivering();
        } else { //itinerary is empty
            destinationNode = map.getWarehouse();
            afterDelivering = false;
            itinerary.add(null); //For itinary.set(index+1, route) to work
        }

        Route route = new Route(departureTime, delivering);
        route.addPassages(Dijkstra.getPath(map.getNodes(), startingNode, node));
        itinerary.add(index, route);
        
        route = new Route(getNextDepartureTime(route, map), afterDelivering);
        route.addPassages(Dijkstra.getPath(map.getNodes(), node, destinationNode));
        itinerary.set(index+1, route); //The old route index became index+1 due to the add
        
        updateDepartureTimes(index+1, map);
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
            departureTime = getNextDepartureTime(before, map);
            startingNode = before.getDestination();
        } else {
            departureTime = map.getStartingHour();
            startingNode = map.getWarehouse();
        }
        
        if (startingNode == map.getWarehouse() && after.getDestination() == map.getWarehouse()) {
            itinerary.clear();
            return;
        }
        
        route = new Route(departureTime, after.isDelivering());
        route.addPassages(Dijkstra.getPath(map.getNodes(), startingNode, after.getDestination()));
        itinerary.set(index, route);
        itinerary.remove(index+1);
        
        updateDepartureTimes(index, map);
    }
    
    void clear() {
        itinerary.clear();
    }
    
    void updateStartingHour(Map map) {
        if (itinerary.isEmpty())
            return;
        
        itinerary.get(0).setDepartureTime(map.getStartingHour());
        updateDepartureTimes(0, map);
    }
        
    private LocalTime getNextDepartureTime(Route route, Map map) {
        LocalTime departureTime = route.getArrivalTime();
        if (route.isDelivering()) {
            Delivery delivery = map.getDeliveries().get(route.getDestination().getId());
            departureTime = departureTime.plusSeconds(delivery.getDuration());
        }
        
        return departureTime;
    }
        
    private void updateDepartureTimes(int indexFrom, Map map) {
        ListIterator<Route> iterator = itinerary.listIterator(indexFrom);
        Route before = iterator.next();
        while (iterator.hasNext()) {
            Route cur = iterator.next();
            cur.setDepartureTime(getNextDepartureTime(before, map));
            before = cur;
        }
    }
}
