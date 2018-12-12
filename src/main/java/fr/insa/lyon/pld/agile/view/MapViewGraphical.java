package fr.insa.lyon.pld.agile.view;

import fr.insa.lyon.pld.agile.controller.MainController;
import fr.insa.lyon.pld.agile.model.*;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author nmesnard, tzhang
 */
public class MapViewGraphical extends MapView
{
    private final MainController controller;
    private final Map map;
    
    private double offsetX;
    private double offsetY;
    private double latitudeMin;
    private double latitudeMax;
    private double longitudeMin;
    private double longitudeMax;
    
    private Dimension preferred = null;

    private Point2D lastMousePosition = new Point2D.Double();
    
    private boolean hasData = false;
    
    BufferedImage imageMap = null;
    BufferedImage imageDeliveries = null;
    BufferedImage imageSelection = null;
    
    private double ratio;
    private double ratioMin;
    private double ratioMax;
    
    private Node selNode = null;
    private int selDeliveryMan = -1;
    
    private boolean isDirection = false;
    private boolean isLegend = false;
    private final MapViewGraphicalLegend legend;
    
    private final JPopupMenu rightClickNodeMenu;
    private final JPopupMenu rightClickDeliveryMenu;
    private final JPopupMenu rightClickUnassignedDeliveryMenu;
    
    private final JMenu addDeliveryJMenu;
    private final JMenu assignDeliveryJMenu;
    private final JMenuItem unassignDeliveryMenuItem;
    private final JMenuItem deleteDeliveryMenuItem;
    
    private final JMenuItem assignDeliveryJMenu2;
    private final JMenuItem deleteDeliveryMenuItem2;
    
