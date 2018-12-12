package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.view.Window;
import fr.insa.lyon.pld.agile.xml.XMLParser;
import java.io.File;

/**
 *
 * @author scheah
 */
public class MapLoadedState extends InitialState {

    public MapLoadedState(MainController controller) {
        super(controller);
    }
    
    @Override
    public void enterState() {
        Window window = controller.getWindow();
        window.setStatusMessage("Prêt");
        window.setButtonsState(true, true, false, false);
    }

    @Override
    public void loadDeliveriesFile() throws Exception {
        File selectedFile = controller.getWindow().promptFile("Chargement de demandes de livraison");
        if (selectedFile == null) return;
        controller.resetCmdList();
        
        Map map = controller.getMap();
        map.setDeliveryManCount(0);
        map.clearDeliveries();
        map.clearWarehouse();
        try {
            XMLParser.loadDeliveries(map, selectedFile.toPath());
        } catch (Exception e) {
            map.clearDeliveries();
            map.clearWarehouse();
            throw e;
        }
        controller.setCurrentState(controller.DELIVERIES_LOADED_STATE);
    }

}
