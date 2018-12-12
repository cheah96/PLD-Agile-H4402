package fr.insa.lyon.pld.agile.model;

/**
 *
 * @author scheah
 */
public class Delivery {
    private final Node node;
    private final int duration;
    private DeliveryMan deliveryMan;
    
    /**
     * constructs a delivery object 
     */

    public Delivery(Node node, int duration) {
        this(node, duration, null);
    }
    /**
     * delivery constructor
     */
    
    public Delivery(Node node, int duration, DeliveryMan deliveryMan) {
        this.node = node;
        this.duration = duration;
        this.deliveryMan = deliveryMan;
    }
    
    /**
     * gets the node attribute of delivery
     * @return node returns the node
     */

    public Node getNode() {
        return node;
    }
    
    /**
     * gets the duration of the delivery
     * @return the constant duration time
     */

    public int getDuration() {
        return duration;
    }
    
    /**
     * gets the delivery man for the delivery
     * @return the delivery man  
     */

    public DeliveryMan getDeliveryMan() {
        return deliveryMan;
    }
    
    /**
     * assigns a delivery man for the delivery
     * @param deliveryMan the delivery man
     */

    void setDeliveryMan(DeliveryMan deliveryMan) {
        this.deliveryMan = deliveryMan;
    }
    
    @Override
    public String toString() {
        return "Delivery{address: " + (node != null ? node.getId() : "null")
                + ", duration: " + duration
                + ", deliveryMan: " + (deliveryMan != null ? deliveryMan.getId() : "null")
                +"}";
    }
}
