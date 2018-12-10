package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.DeliveryMan;
import fr.insa.lyon.pld.agile.model.Map;

/**
 *
 * @author scheah
 */
public class CmdMoveDelivery implements Command {
    private final Map map;
    private final Delivery delivery;
    private final DeliveryMan oldDeliveryMan;
    private final DeliveryMan newDeliveryMan;
    private final int oldIndex;
    private final int newIndex;
    
    public CmdMoveDelivery(Map map, Delivery delivery, DeliveryMan oldDeliveryMan, DeliveryMan newDeliveryMan, int oldIndex, int newIndex) {
        this.map = map;
        this.delivery = delivery;
        this.oldDeliveryMan = oldDeliveryMan;
        this.newDeliveryMan = newDeliveryMan;
        this.oldIndex = oldIndex;
        this.newIndex = newIndex;
    }
    
    @Override
    public void doCmd(){
        map.unassignDelivery(oldIndex, oldDeliveryMan);
        map.assignDelivery(newIndex, delivery, newDeliveryMan);
    }

    @Override
    public void undoCmd() {
        map.unassignDelivery(newIndex, newDeliveryMan);
        map.assignDelivery(oldIndex, delivery, oldDeliveryMan);
    }
    
}
