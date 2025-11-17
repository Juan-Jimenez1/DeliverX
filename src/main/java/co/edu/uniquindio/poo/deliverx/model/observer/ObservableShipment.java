package co.edu.uniquindio.poo.deliverx.model.observer;
import co.edu.uniquindio.poo.deliverx.model.Shipment;
import co.edu.uniquindio.poo.deliverx.model.state.ShipmentState;
import java.util.ArrayList;
import java.util.List;

public class ObservableShipment implements ShipmentSubject{
    private List<ShipmentObserver> observers = new ArrayList<>();
    private Shipment shipment;

    public ObservableShipment(Shipment shipment) {
        this.shipment = shipment;
    }

    @Override
    public void attach(ShipmentObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void detach(ShipmentObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(ShipmentState oldState, ShipmentState newState) {
        for (ShipmentObserver observer : observers) {
            observer.update(shipment, oldState, newState);
        }
    }

    public Shipment getShipment() {
        return shipment;
    }
}
