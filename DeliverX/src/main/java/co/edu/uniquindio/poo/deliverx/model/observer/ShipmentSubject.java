package co.edu.uniquindio.poo.deliverx.model.observer;
import co.edu.uniquindio.poo.deliverx.model.state.ShipmentState;

public interface ShipmentSubject {
    void attach(ShipmentObserver observer);
    void notifyObservers(ShipmentState oldState, ShipmentState newState);

}
