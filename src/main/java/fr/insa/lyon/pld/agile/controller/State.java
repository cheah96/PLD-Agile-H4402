package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.DeliveryMan;
import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.model.Node;
import fr.insa.lyon.pld.agile.view.Window;
import fr.insa.lyon.pld.agile.view.MapViewGraphical;
import java.awt.geom.Point2D;

/**
 *
 * @author scheah
 */
public interface State {
    
    public void enterState(Window window);
    
    
    public void addDelivery(Map map, Node node);
    
    public void validateAddDelivery(Map map, DeliveryMan deliveryMan, int index, CommandList cmdList);
    
    public void cancelAddDelivery();
    
    public void deleteDelivery(Map map, Delivery delivery, CommandList cmdList);
    
    public void moveDelivery(Map map, Delivery delivery, DeliveryMan oldDeliveryMan, DeliveryMan newDeliveryMan, int oldIndex, int newIndex, CommandList cmdList);
    
    public void generateDeliveryMen(Map map, int deliveryMenCount, CommandList cmdList);
    
    public void stopGeneration(Map map);
    
    public void generationFinished(Map map);
    
    
    public void mapClickLeft(Map map, CommandList cmdList, MapViewGraphical mapView, Point2D p);
    
    public void mapClickRight(Map map, CommandList cmdList, MapViewGraphical mapView, Point2D p);
    
}
