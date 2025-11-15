package co.edu.uniquindio.poo.deliverx.model.observer;

import co.edu.uniquindio.poo.deliverx.model.Shipment;
import co.edu.uniquindio.poo.deliverx.model.state.ShipmentState;

public interface ShipmentObserver {
    void update(Shipment shipment, ShipmentState oldState, ShipmentState newState);
}
