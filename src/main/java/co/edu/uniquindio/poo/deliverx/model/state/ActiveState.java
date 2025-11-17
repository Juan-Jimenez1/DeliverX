package co.edu.uniquindio.poo.deliverx.model.state;

import co.edu.uniquindio.poo.deliverx.model.DeliveryMan;

public class ActiveState implements DeliveryManState{
    @Override
    public void handle(DeliveryMan deliveryMan) {
        System.out.println("DeliveryMan " + deliveryMan.getName() + " It's active and available.");
    }

    @Override
    public String getStateName() {
        return "ACTIVE";
    }

    @Override
    public boolean canTransitionTo(DeliveryManState newState) {
        return newState instanceof InRouteDeliveryState || newState instanceof InactiveState;
    }
}
