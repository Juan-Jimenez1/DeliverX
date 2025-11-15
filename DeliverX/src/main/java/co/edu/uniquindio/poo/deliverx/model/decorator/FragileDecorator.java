package co.edu.uniquindio.poo.deliverx.model.decorator;

public class FragileDecorator extends ShipmentDecorator{
    private static final double FRAGILE_COST = 3000.0;

    public FragileDecorator(ShipmentComponent shipment) {
        super(shipment);
    }

    @Override
    public double calculateCost() {
        return super.calculateCost() + FRAGILE_COST;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Manejo Frágil";
    }

}
