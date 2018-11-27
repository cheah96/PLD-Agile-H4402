package fr.insa.lyon.pld.agile.view;

import fr.insa.lyon.pld.agile.model.*;

import java.util.List;

/**
 *
 * @author nmesnard
 */
public interface MapView {
    
    public void setMap(Map newMap);
    
    public void setDeliveries(List<Delivery> newDeliveries);
    
}
