package co.edu.uniquindio.poo.deliverx.model;
import co.edu.uniquindio.poo.deliverx.model.facade.DeliverXFacade;
import co.edu.uniquindio.poo.deliverx.model.state.*;

public class DeliveryMan extends User{
    public DeliveryManState state;
    private String zonaCobertura;
    private Shipment currentShipment;
    private ShipmentState shipmentState;
    private DeliverXFacade deliverXFacade;

    private DeliveryMan(Builder<?> builder){
        super(builder);
        this.state = builder.state;
        this.zonaCobertura = builder.zonaCobertura;
        this.currentShipment = null;
        this.shipmentState = null; // Cambiado: inicializar como null en lugar de intentar acceder a currentShipment
        this.deliverXFacade= new DeliverXFacade();
    }

    public Shipment getCurrentShipment() {
        return currentShipment;
    }


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
            this.shipmentState = shipment.getCurrentState(); // Actualizar el estado cuando se asigna un envío
            changeState(new InRouteDeliveryState());
            return true;
        }
        System.out.println("Deliveryman already has an assigned Shipment");
        return false;
    }

    public void completeShipment() {
        this.currentShipment = null;
        this.shipmentState = null; // Limpiar el estado cuando se completa el envío
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

    public void processCashPayment(boolean customerPaid){
        if(customerPaid){
            currentShipment.getPay().setResult(TransactionResult.APPROVED);
            changeShipmentState( new DeliveredState());
            completeShipment();
        }else{
            currentShipment.getPay().setResult(TransactionResult.DECLINED);
            changeShipmentState( new DeliveredState());
            completeShipment();
        }
    }
    public boolean changeShipmentState(ShipmentState newState) {
        if (shipmentState.canTransitionTo(newState)) {
            ShipmentState oldState = this.shipmentState;
            this.shipmentState = newState;
            this.shipmentState.handle(currentShipment);
            return true;
        } else {
            System.out.println(" Non-allowed state transition: " +
                    state.getStateName() + " → " + newState.getStateName());
            return false;
        }
    }
}