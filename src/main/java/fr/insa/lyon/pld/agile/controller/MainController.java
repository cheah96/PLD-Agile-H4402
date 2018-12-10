package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.*;
import fr.insa.lyon.pld.agile.view.Window;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author Stanley
 */
public class MainController implements PropertyChangeListener{
    private Map map;
    private Window view;
    private State currentState;
    private CommandList cmdList;
    
    protected final InitialState INITIAL_STATE = new InitialState();
    protected final MapLoadedState MAP_LOADED_STATE = new MapLoadedState();
    protected final DeliveriesLoadedState DELIVERIES_LOADED_STATE = new DeliveriesLoadedState();
    protected final AddDeliveryState ADD_DELIVERY_STATE = new AddDeliveryState();
    protected final DeliveryMenComputingState DELIVERY_MEN_COMPUTING_STATE = new DeliveryMenComputingState();
    protected final DeliveryMenGeneratedState DELIVERY_MEN_GENERATED_STATE = new DeliveryMenGeneratedState();

    public MainController(Map map) {
        this.map = map;
        this.view = new Window(map, this);
        this.currentState = INITIAL_STATE;
        this.cmdList = new CommandList();
        map.addPropertyChangeListener(this);
    }

    protected void setCurrentState(State state) {
        currentState = state;
        System.out.println(currentState);
    }
    
     public void addDelivery(Node node) {
        currentState.addDelivery(this, map, node);
    }
    
    public void deleteDelivery(DeliveryMan deliveryMan, int ind) {
        currentState.deleteDelivery(this, map, deliveryMan, ind, cmdList);
    }
    
    public void moveDelivery(Delivery delivery, DeliveryMan oldDeliveryMan, DeliveryMan newDeliveryMan, int oldIndice, int newIndice) {
        currentState.moveDelivery(this, map, delivery, oldDeliveryMan, newDeliveryMan, oldIndice, newIndice, cmdList);
    }
     
    public void generateDeliveryMen(int deliveryMenCount) {
        currentState.generateDeliveryMen(this, map, deliveryMenCount, cmdList);
    }
    
    public void stopGeneration() {
        currentState.stopGeneration(this, map);
    }
    
    public void generationFinished(){
        currentState.generationFinished(this, map);
    }
            
    public void undo() {
        currentState.undo(cmdList);
    }
    
    public void redo() {
        currentState.redo(cmdList);
    }
    
    public void leftClick(Point2D p) {
        currentState.leftClick(this, map, cmdList, view, p);
    }
    
    public void loadMap() throws Exception {
        currentState.loadMap(this, map, cmdList, view);
    }
    
    public void loadDeliveriesFile() throws Exception {
        currentState.loadDeliveriesFile(this, map, cmdList, view);
    }
    
    public void selectedNode(Node node) {
        view.selectNode(node);
    }
    
    public void selectedDeliveryMan(int deliveryManIndex) {
        view.selectDeliveryMan(deliveryManIndex);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        switch (propertyName) {
            case "shortenDeliveriesFinished":
                currentState.generationFinished(this, map);
        }
    }
}
