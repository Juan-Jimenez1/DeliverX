package co.edu.uniquindio.poo.deliverx.model.decorator;

import java.util.List;

public abstract class ShipmentDecorator implements ShipmentComponent{
    protected ShipmentComponent wrappedShipment;

    public ShipmentDecorator(ShipmentComponent shipment) {
        this.wrappedShipment = shipment;
    }

    @Override
    public double calculateCost() {
        wrappedShipment.calculateCost();
    }
//Metete a la llamada

    @Override
    public List<String> getExtras() {
       wrappedShipment.getExtras();
    }
}
