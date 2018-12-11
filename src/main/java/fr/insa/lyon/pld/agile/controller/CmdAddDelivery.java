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
