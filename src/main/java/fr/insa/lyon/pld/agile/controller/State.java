package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.DeliveryMan;
import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.model.Node;
import fr.insa.lyon.pld.agile.view.Window;
import java.awt.geom.Point2D;

/**
 *
 * @author scheah
 */
public interface State {
    
    public void enterState(Window window);
    
    public void addDelivery(MainController controller, Map map, Node node);
    
    public void validateAddDelivery(MainController controller, Map map, DeliveryMan deliveryMan, int ind, CommandList cmdList);
    
    public void cancelAddDelivery(MainController controller);
    
    public void deleteDelivery(MainController controller, Map map,Delivery delivery, CommandList cmdList);
    
    public void moveDelivery(MainController controller, Map map, Delivery delivery, DeliveryMan oldDeliveryMan, DeliveryMan newDeliveryMan, int oldIndice, int newIndice, CommandList cmdList);
    
    public void generateDeliveryMen(MainController controller, Map map, int deliveryMenCount, CommandList cmdList);
    
    public void stopGeneration(MainController controller, Map map);
    
    public void generationFinished(MainController controller, Map map);
    
    public void undo(CommandList cmdList);
    
    public void redo(CommandList cmdList);
    
    public void leftClick(MainController controller, Map map, CommandList cmdList, Window view, Point2D p);
    
    public void rightClick(MainController controller, Map map, CommandList cmdList, Window view, Point2D p);
    
    public void loadMap(MainController controller, Map map, CommandList cmdList, Window view) throws Exception;
    
    public void loadDeliveriesFile(MainController controller, Map map, CommandList cmdList, Window view) throws Exception;
    
}
