package co.edu.uniquindio.poo.deliverx.model.state;
import co.edu.uniquindio.poo.deliverx.model.DeliveryMan;

public interface DeliveryManState {
    void handle(DeliveryMan deliveryMan);
    String getStateName();
    boolean canTransitionTo(DeliveryManState newState);
}
