package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.DeliveryMan;
import fr.insa.lyon.pld.agile.model.Node;
import fr.insa.lyon.pld.agile.view.MapViewGraphical;
import fr.insa.lyon.pld.agile.view.Window;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 *
 * @author scheah
 */
public abstract class DefaultState implements State {
    
    public final MainController controller;
    public DefaultState(MainController controller) {
        this.controller = controller;
    }
    
    @Override
    public void enterState() {
        Window window = controller.getWindow();
        window.clearStatus();
        window.setButtonsState(true, true, true, true);
    }

    @Override
    public void loadMap() throws Exception { }

    @Override
    public void loadDeliveriesFile() throws Exception { }
    
    @Override
    public void addDelivery(Node node, DeliveryMan deliveryMan, int index) {}
    @Override
    public void validateAddDelivery(DeliveryMan deliveryMan, int index) {}
    @Override
    public void cancelAddDelivery() {}
    @Override
    public void deleteDelivery(Delivery delivery) {}
    @Override
    public void assignDelivery(Delivery delivery, DeliveryMan newDeliveryMan, int newIndex) {}
    @Override
    public void unassignDelivery(Delivery delivery) {}
    @Override
    public void generateDeliveryMen(int deliveryMenCount) {}
    
    
    @Override
    public void mapClick(MouseEvent event, MapViewGraphical mapView) {
        if (event.getButton() == MouseEvent.BUTTON1) {
            Point2D p = mapView.getPixelToPoint(event.getX(), event.getY());
            Node closest = mapView.findClosestNode(p);
            controller.selectNode(closest);
        }
    }

    @Override
    public void selectNode(Node node) {
        controller.getWindow().selectNode(node);
    }

    @Override
    public void selectDeliveryMan(int deliveryManIndex) {
        controller.getWindow().selectDeliveryMan(deliveryManIndex);
    }
    
    @Override
    public void btnStatusClick() { }
    @Override
    public void keyEscape() { }
    @Override
    public void handleExternalEvent(String eventName, Object value) { }
    
}
