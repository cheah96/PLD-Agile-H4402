package fr.insa.lyon.pld.agile.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author scheah
 */
public class Map {
    private java.util.Map<Long, Node> nodes;
    private final Node warehouse;
    private final LocalTime startingHour;
    private List<Delivery> deliveries;

    public Map(Node warehouse, LocalTime startingHour) {
        this.nodes = new HashMap();
        this.warehouse = warehouse;
        this.startingHour = startingHour;
        this.deliveries = new ArrayList();
    }

    public java.util.Map<Long, Node> getNodes() {
        return nodes;
    }

    public Node getWarehouse() {
        return warehouse;
    }

    public LocalTime getStartingHour() {
        return startingHour;
    }

    public List<Delivery> getDeliveries() {
        return Collections.unmodifiableList(deliveries);
    }
    
    
}
