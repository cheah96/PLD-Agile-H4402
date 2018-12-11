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
public class DeliveryMenGeneratedState extends DeliveriesLoadedState {
    
    public DeliveryMenGeneratedState(MainController controller) {
        super(controller);
    }
    
    @Override
    public void enterState() {
        Window window = controller.getWindow();
        window.setStatusMessage("Prêt");
        window.setButtonsState(true, true, true, true);
    }
    
    @Override
    public void addDelivery(Node node, DeliveryMan deliveryMan, int index) {
        Delivery delivery = new Delivery(node, index, deliveryMan);
        controller.doCmd(new CmdAddDelivery(controller.getMap(), delivery, deliveryMan, index));
        controller.setCurrentState(controller.DELIVERY_MEN_GENERATED_STATE);
    }
    
    @Override
    public void deleteDelivery(Delivery delivery) {
        controller.doCmd(new CmdRemoveDelivery(controller.getMap(), delivery));
        controller.setCurrentState(controller.DELIVERY_MEN_GENERATED_STATE);
    }
    
    @Override
    public void assignDelivery(Delivery delivery, DeliveryMan newDeliveryMan, int newIndex) {
        controller.doCmd(new CmdAssignDelivery(controller.getMap(), delivery, newDeliveryMan, newIndex));
        controller.setCurrentState(controller.DELIVERY_MEN_GENERATED_STATE);
    }
    
    @Override
    public void unassignDelivery(Delivery delivery) {
        controller.doCmd(new CmdUnassignDelivery(controller.getMap(), delivery));
        controller.setCurrentState(controller.DELIVERY_MEN_GENERATED_STATE);
    }
    
    @Override
    public void mapClickRight(MapViewGraphical mapView, Point2D p) {
        Node closest = mapView.findClosestNode(p);
        mapView.selectNode(closest);
        if(controller.getMap().getNodeDeliveryManIndex(closest) == -1 && controller.getMap().getDeliveries().get(closest.getId()) == null) {
            mapView.showPopupNode(p);
        } else if (controller.getMap().getNodeDeliveryManIndex(closest) == -1){
            mapView.showPopupUnassignedDelivery(p);
        }else {
            mapView.showPopupDelivery(p);
        }
    }
    
}
