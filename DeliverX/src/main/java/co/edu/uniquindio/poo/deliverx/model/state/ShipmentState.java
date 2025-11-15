package co.edu.uniquindio.poo.deliverx.model.state;

import co.edu.uniquindio.poo.deliverx.model.Shipment;

public interface ShipmentState {
    void handle(Shipment shipment);
    String getStateName();
    boolean canTransitionTo(ShipmentState newState);

}
