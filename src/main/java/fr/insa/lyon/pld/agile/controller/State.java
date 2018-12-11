package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.*;

import fr.insa.lyon.pld.agile.view.Window;
import fr.insa.lyon.pld.agile.view.MapViewGraphical;
import java.awt.geom.Point2D;

/**
 *
 * @author scheah
 */
public interface State {
    
    public void enterState();
    
    public void loadMap() throws Exception;
    
    public void loadDeliveriesFile() throws Exception;
    
    public void addDelivery(Node node);
    
    public void validateAddDelivery(DeliveryMan deliveryMan, int index);
    
    public void cancelAddDelivery();
    
    public void deleteDelivery(Delivery delivery);
    
    public void moveDelivery(Delivery delivery, DeliveryMan oldDeliveryMan, DeliveryMan newDeliveryMan, int oldIndex, int newIndex);
    
    public void generateDeliveryMen(int deliveryMenCount);
    
    public void mapClickLeft(MapViewGraphical mapView, Point2D coords);
    
    public void mapClickRight(MapViewGraphical mapView, Point2D coords);
    
    public void selectNode(Node node);
    
    public void selectDeliveryMan(int deliveryManIndex);
    
    public void btnStatusClick();
    
    public void keyEscape();
    
    public void handleExternalEvent(String eventName, Object value);
    
}
