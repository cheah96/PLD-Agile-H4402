package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.DeliveryMan;
import fr.insa.lyon.pld.agile.model.Map;

/**
 *
 * @author scheah
 */
public class CmdAddDelivery implements Command {
    private Map map;
    private Delivery delivery;
    private DeliveryMan deliveryMan;
    private int ind;
    
    public CmdAddDelivery(Map map, Delivery delivery, DeliveryMan deliveryMan, int ind){
        this.map = map;
        this.delivery = delivery;
        this.deliveryMan = deliveryMan;
        this.ind = ind;
    }
    
    @Override
    public void doCmd(){
        map.addDelivery(delivery);
        map.assignDelivery(ind, delivery, deliveryMan);
    }

    @Override
    public void undoCmd() {
        map.unassignDelivery(ind,deliveryMan);
        map.removeDelivery(delivery);
    }
}
