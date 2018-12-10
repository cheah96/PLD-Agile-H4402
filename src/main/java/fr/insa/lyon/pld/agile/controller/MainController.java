package fr.insa.lyon.pld.agile.controller;

import fr.insa.lyon.pld.agile.model.*;
import fr.insa.lyon.pld.agile.view.Window;
import fr.insa.lyon.pld.agile.view.MapViewGraphical;
import fr.insa.lyon.pld.agile.xml.XMLParser;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

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
        this.cmdList = new CommandList();
        setCurrentState(INITIAL_STATE);
        map.addPropertyChangeListener(this);
    }
    
    public Map getMap() { return map; }
    public Window getWindow() { return view; }
    public CommandList getCmdList() { return cmdList; }
    
    protected final void setCurrentState(State state) {
        currentState = state;
        state.enterState(view);
        System.out.println(currentState);
    }
    
    public void addDelivery(Node node) {
        currentState.addDelivery(node);
    }
    
    public void deleteDelivery(Delivery delivery) {
        currentState.deleteDelivery(delivery);
    }
    
    public void moveDelivery(Delivery delivery, DeliveryMan oldDeliveryMan, DeliveryMan newDeliveryMan, int oldIndice, int newIndice) {
        currentState.moveDelivery(delivery, oldDeliveryMan, newDeliveryMan, oldIndice, newIndice);
    }
     
    public void generateDeliveryMen(int deliveryMenCount) {
        currentState.generateDeliveryMen(deliveryMenCount);
    }
    
    public void undo() {
        cmdList.undo();
    }
    
    public void redo() {
        cmdList.redo();
    }
    
    public void btnStatusClick() {
        currentState.btnStatusClick();
    }
    
    public void mapClickLeft(MapViewGraphical mapview, Point2D coords) {
        currentState.mapClickLeft(mapview, coords);
    }
    public void mapClickRight(MapViewGraphical mapview, Point2D coords) {
        currentState.mapClickRight(mapview, coords);
    }
    
    public void loadMap() throws Exception {
        File selectedFile = view.promptFile("Chargement d'un plan");
        if (selectedFile == null) return;
        cmdList.reset();
        map.clear();
        XMLParser.loadMap(map, selectedFile.toPath());
        setCurrentState(MAP_LOADED_STATE);
    }
    
    public void loadDeliveriesFile() throws Exception {
        File selectedFile = view.promptFile("Chargement de demandes de livraison");
        if (selectedFile == null) return;
        cmdList.reset();
        map.clearDeliveries();
        map.clearWarehouse();
        try {
            XMLParser.loadDeliveries(map, selectedFile.toPath());
        } catch (Exception e) {
            map.clearDeliveries();
            map.clearWarehouse();
            throw e;
        }
        setCurrentState(DELIVERIES_LOADED_STATE);
    }
    
    public void selectedNode(Node node) {
        view.selectNode(node);
    }
    
    public void selectedDeliveryMan(int deliveryManIndex) {
        view.selectDeliveryMan(deliveryManIndex);
    }

    public void keyEscape() {
        currentState.keyEscape();
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        currentState.handleExternalEvent(propertyName);
    }
    
}
