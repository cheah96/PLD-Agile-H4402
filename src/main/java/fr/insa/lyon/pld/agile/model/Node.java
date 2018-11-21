package fr.insa.lyon.pld.agile.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author scheah
 */
public class Node {
    private final long id;
    private final double latitude;
    private final double longitude;
    private List<Section> outgoingSections;

    public Node(long id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.outgoingSections = new ArrayList();
    }

    public long getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
    
    public List<Section> getOutgoingSections() {
        return Collections.unmodifiableList(outgoingSections);
    }
}
