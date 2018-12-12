package fr.insa.lyon.pld.agile.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class Drawing {
    
    /**
     * Creates a copy of the image. 
     * @param image the image to copy  
     * @return a copy of the original image 
     */
    public static BufferedImage copyImage(BufferedImage image) {
        // https://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage
        BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g = copy.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return copy;
    }
    
    /**
     * Enables antialiasing on a graphics object.
     * @param g graphics object to enable antialiasing on
     */
    public static void enableAntialiasing(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
    
    /**
     * Gets the color for a delivery man  
     * @param index the number of the delivery man
     * @param count the total number of delivery men
     * @return the corresponding color to this delivery man
     */
    public static Color getColor(int index, int count) {
        return Color.getHSBColor((float) index / count, 1.0f, 1.0f);
    }
    
    /**
     * Creates an enlightened version of the given color
     * @param color the color to enlighten
     * @return the enlightened version of the color
     */
    public static Color getColorBrighter(Color color) {
        float col[] = color.getRGBColorComponents(null);
        int r=(int)(col[0]*127), g=(int)(col[1]*127), b=(int)(col[2]*127);
        return new Color(r+128,g+128,b+128);
    }
    
    /**
     * Creates a darker version of the given color
     * @param color the color to enlighten
     * @return the darker version of the color
     */
    public static Color getColorDarker(Color color) {
        float col[] = color.getRGBColorComponents(null);
        int r=(int)(col[0]*127), g=(int)(col[1]*127), b=(int)(col[2]*127);
        return new Color(r,g,b);
    }
    
    /**
     * draw a thick line between two points
     * @param g the object to draw on
     * @param p1 the first point  
     * @param p2 the second point 
     */
    protected static void drawLineThick(Graphics g, Point p1, Point p2) {
        for (int dx=-1; dx<=1; dx++) {
            for (int dy=-1; dy<=1; dy++) {
                g.drawLine(p1.x+dx, p1.y+dy, p2.x+dx, p2.y+dy);
            }
        }
    }

    /**
     * displays the arrows on the line between two points
     * @param g the object to draw on
     * @param p1 the first point
     * @param p2 the second point
     * @param position distance before next arrow 
     * @return the distance before the next arrow for the next line
     */
    protected static double drawLineArrows(Graphics g, Point p1, Point p2, double position) {
        double step = 150.;
        
        int vy = p2.y - p1.y;
        int vx = p2.x - p1.x;
        
        double x = p1.x;
        double y = p1.y;
        
        if ((vy == 0) && (vx == 0)) {
            return position;
        }
        
        double vlen = getVectorLength(vx,vy);
        
        int safety = 0;
        double total;
        double remaining = getPointsDistance(p1, p2);
        for (; (total = position+remaining) >= step;) {
            if (safety++ > 1000) {
                return position;
            }
            
            x += ((step-position)*vx)/vlen;
            y += ((step-position)*vy)/vlen;
            
            if ((vx*(p2.x-x) + vy*(p2.y-y)) < 0) {
                return position;
            }
            
            drawLineThick(g,
                new Point((int)(x+(-vy-vx)*8./vlen), (int)(y+(vx-vy)*8./vlen)),
                new Point((int)x, (int)y)
            );
            drawLineThick(g,
                new Point((int) x, (int)y),
                new Point((int)(x+((vy-vx)*8./vlen)), (int)(y+(-vx-vy)*8./vlen))
            );
            
            position = 0;
        }
        
        return total; 
    }
    
    /**
     * gets the distance between two points
     * @param p1 the first point
     * @param p2 the second point
     * @return the distance
     */
    protected static double getPointsDistance(Point p1, Point p2) {
        int vx = p2.x - p1.x, vy = p2.y - p1.y;
        return getVectorLength(vx, vy);
    }
    
    /**
     * gets the length of the vector between two vectors
     * @param vx the first vector
     * @param vy the second vector
     * @return the vector
     */
    protected static double getVectorLength(int vx, int vy) {
        return Math.sqrt(((vx*vx)+(vy*vy)));
    }
    
    /**
     * draws a line between two points
     * @param g the object to be drawn on
     * @param p1 the first point
     * @param p2 the second point
     */
    protected static void drawLine(Graphics g, Point p1, Point p2) {
        g.drawLine(p1.x, p1.y, p2.x, p2.y);
    }
    
    /**
     * draws a dot with a selected size on the chosen coordinates 
     * @param g the object to be drawn on
     * @param coords the coordinates of the points
     * @param diameter the diameter for the dot drawn
     */
    protected static void drawDot(Graphics g, Point coords, int diameter) {
        g.fillOval((int) coords.getX()-diameter/2, (int) coords.getY()-diameter/2, diameter, diameter);
    }
    
    /**
     * draws the house
     * @param g the object to be drawn on
     * @param coords the coordinates of the house
     * @param size the size of the house
     */
    protected static void drawHouse(Graphics g, Point coords, int size) {
        // g.fillPolygon(new int[] {coords.x-size/2, coords.x, coords.x+size/2}, new int[] {coords.y+size/2, coords.y+size, coords.y+size/2}, 3);
        g.fillRect(coords.x - size/2, coords.y - size/2, size, size);
    }
    
    /**
     * draws the warehouse
     * @param g the object to be drawn on
     * @param coords the coordinates of the warehouse
     */
    protected static void drawWarehouse(Graphics g, Point coords) {
        g.setColor(Color.black);
        drawHouse(g, coords, 18);
    }
    
    /**
     * draws the deliveries
     * @param g the object to be drawn on
     * @param coords the coordinates of the deliveries
     */
    protected static void drawDelivery(Graphics g, Point coords, Color color) {
        g.setColor(Drawing.getColorDarker(color));
        drawDot(g, coords, 11);
        g.setColor(color);
        drawDot(g, coords, 7);
    }
    
    /**
     * draws the selected node 
     * @param g the object to be drawn on
     * @param color the selected color of the node
     * @param coords the coordinates of the nodes
     */
    protected static void drawSelectedNode(Graphics g, Point coords, Color color) {
        g.setColor(Color.black);
        drawDot(g, coords, 15);
        g.setColor(color);
        drawDot(g, coords, 11);
    }
    
}
