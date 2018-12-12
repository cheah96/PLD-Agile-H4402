package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.DeliveryMan;
import fr.insa.lyon.pld.agile.model.Map;

/**
 *
 * @author scheah
 */
public class CmdRemoveDelivery implements Command {
    private final Map map;
    private final Delivery delivery;
    private final DeliveryMan deliveryMan;
    private final int index;
    
    /**
     * Remove a delivery request from the map using the design pattern Command. 
     * The delivery request is unassigned from its delivery man before it is removed.
     * 
     * @param map the map instance of the application
     * @param delivery the delivery request to be removed
     */
    public CmdRemoveDelivery(Map map, Delivery delivery){
        this.map = map;
        this.delivery = delivery;
        this.deliveryMan = delivery.getDeliveryMan();
        if(this.deliveryMan != null) {
            this.index = this.deliveryMan.getDeliveries().indexOf(delivery);
        }
        else {
            index = -1;
        }
    }
    
    @Override
    public void doCmd(){
        map.removeDelivery(delivery);
    }

    @Override
    public void undoCmd() {
        map.addDelivery(delivery);
        if(this.deliveryMan != null) {
            map.assignDelivery(index, delivery, deliveryMan);
        }
    }
}
