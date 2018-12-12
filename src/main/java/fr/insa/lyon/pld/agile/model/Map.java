package fr.insa.lyon.pld.agile.model;

import fr.insa.lyon.pld.agile.tsp.Dijkstra;
import fr.insa.lyon.pld.agile.tsp.KMeans;
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
import java.util.concurrent.CancellationException;
import java.util.stream.Collectors;
import javax.swing.SwingWorker;

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

    /**
     * Creates an empty map.
     */
    public Map() {
        this(null, null);
    }
    
    /**
     * Creates a map with a warehouse and a starting delivery hour.
     * @param warehouse
     * @param startingHour
     */
    public Map(Node warehouse, LocalTime startingHour) {
        this.nodes = new HashMap<>();
        this.warehouse = warehouse;
        this.startingHour = startingHour;
        this.deliveries = new HashMap<>();
        this.deliveryMen = new ArrayList<>();
        this.pendingSolvers = Collections.synchronizedList(new ArrayList<>());
    }
    
    /**
     * Adds a property change listener.
     * @param listener the listener object
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    /**
     * Removes a property change listener.
     * @param listener the listener object
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }
    
    /**
     * Gets a node from its id.
     * @param id the node's id
     * @return the node
     */
    public Node getNode(long id) {
        return nodes.get(id);
    }

    /**
     * Gets all the nodes.
     * @return the nodes
     */
    public java.util.Map<Long, Node> getNodes() {
        return Collections.unmodifiableMap(nodes);
    }

    /**
     * Gets the warehouse.
     * @return the warehouse
     */
    public Node getWarehouse() {
        return warehouse;
    }

    /**
     * Gets the starting hour.
     * @return the starting hour
     */
    public LocalTime getStartingHour() {
        return startingHour;
    }

    /**
     * Gets the deliveries.
     * @return the deliveries
     */
    public java.util.Map<Long,Delivery> getDeliveries() {
        return Collections.unmodifiableMap(deliveries);
    }
    
    /**
     * Gets the delivery men.
     * @return the delivery men
     */
    public List<DeliveryMan> getDeliveryMen() {
        return Collections.unmodifiableList(deliveryMen);
    }
    
    /**
     * Adds a node to the map.
     * @param node
     * @return true if added, false if the node was already in the map
     */
    public boolean addNode(Node node) {
        // putIfAbsent returns null if the key was absent
        boolean added = nodes.putIfAbsent(node.getId(), node) == null;
        if (added) this.pcs.firePropertyChange("nodes", null, nodes);
        return added;
    }
    
    /**
     * Adds a delivery to the map. Throws if it isn't reachable from the warehouse.
     * @param delivery the delivery to add
     * @return true if added, false if already present
     */
    public boolean addDelivery(Delivery delivery) {
        if (delivery.getNode() == warehouse)
            return false;
        
        System.err.println(delivery);
        if (deliveries.containsKey(delivery.getNode().getId()))
            return false;
        
        if (Dijkstra.getPath(nodes, warehouse, delivery.getNode()) == null || Dijkstra.getPath(nodes, delivery.getNode(), warehouse) == null) {
            throw new UnreachableDeliveryException(delivery);
        }
        
	deliveries.put(delivery.getNode().getId(), delivery);
        this.pcs.firePropertyChange("deliveries", null, deliveries);
        return true;
    }
    
    /**
     * Removes a delivery.
     * @param delivery the delivery to remove
     */
    public void removeDelivery(Delivery delivery) {
        unassignDelivery(delivery);
        if (deliveries.remove(delivery.getNode().getId()) != null)
            this.pcs.firePropertyChange("deliveries", null, deliveries);
    }
    
    /**
     * Sets the warehouse.
     * @param id id of the node that specifies the warehouse
     * @return true if added, false if incorrect node id
     */
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
    
    /**
     * Removes the warehouse.
     */
    public void clearWarehouse() {
        this.warehouse = null;
    }
    
    /**
     * Sets the starting hour of deliveries.
     * @param startingHour the starting hour
     */
    public void setStartingHour(LocalTime startingHour) {
        LocalTime oldStartingHour = startingHour;
        this.startingHour = startingHour;
        this.pcs.firePropertyChange("startingHour", oldStartingHour, startingHour);
        
        for (DeliveryMan deliveryMan : deliveryMen) {
            deliveryMan.updateStartingHour(this);
            this.pcs.firePropertyChange("deliveryMan", null, deliveryMan);
        }
    }
    
    /**
     * Sets the number of delivery men and give them empty roadmaps.
     * @param number the new number of delivery men
     */
    public void setDeliveryManCount(int number) {
        deliveryMen.clear();
        for (int i = 0; i < number; i++) {
            deliveryMen.add(new DeliveryMan(deliveryMen.size()));
        }
        
        this.pcs.firePropertyChange("deliveryMen", null, deliveryMen);
    }
    
    /**
     * Distributes evenly the deliveries to the delivery men.
     */
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
    
    /**
     * Creates background processes to optimize the roadmaps.
     */
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
            tspSolver.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent pce) {
                    switch (pce.getPropertyName()) {
                        case "progress":
                            List<TSPSolverWorker> solvers = new ArrayList<>(pendingSolvers);
                            int progress = solvers.stream().map(TSPSolverWorker::getProgress).min(Integer::compare).get();
                            System.out.println("Progress : " + progress);
                            Map.this.pcs.firePropertyChange("shortenDeliveriesProgress", null, progress);
                            break;
                        case "intermediateBestPath":
                            updateDeliveries((ArrayList<Integer>) pce.getNewValue());
                            break;
                        case "state":
                            if (pce.getNewValue() == SwingWorker.StateValue.DONE) {
                                try {
                                    updateDeliveries(tspSolver.get());
                                } catch (CancellationException e) {
                                    
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                pendingSolvers.remove(tspSolver);

                                if (pendingSolvers.isEmpty()) {
                                    Map.this.pcs.firePropertyChange("shortenDeliveriesFinished", null, deliveryMen);
                                }
                            }
                            break;
                    }
                }
                
                private void updateDeliveries(ArrayList<Integer> bestIds) {
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
                }
            });
            
            pendingSolvers.add(tspSolver);
            tspSolver.execute();
        }
    }
    
    /**
     * Indicates whether the roadmaps are currently being optimized.
     * @return
     */
    public boolean isShorteningDeliveries() {
        return !pendingSolvers.isEmpty();
    }
    
    /**
     * Interrupts the roadmaps optimization process.
     */
    public void stopShorteningDeliveries() {
        for (TSPSolverWorker solver : new ArrayList<>(pendingSolvers))
            solver.cancel(false);
    }
    
    /**
     * Assign a delivery to a delivery man.
     * @param index index of the node to insert it before
     * @param delivery delivery to assign
     * @param deliveryMan delivery man to assign the delivery to
     */
    public void assignDelivery(int index, Delivery delivery, DeliveryMan deliveryMan) {
        deliveryMan.addDelivery(index, delivery, this);
        delivery.setDeliveryMan(deliveryMan);
            
        this.pcs.firePropertyChange("deliveryMan", null, deliveryMan);
    }
    
    /**
     * Assign a delivery to a delivery man.
     * @param delivery delivery to assign
     * @param deliveryMan delivery man to assign the delivery to
     */
    public void assignDelivery(Delivery delivery, DeliveryMan deliveryMan) {
        deliveryMan.addDelivery(delivery, this);
        delivery.setDeliveryMan(deliveryMan);
            
        this.pcs.firePropertyChange("deliveryMan", null, deliveryMan);
    }
    
    /**
     * Unassigns a delivery of a delivery man.
     * @param index index of the delivery to unassign
     * @param deliveryMan the delivery man
     */
    public void unassignDelivery(int index, DeliveryMan deliveryMan) {
        Delivery delivery = deliveryMan.getDeliveries().get(index);
        deliveryMan.removeDelivery(index, this);
        delivery.setDeliveryMan(null);
        
        this.pcs.firePropertyChange("deliveryMan", null, deliveryMan);
    }
    
    /**
     * Unassigns a delivery.
     * @param delivery delivery to unassign
     */
    public void unassignDelivery(Delivery delivery) {
        DeliveryMan deliveryMan = delivery.getDeliveryMan();
        if (deliveryMan == null)
            return;
        
        deliveryMan.removeDelivery(delivery, this);
        delivery.setDeliveryMan(null);
        
        this.pcs.firePropertyChange("deliveryMan", null, deliveryMan);
    }
        
    /**
     * Resets completely the map.
     */
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
    
    /**
     * Removes all the deliveries.
     */
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
    
    /**
     * Get the index of the delivery man responsible of a delivery.
     * @param node the node where the delivery is made
     * @return the index of the delivery man in charge of this delivery, or -1 if there's none
     */
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
