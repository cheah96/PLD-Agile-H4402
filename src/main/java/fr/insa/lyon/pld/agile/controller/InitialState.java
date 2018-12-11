package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.view.Window;
import fr.insa.lyon.pld.agile.xml.XMLParser;
import java.io.File;

/**
 *
 * @author scheah
 */
public class InitialState extends DefaultState {

    public InitialState(MainController controller) {
        super(controller);
    }
    
    @Override
    public void enterState() {
        Window window = controller.getWindow();
        window.setStatusMessage("Prêt");
        window.setButtonsState(true, false, false, false);
    }

    @Override
    public void loadMap() throws Exception {
        File selectedFile = controller.getWindow().promptFile("Chargement d'un plan");
        if (selectedFile == null) return;
        controller.resetCmdList();
        controller.getMap().clear();
        XMLParser.loadMap(controller.getMap(), selectedFile.toPath());
        controller.setCurrentState(controller.MAP_LOADED_STATE);
    }

}
