package fr.insa.lyon.pld.agile.view;

import fr.insa.lyon.pld.agile.controller.MainController;
import fr.insa.lyon.pld.agile.model.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.Point2D;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author nmesnard, tzhang
 */

public class MapViewGraphical extends MapView
{  
    final Map map;
    
    Boolean hasScale = false;
    Boolean hasData = false;
    
    Dimension preferred = null;
    
    Double latitudeMin;
    Double latitudeMax;
    Double longitudeMin;
    Double longitudeMax;
    
    Double ratioX;
    Double ratioY;
    Double ratio;
    
    Double deltaX;
    Double deltaY;
    
    Node sel = null;
    
    int deliveryManIndex = -1;

    private final MouseAdapter mouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (!(hasData && hasScale)) return;

            Point2D coord = getPixelToPoint(e.getX(), e.getY());

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

            sel = closest;

            repaint();
        }
    };
    
    private ComponentListener resizeListener = new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            calcScale();
        }
    };
    
    public MapViewGraphical(Map map, MainController controller)
    {
        this.map = map;
        this.addComponentListener(resizeListener);
        this.addMouseListener(mouseListener);
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
        
        this.repaint();
    }
    
    @Override
    public void updateDeliveries()
    {
        sel = null;
        
        this.repaint();
    }
    
    public void showDeliveryManRound(int deliveryManIndex)
    {
        this.deliveryManIndex = deliveryManIndex;
        
        this.repaint();
    }
    
    @Override
    public void updateDeliveryMen() {
    }

    @Override
    public void updateStartingHour() {
    }

    @Override
    public void updateWarehouse() {
    }
    
    public void calcScale()
    {
        hasScale = false;
        
        if (!hasData) return;
        
        double width = this.getWidth();
        double height = this.getHeight();
        
        ratioX = (longitudeMax - longitudeMin) / width;
        ratioY = (latitudeMax - latitudeMin) / height;
        ratio = (ratioX > ratioY ? ratioX : ratioY);
        
        if (ratio > 0) {
            deltaX = (width - (longitudeMax - longitudeMin) / ratio) / 2;
            deltaY = (height - (latitudeMax - latitudeMin) / ratio) / 2;

            hasScale = true;
        }
        
        this.repaint();
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
        
        if (!(hasData && hasScale)) return;
        
        g.setColor(Color.black);
        
        for (Node n : map.getNodes().values()) {
            Point coordsn1 = getCoordsToPixel(n.getLongitude(), n.getLatitude());
            
            for (Section s : n.getOutgoingSections()) {
                Node n2 = s.getDestination();
                Point coordsn2 = getCoordsToPixel(n2.getLongitude(),n2.getLatitude());
                
                drawSection(g, coordsn1, coordsn2);
            }
        }
        
        if (deliveryManIndex >= 0) {
            g.setColor(Color.blue);
            
            Node prev = map.getWarehouse();
            for (Passage p : map.getDeliveryMen().get(deliveryManIndex).getRound().getItinerary()) {
                Node cur = p.getSection().getDestination();
                
                Point coordsn1 = getCoordsToPixel(prev.getLongitude(), prev.getLatitude());
                Point coordsn2 = getCoordsToPixel(cur.getLongitude(),cur.getLatitude());
               
                drawSectionThick(g, coordsn1, coordsn2);
                
                prev = cur;
            }
            
            g.setColor(Color.black);
        }
        
        for (Delivery d : map.getDeliveries()) {
            Node n = d.getNode();
            Point coordsd = getCoordsToPixel(n.getLongitude(), n.getLatitude());

            drawNode(g, coordsd, 9);
        }
        
        Node wh = map.getWarehouse();
        if (wh != null) {
            Point coordwh = getCoordsToPixel(wh.getLongitude(), wh.getLatitude());
            g.setColor(Color.red);
            drawNode(g, coordwh, 9);
        }
        
        g.setColor(Color.blue);
        
        if (sel != null) {
            Point coordssel = getCoordsToPixel(sel.getLongitude(), sel.getLatitude());
            drawNode(g, coordssel, 9);
        }
    }
    
    protected static void drawSectionThick(Graphics g, Point p1, Point p2) {
        for (int dx=-1; dx<=1; dx++) {
            for (int dy=-1; dy<=1; dy++) {
                g.drawLine(p1.x+dx, p1.y+dy, p2.x+dx, p2.y+dy);
            }
        }
    }
    
    protected static void drawSection(Graphics g, Point p1, Point p2) {
        g.drawLine(p1.x, p1.y, p2.x, p2.y);
    }
    
    protected static void drawNode(Graphics g, Point coords, int diameter) {
        g.fillOval((int) coords.getX()-diameter/2, (int) coords.getY()-diameter/2, diameter, diameter);
    }
    
    protected static double getNodesDistance(Node n1, Node n2) {
        double distlong = n1.getLongitude() - n2.getLongitude();
        double distlat = n1.getLatitude() - n2.getLatitude();
        return Math.sqrt((distlong*distlong) + (distlat*distlat));
    }
    
    public Point getCoordsToPixel(double longitude, double latitude) {
        return new Point(
            (int) ((longitude - longitudeMin) / ratio + deltaX),
            (int) ((latitude - latitudeMin) / ratio + deltaY)
        );
    }
    
    public Point2D getPixelToPoint (double x, double y) {
        return new Point2D.Double(
            (x - deltaX) * ratio + longitudeMin,
            (y - deltaY) * ratio + latitudeMin
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
