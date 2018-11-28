package fr.insa.lyon.pld.agile.model;

/**
 *
 * @author scheah
 */
public class Passage {
    private final Section section;
    private double arrivalTime;
    private double deliveryDuration;

    public Passage(Section section, double arrivalTime, double deliveryDuration) {
        this.section = section;
        this.arrivalTime = arrivalTime;
        this.deliveryDuration = deliveryDuration;
    }

    public Section getSection() {
        return section;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public double getDeliveryDuration() {
        return deliveryDuration;
    }

    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}
