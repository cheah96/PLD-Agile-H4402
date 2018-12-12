package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.DeliveryMan;
import fr.insa.lyon.pld.agile.model.Map;

/**
 *
 * @author scheah
 */
public class CmdAddDelivery implements Command {
    private final Map map;
    private final Delivery delivery;
    private final DeliveryMan deliveryMan;
    private final int index;
    
    /**
     * Add a new delivery request into the existing map and assign it to a delivery man using the design pattern Command
     * 
     * @param map number of nodes in the TSP graph
     * @param delivery edgesCosts[i][j] is the time spent to travel from Node i to Node j, such that 0 <= i < nodes and 0 <= j < nodes
     * @param nodesCost nodesCosts[i] is the time spent visiting node i, such that 0 <= i < nodes
     */
    public CmdAddDelivery(Map map, Delivery delivery, DeliveryMan deliveryMan, int index) {
        this.map = map;
        this.delivery = delivery;
        this.deliveryMan = deliveryMan;
        this.index = index;
    }
    
    @Override
    public void doCmd(){
        map.addDelivery(delivery);
        if( deliveryMan != null) {
            map.assignDelivery(index, delivery, deliveryMan);
        }
    }

    @Override
    public void undoCmd() {
        if( deliveryMan != null) {
            map.unassignDelivery(index, deliveryMan);
        }
        map.removeDelivery(delivery);
    }
    
}
