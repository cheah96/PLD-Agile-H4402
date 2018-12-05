package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.XMLParser;
import fr.insa.lyon.pld.agile.model.*;
import fr.insa.lyon.pld.agile.view.Window;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

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

    public void loadMapFile() throws IOException, SAXException, ParserConfigurationException {
        File selectedFile = view.askFile("Chargement d'un plan");
        if (selectedFile != null)
        {
            map.clear();
            XMLParser.loadMap(map, selectedFile.toPath());
        }
    }
    
    public void loadDeliveriesFile() throws IOException, SAXException, ParserConfigurationException {
        File selectedFile = view.askFile("Chargement de demandes de livraison");
        if (selectedFile != null)
        {
            map.clearDeliveries();
            map.setDeliveryManCount(0);
            XMLParser.loadDeliveries(map, selectedFile.toPath());
        }
    }
    
    
    public void selectedNode(Node node) {
        view.selectNode(node);
    }
    
    public void selectedDeliveryMan(int deliveryManIndex) {
        view.selectDeliveryMan(deliveryManIndex);
    }
    
    public void generateDeliveryMen(int deliveryMenCount) {
        System.err.println("Génération avec " + deliveryMenCount + " livreurs.");
        map.setDeliveryManCount(deliveryMenCount);
        System.err.println("Distribution des livraisons...");
        map.distributeDeliveries();
        System.err.println("Raccourcissement des livraisons...");
        map.shortenDeliveries();
    }
}
