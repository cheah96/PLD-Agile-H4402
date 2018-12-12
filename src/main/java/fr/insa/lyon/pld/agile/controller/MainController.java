package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.*;
import fr.insa.lyon.pld.agile.view.Window;
import fr.insa.lyon.pld.agile.view.MapViewGraphical;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author Stanley
 */
public class MainController implements PropertyChangeListener{
    private final Map map;
    private final Window view;
    private State currentState;
    private final CommandList cmdList;
    
    protected final InitialState INITIAL_STATE = new InitialState(this);
    protected final MapLoadedState MAP_LOADED_STATE = new MapLoadedState(this);
    protected final DeliveriesLoadedState DELIVERIES_LOADED_STATE = new DeliveriesLoadedState(this);
    protected final AddDeliveryState ADD_DELIVERY_STATE = new AddDeliveryState(this);
    protected final DeliveryMenComputingState DELIVERY_MEN_COMPUTING_STATE = new DeliveryMenComputingState(this);
    protected final DeliveryMenGeneratedState DELIVERY_MEN_GENERATED_STATE = new DeliveryMenGeneratedState(this);
    
    public MainController(Map map) {
        this.map = map;
        this.view = new Window(map, this);
        this.cmdList = new CommandList(this);
        setCurrentState(INITIAL_STATE);
        map.addPropertyChangeListener(this);
    }
    
    public Map getMap() { return map; }
    public Window getWindow() { return view; }
    
    protected final void setCurrentState(State state) {
        currentState = state;
        view.clearStatus();
        view.setUndoEnabled(cmdList.canUndo());
        view.setRedoEnabled(cmdList.canRedo());
        state.enterState();
        System.out.println(currentState);
    }
    
    public void addDelivery(Node node, DeliveryMan deliveryMan, int index) {
        currentState.addDelivery(node, deliveryMan, index);
    }
    
    public void deleteDelivery(Delivery delivery) {
        currentState.deleteDelivery(delivery);
    }
    
    public void assignDelivery(Delivery delivery, DeliveryMan newDeliveryMan, int newIndice) {
        currentState.assignDelivery(delivery, newDeliveryMan, newIndice);
    }
     
    public void unassignDelivery(Delivery delivery) {
        currentState.unassignDelivery(delivery);
    }
    
    public void generateDeliveryMen(int deliveryMenCount) {
        currentState.generateDeliveryMen(deliveryMenCount);
    }
    
    public void doCmd(Command cmd) {
        cmdList.addCommand(cmd);
    }
    
    public void undo() {
        cmdList.undo();
        
        updateUndoRedoButtonsState();
    }
    
    public void redo() {
        cmdList.redo();
        
        updateUndoRedoButtonsState();
    }
    
    public void resetCmdList() {
        cmdList.reset();
        updateUndoRedoButtonsState();
    }
    
    private void updateUndoRedoButtonsState() {
        view.setUndoEnabled(cmdList.canUndo());
        view.setRedoEnabled(cmdList.canRedo());
    }
    
    public void btnStatusClick() {
        currentState.btnStatusClick();
    }
    
    public void mapClick(MouseEvent event, MapViewGraphical mapview) {
        currentState.mapClick(event, mapview);
    }
    
    public void loadMap() {
        currentState.loadMap();
    }
    
    public void loadDeliveriesFile() {
        currentState.loadDeliveriesFile();
    }
    
    public void selectNode(Node node) {
        currentState.selectNode(node);
    }
    
    public void selectDeliveryMan(int deliveryManIndex) {
        currentState.selectDeliveryMan(deliveryManIndex);
    }

    public void keyEscape() {
        currentState.keyEscape();
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        currentState.handleExternalEvent(propertyName, evt.getNewValue());
    }
    
}
