package fr.insa.lyon.pld.agile.model;

/**
 *
 * @author scheah
 */
public class Passage {
    private final Section section;
    private double arrivalTime;

    public Passage(Section section, double arrivalTime) {
        this.section = section;
        this.arrivalTime = arrivalTime;
    }

    public Section getSection() {
        return section;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}
