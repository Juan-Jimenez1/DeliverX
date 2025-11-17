package co.edu.uniquindio.poo.deliverx.model.decorator;

import co.edu.uniquindio.poo.deliverx.model.Shipment;

import java.util.ArrayList;
import java.util.List;

public class BasicShipment implements ShipmentComponent{

    @Override
    public double calculateCost() {
        return 0;
    }

    @Override
    public List<String> getExtras() {
        return new ArrayList<>();
    }


}
