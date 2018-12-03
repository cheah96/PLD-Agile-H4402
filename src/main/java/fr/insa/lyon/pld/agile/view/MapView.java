package fr.insa.lyon.pld.agile.view;

import fr.insa.lyon.pld.agile.model.Node;

import javax.swing.JPanel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 *
 * @author nmesnard
 */
public abstract class MapView extends JPanel implements PropertyChangeListener {
    
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
            case "startingHour":
                updateStartingHour();
                break;
            case "warehouse":
                updateWarehouse();
                break;
        }
    }
    
    public abstract void updateNodes();
    public abstract void updateDeliveries();
    public abstract void updateDeliveryMen();
    public abstract void updateStartingHour();
    public abstract void updateWarehouse();
    
    public abstract void selectNode(Node node);
    public abstract void selectDeliveryMan(int deliveryManIndex);
    
}
