package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.view.Window;

/**
 *
 * @author scheah
 */
public class MapLoadedState extends InitialState {

    public MapLoadedState(MainController controller) {
        super(controller);
    }
    
    @Override
    public void enterState(Window window) {
        window.setStatusMessage("Prêt");
        window.setButtonsState(true, true, false, false, false, false);
    }
    
}
