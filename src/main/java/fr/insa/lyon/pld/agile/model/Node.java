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
    
    /**
     * constructs a new node
     * @param id gets node id from xml file
     * @param latitude the latitude number from xml file
     * @param longitude the longitude number from xml file  
     */

    public Node(long id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.outgoingSections = new ArrayList<>();
    }
    
    /**
     * gets the node id
     * @return the node id
     */

    public long getId() {
        return id;
    }
    
    /**
     * gets the latitude of the node
     * @return the latitude
     */

    public double getLatitude() {
        return latitude;
    }
    
    /**
     * gets the longitude of the node
     * @return the longitude
     */

    public double getLongitude() {
        return longitude;
    }
    
    /**
     * gets the outgoing sections 
     * @return the list of sections
     */
    
    public List<Section> getOutgoingSections() {
        return Collections.unmodifiableList(outgoingSections);
    }
    
    /**
     * adds an outgoing section 
     * @param section a section
     */
    
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
        for (Section section : outgoingSections) {
            sectionJoiner.add("\n    " + section);
        }
        builder.append(sectionJoiner);
        builder.append("]}");
        
        return builder.toString();
    }

}
