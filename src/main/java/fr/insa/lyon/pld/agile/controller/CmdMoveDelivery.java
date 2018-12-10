package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.DeliveryMan;
import fr.insa.lyon.pld.agile.model.Map;

/**
 *
 * @author scheah
 */
public class CmdMoveDelivery implements Command {
    Map map;
    private Delivery delivery;
    private DeliveryMan oldDeliveryMan;
    private DeliveryMan newDeliveryMan;
    private int oldIndice;
    private int newIndice;
    
    public CmdMoveDelivery(Map map, Delivery delivery, DeliveryMan oldDeliveryMan, DeliveryMan newDeliveryMan, int oldIndice, int newIndice){
        this.map = map;
        this.delivery = delivery;
        this.oldDeliveryMan = oldDeliveryMan;
        this.newDeliveryMan = newDeliveryMan;
        this.oldIndice = oldIndice;
        this.newIndice = newIndice;
    }
    
    @Override
    public void doCmd(){
        map.unassignDelivery(oldIndice, oldDeliveryMan);
        map.assignDelivery(newIndice, delivery, newDeliveryMan);
    }

    @Override
    public void undoCmd() {
        map.unassignDelivery(newIndice, newDeliveryMan);
        map.assignDelivery(oldIndice, delivery, oldDeliveryMan);
    }
}
