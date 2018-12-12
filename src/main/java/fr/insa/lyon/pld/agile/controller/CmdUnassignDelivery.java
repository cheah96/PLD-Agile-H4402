package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.DeliveryMan;
import fr.insa.lyon.pld.agile.model.Map;

/**
 *
 * @author scheah
 */
public class CmdUnassignDelivery implements Command {
    private final Map map;
    private final Delivery delivery;
    private final DeliveryMan oldDeliveryMan;
    private final int oldIndex;
    
    /**
     * Unassigned a delivery request using the design pattern Command. The 
     * delivery request must be already assigned to a delivery man. The delivery
     * request is not removed from the map.
     * 
     * @param map the map instance of the application
     * @param delivery the delivery request to be unassigned    
     */
    public CmdUnassignDelivery(Map map, Delivery delivery) {
        this.map = map;
        this.delivery = delivery;
        this.oldDeliveryMan = delivery.getDeliveryMan();
        if (oldDeliveryMan != null) {
            this.oldIndex = oldDeliveryMan.getDeliveries().indexOf(this.delivery);
        } else {
            this.oldIndex = -1;
        }
    }
    
    @Override
    public void doCmd(){
        if(oldDeliveryMan != null) {
            map.unassignDelivery(oldIndex, oldDeliveryMan);
        }
    }

    @Override
    public void undoCmd() {
        if(oldDeliveryMan != null) {
            map.assignDelivery(oldIndex, delivery, oldDeliveryMan);
        }
    }
}
