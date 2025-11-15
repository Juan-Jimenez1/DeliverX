package co.edu.uniquindio.poo.deliverx.model;
import co.edu.uniquindio.poo.deliverx.model.state.RequestedState;
import co.edu.uniquindio.poo.deliverx.model.state.ShipmentState;
import java.time.LocalDate;

public class Shipment {
    private String idShipment;
    private Address origin;
    private Address destination;
    private double weight;
    private DeliveryMan deliveryMan;
    private Customer customer;
    private Pay pay;
    private LocalDate dateTime;
    private ShipmentState currentState;


    public Shipment(String idShipment, Address origin, Address destination, double weight, DeliveryMan deliveryMan, Customer customer, Pay pay, LocalDate dateTime) {
        this.idShipment = idShipment;
        this.origin = origin;
        this.destination = destination;
        this.weight = weight;
        this.deliveryMan = deliveryMan;
        this.customer = customer;
        this.pay = pay;
        this.dateTime = dateTime;
        this.currentState = new RequestedState();
    }

    public boolean changeState(ShipmentState newState) {
        if (currentState.canTransitionTo(newState)) {
            ShipmentState oldState = this.currentState;
            this.currentState = newState;
            this.currentState.handle(this);
            return true;
        } else {
            System.out.println("Non-allowed state transition: " +
                    currentState.getStateName() + " → " + newState.getStateName());
            return false;
        }
    }

    public String getIdShipment() {
        return idShipment;
    }

    public void setIdShipment(String idShipment) {
        this.idShipment = idShipment;
    }

    public Address getOrigin() {
        return origin;
    }

    public void setOrigin(Address origin) {
        this.origin = origin;
    }

    public Address getDestination() {
        return destination;
    }

    public void setDestination(Address destination) {
        this.destination = destination;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public DeliveryMan getDeliveryMan() {
        return deliveryMan;
    }

    public void setDeliveryMan(DeliveryMan deliveryMan) {
        this.deliveryMan = deliveryMan;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Pay getPay() {
        return pay;
    }

    public void setPay(Pay pay) {
        this.pay = pay;
    }

    public LocalDate getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDate dateTime) {
        this.dateTime = dateTime;
    }

    public ShipmentState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(ShipmentState currentState) {
        this.currentState = currentState;
    }
}
