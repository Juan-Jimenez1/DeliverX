package co.edu.uniquindio.poo.deliverx.model;

public class DeliveryMan extends User{
    private DeliveryManState state;
    private String zonaCobertura;
    private Shipment shipment;

    private DeliveryMan(Builder<?> builder){
        super(builder);
        this.state = builder.state;
        this.zonaCobertura = builder.zonaCobertura;
    }

//MODIFICAR, QUITAR BUILDER
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
