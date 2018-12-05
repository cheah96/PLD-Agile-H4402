package fr.insa.lyon.pld.agile.model;

import java.time.LocalTime;

/**
 *
 * @author scheah
 */
public class Passage {
    private final Section section;
    private LocalTime arrivalTime;

    public Passage(Section section, LocalTime arrivalTime) {
        this.section = section;
        this.arrivalTime = arrivalTime;
    }

    public Section getSection() {
        return section;
    }

    public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}
