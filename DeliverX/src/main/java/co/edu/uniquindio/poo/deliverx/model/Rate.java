package co.edu.uniquindio.poo.deliverx.model;

import co.edu.uniquindio.poo.deliverx.model.Strategy.RateStrategy;
import co.edu.uniquindio.poo.deliverx.model.decorator.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rate {
    private Shipment shipment;

    public Rate(Shipment shipment) {
        this.shipment = shipment;
    }

    public double calculateCost(RateStrategy strategy, List<String> additionalServices) {

        Address origin = shipment.getOrigin();
        Address destination = shipment.getDestination();
        double weight = shipment.getWeight();

        if (origin == null || destination == null) {
            throw new IllegalArgumentException("Origin and destination cannot be null");
        }

        // 1. Calcular tarifa base con Strategy
        double baseCost = strategy.calculate(origin, destination, weight);

        // 2. Crear BasicShipment (RAÍZ del decorador)
        ShipmentComponent root = new BasicShipment(shipment, baseCost);

        // 3. Aplicar decoradores
        ShipmentComponent decorated = applyAdditionalServices(root, additionalServices);


        decorated.calculateCost();

        return shipment.getPrice();
    }




















    /**
     * Aplica los decoradores correctamente.
     */
    private void applyAdditionalServices(ShipmentComponent component,List<String> services) {
       if (services == null) return;

    //for (String s : services) {
    //switch (s.toUpperCase()) {
    //case "FRAGILE":
    //component = new FragileDecorator(component);
    //break;

    //case "INSURANCE":
    //component = new InsuranceDecorator(component);
    //break;

    //case "PRIORITY":
    //component = new PriorityDecorator(component);
    //break;

    //case "SIGNATURE":
    //component = new SignatureRequiredDecorator(component);
    //break;

    //case "PACKAGING":
    //component = new SpecialPackagingDecorator(component);
    //break;

//}

//shipment.setAdditionalServices(component.getExtras());
//}
}






