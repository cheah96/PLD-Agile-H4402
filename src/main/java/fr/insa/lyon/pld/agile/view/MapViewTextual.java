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
    List<Delivery> deliveries = null;
    
    List<JList> lists = null;
    
    public MapViewTextual()
    {
        recreate();
    }
    
    @Override
    public void setMap(Map newMap)
    {
        map = newMap;
        deliveries = null;
        
        recreate();
    }
    
    @Override
    public void setDeliveries(List<Delivery> newDeliveries)
    {
        deliveries = newDeliveries;
        
        recreate();
    }
    
    protected void recreate() {
        lists = new ArrayList<>();
        
        this.removeAll();
        
        Vector<String> locs = new Vector();
        
        if (deliveries != null) {
            for (Delivery d : deliveries) {
                locs.add("Point " + d.getNode().getId());
            }
        }
        
        newTab("Tous", locs);
        
        
        for (int count=1; count<=3; count++) {
            Vector<String> vect = new Vector<>();
            for (int k=1; k<=count*3; k++) {
                vect.addElement("Point " + k);
            }
            
            newTab("Livreur " + count, vect);
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
