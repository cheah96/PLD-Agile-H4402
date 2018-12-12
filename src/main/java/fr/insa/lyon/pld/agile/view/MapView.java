package fr.insa.lyon.pld.agile.view;

import fr.insa.lyon.pld.agile.model.Node;

import javax.swing.JPanel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author nmesnard
 */
public abstract class MapView extends JPanel implements PropertyChangeListener
{
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        switch (propertyName) {
            case "deliveries":
                updateDeliveries();
                break;
            case "nodes":
                updateNodes();
                break;
            case "deliveryMen":
                updateDeliveryMen();
                break;
            case "deliveryMan":
                updateDeliveryMan();
                break;
            case "startingHour":
                updateStartingHour();
                break;
            case "warehouse":
                updateWarehouse();
                break;
        }
    }
    
    /**
     * updates the nodes
     */
    public abstract void updateNodes();

    /**
     * updates the deliveries
     */
    public abstract void updateDeliveries();

    /**
     * updates delivery men
     */
    public abstract void updateDeliveryMen();

    /**
     * updates delivery man
     */
    public abstract void updateDeliveryMan();

    /**
     * updates the starting hour
     */
    public abstract void updateStartingHour();

    /**
     * updates the warehouse
     */
    public abstract void updateWarehouse();
    
    /**
     * selects the nodes 
     * @param node write node to select
     */
    public abstract void selectNode(Node node);

    /**
     *select the delivery man
     * @param deliveryManIndex gets the associated number of the delivery man
     */
    public abstract void selectDeliveryMan(int deliveryManIndex);
    
}
