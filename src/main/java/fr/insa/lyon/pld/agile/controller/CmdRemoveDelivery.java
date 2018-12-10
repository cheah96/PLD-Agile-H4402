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
    
    public CmdRemoveDelivery(Map map, Delivery delivery){
        this.map = map;
        this.delivery = delivery;
        this.deliveryMan = delivery.getDeliveryMan();
        this.index = this.deliveryMan.getDeliveries().indexOf(delivery);
        System.err.println(this.deliveryMan.getDeliveries());
        System.err.println(this.deliveryMan.getDeliveries().size());
        System.err.println(index);
    }
    
    @Override
    public void doCmd(){
        map.removeDelivery(delivery);
    }

    @Override
    public void undoCmd() {
        map.addDelivery(delivery);
        map.assignDelivery(index, delivery, deliveryMan);
    }
    
}
