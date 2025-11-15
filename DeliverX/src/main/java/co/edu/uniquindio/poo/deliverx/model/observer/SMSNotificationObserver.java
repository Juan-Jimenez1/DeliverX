package co.edu.uniquindio.poo.deliverx.model.observer;

import co.edu.uniquindio.poo.deliverx.model.Shipment;
import co.edu.uniquindio.poo.deliverx.model.state.ShipmentState;

public class SMSNotificationObserver implements ShipmentObserver {
    @Override
    public void update(Shipment shipment, ShipmentState oldState, ShipmentState newState) {
        System.out.println(" SMS sent:");
        System.out.println("   Number: " + shipment.getCustomer().getPhoneNumber());
        System.out.println("   Message: It's Shipment #" + shipment.getIdShipment() +
                " now is in: " + newState.getStateName() +"state");
        System.out.println();
    }
}
