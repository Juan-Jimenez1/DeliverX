package co.edu.uniquindio.poo.deliverx.model.decorator;

import co.edu.uniquindio.poo.deliverx.model.Shipment;

import java.util.List;

public class SignatureRequiredDecorator extends ShipmentDecorator {
    private static final double SIGNATURE_COST = 2000.0;

    public SignatureRequiredDecorator(ShipmentComponent shipment) {
        super(shipment);
    }

    @Override
    public double calculateCost() {
        return super.calculateCost() + SIGNATURE_COST;
    }

    @Override
    public List<String> getExtras() {
        return super.getExtras();
    }
}
