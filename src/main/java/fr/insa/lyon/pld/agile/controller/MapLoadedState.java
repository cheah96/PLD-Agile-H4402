package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.model.UnreachableDeliveryException;
import fr.insa.lyon.pld.agile.view.Window;
import fr.insa.lyon.pld.agile.xml.XMLAttributeFormatException;
import fr.insa.lyon.pld.agile.xml.XMLMissingAttributeException;
import fr.insa.lyon.pld.agile.xml.XMLMultipleDefinitionOfWarehouseException;
import fr.insa.lyon.pld.agile.xml.XMLParser;
import fr.insa.lyon.pld.agile.xml.XMLUndefinedNodeReferenceException;
import fr.insa.lyon.pld.agile.xml.XMLUnexpectedElementException;
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
    public void loadDeliveriesFile() {
        File selectedFile = controller.getWindow().promptFile("Chargement de demandes de livraison");
        if (selectedFile == null) return;
        controller.resetCmdList();
        
        Map map = controller.getMap();
        map.setDeliveryManCount(0);
        map.clearDeliveries();
        map.clearWarehouse();
        
        boolean success = false;
        try {
            XMLParser.loadDeliveries(map, selectedFile.toPath());
            success = true;
        } catch (XMLAttributeFormatException ex) {
            controller.getWindow().popupError("Attribut " + ex.getAttributeName() + " de valeur non conforme (" + ex.getAttributeValue() + ")");
        } catch (XMLMissingAttributeException ex) {
            controller.getWindow().popupError("Attribut manquant : " + ex.getMissingAttributeName());
        } catch (XMLMultipleDefinitionOfWarehouseException ex) {
            controller.getWindow().popupError("Définitions multiples de l'entrepôt");
        } catch (XMLUndefinedNodeReferenceException ex) {
            controller.getWindow().popupError("Référence à un élément non défini : " + ex.getNodeId());
        } catch (XMLUnexpectedElementException ex) {
            controller.getWindow().popupError("Élément inattendu trouvé : " + ex.getElementName());
        } catch (UnreachableDeliveryException ex) {
            controller.getWindow().popupError("Point de livraison " + ex.getDelivery().getNode().getId() + " inaccessible en aller-retour depuis l'entrepôt");
        } catch (Exception ex) {
            ex.printStackTrace();
            controller.getWindow().popupError("Fichier non conforme.");
        }
        
        if (success)
            controller.setCurrentState(controller.DELIVERIES_LOADED_STATE);
        else {
            map.clearDeliveries();
            map.clearWarehouse();
            controller.setCurrentState(controller.MAP_LOADED_STATE);
        }
    }

}
