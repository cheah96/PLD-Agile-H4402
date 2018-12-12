package fr.insa.lyon.pld.agile.view;

import fr.insa.lyon.pld.agile.controller.MainController;
import fr.insa.lyon.pld.agile.model.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;

public class MapViewTextual extends MapView
{
    private final MainController controller;
    private final Map map;
    
    private final JTabbedPane panTabs;
    
    private List<JList> jlists = null;
    private List<DefaultListModel<ListItem>> lists = null;
    
    private Node selNode = null;
    private int selDeliveryMan = -1;
    
    Boolean raiseevents = true;
    
    TransferHandler transferHandler = new TransferHandler() {
        @Override
        public boolean canImport(TransferHandler.TransferSupport ts) {
            return true;
        }
        
        @Override
        public int getSourceActions(JComponent c) {
            return TransferHandler.COPY_OR_MOVE;
        }
        
        @Override
        protected Transferable createTransferable(JComponent c) {
            JList<ListItem> list = (JList<ListItem>) c;
            return new StringSelection(list.getSelectedValue().repr);
        }
        
        @Override
        public boolean importData(TransferHandler.TransferSupport info) {
            if (!info.isDrop()) {
                return false;
            }
            
            JList<ListItem> list = (JList<ListItem>)info.getComponent();
            DefaultListModel<ListItem> model = (DefaultListModel<ListItem>) list.getModel();
            JList.DropLocation dl = (JList.DropLocation)info.getDropLocation();
            int oldIndex = list.getSelectedIndex();
            int newIndex = dl.getIndex();
            
            if (newIndex < 0 || oldIndex == newIndex || oldIndex+1 == newIndex) {
                return false;
            }
            
            boolean insert = dl.isInsert();
            
            if (insert) {
                ListItem selected = list.getSelectedValue();
                
                Delivery delivery = map.getDeliveries().get(selected.node.getId());
                if (delivery == null) {
                    return false;
                }
                
                int offset = 0;
                Node targetNode = model.get(newIndex).node;
                if (targetNode == map.getWarehouse()) {
                    newIndex--;
                    targetNode = model.get(newIndex).node;
                    offset = 1;
                }
                
                Delivery targetDelivery = map.getDeliveries().get(targetNode.getId());
                
                DeliveryMan deliveryMan = map.getDeliveryMen().get(map.getNodeDeliveryManIndex(selected.getNode()));
                DeliveryMan targetDeliveryMan = map.getDeliveryMen().get(map.getNodeDeliveryManIndex(targetDelivery.getNode()));
                
                int targetIndex = targetDeliveryMan.getDeliveries().indexOf(targetDelivery);
                if (targetIndex < 0) {
                    return false;
                }
                
                if (deliveryMan == targetDeliveryMan && oldIndex < newIndex) {
                    offset--;
                }
                
                if (targetIndex+offset < 0 || targetIndex+offset > targetDeliveryMan.getDeliveries().size()) {
                    return false;
                }
                
                controller.assignDelivery(delivery, targetDeliveryMan, targetIndex+offset);
                return true;
            } else {
                return false;
            }
        }
    };
    
