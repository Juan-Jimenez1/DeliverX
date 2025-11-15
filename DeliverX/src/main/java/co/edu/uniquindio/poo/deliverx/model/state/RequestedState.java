package co.edu.uniquindio.poo.deliverx.model.state;

import co.edu.uniquindio.poo.deliverx.model.Shipment;

public class RequestedState implements ShipmentState{
    @Override
    public void handle(Shipment shipment) {
        System.out.println("Shipment " + shipment.getIdShipment() + " It has been requested and is awaiting assignment.");
    }

    @Override
    public String getStateName() {
        return "REQUESTED";
    }

    @Override
    public boolean canTransitionTo(ShipmentState newState) {
        return newState instanceof AssignedState || newState instanceof CancelledState;
    }
}
