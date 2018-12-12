package fr.insa.lyon.pld.agile.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Route {
    private final List<Passage> passages;
    private LocalTime departureTime;
    private boolean delivering;
    
    public Route(LocalTime departureTime, boolean delivering) {
        this.departureTime = departureTime;
	this.passages = new ArrayList<>();
	this.delivering = delivering;
    }
    
    public Node getDestination() {
        if (!passages.isEmpty()) {
            return passages.get(passages.size()-1).getSection().getDestination();
        } else {
            return null;
        }
    }
    
    public List<Passage> getPassages() {
        return Collections.unmodifiableList(passages);
    }
    
    public boolean isDelivering() {
        return delivering;
    }
    
    public LocalTime getArrivalTime() {
        if (!passages.isEmpty()) {
            return passages.get(passages.size()-1).getArrivalTime();
        } else {
            return departureTime;
        }
    }

    void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
        LocalTime arrivalTime = departureTime;
        for (Passage passage : passages) {
            arrivalTime = arrivalTime.plusSeconds(passage.getSection().getDuration());
            passage.setArrivalTime(arrivalTime);
        }
    }
    
    void addPassage(Section section) {
        LocalTime arrivalTime;
        if (!passages.isEmpty()) {
            arrivalTime = passages.get(passages.size()-1).getArrivalTime();
        } else {
            arrivalTime = departureTime;
        }
        arrivalTime = arrivalTime.plusSeconds(section.getDuration());
        passages.add(new Passage(section, arrivalTime));
    }
    
    void addPassages(List<Section> sections) {
        for (Section section : sections) {
            addPassage(section);
        }
    }
    
    void setDelivering(boolean delivering) {
	this.delivering = delivering;
    }
}
