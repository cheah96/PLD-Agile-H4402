package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.view.Window;

public class DeliveriesLoadedState extends MapLoadedState { 
    
	/**
     * constructs the state of deliveries loaded 
     *@param controller the main controller
     */
    public DeliveriesLoadedState(MainController controller) {
        super(controller);
    }
    
    @Override
    public void enterState() {
        Window window = controller.getWindow();
        window.setStatusMessage("Prêt");
        window.setButtonsState(true, true, true, false);
    }
    
    @Override
    public void generateDeliveryMen(int deliveryMenCount)
    {
        Map map = controller.getMap();
        
        System.out.println("Génération avec " + deliveryMenCount + " livreurs.");
        map.setDeliveryManCount(deliveryMenCount);
        System.out.println("Distribution des livraisons...");
        map.distributeDeliveries();
        System.out.println("Raccourcissement des livraisons...");
        map.shortenDeliveriesInBackground();
        controller.resetCmdList();
        
        controller.setCurrentState(controller.DELIVERY_MEN_COMPUTING_STATE);
    }
    
}
