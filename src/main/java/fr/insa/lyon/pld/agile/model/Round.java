package fr.insa.lyon.pld.agile.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author scheah
 */
public class Round {
    private final List<Passage> itinerary;

    public Round() {
        this.itinerary = new ArrayList<>();
    }

    public List<Passage> getItinerary() {
        return Collections.unmodifiableList(itinerary);
    }
    
    void addPassage(Section section, double deliveryDuration) {
        double arrivalTime = section.getLength()/1000./15.*60.*60.;
        if (!itinerary.isEmpty()) {
            Passage last = itinerary.get(itinerary.size()-1);
            arrivalTime += last.getArrivalTime() + last.getDeliveryDuration();
        }
        Passage passage = new Passage(section, arrivalTime, deliveryDuration);
        itinerary.add(passage);
    }
    
    void clear() {
        itinerary.clear();
    }
}
