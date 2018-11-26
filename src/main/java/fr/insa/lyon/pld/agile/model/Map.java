package fr.insa.lyon.pld.agile.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;

/**
 *
 * @author scheah
 */
public class Map {
    private java.util.Map<Long, Node> nodes;
    private Node warehouse;
    private LocalTime startingHour;
    private List<Delivery> deliveries;

    public Map() {
        this.nodes = new HashMap();
        this.deliveries = new ArrayList();
    }
    
    public Map(Node warehouse, LocalTime startingHour) {
        this.nodes = new HashMap();
        this.warehouse = warehouse;
        this.startingHour = startingHour;
        this.deliveries = new ArrayList();
    }

    public java.util.Map<Long, Node> getNodes() {
        return Collections.unmodifiableMap(nodes);
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
    
    public boolean addNode(Node node) {
        // putIfAbsent return null if the key was absent
        return nodes.putIfAbsent(node.getId(), node) == null;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner("\n");
        for (Node node : getNodes().values()) {
            joiner.add(node.toString());
        }
        
        return joiner.toString();
    }
}
