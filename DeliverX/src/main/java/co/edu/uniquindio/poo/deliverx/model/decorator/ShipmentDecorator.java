package co.edu.uniquindio.poo.deliverx.model.decorator;

public abstract class ShipmentDecorator implements ShipmentComponent{
    protected ShipmentComponent wrappedShipment;

    public ShipmentDecorator(ShipmentComponent shipment) {
        this.wrappedShipment = shipment;
    }

    @Override
    public double calculateCost() {
        return wrappedShipment.calculateCost();
    }

    @Override
    public String getDescription() {
        return wrappedShipment.getDescription();
    }
}
