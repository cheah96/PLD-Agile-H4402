package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.*;
import fr.insa.lyon.pld.agile.view.MapViewGraphical;
import fr.insa.lyon.pld.agile.view.Window;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
    
    /**
     * constructs a main controller
     * @param map the map 
     */
  
    public MainController(Map map) {
        this.map = map;
        this.view = new Window(map, this);
        this.cmdList = new CommandList(this);
        setCurrentState(INITIAL_STATE);
        map.addPropertyChangeListener(this);
    }
    
    /**
     * gets the map 
     * @return the map
     */
    
    public Map getMap() { return map; }
    
    /**
     * gets the window object 
     * @return the window 
     */
    public Window getWindow() { return view; }
    
    /**
     * gets the outgoing sections 
     * @return the list of sections
     */
    
    protected final void setCurrentState(State state) {
        currentState = state;
        view.clearStatus();
        view.setUndoEnabled(cmdList.canUndo());
        view.setRedoEnabled(cmdList.canRedo());
        state.enterState();
        System.out.println(currentState);
    }
    
    /**
     * adds a new delivery 
     * @param node a node
     * @param deliveryMan a delivery man
     * @param index the index of the delivery
     */
    public void addDelivery(Node node, DeliveryMan deliveryMan, int index) {
        currentState.addDelivery(node, deliveryMan, index);
    }
    
    /**
     * deletes a delivery 
     * @param delivery the delivery to be deleted
     */
    public void deleteDelivery(Delivery delivery) {
        currentState.deleteDelivery(delivery);
    }
    
    /**
     * assigns a delivery to a delivery man 
     * @param delivery a delivery
     * @param newDeliveryMan a delivery man 
     * @param newIndice the index of the delivery to add the new delivery before
     */
    
    public void assignDelivery(Delivery delivery, DeliveryMan newDeliveryMan, int newIndice) {
        currentState.assignDelivery(delivery, newDeliveryMan, newIndice);
    }
     
    /**
     * unassigns a delivery from a delivery man
     * @param delivery a delivery
     */
    
    public void unassignDelivery(Delivery delivery) {
        currentState.unassignDelivery(delivery);
    }
    /**
     * generates a delivery man
     * @param deliveryMenCount delivery men 
     */
    
    public void generateDeliveryMen(int deliveryMenCount) {
        currentState.generateDeliveryMen(deliveryMenCount);
    }
    
    /**
     * executes the command of adding, removing or modifying
     * @param cmd a command to execute
     */
    
    public void doCmd(Command cmd) {
        cmdList.addCommand(cmd);
    }
    
    /**
     * undo a command from the list of commands
     */
    
    public void undo() {
        cmdList.undo();
        
        updateUndoRedoButtonsState();
    }
    
    /**
     * redo a command from a command list
     */
    
    public void redo() {
        cmdList.redo();
        
        updateUndoRedoButtonsState();
    }
    
    /**
     * resets the command list
     */
    
    public void resetCmdList() {
        cmdList.reset();
        updateUndoRedoButtonsState();
    }
    
    /**
     * update the current state of the undo and redo buttons
     */
    private void updateUndoRedoButtonsState() {
        view.setUndoEnabled(cmdList.canUndo());
        view.setRedoEnabled(cmdList.canRedo());
    }
    
    /**
     * when the button on the status bar is clicked
     */  
    public void btnStatusClick() {
        currentState.btnStatusClick();
    }
    
    /**
     * when a button on the map is clicked
     * @param event the mouse event
     * @param mapview the view of the map on the windows
     */
    public void mapClick(MouseEvent event, MapViewGraphical mapview) {
        currentState.mapClick(event, mapview);
    }
    
    /**
     * loads a map
     */
    public void loadMap() {
        currentState.loadMap();
    }
    
    /**
     * loads the delivery files
     */
    public void loadDeliveriesFile() {
        currentState.loadDeliveriesFile();
    }
    
    /**
     * selects a node
     * @param node a node
     */
    public void selectNode(Node node) {
        currentState.selectNode(node);
    }
    
    /**
     * selects a delivery man 
     *@param deliveryManIndex the index number of the delivery man
     */
    public void selectDeliveryMan(int deliveryManIndex) {
        currentState.selectDeliveryMan(deliveryManIndex);
    }
    
    /**
     * set the current state when the esc button is clicked 
     */ 
    public void keyEscape() {
        currentState.keyEscape();
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        currentState.handleExternalEvent(propertyName, evt.getNewValue());
    }
    
}
