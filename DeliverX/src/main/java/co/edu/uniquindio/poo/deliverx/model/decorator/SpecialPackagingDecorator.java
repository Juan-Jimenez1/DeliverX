package co.edu.uniquindio.poo.deliverx.model.decorator;

public class SpecialPackagingDecorator extends ShipmentDecorator{
    private static final double PACKAGING_COST = 4000.0;

    public SpecialPackagingDecorator(ShipmentComponent shipment) {
        super(shipment);
    }

    @Override
    public double calculateCost() {
        return super.calculateCost() + PACKAGING_COST;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Empaque Especial";
    }
}
