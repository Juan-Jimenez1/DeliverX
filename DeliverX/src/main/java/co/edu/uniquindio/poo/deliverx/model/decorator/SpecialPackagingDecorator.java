package co.edu.uniquindio.poo.deliverx.model.decorator;

import co.edu.uniquindio.poo.deliverx.model.Shipment;

import java.util.List;

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
    public List<String> getExtras() {
        List<String> extras = super.getExtras();
        extras.add("SPECIAL PACKAGING");
        return extras;
    }
}


