package co.edu.uniquindio.poo.deliverx.model;

import co.edu.uniquindio.poo.deliverx.model.Strategy.RateStrategy;
import co.edu.uniquindio.poo.deliverx.model.decorator.*;

import java.util.List;

public class Rate {
    private Shipment shipment;

    public Rate(Shipment shipment) {
        this.shipment = shipment;
    }

    public void applyExtras(RateStrategy strategy, List<String> additionalServices) {


        //Crear BasicShipment (RAÍZ del decorador)
        ShipmentComponent root = new BasicShipment();
        double extraPrice = calculateExtraPrice(root, additionalServices);
        shipment.setPrice(shipment.getPrice() + extraPrice);
        applyAdditionalServices(root, additionalServices);
    }


    public double calculateExtraPrice(ShipmentComponent component, List<String> services) {
        if (services == null) return 0;

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
        return component.calculateCost();
    }


    /**
     * Aplica los decoradores correctamente.
     */
    private void applyAdditionalServices(ShipmentComponent component,List<String> services) {
        if (services == null) return;

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
        shipment.setAdditionalServices(component.getExtras());
    }
}






