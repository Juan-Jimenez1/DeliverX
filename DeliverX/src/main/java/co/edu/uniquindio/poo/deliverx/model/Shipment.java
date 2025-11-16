package co.edu.uniquindio.poo.deliverx.model;
import co.edu.uniquindio.poo.deliverx.ShipmentType;
import co.edu.uniquindio.poo.deliverx.model.state.RequestedState;
import co.edu.uniquindio.poo.deliverx.model.state.ShipmentState;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Shipment {
    private String idShipment;
    private Address origin;
    private Address destination;
    private double weight;
    private DeliveryMan deliveryMan;
    private Customer customer;
    private double price;
    private List<String> extraServices;
    private ShipmentType type;
    private Pay pay;
    private LocalDate dateTime;
    private ShipmentState currentState;
    private List<String> additionalServices = new ArrayList<>();


    public Shipment(String idShipment, Address origin, Address destination, double weight, DeliveryMan deliveryMan, Customer customer, LocalDate dateTime,ShipmentType type) {
        this.idShipment = idShipment;
        this.origin = origin;
        this.destination = destination;
        this.weight = weight;
        this.deliveryMan = deliveryMan;
        this.customer = customer;
        this.dateTime = dateTime;
        this.currentState = new RequestedState();
        this.price= 0;
        this.type = type;
        this.extraServices = new ArrayList<>();
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    public List<String> getAdditionalServices() {
        return additionalServices;
    }
    public void setAdditionalServices(List<String> additionalServices) {
        this.additionalServices = additionalServices;
    }
    public ShipmentType getType() {
        return type;
    }
    public void setType(ShipmentType type) {
        this.type = type;
    }
    public List<String> getExtraServices() {
        return extraServices;
    }
    public void setExtraServices(List<String> extraServices) {}
}
