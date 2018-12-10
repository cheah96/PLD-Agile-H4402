package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.DeliveryMan;
import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.model.Node;
import fr.insa.lyon.pld.agile.view.MapViewGraphical;
import fr.insa.lyon.pld.agile.view.Window;
import java.awt.geom.Point2D;

/**
 *
 * @author scheah
 */
public class DeliveryMenGeneratedState extends DeliveriesLoadedState {
    
    public DeliveryMenGeneratedState(MainController controller) {
        super(controller);
    }
    
    @Override
    public void enterState(Window window) {
        window.setStatusMessage("Prêt");
        window.setButtonsState(true, true, true, true, true, true);
    }
    
    @Override
    public void addDelivery(Node node) {
        controller.ADD_DELIVERY_STATE.prepareState(node);
        controller.setCurrentState(controller.ADD_DELIVERY_STATE);
    }
    
    @Override
    public void deleteDelivery(Delivery delivery) {
        controller.getCmdList().addCommand(new CmdRemoveDelivery(controller.getMap(), delivery));
        controller.setCurrentState(controller.DELIVERY_MEN_GENERATED_STATE);
    }
    
    @Override
    public void moveDelivery(Delivery delivery, DeliveryMan oldDeliveryMan, DeliveryMan newDeliveryMan, int oldIndex, int newIndex) {
        controller.getCmdList().addCommand(new CmdMoveDelivery(controller.getMap(), delivery, oldDeliveryMan, newDeliveryMan, oldIndex, newIndex));
        controller.setCurrentState(controller.DELIVERY_MEN_GENERATED_STATE);
    }
    
    @Override
    public void mapClickRight(MapViewGraphical mapView, Point2D p) {
        Node closest = mapView.findClosestNode(p);
        if (closest != null) {
            Map map = controller.getMap();
            int deliveryManIndex = map.getNodeDeliveryManIndex(closest);
            if (deliveryManIndex >= 0) {
                Delivery delivery = map.getDeliveries().get(closest.getId());
                deleteDelivery(delivery);
            }
        }
    }
    
}
