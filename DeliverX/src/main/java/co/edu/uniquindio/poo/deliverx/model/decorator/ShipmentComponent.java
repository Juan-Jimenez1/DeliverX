package co.edu.uniquindio.poo.deliverx.model.decorator;

import java.util.List;

public interface ShipmentComponent {
    double calculateCost();
    List<String> getExtras();
}

