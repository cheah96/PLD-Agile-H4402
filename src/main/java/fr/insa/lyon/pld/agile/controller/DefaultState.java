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
public abstract class DefaultState implements State{
    @Override
    public void addDelivery(MainController controller, Map map, Node node) {}
    @Override
    public void validateAddDelivery(MainController controller, Map map, DeliveryMan deliveryMan, int ind, CommandList cmdList) {}
    @Override
    public void cancelAddDelivery(MainController controller) {}
    @Override
    public void deleteDelivery(MainController controller, Map map, DeliveryMan deliveryMan, int ind, CommandList cmdList) {}
    @Override
    public void moveDelivery(MainController controller, Map map, Delivery delivery, DeliveryMan oldDeliveryMan, DeliveryMan newDeliveryMan, int oldIndice, int newIndice, CommandList cmdList) {}
    @Override
    public void generateDeliveryMen(MainController controller, Map map, int deliveryMenCount, CommandList cmdList) {}
    @Override
    public void stopGeneration(MainController controller, Map map) {}
    @Override
    public void undo(CommandList cmdList) {}
    @Override
    public void redo(CommandList cmdList) {}
    @Override
    public void leftClick(MainController controller, Map map, CommandList listeDeCdes, Window view, Point2D p) {}
    @Override
    public void rightClick(MainController controller, Map map, CommandList cmdList, Window view, Point2D p) {}
    @Override
    public void loadMap(MainController controller, Map map, CommandList cmdList, Window view) throws Exception {}
    @Override
    public void loadDeliveriesFile(MainController controller, Map map, CommandList cmdList, Window view) throws Exception {}
}
