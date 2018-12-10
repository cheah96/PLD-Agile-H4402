package fr.insa.lyon.pld.agile.model;

import fr.insa.lyon.pld.agile.tsp.Dijkstra;
import fr.insa.lyon.pld.agile.tsp.KMeans;
import fr.insa.lyon.pld.agile.tsp.TSPSolver;
import fr.insa.lyon.pld.agile.tsp.TSPSolverFactory;
import fr.insa.lyon.pld.agile.tsp.TSPSolverWorker;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
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
    private final List<TSPSolverWorker> pendingSolvers;
    
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
        this.pendingSolvers = Collections.synchronizedList(new ArrayList<TSPSolverWorker>());
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
	if (deliveries.putIfAbsent(delivery.getNode().getId(), delivery) == null)
            this.pcs.firePropertyChange("deliveries", null, deliveries);
    }
    
    public void removeDelivery(Delivery delivery) {
        unassignDelivery(delivery);
        if (deliveries.remove(delivery.getNode().getId()) != null)
            this.pcs.firePropertyChange("deliveries", null, deliveries);
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
    
    public void clearWarehouse() {
        this.warehouse = null;
    }
    
    public void setStartingHour(LocalTime startingHour) {
        LocalTime oldStartingHour = startingHour;
        this.startingHour = startingHour;
        this.pcs.firePropertyChange("startingHour", oldStartingHour, startingHour);
        
        for (DeliveryMan deliveryMan : deliveryMen) {
            deliveryMan.updateStartingHour(this);
            this.pcs.firePropertyChange("deliveryMan", null, deliveryMan);
        }
    }
    
    public void setDeliveryManCount(int number) {
        deliveryMen.clear();
        for (int i = 0; i < number; i++) {
            deliveryMen.add(new DeliveryMan(deliveryMen.size()));
        }
        
        this.pcs.firePropertyChange("deliveryMen", null, deliveryMen);
    }
    
    public void distributeDeliveries() {
        // TODO / WARNING : deliveryNodes is modified by kMeans
        List<Node> deliveryNodes = deliveries.values().stream().map(Delivery::getNode).collect(Collectors.toList());
        int[] clusters = KMeans.kMeans(deliveryNodes, deliveryMen.size(), this.warehouse);
        
        ListIterator<Node> nodeIterator = deliveryNodes.listIterator();
        for (int i = 0; i < clusters.length; i++) {
            Node deliveryNode = nodeIterator.next();
            assignDelivery(deliveries.get(deliveryNode.getId()), deliveryMen.get(clusters[i]));
        }
        
        for (DeliveryMan deliveryMan : deliveryMen)
            this.pcs.firePropertyChange("deliveryMan", null, deliveryMan);
    }
    
    public void shortenDeliveriesInBackground() {
        for (DeliveryMan deliveryMan : deliveryMen) {
            int nodeCount = deliveryMan.getDeliveries().size()+1;
            List<Node> deliveryNodes = new ArrayList<>(nodeCount);
            deliveryNodes.add(warehouse);
            
            int[][] edgesCosts = new int[nodeCount][nodeCount];
            int[] nodesCost = new int[nodeCount];
            
            {
                int i = 1;
                for (Delivery delivery : deliveryMan.getDeliveries()) {
                    deliveryNodes.add(delivery.getNode());
                    nodesCost[i] = delivery.getDuration();
                    i++;
                }
            }
            
            ListIterator<Node> fromIterator = deliveryNodes.listIterator();
            for (int i = 0; i < nodeCount; i++) {
                Node from = fromIterator.next();
                java.util.Map<Long, Double> distances = Dijkstra.getDistances(nodes, from);
                
                ListIterator<Node> toIterator = deliveryNodes.listIterator();
                for (int j = 0; j < nodeCount; j++) {
                    Node to = toIterator.next();
                    edgesCosts[i][j] = (int) Section.computeDuration(distances.get(to.getId()));
                }
            }

            TSPSolverWorker tspSolver = TSPSolverFactory.getSolver(nodeCount, edgesCosts, nodesCost);
            tspSolver.addPropertyChangeListener((PropertyChangeEvent pce) -> {
                if (!pce.getPropertyName().equals("intermediateBestPath") && !pce.getPropertyName().equals("finalBestPath"))
                    return;
                
                ArrayList<Integer> bestIds = (ArrayList<Integer>) pce.getNewValue();
                deliveryMan.clear();
                
                for (Integer index : bestIds) {
                    if (index != 0) {
                        Delivery delivery = deliveries.get(deliveryNodes.get(index).getId());
                        if (delivery != null)
                            deliveryMan.addDelivery(delivery, Map.this);
                        else
                            throw new RuntimeException("Delivery not found");
                    }
                }
                
                Map.this.pcs.firePropertyChange("deliveryMan", null, deliveryMan);
                
                if (pce.getPropertyName().equals("finalBestPath")) {
                    System.err.println("Remove");
                    pendingSolvers.remove(tspSolver);
                    
                    if (pendingSolvers.isEmpty()) {
                        Map.this.pcs.firePropertyChange("shortenDeliveriesFinished", null, deliveryMen);
                    }
                }
            });
            
            pendingSolvers.add(tspSolver);
            tspSolver.execute();
        }
    }
    
    public boolean isShorteningDeliveries() {
        return !pendingSolvers.isEmpty();
    }
    
    public void stopShorteningDeliveries() {
        for (TSPSolverWorker solver : new ArrayList<>(pendingSolvers))
            solver.cancel(false);
    }
    
    public void assignDelivery(int index, Delivery delivery, DeliveryMan deliveryMan) {
        if (deliveryMan.addDelivery(index, delivery, this)) { //TODO : handle unreachable deliveries
            delivery.setDeliveryMan(deliveryMan);
            
            this.pcs.firePropertyChange("deliveryMan", null, deliveryMan);
        }
    }
    
    public void assignDelivery(Delivery delivery, DeliveryMan deliveryMan) {
        if (deliveryMan.addDelivery(delivery, this)) {
            delivery.setDeliveryMan(deliveryMan);
            
            this.pcs.firePropertyChange("deliveryMan", null, deliveryMan);
        }
    }
    
    public void unassignDelivery(int index, DeliveryMan deliveryMan) {
        Delivery delivery = deliveryMan.getDeliveries().get(index);
        deliveryMan.removeDelivery(index, this);
        delivery.setDeliveryMan(null);
        
        this.pcs.firePropertyChange("deliveryMan", null, deliveryMan);
    }
    
    public void unassignDelivery(Delivery delivery) {
        DeliveryMan deliveryMan = delivery.getDeliveryMan();
        if (deliveryMan == null)
            return;
        
        deliveryMan.removeDelivery(delivery, this);
        delivery.setDeliveryMan(null);
        
        this.pcs.firePropertyChange("deliveryMan", null, deliveryMan);
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
