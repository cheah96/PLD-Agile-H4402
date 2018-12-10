package fr.insa.lyon.pld.agile.view;

import fr.insa.lyon.pld.agile.controller.MainController;
import fr.insa.lyon.pld.agile.model.*;

import java.awt.Color;
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

/**
 *
 * @author nmesnard, tzhang
 */
public class MapViewGraphical extends MapView
{
    private final MainController controller;
    private final Map map;
    
    private double latitudeMin;
    private double latitudeMax;
    private double longitudeMin;
    private double longitudeMax;
    
    private Dimension preferred = null;
    
    private int width;
    private int height;
    private int deltaX;
    private int deltaY;
    
    private boolean hasScale = false;
    private boolean hasData = false;
    
    BufferedImage imageMap = null;
    BufferedImage imageDeliveries = null;
    BufferedImage imageSelection = null;
    private int imageWidth;
    private int imageHeight;
    
    private double ratioX;
    private double ratioY;
    private double ratio;
    
    private Node selNode = null;
    private int selDeliveryMan = -1;
    
    private boolean isDirection = false;
    private boolean isLegend = false;
    private final MapViewGraphicalLegend legend;
    
    private final MouseAdapter mouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            eventClicked(e);
        }
    };
    
    private final ComponentListener resizeListener = new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            calcScale();
            repaint();
        }
    };
    
    public MapViewGraphical(Map map, MainController controller)
    {
        this.map = map;
        this.controller = controller;
        this.addComponentListener(resizeListener);
        this.addMouseListener(mouseListener);
        
        legend = new MapViewGraphicalLegend();
        legend.setVisible(false);
        this.setLayout(null);
        this.add(legend);
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
            
            latitudeMin = Collections.min(latitudes);
            latitudeMax = Collections.max(latitudes);
            longitudeMin = Collections.min(longitudes);
            longitudeMax = Collections.max(longitudes);
        }
        
        calcScale();
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
    
    public void calcScale() {
        hasScale = false;
        
        if (!hasData) return;
        
        imageWidth = this.getWidth();
        imageHeight = this.getHeight();
        
        ratioX = imageWidth / (longitudeMax - longitudeMin);
        ratioY = imageHeight / (latitudeMax - latitudeMin);
        ratio = (ratioX < ratioY ? ratioX : ratioY);
        
        if (ratio > 0) {
            imageWidth = (int) ((longitudeMax - longitudeMin) * ratio);
            imageHeight = (int) ((latitudeMax - latitudeMin) * ratio);
            
            hasScale = true;
        }
        
        imageMap = null;
    }
    
    public void eventClicked(MouseEvent e) {
        if (!(hasData && hasScale)) return;
        
        Point2D coord = getPixelToPoint(e.getX() - deltaX, this.getHeight() - deltaY - e.getY());
        
        if(e.getButton()== MouseEvent.BUTTON1) {
            controller.leftClick(coord);
        }else {
            controller.rightClick(coord);
        }
    }
    
    @Override
    public void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        
        if (!(hasData && hasScale)) return;
        
        if (imageMap == null) paintMap();
        if (imageDeliveries == null) paintDeliveries();
        if (imageSelection == null) paintSelection();
        
        width = this.getWidth();
        height = this.getHeight();
        deltaX = (width - imageWidth) / 2;
        deltaY = (height - imageHeight) / 2;
        
        g0.clearRect(0, 0, width, height);
        g0.drawImage(imageSelection, deltaX, deltaY+imageHeight, imageWidth, -imageHeight, null);
    }
    
    private void paintMap() {
        imageMap = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = imageMap.createGraphics();
        
        g.setBackground(Color.white);
        g.clearRect(0, 0, imageWidth, imageHeight);
        
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
        
        List<DeliveryMan> deliveryMen = map.getDeliveryMen();
        if (!deliveryMen.isEmpty()) {
            int indexMan = 0;
            for (DeliveryMan deliveryMan : deliveryMen) {
                g.setColor(Drawing.getColor(indexMan, deliveryMen.size()));
                
                Node n1 = map.getWarehouse();
                Point coordsn1 = getCoordsToPixel(n1);
                
                if (selDeliveryMan < 0 || selDeliveryMan == indexMan) {
                    float position = 0f;
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
            (int) ((longitude - longitudeMin) * ratio),
            (int) ((latitude - latitudeMin) * ratio)
        );
    }
    
    public Point2D getPixelToPoint (double x, double y) {
        return new Point2D.Double(
            x / ratio + longitudeMin,
            y / ratio + latitudeMin
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
            (int) (preferredPixelLength * (longitudeMax - longitudeMin) / avgSectionLength),
            (int) (preferredPixelLength * (latitudeMax - latitudeMin) / avgSectionLength)
        );
        
    }
    
    @Override
    public Dimension getPreferredSize() {
        return (preferred != null ? preferred : super.getPreferredSize());
    }
    
}
