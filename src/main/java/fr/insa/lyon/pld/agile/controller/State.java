package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.*;

import fr.insa.lyon.pld.agile.view.MapViewGraphical;
import java.awt.event.MouseEvent;

/**
 *
 * @author scheah, nmesnard
 */
public interface State {
    
    /**
     * Sets up the window accordingly to the state.
     */
    public void enterState();
    
    /**
     * Trigered when the "Open Map" button is clicked.
     */
    public void loadMap();
    
    /**
     * Trigered when the "Open Deliveries file" button is clicked.
     */
    public void loadDeliveriesFile();
    
    /**
     * Triggered when there's a request to add a delivery.
     * @param node the node corresponding to the delivery's location
     * @param deliveryMan the delivery man to add the delivery to
     * @param index the index of the delivery to add this node before
     */
    public void addDelivery(Node node, DeliveryMan deliveryMan, int index);
    
    /**
     * Trigered when the request of adding a delivery is confirmed.
     * @param deliveryMan the delivery man to add the delivery to
     * @param index the index the node to add the delivery before
     */
    public void validateAddDelivery(DeliveryMan deliveryMan, int index);
    
    /**
     * Trigered when the request of adding a delivery is cancelled.
     */
    public void cancelAddDelivery();
    
    /**
     * Triggered when there's a request to remove a delivery.
     * @param delivery the delivery to remove
     */
    public void deleteDelivery(Delivery delivery);
    
    /**
     * Triggered when there's a request to assign a delivery to a delivery man.
     * @param delivery the delivery to assign
     * @param newDeliveryMan the delivery man to assign the delivery to
     * @param newIndex the index of the delivery from the delivery man's deliveries list
     */
    public void assignDelivery(Delivery delivery, DeliveryMan newDeliveryMan, int newIndex);
    
    /**
     * Triggered when there's a request to unassign a delivery of a delivery man.
     * @param delivery the delivery to unassign
     */
    public void unassignDelivery(Delivery delivery);
    
    /**
     * Triggered when there's a request to generate the delivery men's courses.
     * @param deliveryMenCount
     */
    public void generateDeliveryMen(int deliveryMenCount);
    
    /**
     * Triggered when the map is clicked.
     * @param event the MouseEvent
     * @param mapView the GraphicalMapView clicked
     */
    public void mapClick(MouseEvent event, MapViewGraphical mapView);
    
    /**
     * Triggered when a node is selected.
     * @param node the selected node
     */
    public void selectNode(Node node);
    
    /**
     * Triggered when a delivery man is selected.
     * @param deliveryManIndex the selected delivery man's index
     */
    public void selectDeliveryMan(int deliveryManIndex);
    
    /**
     * Triggered when the status button is clicked.
     */
    public void btnStatusClick();
    
    /**
     * Triggered when the escape key is pressed.
     */
    public void keyEscape();
    
    /**
     * Trigerred when an external event occurs.
     * @param eventName the name of the event
     * @param value the object related to the event
     */
    public void handleExternalEvent(String eventName, Object value);
    
}
