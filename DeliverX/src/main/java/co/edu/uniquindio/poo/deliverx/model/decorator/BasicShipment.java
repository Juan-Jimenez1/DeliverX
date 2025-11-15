package co.edu.uniquindio.poo.deliverx.model.decorator;

import co.edu.uniquindio.poo.deliverx.model.Shipment;

public class BasicShipment implements ShipmentComponent{
    private Shipment shipment;
    private double baseCost;

    public BasicShipment(Shipment shipment, double baseCost) {
        this.shipment = shipment;
        this.baseCost = baseCost;
    }

    @Override
    public double calculateCost() {
        return baseCost;
    }

    @Override
    public String getDescription() {
        return "Envío básico";
    }

    public Shipment getShipment() {
        return shipment;
    }
}
