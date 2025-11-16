package co.edu.uniquindio.poo.deliverx.model;
import co.edu.uniquindio.poo.deliverx.model.state.ActiveState;
import co.edu.uniquindio.poo.deliverx.model.state.DeliveryManState;
import co.edu.uniquindio.poo.deliverx.model.state.InRouteDeliveryState;

public class DeliveryMan extends User{
    private DeliveryManState state;
    private String zonaCobertura;
    private Shipment currentShipment;

    private DeliveryMan(Builder<?> builder){
        super(builder);
        this.state = builder.state;
        this.zonaCobertura = builder.zonaCobertura;
    }

//MODIFICAR, QUITAR BUILDER// No //oka
    public static class Builder<T extends Builder<T>> extends User.Builder<T>{
        private DeliveryManState state;
        private String zonaCobertura;

        public T state(DeliveryManState state) {
            this.state = state;
            return self();
        }

        public T zonaCobertura(String zonaCobertura) {
            this.zonaCobertura = zonaCobertura;
            return self();
        }

        @Override
        public DeliveryMan build() {
            return new DeliveryMan(this);
        }
    }

    public boolean changeState(DeliveryManState newState) {
        if (state.canTransitionTo(newState)) {
            DeliveryManState oldState = this.state;
            this.state = newState;
            this.state.handle(this);
            return true;
        } else {
            System.out.println(" Non-allowed state transition: " +
                    state.getStateName() + " → " + newState.getStateName());
            return false;
        }
    }

    public boolean assignShipment(Shipment shipment) {
        if (this.currentShipment == null) {
            this.currentShipment = shipment;
            changeState(new InRouteDeliveryState());
            return true;
        }
        System.out.println("Deliveryman already has an assigned Shipment");
        return false;
    }

    public void completeShipment() {
        this.currentShipment = null;
        changeState(new ActiveState());
    }

    public DeliveryManState getState() {
        return state;
    }

    public void setState(DeliveryManState state) {
        this.state = state;
    }

    public String getZonaCobertura() {
        return zonaCobertura;
    }

    public void setZonaCobertura(String zonaCobertura) {
        this.zonaCobertura = zonaCobertura;
    }
}
