package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.XMLParser;
import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.view.Window;
import java.io.File;

/**
 *
 * @author scheah
 */
public class InitialState extends DefaultState {
    @Override
    public void loadMap(MainController controller, Map map, CommandList cmdList, Window view) throws Exception {
        File selectedFile = view.askFile("Chargement d'un plan");
        if (selectedFile != null) {
            map.clear();
            XMLParser.loadMap(map, selectedFile.toPath());
        }
        controller.setCurrentState(controller.MAP_LOADED_STATE);
        cmdList.reset();
    }
}