    /**
     * creates a new map view textual
     * @param map the map view textual's map
     * @param controller the controller managing the view
     */
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
                controller.selectDeliveryMan(selDeliveryMan);
            }
        });
    }
    
    /**
     * updates the nodes
     */
    @Override
    public void updateNodes() {
    }
    
    /**
     * updates the deliveries
     */
    @Override
    public void updateDeliveries() {
        recreate();
    }
    
    /**
     * updates the delivery men
     */
    @Override
    public void updateDeliveryMen() {
        selDeliveryMan = -1;
        recreate();
    }
    
    /**
     * updates the delivery man
     */
    @Override
    public void updateDeliveryMan() {
        recreate();
    }
    
    /**
     * updates the starting hour
     */
    @Override
    public void updateStartingHour() {
    }
    
    /**
     * updates the warehouse
     */
    @Override
    public void updateWarehouse() {
    }
    
    public Node getSelectedNode() {
        return selNode;
    }
    
    /**
     * selects a specific node
     * @param node a node selected
     */
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
                
                jl.setDragEnabled(true);
                jl.setTransferHandler(transferHandler);
                jl.setDropMode(DropMode.INSERT);
                
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
    
    /**
     * selects the corresponding number of a delivery man
     * @param deliveryManIndex
     */
    @Override
    public void selectDeliveryMan(int deliveryManIndex) {
        if (deliveryManIndex >= map.getDeliveryMen().size()) {
            return;
        }
        if (selDeliveryMan != deliveryManIndex) {
            selDeliveryMan = deliveryManIndex;
        }
        if (panTabs.getSelectedIndex() != selDeliveryMan+1) {
            panTabs.setSelectedIndex(selDeliveryMan+1);
        }
    }
    
    /**
     * recreates a new itinerary 
     */
    protected final void recreate() {
        jlists = new ArrayList<>();
        lists = new ArrayList<>();
        
        int prevDeliveryMan = selDeliveryMan;
        Node prevNode = selNode;
        
        panTabs.removeAll();
        
        DefaultListModel<ListItem> items = new DefaultListModel<>();
        
        if (map != null) {
            for (DeliveryMan deliveryMan : map.getDeliveryMen()) {
                for (Route route : deliveryMan.getRound().getItinerary()) {
                    items.addElement(new ListItem(route));
                }
            }
            
            int indexNode = items.size();
            for (Delivery d : map.getDeliveries().values()) {
                if (d.getDeliveryMan() == null) {
                    indexNode++;
                    items.addElement(new ListItem(d.getNode(), "Point de livraison " + indexNode));
                }
            }
        }
        
        newTab("Tous", items, "");
        
        if (map != null) {
            int indexMan = 0;
            for (DeliveryMan deliveryMan : map.getDeliveryMen()) {
                indexMan++;
                
                items = new DefaultListModel<>();
                for (Route route : deliveryMan.getRound().getItinerary()) {
                    items.addElement(new ListItem(route));
                }
                
                String info;
                List<Route> itinerary = deliveryMan.getRound().getItinerary();
                if (!itinerary.isEmpty()) {
                    LocalTime arrivalTime = itinerary.get(itinerary.size()-1).getArrivalTime();
                    info = "Arrivée : " + TimeToText(arrivalTime);
                } else {
                    info = "Itinéraire vide !";
                }
                
                newTab("L" + indexMan, items, info);
            }
        }
        
        selNode = null;
        selectNode(prevNode);
        selDeliveryMan = -1;
        if (prevDeliveryMan < map.getDeliveryMen().size()) {
            selectDeliveryMan(prevDeliveryMan);
        }
    }
    
    /**
     * constructs a new table on the left hand side of the display screen
     * @param tabName the name of said tab
     * @param tabList a list containing all the tabs
     * @param infos the info to be put in the tabs
     */
    protected void newTab(String tabName, DefaultListModel<ListItem> tabList, String infos) {
        int tabIndex = jlists.size();
        
        JList<ListItem> lstList = new JList<>(tabList);
        lstList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jlists.add(lstList);
        lists.add(tabList);
        
        lstList.addListSelectionListener((ListSelectionEvent e) -> {
            if (!raiseevents) {
                return;
            }
            int index = lstList.getSelectedIndex();
            Node node = (index == -1 ? null : tabList.get(index).getNode());
            if (selNode != node) {
                selectNode(node);
                controller.selectNode(node);
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
    
    /**
     * converts the time to readable text
     * @param time the local time
     * @return the text displaying the local time
     */
    public static String TimeToText(LocalTime time) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(dtf);
    }
    
    /**
     * Lists the items
     */
    public class ListItem
    {
        Node node;
        String repr;
        
        /**
         * list the items
         * @param route a specified route 
         */
        public ListItem(Route route) {
            node = route.getDestination();
            List<Passage> passages = route.getPassages();
            repr = TimeToText(route.getArrivalTime());
            if (!passages.isEmpty()) {
                repr += " - " ;
                String sectionName = passages.get(passages.size()-1).getSection().getName();
                if (sectionName == null || sectionName.isEmpty()) {
                    repr += "Rue anonyme";
                } else {
                    repr += sectionName;
                }
            }
        }
        
        /**
         * lists the items
         * @param node the node 
         * @param repr an attribute of the list of items
         */
        public ListItem(Node node, String repr) {
            this.node = node;
            this.repr = repr;
        }
        
        @Override
        public String toString() {
            return repr;
        }
        
        /**
         * gets the node
         * @return the node 
         */
        public Node getNode() {
            return node;
        }
    }
    
    private class ListItemRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
            Component c = super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
            Color color = getNodeColor(((ListItem) value).getNode(), Color.gray);
            if (!isSelected) {
                color = Drawing.getColorBrighter(Drawing.getColorBrighter(color));
            }
            c.setBackground(Drawing.getColorBrighter(color));
            return c;
        }
    }
    
    private Color getNodeColor(Node n, Color normal) {
        int deliveryManIndex = -1;
        if (n != null) {
            deliveryManIndex = map.getNodeDeliveryManIndex(n);
        }
        if (deliveryManIndex < 0) {
            return normal;
        }
        return Drawing.getColor(deliveryManIndex, map.getDeliveryMen().size());
    }
    
}
