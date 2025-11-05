package co.edu.uniquindio.poo.deliverx.model;

public class Address {
    private String addressId;
    private String street;
    private String city;
    private String type;
    private String latitude;
    private String longitude;


    public Address(String addressId, String street, String city, String type, String latitude, String longitude) {
        this.addressId = addressId;
        this.street = street;
        this.city = city;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getAddressId() {
        return addressId;
    }
    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }
    public String getStreet() {
        return street;
    }
    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
