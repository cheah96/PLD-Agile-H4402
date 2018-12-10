package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.xml.XMLParser;
import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.DeliveryMan;
import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.model.Node;
import fr.insa.lyon.pld.agile.view.Window;
import java.awt.geom.Point2D;
import java.io.File;

/**
 *
 * @author scheah
 */
public class DeliveryMenGeneratedState extends DeliveriesLoadedState {
    @Override
    public void addDelivery(MainController controller, Map map, Node node) {
        controller.ADD_DELIVERY_STATE.enterAction(map, node);
        controller.setCurrentState(controller.ADD_DELIVERY_STATE);
    }
    
    @Override
    public void deleteDelivery(MainController controller, Map map, DeliveryMan deliveryMan, int ind, CommandList cmdList) {
        cmdList.addCommand(new CmdRemoveDelivery(map, deliveryMan, ind));
        controller.setCurrentState(controller.DELIVERY_MEN_GENERATED_STATE);
    }
    
    @Override
    public void moveDelivery(MainController controller, Map map, Delivery delivery, DeliveryMan oldDeliveryMan, DeliveryMan newDeliveryMan, int oldIndice, int newIndice, CommandList cmdList) {
        cmdList.addCommand(new CmdMoveDelivery(map, delivery, oldDeliveryMan, newDeliveryMan, oldIndice, newIndice));
        controller.setCurrentState(controller.DELIVERY_MEN_GENERATED_STATE);
    }
    
    @Override
    public void rightClick(MainController controller, Map map, CommandList cmdList, Window view, Point2D p) {
        Node closest = selectNode(controller, map, cmdList, view, p);
        if(closest != null) {
            //view.showOptionsNode(closest);
        }
    }
    
    @Override
    public void undo(CommandList cmdList) {
        cmdList.undo();
    }
    @Override
    public void redo(CommandList cmdList) {
        cmdList.redo();
    }
}
