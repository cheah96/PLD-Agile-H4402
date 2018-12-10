package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.DeliveryMan;
import fr.insa.lyon.pld.agile.model.Map;

/**
 *
 * @author scheah
 */
public class CmdRemoveDelivery implements Command {
    private Map map;
    private DeliveryMan deliveryMan;
    private int ind;
    private Delivery delivery;
    
    public CmdRemoveDelivery(Map map, Delivery delivery){
        this.map = map;
        this.delivery = delivery;
        this.deliveryMan = delivery.getDeliveryMan();
        this.ind = this.deliveryMan.getDeliveries().indexOf(delivery);
        System.err.println(this.deliveryMan.getDeliveries());
        System.err.println(this.deliveryMan.getDeliveries().size());
        System.err.println(ind);
    }
    
    @Override
    public void doCmd(){
        map.removeDelivery(delivery);
    }

    @Override
    public void undoCmd() {
        map.addDelivery(delivery);
        map.assignDelivery(ind, delivery, deliveryMan);
    }
}
