package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.DeliveryMan;
import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.model.Node;
import fr.insa.lyon.pld.agile.view.Window;

/**
 *
 * @author scheah
 */
public class AddDeliveryState extends DefaultState {
    private Node node;
    private int duration; 
    
    public AddDeliveryState(MainController controller) {
        super(controller);
    }
    
    protected void prepareState(Map map, Node node) {
        this.node = node;
    }
    @Override
    public void enterState(Window window) {
        window.setStatusMessage("Ajout d'un point de livraison");
        window.setButtonsState(false, false, false, false, false, false);
    }
    
    @Override
    public void validateAddDelivery(Map map, DeliveryMan deliveryMan, int ind, CommandList cmdList) {
        Delivery toBeAdded = new Delivery(node, duration, deliveryMan);
        cmdList.addCommand(new CmdAddDelivery(map, toBeAdded, deliveryMan, ind));
        controller.setCurrentState(controller.DELIVERY_MEN_GENERATED_STATE);
    }
    
    @Override
    public void cancelAddDelivery() {
        controller.setCurrentState(controller.DELIVERY_MEN_GENERATED_STATE);
    }
    
}
