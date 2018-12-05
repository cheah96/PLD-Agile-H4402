package fr.insa.lyon.pld.agile.view;

import fr.insa.lyon.pld.agile.controller.MainController;
import fr.insa.lyon.pld.agile.model.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.awt.GridBagConstraints;

import javax.swing.*;
import javax.swing.event.*;

import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author nmesnard
 */
public class MapViewTextual extends MapView
{
    private final MainController controller;
    private final Map map;
    
    private final JTabbedPane panTabs;
    
    private List<JList> jlists = null;
    private List<DefaultListModel<ListItem>> lists = null;
    
    private Node selNode = null;
    
    Boolean raiseevents = true;
    
    public MapViewTextual(Map map, MainController controller) {
        this.map = map;
        this.controller = controller;
        
        panTabs = new JTabbedPane();
        
        this.setLayout(new BorderLayout());
        this.add(panTabs, BorderLayout.CENTER);
        
        recreate();
        
        panTabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int deliveryManIndex = panTabs.getSelectedIndex()-1;
                selectDeliveryMan(deliveryManIndex);
                controller.selectedDeliveryMan(panTabs.getSelectedIndex()-1);
            }
        });
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
    
    @Override
    public void selectNode(Node node) {
        if (selNode != node) {
            selNode = node;
            
            int deliveryManTab = panTabs.getSelectedIndex()-1;
            int deliveryManNode = map.getNodeDeliveryManIndex(node);
            
            if (deliveryManNode >= 0 && deliveryManTab >= 0) {
                if (deliveryManNode != deliveryManTab) {
                    selectDeliveryMan(deliveryManNode);
                }
            }
            
            raiseevents = false;
            for (int il = 0; il < lists.size(); il++) {
                JList jl = jlists.get(il);
                DefaultListModel<ListItem> l = lists.get(il);
                int indexList;
                
                indexList = jl.getSelectedIndex();
                if (indexList >= 0) {
                    if (l.get(indexList).getNode() == selNode) {
                        continue;
                    } else {
                        jl.clearSelection();
                    }
                }
                
                if (il == 0 || il-1 == deliveryManNode) {
                    for (indexList = 0; indexList < l.size(); indexList++) {
                        ListItem item = l.get(indexList);
                        if (item.getNode() == selNode) {
                            jl.setSelectedIndex(indexList);
                        }
                    }
                }
            }
            raiseevents = true;
        }
    }
    
    @Override
    public void selectDeliveryMan(int deliveryManIndex) {
        if (panTabs.getSelectedIndex() != deliveryManIndex+1) {
            panTabs.setSelectedIndex(deliveryManIndex+1);
        }
    }
    
    protected final void recreate() {
        jlists = new ArrayList<>();
        lists = new ArrayList<>();
        
        panTabs.removeAll();
        
        DefaultListModel<ListItem> itemsAll = new DefaultListModel<>();
        
        int indexMan;
        int indexNode;
        
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        
        if (map != null) {
            indexMan = 0;
            for (DeliveryMan deliveryMan : map.getDeliveryMen())
            {
                indexMan++;
                
                indexNode = 0;
                
                for (Route route : deliveryMan.getRound().getItinerary()) {
                    indexNode++;
                    List<Passage> passages = route.getPassages();
                    itemsAll.addElement(new ListItem(route.getDestination(), route.getArrivalTime().format(dtf)
                            + " - "
                            + passages.get(passages.size()-1).getSection().getName()));
                }
                
            }
            
            if (indexMan == 0) {
                indexNode = 0;
                for (Delivery d : map.getDeliveries().values()) {
                    indexNode++;
                    itemsAll.addElement(new ListItem(d.getNode(), "Point " + indexNode));
                }
            }
        }
        
        newTab("Tous", itemsAll, "");
        
        if (map != null) {
            indexMan = 0;
            
            for (DeliveryMan deliveryMan : map.getDeliveryMen())
            {
                indexMan++;
                
                indexNode = 0;
                DefaultListModel<ListItem> items = new DefaultListModel<>();
                //deliveryMan.getRound().getItinerary()
                
                //TODO : refactor this copy-paste !
                for (Route route : deliveryMan.getRound().getItinerary()) {
                    indexNode++;
                    List<Passage> passages = route.getPassages();
                    items.addElement(new ListItem(route.getDestination(), route.getArrivalTime().format(dtf)
                            + " - "
                            + passages.get(passages.size()-1).getSection().getName()));
                }
                
                List<Route> itinerary = deliveryMan.getRound().getItinerary();
                String info = "Itinéraire vide !";
                if(!itinerary.isEmpty()) {
                    LocalTime arrivalTime = itinerary.get(itinerary.size()-1).getArrivalTime();
                    info = "Arrivée : " + arrivalTime.format(dtf);
                }
                
                
                newTab("L" + indexMan, items, info);
            }
        }
    }
    
    protected void newTab(String tabName, DefaultListModel<ListItem> tabList, String infos) {
        int tabIndex = jlists.size();
        
        JList<ListItem> lstList = new JList<>(tabList);
        lstList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jlists.add(lstList);
        lists.add(tabList);
        
        lstList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!raiseevents) return;
                int index = lstList.getSelectedIndex();
                Node node = (index == -1 ? null : tabList.get(index).getNode());
                if (selNode != node) {
                    selectNode(node);
                    controller.selectedNode(node);
                }
            }
        });
        
        JPanel panLivreur = new JPanel();
        JLabel lblInfos = new JLabel(infos);
        panLivreur.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.gridx = 0;
        c.gridy = 0;
        panLivreur.add(lblInfos, c);
        c.weighty = 1;
        c.gridwidth = 1;
        c.gridy = 1;
        JScrollPane boxList = new JScrollPane(lstList);
        boxList.setPreferredSize(new Dimension(120, 240));
        panLivreur.add(boxList, c);
        
        panTabs.addTab(tabName, panLivreur);
        
        if (tabIndex > 0) {
            Color tabColor = Drawing.getColorBrighter(Drawing.getColor(tabIndex-1, map.getDeliveryMen().size()));
            panTabs.setBackgroundAt(tabIndex, tabColor);
            lblInfos.setBackground(tabColor);
            lblInfos.setOpaque(true);
        } // else {
            lstList.setCellRenderer( new ListItemRenderer() );
        // }
    }
    
    public class ListItem
    {
        Node node;
        String repr;
        
        public ListItem(Node node, String repr) {
            this.node = node;
            this.repr = repr;
        }
        
        @Override
        public String toString() {
            return repr;
        }
        
        public Node getNode() {
            return node;
        }
    }
    
    private class ListItemRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
            Component c = super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
            Color color = getNodeColor(((ListItem) value).getNode(), Color.gray);
            if (!isSelected) color = Drawing.getColorBrighter(color);
            c.setBackground(Drawing.getColorBrighter(color));
            return c;
        }
    }
    
    private Color getNodeColor(Node n, Color normal) {
        int deliveryManIndex = map.getNodeDeliveryManIndex(n);
        if (deliveryManIndex < 0) return normal;
        return Drawing.getColor(deliveryManIndex, map.getDeliveryMen().size());
    }
    
}
