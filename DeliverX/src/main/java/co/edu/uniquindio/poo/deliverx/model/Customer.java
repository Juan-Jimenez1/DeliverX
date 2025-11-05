package co.edu.uniquindio.poo.deliverx.model;

import java.util.ArrayList;
import java.util.List;

public class Customer extends User{
    private List<Address> listAddresses;
    private List<Shipment> shipmentList;

    public Customer(Builder<?> builder) {
        super(builder);
        this.listAddresses = new ArrayList<>();
        this.shipmentList = new ArrayList<>();
    }





    public static class Builder<T extends Builder<T>> extends User.Builder<T>{
        @Override
        public Customer build() {
            return new Customer(this);
        }
    }

    public List<Address> getListAddresses() {
        return listAddresses;
    }

    public void setListAddresses(List<Address> listAddresses) {
        this.listAddresses = listAddresses;
    }

    public List<Shipment> getShipmentList() {
        return shipmentList;
    }

    public void setShipmentList(List<Shipment> shipmentList) {
        this.shipmentList = shipmentList;
    }
}
