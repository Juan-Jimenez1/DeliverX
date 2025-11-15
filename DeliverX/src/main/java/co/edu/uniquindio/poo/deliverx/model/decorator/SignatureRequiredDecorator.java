package co.edu.uniquindio.poo.deliverx.model.decorator;

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
    public String getDescription() {
        return super.getDescription() + " + Firma Requerida";
    }
}
