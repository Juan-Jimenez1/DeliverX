package co.edu.uniquindio.poo.deliverx.model.state;

import co.edu.uniquindio.poo.deliverx.model.Shipment;

public class DeliveredState implements ShipmentState{
    @Override
    public void handle(Shipment shipment) {
        System.out.println("Shipment " + shipment.getIdShipment() + " has been successfully delivered.");
    }

    @Override
    public String getStateName() {
        return "DELIVERED";
    }

    @Override
    public boolean canTransitionTo(ShipmentState newState) {
        return false; // Estado final
    }
}
