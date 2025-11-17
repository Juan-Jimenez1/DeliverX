package co.edu.uniquindio.poo.deliverx.model.state;
import co.edu.uniquindio.poo.deliverx.model.DeliveryMan;

public class InRouteDeliveryState implements DeliveryManState{
    @Override
    public void handle(DeliveryMan deliveryMan) {
        System.out.println("Deliveryman " + deliveryMan.getName() + " It's in route with a shipment.");
    }

    @Override
    public String getStateName() {
        return "ROUTE";
    }

    @Override
    public boolean canTransitionTo(DeliveryManState newState) {
        return newState instanceof ActiveState || newState instanceof InactiveState;
    }

}
