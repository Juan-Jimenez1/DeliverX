package co.edu.uniquindio.poo.deliverx.model.decorator;

import co.edu.uniquindio.poo.deliverx.model.Shipment;

import java.util.List;

public class PriorityDecorator extends ShipmentDecorator {
    private static final double PRIORITY_COST = 8000.0;

    public PriorityDecorator(ShipmentComponent shipment) {
        super(shipment);
    }

    @Override
    public double calculateCost() {
        return super.calculateCost() + PRIORITY_COST;
    }

    @Override
    public List<String> getExtras() {
        List<String> extras = super.getExtras();
        extras.add("PRIORITY");
        return extras;
    }
}
