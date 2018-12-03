package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.XMLParser;
import fr.insa.lyon.pld.agile.model.*;
import fr.insa.lyon.pld.agile.view.Window;
import java.io.File;

/**
 *
 * @author Stanley
 */
public class MainController {
    Map map;
    Window view;

    public MainController(Map map) {
        this.map = map;
        this.view = new Window(map, this);
    }

    public void loadNodesFile() throws Exception {
        File selectedFile = view.askFile("Chargement d'un plan");
        if (selectedFile != null)
        {
            map.clear();
            XMLParser.loadNodes(map, selectedFile.toPath());
        }
    }
    
    public void loadDeliveriesFile() throws Exception {
        File selectedFile = view.askFile("Chargement de demandes de livraison");
        if (selectedFile != null)
        {
            map.clearDeliveries();
            XMLParser.loadDeliveries(map, selectedFile.toPath());
        }
    }
    
    
    public void selectedNode(Node node) {
        view.selectNode(node);
    }
    
    public void selectedDeliveryMan(int deliveryManIndex) {
        view.selectDeliveryMan(deliveryManIndex);
    }
    
}
