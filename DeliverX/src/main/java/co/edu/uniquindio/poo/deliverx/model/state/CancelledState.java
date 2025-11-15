package co.edu.uniquindio.poo.deliverx.model.state;

import co.edu.uniquindio.poo.deliverx.model.Shipment;

public class CancelledState implements ShipmentState {
    @Override
    public void handle(Shipment shipment) {
        System.out.println("Shipment " + shipment.getIdShipment() + " it's been cancelled.");
    }

    @Override
    public String getStateName() {
        return "CANCELLED";
    }

    @Override
    public boolean canTransitionTo(ShipmentState newState) {
        return false; // Estado final
    }
}
