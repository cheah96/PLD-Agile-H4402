package fr.insa.lyon.pld.agile.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;

/**
 *
 * @author nmesnard
 */
public class MapViewGraphicalLegend extends JPanel
{
    EmptyBorder spacer = new EmptyBorder(4, 4, 4, 4);
    
    /**
     * Creates a Legend.
     */
    public MapViewGraphicalLegend() {
        this.setLayout(new GridBagLayout());
        this.setBackground(Color.getHSBColor(0f, 0f, 7f/8));
        this.setBorder(spacer);
        
        GridBagConstraints c = new GridBagConstraints();
        c.weighty = 0; c.weightx = 0; c.gridx = 0; c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        
        JIcon icoWarehouse = new JIcon((g,coords) -> Drawing.drawWarehouse(g,coords));
        JLabel lblWarehouse = new JLabel("Entrepôt"); lblWarehouse.setBorder(spacer);
        this.add(icoWarehouse, c); c.gridx++;
        this.add(lblWarehouse, c); c.gridx=0; c.gridy++;
        
        JIcon icoDelivery = new JIcon((g,coords) -> Drawing.drawDelivery(g,coords, Color.gray));
        JLabel lblDelivery = new JLabel("Point de livraison"); lblDelivery.setBorder(spacer);
        this.add(icoDelivery, c); c.gridx++;
        this.add(lblDelivery, c); c.gridx=0; c.gridy++;
        
        JIcon icoSelected = new JIcon((g,coords) -> Drawing.drawSelectedNode(g,coords, Color.gray));
        JLabel lblSelected = new JLabel("Noeud sélectionné"); lblSelected.setBorder(spacer);
        this.add(icoSelected, c); c.gridx++;
        this.add(lblSelected, c); c.gridx=0; c.gridy++;
        
        JIcon icoSection = new JIcon((g,coords) -> Drawing.drawLine(g,
            new Point(coords.x/2,coords.y*3/4),
            new Point(coords.x*7/4,coords.y*6/4)
        ));
        JLabel lblSection = new JLabel("Tronçon"); lblSection.setBorder(spacer);
        this.add(icoSection, c); c.gridx++;
        this.add(lblSection, c); c.gridx=0; c.gridy++;
        
        JIcon icoJourney = new JIcon((g,coords) -> Drawing.drawLineThick(g,
            new Point(coords.x/2,coords.y*3/4),
            new Point(coords.x*7/4,coords.y*6/4)
        ));
        JLabel lblJourney = new JLabel("Trajet"); lblJourney.setBorder(spacer);
        this.add(icoJourney, c); c.gridx++;
        this.add(lblJourney, c); c.gridx=0; c.gridy++;
        
        
        this.setSize(this.getPreferredSize());
        
        DragListener drag = new DragListener();
        this.addMouseListener(drag);
        this.addMouseMotionListener(drag);
    }
    
    /**
     * drawing function wrapper
     */
    public interface Drawer {
        
        /**
         * icon drawing function
         * @param g object to draw on
         * @param coords coordinates of the middle of the component
         */
        public void draw(Graphics g, Point coords);
        
    }
    
    /**
     * custom icon component
     */
    public class JIcon extends JPanel {
        private final Drawer drawer;
        private final Point coords;
        
        /**
         * icon constructor
         * @param drawer icon drawing function
         */
        public JIcon(Drawer drawer) {
            this.drawer = drawer;
            this.setBorder(spacer);
            this.setPreferredSize(new Dimension(28,28));
            coords = new Point(14,14);
        }
        
        @Override
        public void paintComponent(Graphics g) {
            if (g instanceof Graphics2D)
                Drawing.enableAntialiasing((Graphics2D) g);
            g.setColor(Color.gray);
            drawer.draw(g, coords);
        }
        
    }
    
    /**
     * Makes a component draggable.
     */
    // https://tips4java.wordpress.com/2009/06/14/moving-windows/
    public class DragListener extends MouseInputAdapter
    {
        Point location;
        MouseEvent pressed;
        
        @Override
        public void mousePressed(MouseEvent me) {
            pressed = me;
        }
        
        @Override
        public void mouseDragged(MouseEvent me) {
            Component component = me.getComponent();
            location = component.getLocation(location);
            int x = location.x - pressed.getX() + me.getX();
            int y = location.y - pressed.getY() + me.getY();
            component.setLocation(x, y);
         }
    }
    
}
