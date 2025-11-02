package co.edu.uniquindio.poo.deliverx.model;

import java.time.LocalDate;

public class Shipment {
    private String idShipment;
    private Address origin;
    private Address destination;
    private double weight;
    private DeliveryMan deliveryMan;
    private Customer customer;
    private LocalDate dateTime;
}
