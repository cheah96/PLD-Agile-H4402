package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.DeliveryMan;
import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.model.Node;
import fr.insa.lyon.pld.agile.view.Window;

public class AddDeliveryState extends DefaultState {
    private Node node;
    private int duration; 
    
    public AddDeliveryState(MainController controller) {
        super(controller);
    }
    
    protected void prepareState(Node node) {
        this.node = node;
    }
    
    @Override
    public void enterState() {
        Window window = controller.getWindow();
        window.setStatusMessage("Ajout d'un point de livraison");
        window.setStatusButton("Annuler");
        window.setButtonsState(false, false, false, false);
    }
    
    @Override
    public void btnStatusClick() {
        cancelAddDelivery();
    }
    
    @Override
    public void keyEscape() {
        cancelAddDelivery();
    }
    
    @Override
    public void validateAddDelivery(DeliveryMan deliveryMan, int index) {
        Map map = controller.getMap();
        
        Delivery toBeAdded = new Delivery(node, duration, deliveryMan);
        controller.doCmd(new CmdAddDelivery(map, toBeAdded, deliveryMan, index));
        controller.setCurrentState(controller.DELIVERY_MEN_GENERATED_STATE);
    }
    
    @Override
    public void cancelAddDelivery() {
        controller.setCurrentState(controller.DELIVERY_MEN_GENERATED_STATE);
    }
    
}
