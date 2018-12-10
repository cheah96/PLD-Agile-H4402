package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.view.Window;

/**
 *
 * @author scheah
 */
public class DeliveryMenComputingState extends DefaultState {

    public DeliveryMenComputingState(MainController controller) {
        super(controller);
    }
    
    @Override
    public void enterState(Window window) {
        window.setStatusMessage("Génération des tournées en cours...");
        window.setButtonsState(false, false, true, false, false, false);
    }
    
    @Override
    public void stopGeneration(Map map)
    {
        System.err.println("Arrêt des calculs...");
        map.stopShorteningDeliveries();
        controller.setCurrentState(controller.DELIVERY_MEN_GENERATED_STATE);
    }
    
    @Override
    public void generationFinished(Map map)
    {
        controller.setCurrentState(controller.DELIVERY_MEN_GENERATED_STATE);
    }
}
