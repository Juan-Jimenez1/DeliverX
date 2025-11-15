package co.edu.uniquindio.poo.deliverx.model.state;
import co.edu.uniquindio.poo.deliverx.model.DeliveryMan;

public class InactiveState implements DeliveryManState{
    @Override
    public void handle(DeliveryMan deliveryMan) {
        System.out.println("Deliveryman " + deliveryMan.getName() + " it's inactive.");
    }

    @Override
    public String getStateName() {
        return "INACTIVE";
    }

    @Override
    public boolean canTransitionTo(DeliveryManState newState) {
        return newState instanceof ActiveState;
    }

}
