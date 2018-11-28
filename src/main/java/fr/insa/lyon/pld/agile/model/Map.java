package fr.insa.lyon.pld.agile.model;

import fr.insa.lyon.pld.agile.tsp.Dijkstra;
import fr.insa.lyon.pld.agile.tsp.KMeansV1;
import fr.insa.lyon.pld.agile.tsp.TSPSolver;
import fr.insa.lyon.pld.agile.tsp.TSPSolverImplementation2;
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
    private final List<Delivery> deliveries;
    private final List<DeliveryMan> deliveryMen;

    public Map() {
        this(null, null);
    }
    
    public Map(Node warehouse, LocalTime startingHour) {
        this.nodes = new HashMap<>();
        this.warehouse = warehouse;
        this.startingHour = startingHour;
        this.deliveries = new ArrayList<>();
        this.deliveryMen = new ArrayList<>();
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

    public List<Delivery> getDeliveries() {
        return Collections.unmodifiableList(deliveries);
    }
    
    public List<DeliveryMan> getDeliveryMen() {
        return Collections.unmodifiableList(deliveryMen);
    }
    
    public boolean addNode(Node node) {
        // putIfAbsent return null if the key was absent
        return nodes.putIfAbsent(node.getId(), node) == null;
    }

    public void addDelivery(Delivery delivery) {
        deliveries.add(delivery);
    }
    
    public boolean setWarehouse(long id) {
        Node newWarehouse = this.nodes.get(id);
        if (newWarehouse != null) {
            this.warehouse = newWarehouse;
            return true;
        } else {
            return false;
        }
    }
    
    public void setStartingHour(LocalTime startingHour) {
        this.startingHour = startingHour;
    }
    
    public void setDeliveryManCount(int number) {
        deliveryMen.clear();
        for (int i = 0; i < number; i++)
            deliveryMen.add(new DeliveryMan(deliveryMen.size()));
    }
    
    public void distributeDeliveries() {
        List<Node> deliveryNodes = deliveries.stream().map(Delivery::getNode).collect(Collectors.toList());
        int[] clusters = KMeansV1.kMeans(deliveryNodes, deliveryMen.size());
        
        for (int i = 0; i < clusters.length; i++) {
            assignDelivery(deliveries.get(i), deliveryMen.get(clusters[i]));
        }
    }
    
    public void shortenDeliveries() {
        for (DeliveryMan deliveryMan : deliveryMen) {
            TSPSolver tspSolver = new TSPSolverImplementation2();
            List<Delivery> deliveries = deliveryMan.getDeliveries();
            int[][] edgesCosts = new int[deliveries.size()][deliveries.size()];
            int[] nodesCost = new int[deliveries.size()];
            
            for (int i = 0; i < deliveries.size(); i++) {
                for (int j = 0; j < deliveries.size(); j++) {
                    edgesCosts[i][j] = (int) (Dijkstra.dijkstraLength(nodes, deliveries.get(i).getNode(), deliveries.get(j).getNode())/1000./15.*60.*60.);
                }
                
                nodesCost[i] = deliveries.get(i).getDuration();
            }

            tspSolver.solve(1000, deliveries.size(), edgesCosts, nodesCost);
            
            List<Delivery> best = new ArrayList<>();
            for (int i = 0; i < deliveries.size(); i++) {
                best.add(deliveries.get(tspSolver.getBestNode(i)));
            }
            
            deliveryMan.clear();
            for (Delivery d : best)
                deliveryMan.addDelivery(d, this);
        }
    }
    
    public void assignDelivery(Delivery delivery, DeliveryMan deliveryMan) {
        delivery.setDeliveryMan(deliveryMan);
        deliveryMan.addDelivery(delivery, this);
    }
    
    public void clear() {
        nodes.clear();
        warehouse = null;
        startingHour = null;
        deliveries.clear();
        deliveryMen.clear();
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
        for (Delivery delivery : getDeliveries()) {
            joiner.add(delivery.toString());
        }
        builder.append(joiner);
        builder.append("\n  ]\n");
        builder.append("}");
        
        return builder.toString();
    }
}
