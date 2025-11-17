package co.edu.uniquindio.poo.deliverx.model.observer;

import co.edu.uniquindio.poo.deliverx.model.Shipment;
import co.edu.uniquindio.poo.deliverx.model.state.ShipmentState;

public class EmailNotificationObserver implements ShipmentObserver{
    @Override
    public void update(Shipment shipment, ShipmentState oldState, ShipmentState newState) {
        System.out.println("EMAIL sent to the Client");
        System.out.println("   Shipment #" + shipment.getIdShipment());
        System.out.println("   State changed from: " + (oldState != null ? oldState.getStateName() : "NONE") +
                " → " + newState.getStateName());
        System.out.println("   Addressee: " + shipment.getCustomer().getEmail());
        System.out.println();
    }


}
