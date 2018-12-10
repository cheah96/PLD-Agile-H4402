package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.view.Window;

/**
 *
 * @author scheah
 */
public class DeliveryMenComputingState extends DefaultState { // extends DefaultState {
    
    @Override
    public void enterState(Window window) {
        window.setStatusMessage("Génération des tournées en cours...");
    }
    
    @Override
    public void stopGeneration(MainController controller, Map map)
    {
        System.err.println("Arrêt des calculs...");
        map.stopShorteningDeliveries();
        controller.setCurrentState(controller.DELIVERY_MEN_GENERATED_STATE);
    }
    
    @Override
    public void generationFinished(MainController controller, Map map)
    {
        controller.setCurrentState(controller.DELIVERY_MEN_GENERATED_STATE);
    }
}
