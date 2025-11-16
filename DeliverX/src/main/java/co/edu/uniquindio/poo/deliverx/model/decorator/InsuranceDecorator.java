package co.edu.uniquindio.poo.deliverx.model.decorator;

import co.edu.uniquindio.poo.deliverx.model.Shipment;

import java.util.List;

public class InsuranceDecorator extends ShipmentDecorator {
    private static final double INSURANCE_COST = 5000.0;

    public InsuranceDecorator(ShipmentComponent shipment) {
        super(shipment);
    }

    @Override
    public double calculateCost() {
        return super.calculateCost() + INSURANCE_COST;
    }

    @Override
    public List<String> getExtras() {
        return super.getExtras();
    }
}



