package fr.insa.lyon.pld.agile.model;

import fr.insa.lyon.pld.agile.tsp.Dijkstra;
import fr.insa.lyon.pld.agile.tsp.KMeansV1;
import fr.insa.lyon.pld.agile.tsp.TSPSolver;
import fr.insa.lyon.pld.agile.tsp.TSPSolverFactory;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 *
 * @author scheah
 */
public class Map {
    private final java.util.Map<Long, Node> nodes;
    private Node warehouse;
    private LocalTime startingHour;
    private final java.util.Map<Long,Delivery> deliveries;
    private final List<DeliveryMan> deliveryMen;
    
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public Map() {
        this(null, null);
    }
    
    public Map(Node warehouse, LocalTime startingHour) {
        this.nodes = new HashMap<>();
        this.warehouse = warehouse;
        this.startingHour = startingHour;
        this.deliveries = new HashMap<>();
        this.deliveryMen = new ArrayList<>();
    }
    
     public void addPropertyChangeListener(PropertyChangeListener listener) {
         this.pcs.addPropertyChangeListener(listener);
     }

     public void removePropertyChangeListener(PropertyChangeListener listener) {
         this.pcs.removePropertyChangeListener(listener);
     }
    
    public Node getNode(long id) {
        return nodes.get(id);
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

    public java.util.Map<Long,Delivery> getDeliveries() {
        return Collections.unmodifiableMap(deliveries);
    }
    
    public List<DeliveryMan> getDeliveryMen() {
        return Collections.unmodifiableList(deliveryMen);
    }
    
    public boolean addNode(Node node) {
        // putIfAbsent returns null if the key was absent
        boolean added = (nodes.putIfAbsent(node.getId(), node) == null);
        if (added) this.pcs.firePropertyChange("nodes", null, nodes);
        return added;
    }
    
    public void addDelivery(Delivery delivery) {
	boolean added = (deliveries.putIfAbsent(delivery.getNode().getId(), delivery) == null);
	if (added) this.pcs.firePropertyChange("deliveries", null, deliveries);
    }
    
    public boolean setWarehouse(long id) {
        Node oldWarehouse = warehouse;
        Node newWarehouse = this.nodes.get(id);
        if (newWarehouse != null) {
            this.warehouse = newWarehouse;
            this.pcs.firePropertyChange("warehouse", oldWarehouse, warehouse);
            return true;
        } else {
            return false;
        }
    }
    
    public void setStartingHour(LocalTime startingHour) {
        LocalTime oldStartingHour = startingHour;
        this.startingHour = startingHour;
        this.pcs.firePropertyChange("startingHour", oldStartingHour, startingHour);
    }
    
    public void setDeliveryManCount(int number) {
        deliveryMen.clear();
        for (int i = 0; i < number; i++)
            deliveryMen.add(new DeliveryMan(deliveryMen.size()));
        this.pcs.firePropertyChange("deliveryMen", null, deliveryMen);
    }
    
    public void distributeDeliveries() {
        List<Node> deliveryNodes = deliveries.values().stream().map(Delivery::getNode).collect(Collectors.toList());
        int[] clusters = KMeansV1.kMeans(deliveryNodes, deliveryMen.size());
        
        for (int i = 0; i < clusters.length; i++) {
            assignDelivery(deliveries.get(deliveryNodes.get(i).getId()), deliveryMen.get(clusters[i]));
        }
        
        this.pcs.firePropertyChange("deliveryMen", null, deliveryMen);
    }
    
    public void shortenDeliveries() {
        for (DeliveryMan deliveryMan : deliveryMen) {
            List<Delivery> deliveries = deliveryMan.getDeliveries();
            TSPSolver tspSolver = TSPSolverFactory.getSolver(deliveries.size());
            int[][] edgesCosts = new int[deliveries.size()][deliveries.size()];
            int[] nodesCost = new int[deliveries.size()];
            
            for (int i = 0; i < deliveries.size(); i++) {
                java.util.Map<Long, Double> distances = Dijkstra.getDistances(nodes, deliveries.get(i).getNode());
                for (int j = 0; j < deliveries.size(); j++) {
                    edgesCosts[i][j] = (int) (distances.get(deliveries.get(j).getNode().getId())/1000./15.*60.*60.);
                }
                
                nodesCost[i] = deliveries.get(i).getDuration();
            }

            tspSolver.solve(1000, deliveries.size(), edgesCosts, nodesCost);
            
            List<Delivery> best = new ArrayList<>();
            for (int i = 0; i < deliveries.size(); i++) {
                best.add(deliveries.get(tspSolver.getBestNode(i)));
            }
            
            deliveryMan.clear();
            for (Delivery d : best) {
                deliveryMan.addDelivery(d, this);
            }
        }
        
        this.pcs.firePropertyChange("deliveryMen", null, deliveryMen);
    }
    
    public void assignDelivery(Delivery delivery, DeliveryMan deliveryMan) {
        delivery.setDeliveryMan(deliveryMan);
        deliveryMan.addDelivery(delivery, this);
        
        this.pcs.firePropertyChange("deliveryMen", null, deliveryMen);
    }
    
    public void clear() {
        nodes.clear();
        Node oldWarehouse = warehouse;
        warehouse = null;
        LocalTime oldStartingHour = startingHour;
        startingHour = null;
        deliveries.clear();
        deliveryMen.clear();
        this.pcs.firePropertyChange("nodes", null, nodes);
        this.pcs.firePropertyChange("warehouse", oldWarehouse, warehouse);
        this.pcs.firePropertyChange("startingHour", oldStartingHour, startingHour);
        this.pcs.firePropertyChange("deliveries", null, deliveries);
        this.pcs.firePropertyChange("deliveryMen", null, deliveryMen);
    }
    public void clearDeliveries() {
        for (DeliveryMan deliveryMan : deliveryMen) {
            deliveryMan.clear();
        }
        this.pcs.firePropertyChange("deliveryMen", null, deliveryMen);
        
        deliveries.clear();
        this.pcs.firePropertyChange("deliveries", null, deliveries);
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Map {\n");
        builder.append("  nodes: [\n    ");
        StringJoiner joiner = new StringJoiner(",\n    ");
        for (Node node : getNodes().values()) {
            joiner.add(node.toString());
        }
        builder.append(joiner);
        builder.append("\n  ],\n");
        builder.append("  warehouse: ");
        builder.append(warehouse != null ? warehouse.getId() : "null");
        builder.append(",\n");
        builder.append("  startingHour: ");
        builder.append(startingHour);
        builder.append(",\n");
        builder.append("  deliveries: [\n    ");
        joiner = new StringJoiner(",\n    ");
        for (Delivery delivery : getDeliveries().values()) {
            joiner.add(delivery.toString());
        }
        builder.append(joiner);
        builder.append("\n  ]\n");
        builder.append("}");
        
        return builder.toString();
    }
    
    public int getNodeDeliveryManIndex(Node node) {
        if (node != getWarehouse()) {
            Delivery d = deliveries.get(node.getId());
            if(d != null && d.getDeliveryMan() != null) {
                return d.getDeliveryMan().getId();
            }
        }
        
        return -1;
    }
    
}
