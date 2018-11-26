package fr.insa.lyon.pld.agile.view;

import fr.insa.lyon.pld.agile.model.*;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

class JCanvas extends JPanel implements MouseListener
{
    Map map;
    
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
    
    public JCanvas()
    {
        this.addMouseListener(this);
    }

    public void setmap(Map newmap)
    {
        map=newmap;

        if ((map == null) || (map.getNodes() == null) || map.getNodes().isEmpty()) {
            return;
        }

        List<Double> latitudes = new ArrayList<Double>();
        List<Double> longitudes = new ArrayList<Double>();
        for (Node n : map.getNodes().values()) {
            latitudes.add(n.getLatitude());
            longitudes.add(n.getLongitude());
        }
        
        latitudesMin = Collections.min(latitudes);
        latitudesMax = Collections.max(latitudes);
        longitudesMin = Collections.min(longitudes);
        longitudesMax = Collections.max(longitudes);
        
        this.repaint();
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        g.clearRect(0, 0, this.getWidth(), this.getHeight());

        if ((map == null) || (map.getNodes() == null) || map.getNodes().isEmpty()) {
            return;
        }

        if (sel != null) {
            int diameter = 15;
            Point coordssel = getCoordsToPixel(sel.getLongitude(),sel.getLatitude());
            g.drawOval((int) coordssel.getX()-diameter/2, (int) coordssel.getY()-diameter/2, diameter, diameter);
        }
        
        ratioX = (longitudesMax - longitudesMin) / this.getWidth();
        ratioY = (latitudesMax - latitudesMin) / this.getHeight();
        ratio = (ratioX > ratioY ? ratioX : ratioY);
        
        deltaX = (this.getWidth() - (longitudesMax-longitudesMin)/ratio)/2;
        deltaY = (this.getHeight() - (latitudesMax-latitudesMin)/ratio)/2;
        
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
    
    @Override
    public void mouseClicked(MouseEvent e)
    {
        Point2D coord = getPixelToPoint(e.getX(),e.getY());
        
        if ((map == null) || (map.getNodes() == null) || map.getNodes().isEmpty()) {
            return;
        }
        
        double closestdistance = -1;
        Node closest = new Node(0, e.getX(), e.getY());
        for (Node n : map.getNodes().values()) {
            double distance = Math.pow((coord.getX() - n.getLongitude()), 2)
                            + Math.pow((coord.getY() - n.getLatitude()), 2);
            if (closestdistance < 0 || distance < closestdistance) {
                closest = n;
                closestdistance = distance;
            }
        }

        sel = closest;

        this.repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    // ... other MouseListener methods ... //
}
