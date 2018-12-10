package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.view.Window;

/**
 *
 * @author scheah
 */
public class DeliveriesLoadedState extends MapLoadedState { 
    
    public DeliveriesLoadedState(MainController controller) {
        super(controller);
    }
    
    @Override
    public void enterState(Window window) {
        window.setStatusMessage("Prêt");
        window.setButtonsState(true, true, true, false, false, false);
    }
    
    @Override
    public void generateDeliveryMen(int deliveryMenCount)
    {
        Map map = controller.getMap();
        CommandList cmdList = controller.getCmdList();
        
        System.out.println("Génération avec " + deliveryMenCount + " livreurs.");
        map.setDeliveryManCount(deliveryMenCount);
        System.out.println("Distribution des livraisons...");
        map.distributeDeliveries();
        System.out.println("Raccourcissement des livraisons...");
        map.shortenDeliveriesInBackground();
        cmdList.reset();
        
        controller.setCurrentState(controller.DELIVERY_MEN_COMPUTING_STATE);
    }
    
}
