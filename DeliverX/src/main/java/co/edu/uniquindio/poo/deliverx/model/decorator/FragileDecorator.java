package co.edu.uniquindio.poo.deliverx.model.decorator;
import co.edu.uniquindio.poo.deliverx.model.Shipment;

import java.util.List;

public class FragileDecorator extends ShipmentDecorator{
    private static final double FRAGILE_COST = 3000.0;

    public FragileDecorator(ShipmentComponent wrapped) {
        super(wrapped);
    }

    @Override
    public double calculateCost() {
        return super.calculateCost() + FRAGILE_COST;
    }

    @Override
    public List<String> getExtras() {
        return super.getExtras();
    }
}
