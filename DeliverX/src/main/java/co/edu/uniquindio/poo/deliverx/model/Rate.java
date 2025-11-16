package co.edu.uniquindio.poo.deliverx.model;

import co.edu.uniquindio.poo.deliverx.model.Strategy.RateStrategy;
import co.edu.uniquindio.poo.deliverx.model.decorator.*;

import java.util.List;

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

        double baseCost = strategy.calculate(origin, destination, weight);
        ShipmentComponent component = new BasicShipment(shipment, baseCost);
        component = applyAdditionalServices(component, additionalServices);
        component.calculateCost();


        return shipment.getPrice(); //0
    }

    /**
     * Aplica los decoradores correctamente.
     */
    private ShipmentComponent applyAdditionalServices(ShipmentComponent component, Shipment shipment) {
        if (services == null) return component;

        for (String s : services) {
            switch (s.toUpperCase()) {
                case "FRAGILE":
                    component = new FragileDecorator(component);
                    break;

                case "INSURANCE":
                    component = new InsuranceDecorator(component);
                    break;

                case "PRIORITY":
                    component = new PriorityDecorator(component);
                    break;

                case "SIGNATURE":
                    component = new SignatureRequiredDecorator(component);
                    break;

                case "PACKAGING":
                    component = new SpecialPackagingDecorator(component);
                    break;
            }
        }

        shipment.setAdditionalServices(component.getDescription());
    }
}




