package fr.insa.lyon.pld.agile.model;

import java.time.LocalTime;

public class Passage {
    private final Section section;
    private LocalTime arrivalTime;
    

    /**
     * Constructs a passage
     * @param section the section corresponding to the passage
     * @param arrivalTime the time of arrival at the end of the section
     */
    public Passage(Section section, LocalTime arrivalTime) {
        this.section = section;
        this.arrivalTime = arrivalTime;
    }

    /**
     * Gets the passage's section.
     * @return the underlying section
     */
    public Section getSection() {
        return section;
    }

    
    /**
     * gets the arrival time of a section
     * @return the local time
     */
    
    public LocalTime getArrivalTime() {
        return arrivalTime;
    }
    
    /**
     * Sets the passage's arrival time.
     * @param arrivalTime the passage's arrival time
     */

    void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}
