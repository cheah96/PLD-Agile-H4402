package fr.insa.lyon.pld.agile.view;

import fr.insa.lyon.pld.agile.controller.MainController;
import fr.insa.lyon.pld.agile.model.*;
import java.awt.BorderLayout;

import java.awt.GridLayout;
import javax.swing.*;

import java.util.List;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author nmesnard
 */
public class MapViewTextual extends MapView
{
    final Map map;
    final MainController controller;
    
    private JTabbedPane jTabbedPane;
    
    private List<JList> lists = null;
    
    public MapViewTextual(Map map, MainController controller) {
        this.map = map;
        this.controller = controller;
        this.jTabbedPane = new JTabbedPane();
        setLayout(new BorderLayout());
        add(jTabbedPane, BorderLayout.CENTER);
        recreate();
        
        this.jTabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                controller.showDeliveryManRound(jTabbedPane.getSelectedIndex()-1);
            }
        });
    }
    
    public int getActiveDeliveryManIndex() {
        return jTabbedPane.getSelectedIndex()-1;
    }
    
    @Override
    public void showDeliveryManRound(int deliveryManIndex) {
        jTabbedPane.setSelectedIndex(deliveryManIndex+1);
    }
    
    protected void recreate() {
        lists = new ArrayList<>();
        
        jTabbedPane.removeAll();
        
        DefaultListModel<String> locs = new DefaultListModel<>();
        
        for (Delivery d : map.getDeliveries()) {
            locs.addElement("Point " + d.getNode().getId());
        }
        
        newTab("Tous", locs);
        
        for (DeliveryMan deliveryMan : map.getDeliveryMen())
        {
            DefaultListModel<String> vect = new DefaultListModel<>();
            for (Delivery d : deliveryMan.getDeliveries()) {
                vect.addElement("Point " + d.getNode().getId());
            }

            List<Passage> itinary = deliveryMan.getRound().getItinerary();
            int arrivalTime = 0;
            if (!itinary.isEmpty())
                arrivalTime = (int)itinary.get(itinary.size()-1).getArrivalTime();
            
            newTab("n°" + deliveryMan.getId() + " (" + arrivalTime + ")", vect);
        }
    }
    
    protected void newTab(String tabName, DefaultListModel<String> tabList) {
        JList<String> lstList = new JList<>(tabList);
        lists.add(lstList);
        
        JPanel panLivreur = new JPanel();
        panLivreur.setLayout(new GridLayout(1, 1));
        panLivreur.add(lstList);
        jTabbedPane.addTab(tabName, panLivreur); //, null, panLivreur, livreurName);
    }

    @Override
    public void updateNodes() {
        
    }
    
    @Override
    public void updateDeliveries() {
        recreate();
    }
    
    @Override
    public void updateDeliveryMen() {
        recreate();
    }

    @Override
    public void updateStartingHour() {
        
    }

    @Override
    public void updateWarehouse() {
        
    }
}
