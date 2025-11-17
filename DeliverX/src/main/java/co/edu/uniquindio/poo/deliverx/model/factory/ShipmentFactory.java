package co.edu.uniquindio.poo.deliverx.model.factory;

import co.edu.uniquindio.poo.deliverx.model.ShipmentType;
import co.edu.uniquindio.poo.deliverx.model.*;
import co.edu.uniquindio.poo.deliverx.model.Strategy.RateStrategy;
import co.edu.uniquindio.poo.deliverx.model.facade.DeliverXFacade;

import java.util.List;

public class ShipmentFactory {
   static DeliverXFacade deliverXFacade = new DeliverXFacade();
    public static Shipment createShipment(
            String type,
            String shipmentId,
            Customer customer,
            Address origin,
            Address destination,
            double weight, RateStrategy rateStrategy, List<String> services, PaymentMethod paymentMethod) {

        switch (type.toLowerCase()) {
            case "normal":
                return deliverXFacade.createNormalCompleteShipment(shipmentId, customer, origin, destination, weight, rateStrategy, services, paymentMethod, ShipmentType.NORMAL);

            case "express":
                return deliverXFacade.createExpressCompleteShipment(shipmentId, customer, origin, destination, weight, rateStrategy, services, paymentMethod, ShipmentType.EXPRESS);

            default:
                throw new IllegalArgumentException("Invalid shipment type: " + type);
        }
    }

}
