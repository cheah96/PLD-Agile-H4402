package fr.insa.lyon.pld.agile.view;

import fr.insa.lyon.pld.agile.model.*;

import java.awt.GridLayout;

import javax.swing.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author nmesnard
 */
public class MapViewTextual extends JTabbedPane implements MapView
{
    Map map = null;
    
    List<JList> lists = null;
    
    public MapViewTextual()
    {
        recreate();
    }
    
    @Override
    public void setMap(Map newMap)
    {
        map = newMap;
        
        recreate();
    }
    
    @Override
    public void setDeliveries(List<Delivery> newDeliveries)
    {
        recreate();
    }
    
    protected void recreate() {
        lists = new ArrayList<>();
        
        this.removeAll();
        
        if (map == null)
            return;
        
        Vector<String> locs = new Vector();
        
        for (Delivery d : map.getDeliveries()) {
            locs.add("Point " + d.getNode().getId());
        }
        
        newTab("Tous", locs);
        
        for (DeliveryMan deliveryMan : map.getDeliveryMen())
        {
            Vector<String> vect = new Vector<>();
            for (Delivery d : deliveryMan.getDeliveries()) {
                vect.addElement("Point " + d.getNode().getId());
            }
            
            List<Passage> itinary = deliveryMan.getRound().getItinerary();
            newTab("Livreur " + deliveryMan.getId() + " (" + (int)itinary.get(itinary.size()-1).getArrivalTime() + ")", vect);
        }
    }
    
    protected void newTab(String tabName, Vector<String> tabList) {
        JList<String> lstList = new JList<>(tabList);
        lists.add(lstList);
        
        JPanel panLivreur = new JPanel();
        panLivreur.setLayout(new GridLayout(1, 1));
        panLivreur.add(lstList);
        this.addTab(tabName, panLivreur); //, null, panLivreur, livreurName);
    }
    
}
