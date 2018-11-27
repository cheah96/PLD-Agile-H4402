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
}
