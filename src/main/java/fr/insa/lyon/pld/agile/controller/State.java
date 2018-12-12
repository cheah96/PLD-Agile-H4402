package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.*;

import fr.insa.lyon.pld.agile.view.MapViewGraphical;
import java.awt.event.MouseEvent;

/**
 *
 * @author scheah
 */
public interface State {
    
    public void enterState();
    
    public void loadMap();
    
    public void loadDeliveriesFile();
    
    public void addDelivery(Node node, DeliveryMan deliveryMan, int index);
    
    public void validateAddDelivery(DeliveryMan deliveryMan, int index);
    
    public void cancelAddDelivery();
    
    public void deleteDelivery(Delivery delivery);
    
    public void assignDelivery(Delivery delivery, DeliveryMan newDeliveryMan, int newIndex);
    
    public void unassignDelivery(Delivery delivery);
    
    public void generateDeliveryMen(int deliveryMenCount);
    
    public void mapClick(MouseEvent event, MapViewGraphical mapView);
    
    public void selectNode(Node node);
    
    public void selectDeliveryMan(int deliveryManIndex);
    
    public void btnStatusClick();
    
    public void keyEscape();
    
    public void handleExternalEvent(String eventName, Object value);
    
}
