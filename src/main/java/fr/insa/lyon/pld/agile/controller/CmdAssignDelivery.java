package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.DeliveryMan;
import fr.insa.lyon.pld.agile.model.Map;

/**
 *
 * @author scheah
 */
public class CmdAssignDelivery implements Command {
    private final Map map;
    private final Delivery delivery;
    private final DeliveryMan oldDeliveryMan;
    private final DeliveryMan newDeliveryMan;
    private final int oldIndex;
    private final int newIndex;
    
    public CmdAssignDelivery(Map map, Delivery delivery, DeliveryMan newDeliveryMan, int newIndex) {
        this.map = map;
        this.delivery = delivery;
        this.oldDeliveryMan = delivery.getDeliveryMan();
        this.newDeliveryMan = newDeliveryMan;
        if (oldDeliveryMan != null) {
            this.oldIndex = oldDeliveryMan.getDeliveries().indexOf(this.delivery);
        } else {
            this.oldIndex = -1;
        }
        this.newIndex = newIndex;
    }
    
    @Override
    public void doCmd(){
        if(oldDeliveryMan != null) {
            map.unassignDelivery(oldIndex, oldDeliveryMan);
        }
        map.assignDelivery(newIndex, delivery, newDeliveryMan);
    }

    @Override
    public void undoCmd() {
        map.unassignDelivery(newIndex, newDeliveryMan);
        if(oldDeliveryMan != null) {
            map.assignDelivery(oldIndex, delivery, oldDeliveryMan);
        }
    }
}
