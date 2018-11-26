package fr.insa.lyon.pld.agile.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 *
 * @author scheah
 */
public class Node {
    private final long id;
    private final double latitude;
    private final double longitude;
    private final List<Section> outgoingSections;

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
    
    public void addOutgoingSection(Section section) {
        outgoingSections.add(section);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Node{id: ");
        builder.append(getId());
        builder.append(", latitude: ");
        builder.append(getLatitude());
        builder.append(", longitude: ");
        builder.append(getLongitude());
        builder.append(", outgoingSections: [");
        StringJoiner sectionJoiner = new StringJoiner(",");
        for (Section section : outgoingSections)
        {
            sectionJoiner.add("\n    " + section);
        }
        builder.append(sectionJoiner);
        builder.append("]}");
        
        return builder.toString();
    }

}
