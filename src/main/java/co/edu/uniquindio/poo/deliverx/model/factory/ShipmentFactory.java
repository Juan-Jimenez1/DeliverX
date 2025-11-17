package co.edu.uniquindio.poo.deliverx.model.factory;

import co.edu.uniquindio.poo.deliverx.model.Address;
import co.edu.uniquindio.poo.deliverx.model.Customer;
import co.edu.uniquindio.poo.deliverx.model.Shipment;
import co.edu.uniquindio.poo.deliverx.model.state.RequestedState;

import java.time.LocalDate;

public class ShipmentFactory {
    public static Shipment createShipment(
            String type,
            String shipmentId,
            Customer customer,
            Address origin,
            Address destination,
            double weight) {

        switch (type.toLowerCase()) {
            case "normal":
                return createNormalShipment(shipmentId, customer, origin, destination, weight);

            case "priority":
                return createPriorityShipment(shipmentId, customer, origin, destination, weight);

            default:
                throw new IllegalArgumentException("Invalid shipment type: " + type);
        }
    }

    private static Shipment createNormalShipment(
            String shipmentId,
            Customer customer,
            Address origin,
            Address destination,
            double weight) {

        Shipment shipment = new Shipment(
                shipmentId,
                origin,
                destination,
                weight,
                null, // Sin repartidor inicialmente
                customer,
                null, // Sin pago inicialmente
                LocalDate.now()
        );

        shipment.setCurrentState(new RequestedState());

        System.out.println("Normal Shipment created - ID: " + shipmentId);

        return shipment;
    }

    private static Shipment createPriorityShipment(
            String shipmentId,
            Customer customer,
            Address origin,
            Address destination,
            double weight) {

        Shipment shipment = new Shipment(
                shipmentId,
                origin,
                destination,
                weight,
                null,
                customer,
                null,
                LocalDate.now()
        );

        shipment.setCurrentState(new RequestedState());

        System.out.println(" Normal Shipment created - ID: " + shipmentId);

        return shipment;
    }


    public static Shipment createShipment(
            ShipmentType type,
            String shipmentId,
            Customer customer,
            Address origin,
            Address destination,
            double weight) {

        String typeString = type == ShipmentType.NORMAL ? "normal" : "priority";
        return createShipment(typeString, shipmentId, customer, origin, destination, weight);
    }

    public static String generateShipmentId() {
        return "ENV-" + System.currentTimeMillis();
    }

}
