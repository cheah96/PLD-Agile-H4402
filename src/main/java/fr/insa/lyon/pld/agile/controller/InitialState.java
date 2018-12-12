package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.view.Window;
import fr.insa.lyon.pld.agile.xml.XMLAttributeFormatException;
import fr.insa.lyon.pld.agile.xml.XMLDuplicateNodeException;
import fr.insa.lyon.pld.agile.xml.XMLMissingAttributeException;
import fr.insa.lyon.pld.agile.xml.XMLParser;
import fr.insa.lyon.pld.agile.xml.XMLUndefinedNodeReferenceException;
import fr.insa.lyon.pld.agile.xml.XMLUnexpectedElementException;
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
    public void loadMap() {
        File selectedFile = controller.getWindow().promptFile("Chargement d'un plan");
        if (selectedFile == null) return;
        controller.resetCmdList();
        controller.getMap().clear();
        
        boolean success = false;
        try {
            XMLParser.loadMap(controller.getMap(), selectedFile.toPath());
            success = true;
        } catch (XMLAttributeFormatException ex) {
            controller.getWindow().popupError("Attribut " + ex.getAttributeName() + " de valeur non conforme (" + ex.getAttributeValue() + ")");
        } catch (XMLMissingAttributeException ex) {
            controller.getWindow().popupError("Attribut manquant : " + ex.getMissingAttributeName());
        } catch (XMLDuplicateNodeException ex) {
            controller.getWindow().popupError("Définitions multiples d'un même élément : " + ex.getNodeId());
        } catch (XMLUndefinedNodeReferenceException ex) {
            controller.getWindow().popupError("Référence à un élément non défini : " + ex.getNodeId());
        } catch (XMLUnexpectedElementException ex) {
            controller.getWindow().popupError("Elément inattendu trouvé : " + ex.getElementName());
        } catch (Exception ex) {
            ex.printStackTrace();
            controller.getWindow().popupError("Fichier non conforme.");
        }
        
        if (success)
            controller.setCurrentState(controller.MAP_LOADED_STATE);
        else {
            controller.getMap().clear();
            controller.setCurrentState(controller.INITIAL_STATE);
        }
    }

}
