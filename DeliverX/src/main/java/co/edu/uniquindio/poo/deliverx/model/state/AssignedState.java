package co.edu.uniquindio.poo.deliverx.model.state;

import co.edu.uniquindio.poo.deliverx.model.Shipment;

public class AssignedState implements ShipmentState{
    @Override
    public void handle(Shipment shipment) {
        System.out.println("Shipment " + shipment.getIdShipment() + " assigned to deliveryman: " +
                (shipment.getDeliveryMan() != null ? shipment.getDeliveryMan().getName() : "Unassigned"));
    }

    @Override
    public String getStateName() {
        return "ASSIGNED";
    }

    @Override
    public boolean canTransitionTo(ShipmentState newState) {
        return newState instanceof InRouteState;
    }
}
