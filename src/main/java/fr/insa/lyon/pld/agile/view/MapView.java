package fr.insa.lyon.pld.agile.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;

/**
 *
 * @author nmesnard
 */
public abstract class MapView extends JPanel implements PropertyChangeListener{
    
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if ("deliveries".equals(propertyName)) {
            updateDeliveries();
        } 
        if ("nodes".equals(propertyName)) {
            updateNodes();
        }
        if ("deliveryMen".equals(propertyName)) {
            updateDeliveryMen();
        }
        if ("startingHour".equals(propertyName)) {
            updateStartingHour();
        }
        if ("warehouse".equals(propertyName)) {
            updateWarehouse();
        }
    }
    
    public abstract void updateNodes();
    public abstract void updateDeliveries();
    public abstract void updateDeliveryMen();
    public abstract void updateStartingHour();
    public abstract void updateWarehouse();
    
    public abstract void showDeliveryManRound(int deliveryManIndex);
}
