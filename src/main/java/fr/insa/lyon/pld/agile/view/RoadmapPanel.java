package fr.insa.lyon.pld.agile.view;

import fr.insa.lyon.pld.agile.controller.MainController;
import fr.insa.lyon.pld.agile.model.Delivery;
import fr.insa.lyon.pld.agile.model.Map;
import fr.insa.lyon.pld.agile.model.Node;
import fr.insa.lyon.pld.agile.model.Passage;
import fr.insa.lyon.pld.agile.model.Route;
import fr.insa.lyon.pld.agile.model.Section;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 *
 * @author momoh
 */
public class RoadmapPanel extends MapView {
    
    private class RoutePart {
        String sectionName;
        Passage firstPassage;
        long duration; // seconds
        double distance; // meters
        Delivery delivery;
        
        public RoutePart(String sectionName, Passage firstPassage, double distance, long duration, Delivery delivery){
            this.sectionName = sectionName;
            this.firstPassage = firstPassage;
            this.duration = duration;
            this.distance = distance;
            this.delivery = delivery;
        }
        public void setDuration(int duration){
            this.duration = duration;
        }
        public void setDistance(double distance){
            this.distance = distance;
        }
        public long getDuration(){
            return this.duration;
        }
        public double getDistance(){
            return this.distance;
        }
        @Override
        public String toString(){
            return this.firstPassage.getArrivalTime().toString().substring(0, 5) + " - " + this.sectionName + " (" + Long.toString(Math.round(this.distance)) + " m)";
        }
    }
    
    
    private final MainController controller;
    private final Map map;
    JList<String> roadMapParts;
    
    public RoadmapPanel(Map map, MainController controller){
        this.controller = controller;
        this.map = map;
        
    }
    
    private List<RoutePart> buildRoadmap(Map map, int deliveryManIndex){
        List<RoutePart> routeParts = new ArrayList<RoutePart>();
        
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
                }
                else {
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
            } else
                routeParts.add(new RoutePart(routePartName, firstPassage, distance, duration, null));
        }
        
        return routeParts;
    }
    
    public void displayRoadmap(int deliveryManIndex){
        try {
            this.removeAll();
            
            DefaultListModel<String> model = new DefaultListModel<>(); 
            this.roadMapParts = new JList<>(model);
            DefaultListModel<String> reallist = (DefaultListModel<String>)this.roadMapParts.getModel();

            List<RoutePart> routeParts = buildRoadmap(this.map, deliveryManIndex);
            
            reallist.addElement("*** Feuille de route du livreur "+Integer.toString(deliveryManIndex+1) + " ***");
            
            for(RoutePart part : routeParts){
                reallist.addElement(part.toString());
                if (part.delivery != null) reallist.addElement("Livraison (" + part.delivery.getDuration() + " s)");
            }
            
            reallist.addElement("***");
            
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
