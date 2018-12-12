package fr.insa.lyon.pld.agile.model;

public class Section {
    public static final double SPEED = 15.*1000./3600.; //15 km/h => 15.*1000./3600./s
    
    private final String name;
    private final double length;
    private final Node destination;
    
    /**
     * constructs a new section 
     * @param name the name of the section
     * @param length the length in meters of the section
     * @param the final destination of the section
     */

    public Section(String name, double length, Node destination) {
        this.name = name;
        this.length = length;
        this.destination = destination;
    }
    
    /**
     * gets the name of the section 
     * @return the section name
     */
    
    public String getName() {
        return name;
    }
    
    /**
     * gets the length of the section 
     * @return the length of the section
     */

    public double getLength() {
        return length;
    }
    
    /**
     * gets the destination of the section 
     * @return the node of the final destination
     */

    public Node getDestination() {
        return destination;
    }
    
    /**
     * calculates the duration    
     * @return the duration in meters
     */
    
    public long getDuration() {
        return computeDuration(length);
    }

    @Override
    public String toString() {
        return "Section{name: " + getName() + ", length: " + getLength() + ", destination: " + getDestination().getId() +"}";
    }
    
    public static long computeDuration(double length) {
        return Math.round(length/SPEED);
    }
}
