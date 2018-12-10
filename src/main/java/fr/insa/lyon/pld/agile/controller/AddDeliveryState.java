package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.DeliveryMan;
import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.model.Node;
import fr.insa.lyon.pld.agile.view.Window;
import java.awt.geom.Point2D;

/**
 *
 * @author scheah
 */
public class AddDeliveryState extends DefaultState {
    private Node node;
    private int duration; 
    
    @Override
    public void enterState(Window window) {
        window.setStatusMessage("Ajout d'un point de livraison");
    }
    
    @Override
    public void validateAddDelivery(MainController controller, Map map, DeliveryMan deliveryMan, int ind, CommandList cmdList) {
        Delivery toBeAdded = new Delivery(node, duration, deliveryMan);
        cmdList.addCommand(new CmdAddDelivery(map, toBeAdded, deliveryMan, ind));
        controller.setCurrentState(controller.DELIVERY_MEN_GENERATED_STATE);
    }
    
    @Override
    public void cancelAddDelivery(MainController controller) {
        controller.setCurrentState(controller.DELIVERY_MEN_GENERATED_STATE);
    }
    
    protected void enterAction(Map map, Node node) {
        this.node = node;
    }
    
}
