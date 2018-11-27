package fr.insa.lyon.pld.agile.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author scheah
 */
public class DeliveryMan {
    private final int id;
    private final Round round;
    private final List<Delivery> deliveries;

    public DeliveryMan(int id) {
        this.id = id;
        this.round = new Round();
        this.deliveries = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public Round getRound() {
        return round;
    }

    public List<Delivery> getDeliveries() {
        return Collections.unmodifiableList(deliveries);
    }
    
    void addDelivery(Delivery delivery) {
        if (deliveries.contains(delivery))
            throw new RuntimeException("DeliveryMan already deliver there"); //TODO : Better error handling
        deliveries.add(delivery);
        //TODO : compute round
    }
    
}
