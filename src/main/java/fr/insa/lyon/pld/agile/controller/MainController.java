package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.XMLParser;
import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.model.Node;
import fr.insa.lyon.pld.agile.view.Window;
import java.awt.geom.Point2D;
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

    public void loadNodesFile() throws IOException, SAXException, ParserConfigurationException {
        File selectedFile = view.askFile("Chargement d'un plan");
        if (selectedFile != null)
        {
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            map.clear();
            XMLParser.loadNodes(map, selectedFile.toPath());
        }
    }
    
    public void loadDeliveriesFile() throws IOException, SAXException, ParserConfigurationException {
        File selectedFile = view.askFile("Chargement de demandes de livraison");
        if (selectedFile != null)
        {
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());
            XMLParser.loadDeliveries(map, selectedFile.toPath());
        }
    }
    
    public void showDeliveryManRound(int deliveryManIndex) {
        view.showDeliveryManRound(deliveryManIndex);
    }
}
