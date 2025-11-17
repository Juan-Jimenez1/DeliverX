package co.edu.uniquindio.poo.deliverx.model;

public class Address {
    private String addressId;
    private String street;
    private String city;
    private String type;
    private double latitude;
    private double longitude;


    public Address(String addressId, String street, String city, String type, double latitude, double longitude) {
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Address=" +
                "Address ID:'" + addressId + '\'' +
                ", Street='" + street + '\'' +
                ", City='" + city + '\'' +
                ", Address Type='" + type + '\'' +
                ", Latitude=" + latitude +
                ", Longitude=" + longitude ;
    }
}
