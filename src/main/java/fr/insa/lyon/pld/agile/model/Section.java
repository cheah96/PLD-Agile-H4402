package fr.insa.lyon.pld.agile.model;

/**
 *
 * @author scheah
 */
public class Section {
    public static double SPEED = 15.*1000./3600.; //15 km/h => 15.*1000./3600./s
    
    private final String name;
    private final double length;
    private final Node destination;

    public Section(String name, double length, Node destination) {
        this.name = name;
        this.length = length;
        this.destination = destination;
    }

    public String getName() {
        return name;
    }

    public double getLength() {
        return length;
    }

    public Node getDestination() {
        return destination;
    }
    
    public long getDuration() {
        return (long) Math.round(length/SPEED);
    }

    @Override
    public String toString() {
        return "Section{name: " + getName() + ", length: " + getLength() + ", destination: " + getDestination().getId() +"}";
    }
}
