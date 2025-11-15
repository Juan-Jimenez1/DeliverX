package co.edu.uniquindio.poo.deliverx.model.state;

import co.edu.uniquindio.poo.deliverx.model.Shipment;

public class InRouteState implements ShipmentState{
    @Override
    public void handle(Shipment shipment) {
        System.out.println("Shipment " + shipment.getIdShipment() + " is on its way to its destination.");
    }

    @Override
    public String getStateName() {
        return "IN_ROUTE";
    }

    @Override
    public boolean canTransitionTo(ShipmentState newState) {
        return newState instanceof DeliveredState;
    }
}
