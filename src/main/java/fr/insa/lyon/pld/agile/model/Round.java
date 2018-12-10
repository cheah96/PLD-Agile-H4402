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
    
    boolean addNode(int index, Node node, boolean delivering, Map map) {
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
            departureTime = getNextDepartureTime(before, map);
            afterDelivering = true;
        } else {
            startingNode = map.getWarehouse();
            destinationNode = startingNode;
            departureTime = map.getStartingHour();
            afterDelivering = false;
        }
        
        List<Section> firstSections = Dijkstra.getPath(map.getNodes(), startingNode, node);
        List<Section> secondSections = Dijkstra.getPath(map.getNodes(), node, destinationNode);
        
        if (firstSections == null || secondSections == null)
            return false;

        if (itinerary.isEmpty())
            itinerary.add(null); //For itinary.set to work
        
        Route route = new Route(departureTime, delivering);
        route.addPassages(firstSections);
        itinerary.add(index, route);
        
        route = new Route(route.getArrivalTime(), afterDelivering);
        route.addPassages(secondSections);
        itinerary.set(index+1, route); //The old route index became index+1 due to the add
        
        updateDepartureTimes(index+1, map);
        
        return true;
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
