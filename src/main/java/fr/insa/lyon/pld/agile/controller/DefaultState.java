package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.DeliveryMan;
import fr.insa.lyon.pld.agile.model.Node;
import fr.insa.lyon.pld.agile.view.MapViewGraphical;
import fr.insa.lyon.pld.agile.view.Window;
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
        window.setButtonsState(true, true, true, true, true, true);
    }

    @Override
    public void loadMap() throws Exception { }

    @Override
    public void loadDeliveriesFile() throws Exception { }
    
    @Override
    public void addDelivery(Node node) {}
    @Override
    public void validateAddDelivery(DeliveryMan deliveryMan, int index) {}
    @Override
    public void cancelAddDelivery() {}
    @Override
    public void deleteDelivery(Delivery delivery) {}
    @Override
    public void moveDelivery(Delivery delivery, DeliveryMan oldDeliveryMan, DeliveryMan newDeliveryMan, int oldIndex, int newIndex) {}
    @Override
    public void generateDeliveryMen(int deliveryMenCount) {}
    
    
    @Override
    public void mapClickLeft(MapViewGraphical mapView, Point2D coords) {
        mapView.selectNode(mapView.findClosestNode(coords));
    }
    
    @Override
    public void mapClickRight(MapViewGraphical mapView, Point2D coords) { }

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
