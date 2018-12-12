package fr.insa.lyon.pld.agile.view;

import fr.insa.lyon.pld.agile.controller.MainController;
import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.model.Node;
import fr.insa.lyon.pld.agile.model.Passage;
import fr.insa.lyon.pld.agile.model.Route;
import fr.insa.lyon.pld.agile.model.Section;
import java.awt.BorderLayout;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

/**
 * The part of the main window which displays the roadmap
 */
public class RoadmapPanel extends MapView {
    
    /**
     * A routePart is a part of the roadmap
     */
    private class RoutePart {
        /**
         * the name of the section (street)
         */
        String sectionName;
        /**
         * the first passage of the street
         */
        Passage firstPassage;
        /**
         * the time in seconds needed to run through the street
         */
        long duration;
        /**
         * the length of the street
         */
        double distance;
        Delivery delivery;
        
        public RoutePart(String sectionName, Passage firstPassage, double distance, long duration, Delivery delivery){
            this.sectionName = sectionName;
            if (sectionName == null || sectionName.isEmpty()) {
                this.sectionName = "Rue anonyme";
            }
            
            this.firstPassage = firstPassage;
            this.duration = duration;
            this.distance = distance;
            this.delivery = delivery;
        }
        
        @Override
        public String toString(){
            return this.firstPassage.getArrivalTime().toString().substring(0, 5) + " - " + this.sectionName + " (" + Long.toString(Math.round(this.distance)) + " m)";
        }
    }
    
    
    /**
     * the main controller of the app
     */
    private final MainController controller;
    /**
     * the map
     */
    private final Map map;
    /**
     * the component where the roadMapParts are displayed
     */
    JList<String> roadMapParts;
    
    TransferHandler transferHandler = new TransferHandler() {
        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.COPY_OR_MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            JList<String> list = (JList<String>) c;
            return new StringSelection(String.join("\n",list.getSelectedValuesList()));
        }
    };
        
    /**
     * Creates the roadmap object.
     * @param map the map to create roadmaps for
     * @param controller the corresponding controller
     */
    public RoadmapPanel(Map map, MainController controller){
        this.controller = controller;
        this.map = map;
        
    }
    
    /**
     * build the roadmap of a deliveryman 
     * 
     * @param map the map
     * @param deliveryManIndex the index of the deliveryman
     * 
     * @return the parts of the roadmap
     */
    private List<RoutePart> buildRoadmap(Map map, int deliveryManIndex){
        List<RoutePart> routeParts = new ArrayList<>();
        
        String routePartName = null;
        double distance = 0;
        long duration = 0;
        Passage firstPassage = null;
        
        for (Route route : map.getDeliveryMen().get(deliveryManIndex).getRound().getItinerary()){
            for (Passage location : route.getPassages()) {
                Section currentSection = location.getSection();
                if (routePartName == null && firstPassage == null){
                    routePartName = currentSection.getName();
                    firstPassage = location;
                }

                if(currentSection.getName().equals(routePartName)){
                    distance += currentSection.getLength();
                    duration += currentSection.getDuration();
                } else {
                    routeParts.add(new RoutePart(routePartName, firstPassage, distance, duration, null));
                    firstPassage = location;
                    routePartName = currentSection.getName();
                    distance = currentSection.getLength();
                    duration = currentSection.getDuration();
                }
            }
            
            if (route.isDelivering()) {
                Delivery delivery = map.getDeliveries().get(route.getDestination().getId());
                routeParts.add(new RoutePart(routePartName, firstPassage, distance, duration, delivery));
            } else {
                routeParts.add(new RoutePart(routePartName, firstPassage, distance, duration, null));
            }
        }
        
        return routeParts;
    }
    
    /**
     * Displays a delivery man's roadmap.
     * @param deliveryManIndex index of the delivery man
     */
    public void displayRoadmap(int deliveryManIndex){
        try {
            this.removeAll();
            
            DefaultListModel<String> model = new DefaultListModel<>(); 
            this.roadMapParts = new JList<>(model);
            this.roadMapParts.setDragEnabled(true);
            this.roadMapParts.setTransferHandler(transferHandler);

            DefaultListModel<String> reallist = (DefaultListModel<String>)this.roadMapParts.getModel();

            List<RoutePart> routeParts = buildRoadmap(this.map, deliveryManIndex);
            
            reallist.addElement("*** Feuille de route du livreur " + Integer.toString(deliveryManIndex+1) + " ***");
            
            for(RoutePart part : routeParts){
                reallist.addElement(part.toString());
                if (part.delivery != null) {
                    reallist.addElement("Livraison (" + part.delivery.getDuration() + " s)");
                }
            }
            
            reallist.addElement("*** Fin de la feuille de route du livreur " + Integer.toString(deliveryManIndex+1) + " ***");
            
            this.setLayout(new BorderLayout());
            this.add(this.roadMapParts, BorderLayout.CENTER);
            this.updateUI();
        } catch (Exception ex) {
            System.err.println("Error when displaying the roadmap !");
            ex.printStackTrace();
        }
    }

    @Override
    public void updateNodes() {
    }

    @Override
    public void updateDeliveries() {
    }

    @Override
    public void updateDeliveryMen() {
    }

    @Override
    public void updateDeliveryMan() {
    }

    @Override
    public void updateStartingHour() {
    }

    @Override
    public void updateWarehouse() {
    }

    @Override
    public void selectNode(Node node) {
    }

    @Override
    public void selectDeliveryMan(int deliveryManIndex) {
        if(deliveryManIndex>=0) {
            displayRoadmap(deliveryManIndex);
        } else{
            removeAll();
            repaint();
        }
    }
    
    
}