    private final ActionListener unassignDeliveryListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            controller.unassignDelivery(map.getDeliveries().get(selNode.getId()));
        }
    };
    
    private final ActionListener deleteDeliveryListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            controller.deleteDelivery(map.getDeliveries().get(selNode.getId()));
        }
    };
    
    private final ActionListener addDeliveryListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem)e.getSource();
            int index = (Integer)item.getClientProperty("deliveryManIndex");
            DeliveryMan deliveryMan = map.getDeliveryMen().get(index-1);
            controller.addDelivery(selNode, deliveryMan, deliveryMan.getDeliveries().size());
        }
    };
    
    private final ActionListener assignDeliveryListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JMenuItem item = (JMenuItem)e.getSource();
            int index = (Integer)item.getClientProperty("deliveryManIndex");
            DeliveryMan deliveryMan = map.getDeliveryMen().get(index-1);
            controller.assignDelivery(map.getDeliveries().get(selNode.getId()), deliveryMan, deliveryMan.getDeliveries().size());
        }
    };
    
    private final MouseAdapter mouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (!hasData) return;
            controller.mapClick(e, MapViewGraphical.this);
        }
        
        @Override
        public void mousePressed(MouseEvent event) {
            lastMousePosition.setLocation(event.getX(), event.getY());
        }
    
        @Override
        public void mouseDragged(MouseEvent event) {
            if ((event.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == 0) //Return if not left button
                return;

            Point2D actualMousePosition = new Point2D.Double(event.getX(), event.getY());
            offsetX -= (actualMousePosition.getX()-lastMousePosition.getX())/ratio;
            offsetY += (actualMousePosition.getY()-lastMousePosition.getY())/ratio;
            
            lastMousePosition = actualMousePosition;
            setCursor(new Cursor(Cursor.MOVE_CURSOR));
            imageMap = null;
            MapViewGraphical.this.repaint();
        }
    
        @Override
        public void mouseReleased(MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1) //Left button
                setCursor(Cursor.getDefaultCursor());
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent event) {
            lastMousePosition.setLocation(event.getX(), event.getY());
            updateRatio(Math.pow(1.25, -event.getWheelRotation()));
        }
    };
    
    private final ComponentListener resizeListener = new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            updateRatioMinMax();
            imageMap = null;
            repaint();
        }
    };
    
    public MapViewGraphical(Map map, MainController controller)
    {
        this.map = map;
        this.controller = controller;
        this.addComponentListener(resizeListener);
        this.addMouseListener(mouseListener);
        this.addMouseMotionListener(mouseListener);
        this.addMouseWheelListener(mouseListener);
        
        legend = new MapViewGraphicalLegend();
        legend.setVisible(false);
        this.setLayout(null);
        this.add(legend);
        
        rightClickDeliveryMenu = new JPopupMenu();
        assignDeliveryJMenu = new JMenu("Affecter cette livraison à ...");
        unassignDeliveryMenuItem = new JMenuItem("Désaffecter cette livraison");
        deleteDeliveryMenuItem = new JMenuItem("Supprimer cette livraison");
        unassignDeliveryMenuItem.addActionListener(unassignDeliveryListener);
        deleteDeliveryMenuItem.addActionListener(deleteDeliveryListener);
        rightClickDeliveryMenu.add(assignDeliveryJMenu);
        rightClickDeliveryMenu.add(unassignDeliveryMenuItem);
        rightClickDeliveryMenu.add(deleteDeliveryMenuItem);
        
        assignDeliveryJMenu2 = new JMenu("Affecter cette livraison à ...");
        deleteDeliveryMenuItem2 = new JMenuItem("Supprimer cette livraison");
        assignDeliveryJMenu2.addActionListener(unassignDeliveryListener);
        deleteDeliveryMenuItem2.addActionListener(deleteDeliveryListener);
        rightClickUnassignedDeliveryMenu = new JPopupMenu();
        rightClickUnassignedDeliveryMenu.add(assignDeliveryJMenu2);
        rightClickUnassignedDeliveryMenu.add(deleteDeliveryMenuItem2);

        rightClickNodeMenu = new JPopupMenu();
        addDeliveryJMenu = new JMenu("Ajouter une livraison ici à ...");
        rightClickNodeMenu.add(addDeliveryJMenu);
    }
    
    @Override
    public void updateNodes()
    {
        hasData = (map != null) && (map.getNodes() != null) && (!map.getNodes().isEmpty());
        
        if (hasData) {
            List<Double> latitudes = new ArrayList<>();
            List<Double> longitudes = new ArrayList<>();
            for (Node n : map.getNodes().values()) {
                latitudes.add(n.getLatitude());
                longitudes.add(n.getLongitude());
            }
            
            longitudeMin = Collections.min(longitudes);
            latitudeMin = Collections.min(latitudes);
            latitudeMax = Collections.max(latitudes);
            longitudeMax = Collections.max(longitudes);
        
            updateRatioMinMax();
            ratio = ratioMin;
            
            offsetX = longitudeMin - (getWidth()/ratio-(longitudeMax - longitudeMin))/2.;
            offsetY = latitudeMin - (getHeight()/ratio-(latitudeMax - latitudeMin))/2.;
        }
        
        calcPreferredSize();
        
        selNode = null;
        
        imageMap = null;
        this.repaint();
    }
    
    @Override
    public void updateDeliveries() {
        imageDeliveries = null;
        this.repaint();
    }
    
    @Override
    public void updateDeliveryMen() {
        selDeliveryMan = -1;
        updatePopupMenu();
        updateDeliveries();
    }
    
    @Override
    public void updateDeliveryMan() {
        updateDeliveries();
    }
    
    @Override
    public void updateStartingHour() {
    }
    
    @Override
    public void updateWarehouse() {
        updateNodes();
    }
    
    @Override
    public void selectNode(Node node) {
        if (selNode != node) {
            selNode = node;
            imageSelection = null;
            this.repaint();
        }
    }
    
    @Override
    public void selectDeliveryMan(int deliveryManIndex) {
        if (selDeliveryMan != deliveryManIndex) {
            selDeliveryMan = deliveryManIndex;
            imageDeliveries = null;
            this.repaint();
        }
    }
    
    public void showLegend(boolean visibility) {
        if (isLegend != visibility) {
            isLegend = visibility;
            if (visibility) legend.setLocation(12, 12);
            legend.setVisible(visibility);
        }
    }
    
    public void showDirection(boolean visibility) {
        if (isDirection != visibility) {
            isDirection = visibility;
            imageDeliveries = null;
            this.repaint();
        }
    }

    public Node findClosestNode(Point2D coord) {
        double closestdistance = -1;
        Node closest = null;
        for (Node n : map.getNodes().values()) {
            double distance = Math.pow((coord.getX() - n.getLongitude()), 2)
                            + Math.pow((coord.getY() - n.getLatitude()), 2);
            if (closestdistance < 0 || distance < closestdistance) {
                closestdistance = distance;
                closest = n;
            }
        }
        
        if (closestdistance > 15.0) {
            closest = null;
        }
        
        return closest;
    }
    
    public void showPopupNode(Point p) {
        updatePopupMenu();
        rightClickNodeMenu.show(this, p.x, p.y);
    }
    
    public void showPopupUnassignedDelivery(Point p) {
        updatePopupMenu();
        rightClickUnassignedDeliveryMenu.show(this, p.x, p.y);
    }
    
    public void showPopupDelivery(Point p) {
        updatePopupMenu();
        rightClickDeliveryMenu.show(this, p.x, p.y);
    }
    
    private void updatePopupMenu() {
        addDeliveryJMenu.removeAll();
        assignDeliveryJMenu.removeAll();
        assignDeliveryJMenu2.removeAll();
        int indexMan = 0;
        for( DeliveryMan d : map.getDeliveryMen()) {
            indexMan++;
            if(selNode != null && indexMan == map.getNodeDeliveryManIndex(selNode)+1) {
                System.err.println(selNode);
                System.err.println(indexMan);
                continue;
            }
            JMenuItem itemAdd = new JMenuItem("Livreur " + indexMan);
            itemAdd.putClientProperty("deliveryManIndex", indexMan);
            itemAdd.addActionListener(addDeliveryListener);
            addDeliveryJMenu.add(itemAdd);
            
            JMenuItem itemAssign = new JMenuItem("Livreur " + indexMan);
            itemAssign.putClientProperty("deliveryManIndex", indexMan);
            itemAssign.addActionListener(assignDeliveryListener);
            assignDeliveryJMenu.add(itemAssign);
            
            JMenuItem itemAssign2 = new JMenuItem("Livreur " + indexMan);
            itemAssign2.putClientProperty("deliveryManIndex", indexMan);
            itemAssign2.addActionListener(assignDeliveryListener);
            assignDeliveryJMenu2.add(itemAssign2);
        }
    }
    
    @Override
    public void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        
        if (!hasData) return;
        
        if (imageMap == null) paintMap();
        if (imageDeliveries == null) paintDeliveries();
        if (imageSelection == null) paintSelection();
        
        int width = this.getWidth();
        int height = this.getHeight();
        
        g0.clearRect(0, 0, width, height);
        g0.drawImage(imageSelection, 0, getHeight(), getWidth(), -getHeight(), null);
    }
    
    private void paintMap() {
        if ((longitudeMax-longitudeMin)*ratio >= getWidth()) {
            offsetX = Math.max(offsetX, longitudeMin);
            offsetX = Math.min(offsetX, longitudeMax-getWidth()/ratio);
        } else {
            offsetX = longitudeMin - (getWidth()/ratio-(longitudeMax - longitudeMin))/2.;
        }
        
        if ((latitudeMax-latitudeMin)*ratio >= getHeight()) {
            offsetY = Math.max(offsetY, latitudeMin);
            offsetY = Math.min(offsetY, latitudeMax-getHeight()/ratio);
        } else {
            offsetY = latitudeMin - (getHeight()/ratio-(latitudeMax - latitudeMin))/2.;
        }
            
        imageMap = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imageMap.createGraphics();
        Drawing.enableAntialiasing(g);
        
        g.setBackground(Color.lightGray);
        g.clearRect(0, 0, getWidth(), getHeight());
        
        g.setColor(Color.white);
        Point topLeft = getCoordsToPixel(longitudeMin, latitudeMin);
        g.fillRect(topLeft.x,
                   topLeft.y,
                   (int) ((longitudeMax - longitudeMin)*ratio)+1,
                   (int) ((latitudeMax - latitudeMin)*ratio)+1);
        
        g.setColor(Color.gray);
        for (Node n1 : map.getNodes().values()) {
            Point coordsn1 = getCoordsToPixel(n1);
            
            for (Section s : n1.getOutgoingSections()) {
                Node n2 = s.getDestination();
                Point coordsn2 = getCoordsToPixel(n2);
                
                Drawing.drawLine(g, coordsn1, coordsn2);
            }
        }
        
        g.dispose();
        imageDeliveries = null;
    }
    
    private void paintDeliveries() {
        imageDeliveries = Drawing.copyImage(imageMap);
        Graphics2D g = imageDeliveries.createGraphics();
        Drawing.enableAntialiasing(g);
        
        List<DeliveryMan> deliveryMen = map.getDeliveryMen();
        if (!deliveryMen.isEmpty()) {
            int indexMan = 0;
            for (DeliveryMan deliveryMan : deliveryMen) {
                g.setColor(Drawing.getColor(indexMan, deliveryMen.size()));
                
                Node n1 = map.getWarehouse();
                Point coordsn1 = getCoordsToPixel(n1);
                
                if (selDeliveryMan < 0 || selDeliveryMan == indexMan) {
                    double position = 0.;
                    for (Route route : deliveryMan.getRound().getItinerary())
                    {
                        for (Passage p : route.getPassages()) {
                            Node n2 = p.getSection().getDestination();
                            Point coordsn2 = getCoordsToPixel(n2);

                            Drawing.drawLineThick(g, coordsn1, coordsn2);
                            if (isDirection) position = Drawing.drawLineArrows(g, coordsn1, coordsn2, position);

                            coordsn1 = coordsn2;
                        }
                    }
                }
                indexMan++;
            }
        }
        
        for (Delivery d : map.getDeliveries().values()) {
            Node n = d.getNode();
            Point coords = getCoordsToPixel(d.getNode());
            Color color = getNodeColor(n, Color.gray);
            Drawing.drawDelivery(g, coords, color);
        }
        
        g.dispose();
        imageSelection = null;
    }
    
    private void paintSelection() {
        imageSelection = Drawing.copyImage(imageDeliveries);
        Graphics2D g = imageSelection.createGraphics();
        Drawing.enableAntialiasing(g);
        
        Node wh = map.getWarehouse();
        if (wh != null) {
            Drawing.drawWarehouse(g, getCoordsToPixel(wh));
        }
        
        if (selNode != null) {
            Point coords = getCoordsToPixel(selNode);
            Color color = getNodeColor(selNode, Color.gray);
            Drawing.drawSelectedNode(g, coords, color);
        }
        
        g.dispose();
    }
    
    protected void drawNode(Graphics g, Node n, int diameter) {
        Drawing.drawDot(g, getCoordsToPixel(n), diameter);
    }
    
    private Color getNodeColor(Node n, Color normal) {
        int deliveryManIndex = map.getNodeDeliveryManIndex(n);
        if (deliveryManIndex < 0) return normal;
        return Drawing.getColor(deliveryManIndex, map.getDeliveryMen().size());
    }
    
    protected static double getNodesDistance(Node n1, Node n2) {
        double distlong = n1.getLongitude() - n2.getLongitude();
        double distlat = n1.getLatitude() - n2.getLatitude();
        return Math.sqrt((distlong*distlong) + (distlat*distlat));
    }
    
    public Point getCoordsToPixel(Node n) {
        return getCoordsToPixel(n.getLongitude(), n.getLatitude());
    }
    public Point getCoordsToPixel(double longitude, double latitude) {
        return new Point(
            (int) ((longitude - offsetX) * ratio),
            (int) ((latitude - offsetY) * ratio)
        );
    }
    
    public Point2D getPixelToPoint (double x, double y) {
        return new Point2D.Double(
            x / ratio + offsetX,
            (getHeight() - y) / ratio + offsetY
        );
    }
    
    protected void calcPreferredSize() {
        preferred = null;
        
        if (!hasData) return;
        
        long sectionsCount = 0L;
        double sectionsLength = 0.0;
        
        for (Node n : map.getNodes().values()) {
            for (Section s : n.getOutgoingSections()) {
                sectionsLength += getNodesDistance(n, s.getDestination());
                sectionsCount++;
            }
        }
        
        if (sectionsCount == 0L || sectionsLength == 0.0) return;
        
        double avgSectionLength = sectionsLength / sectionsCount;
        double preferredPixelLength = 15.0;
        
        preferred = new Dimension (
            (int) (preferredPixelLength * (longitudeMax - offsetX) / avgSectionLength),
            (int) (preferredPixelLength * (latitudeMax - offsetY) / avgSectionLength)
        );
        
    }
    
    @Override
    public Dimension getPreferredSize() {
        return (preferred != null ? preferred : super.getPreferredSize());
    }
    
    private void updateRatioMinMax() {
        double ratioX = getWidth()/(longitudeMax - longitudeMin);
        double ratioY = getHeight()/(latitudeMax - latitudeMin);
        ratioMin = Math.min(ratioX,ratioY);
        ratioMax = 10000000;
        if (ratio < ratioMin)
            ratio = ratioMin;
        if (ratio > ratioMax)
            ratio = ratioMax;
    }
    
    private void updateRatio(double scaleFactor) {
        if (ratio * scaleFactor < ratioMin || ratio * scaleFactor > ratioMax)
            return;
        
        /* On soustrait la différence entre la position de la souris
         * (dans le système de coordonnées de la carte) après le zoom et avant le zoom
         * Ainsi, le point de la carte en dessous de la souris le reste après le zoom
         * Offset -= PosSourisCarteAprès - PosSourisCarteAvant
         *           -= PosSouris/ZoomAprès - PosSouris/ZoomAvant
         *           -= PosSouris/ZoomAvant/FacteurDeZoom - PosSouris/ZoomAvant 
         *           -= PosSouris/ZoomAvant * (1/Facteur de Zoom - 1)
         * (Pas besoin de tenir compte de l'offset car la soustraction l'annule) */
        
        offsetX -= lastMousePosition.getX()/ratio*(1./scaleFactor - 1);
        offsetY -= (getHeight()-lastMousePosition.getY())/ratio*(1./scaleFactor - 1); // Attention à l'inversion des Y
        ratio *= scaleFactor;
        
        imageMap = null;
        repaint();
    }
}
