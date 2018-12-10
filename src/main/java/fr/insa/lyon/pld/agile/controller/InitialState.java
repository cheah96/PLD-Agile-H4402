package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.view.Window;

/**
 *
 * @author scheah
 */
public class InitialState extends DefaultState {

    public InitialState(MainController controller) {
        super(controller);
    }
    
    @Override
    public void enterState(Window window) {
        window.setStatusMessage("Prêt");
        window.setButtonsState(true, false, false, false, false, false);
    }
    
}
