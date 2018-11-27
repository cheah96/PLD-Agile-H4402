package fr.insa.lyon.pld.agile.view;

import fr.insa.lyon.pld.agile.model.*;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

/**
 *
 * @author nmesnard, tzhang
 */

public class MapViewGraphical extends JPanel implements MapView
{
    Map map;
    List<Delivery> deliveries;
    
    Boolean hasSize = false;
    Boolean hasData = false;
    
    Double latitudesMin;
    Double latitudesMax;
    Double longitudesMin;
    Double longitudesMax;
    
    Double ratioX;
    Double ratioY;
    Double ratio;
    
    Double deltaX;
    Double deltaY;
    
    Node sel = null;
    
    private MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            eventClicked(e);
        }
    };
    
    private ComponentListener resizeListener = new ComponentAdapter() {
        @Override
        public void componentResized(ComponentEvent e) {
            eventResized();
        }
    };
    
    public MapViewGraphical()
    {
        this.addComponentListener(resizeListener);
        this.addMouseListener(mouseListener);
    }
    
    @Override
    public void setMap(Map newMap)
    {
        map = newMap;

        hasData = (map != null) && (map.getNodes() != null) && (!map.getNodes().isEmpty());
        
        if (hasData) {
            List<Double> latitudes = new ArrayList<>();
            List<Double> longitudes = new ArrayList<>();
            for (Node n : map.getNodes().values()) {
                latitudes.add(n.getLatitude());
                longitudes.add(n.getLongitude());
            }

            latitudesMin = Collections.min(latitudes);
            latitudesMax = Collections.max(latitudes);
            longitudesMin = Collections.min(longitudes);
            longitudesMax = Collections.max(longitudes);
            
            eventResized();
        }
        
        this.repaint();
    }
    
    @Override
    public void setDeliveries(List<Delivery> newDeliveries)
    {
        sel = null;
        
        // TODO
    }
    
    public void eventResized()
    {
        if (!hasData) return;
        
        ratioX = (longitudesMax - longitudesMin) / this.getWidth();
        ratioY = (latitudesMax - latitudesMin) / this.getHeight();
        ratio = (ratioX > ratioY ? ratioX : ratioY);
        
        deltaX = (this.getWidth() - (longitudesMax-longitudesMin)/ratio)/2;
        deltaY = (this.getHeight() - (latitudesMax-latitudesMin)/ratio)/2;
        
        hasSize = (ratio > 0);
    }
    
    public void eventClicked(MouseEvent e)
    {
        if (!(hasData && hasSize)) return;
        
        Point2D coord = getPixelToPoint(e.getX(),e.getY());
        
        double closestdistance = -1;
        Node closest = new Node(0, e.getX(), e.getY());
        for (Node n : map.getNodes().values()) {
            double distance = Math.pow((coord.getX() - n.getLongitude()), 2)
                            + Math.pow((coord.getY() - n.getLatitude()), 2);
            if (closestdistance < 0 || distance < closestdistance) {
                closestdistance = distance;
                closest = n;
            }
        }
        
        sel = closest;
        
        this.repaint();
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        g.clearRect(0, 0, this.getWidth(), this.getHeight());
        
        if (!(hasData && hasSize)) return;
        
        if (sel != null) {
            int diameter = 15;
            Point coordssel = getCoordsToPixel(sel.getLongitude(),sel.getLatitude());
            g.drawOval((int) coordssel.getX()-diameter/2, (int) coordssel.getY()-diameter/2, diameter, diameter);
        }
        
        for (Node n : map.getNodes().values()) {
            Point coordsn = getCoordsToPixel(n.getLongitude(), n.getLatitude());
            
            for (Section s : n.getOutgoingSections()) {
                Node n2 = s.getDestination();
                Point coordsn2 = getCoordsToPixel(n2.getLongitude(),n2.getLatitude());
                
                g.drawLine(coordsn.x, coordsn.y, coordsn2.x, coordsn2.y);
            }
        }
    }

    public Point getCoordsToPixel(double longitude, double latitude) {
        return new Point(
            (int) ((longitude - longitudesMin) / ratio + deltaX),
            (int) ((latitude - latitudesMin) / ratio + deltaY)
        );
    }
    
    public Point2D getPixelToPoint (double x, double y) {
        return new Point2D.Double(
            (x - deltaX) * ratio + longitudesMin,
            (y - deltaY) * ratio + latitudesMin
        );
    }
    
}
