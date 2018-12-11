package fr.insa.lyon.pld.agile.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 *
 * @author nmesnard
 */
public class Drawing {
    
    // https://stackoverflow.com/questions/3514158/how-do-you-clone-a-bufferedimage
    public static BufferedImage copyImage(BufferedImage image) {
        BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g = copy.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return copy;
    }
    
    public static Color getColor(int index, int count) {
        return Color.getHSBColor((float) index / count, 1.0f, 1.0f);
    }
    
    public static Color getColorBrighter(Color color) {
        float col[] = color.getRGBColorComponents(null);
        int r=(int)(col[0]*127), g=(int)(col[1]*127), b=(int)(col[2]*127);
        return new Color(r+128,g+128,b+128);
    }
    
    public static Color getColorDarker(Color color) {
        float col[] = color.getRGBColorComponents(null);
        int r=(int)(col[0]*127), g=(int)(col[1]*127), b=(int)(col[2]*127);
        return new Color(r,g,b);
    }
    
    protected static void drawLineThick(Graphics g, Point p1, Point p2) {
        for (int dx=-1; dx<=1; dx++) {
            for (int dy=-1; dy<=1; dy++) {
                g.drawLine(p1.x+dx, p1.y+dy, p2.x+dx, p2.y+dy);
            }
        }
    }
    protected static double drawLineArrows(Graphics g, Point p1, Point p2, double position) {
        double step = 150.;
        
        int vy = p2.y - p1.y;
        int vx = p2.x - p1.x;
        
        double x = p1.x;
        double y = p1.y;
        
        if ((vy == 0) && (vx == 0)) return position;
        
        double vlen = getVectorLength(vx,vy);
        
        int safety = 0;
        double total;
        double remaining = getPointsDistance(p1, p2);
        for (; (total = position+remaining) >= step;) {
            if (safety++ > 1000) return position;
            
            x += ((step-position)*vx)/vlen;
            y += ((step-position)*vy)/vlen;
            
            if ((vx*(p2.x-x) + vy*(p2.y-y)) < 0) return position;
            
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
    
    protected static double getPointsDistance(Point p1, Point p2) {
        int vx = p2.x - p1.x, vy = p2.y - p1.y;
        return getVectorLength(vx, vy);
    }
    protected static double getVectorLength(int vx, int vy) {
        return Math.sqrt((double) ((vx*vx)+(vy*vy)));
    }
    
    protected static void drawLine(Graphics g, Point p1, Point p2) {
        g.drawLine(p1.x, p1.y, p2.x, p2.y);
    }
    
    protected static void drawDot(Graphics g, Point coords, int diameter) {
        g.fillOval((int) coords.getX()-diameter/2, (int) coords.getY()-diameter/2, diameter, diameter);
    }
    
    protected static void drawHouse(Graphics g, Point coords, int size) {
        // g.fillPolygon(new int[] {coords.x-size/2, coords.x, coords.x+size/2}, new int[] {coords.y+size/2, coords.y+size, coords.y+size/2}, 3);
        g.fillRect(coords.x - size/2, coords.y - size/2, size, size);
    }
    
    protected static void drawWarehouse(Graphics g, Point coords) {
        g.setColor(Color.black);
        drawHouse(g, coords, 18);
    }
    
    protected static void drawDelivery(Graphics g, Point coords, Color color) {
        g.setColor(Drawing.getColorDarker(color));
        drawDot(g, coords, 11);
        g.setColor(color);
        drawDot(g, coords, 7);
    }
    
    protected static void drawSelectedNode(Graphics g, Point coords, Color color) {
        g.setColor(Color.black);
        drawDot(g, coords, 15);
        g.setColor(color);
        drawDot(g, coords, 11);
    }
    
}
