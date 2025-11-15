package co.edu.uniquindio.poo.deliverx.model.decorator;

public class PriorityDecorator extends ShipmentDecorator{
    private static final double PRIORITY_COST = 8000.0;

    public PriorityDecorator(ShipmentComponent shipment) {
        super(shipment);
    }

    @Override
    public double calculateCost() {
        return super.calculateCost() + PRIORITY_COST;
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Entrega Prioritaria";
    }
}
