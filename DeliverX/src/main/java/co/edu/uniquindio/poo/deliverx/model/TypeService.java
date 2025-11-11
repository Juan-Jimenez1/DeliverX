
package co.edu.uniquindio.poo.deliverx.model;

public enum TypeService {
    SAFE("Safe Delivery"),
    FRAGILE("Fragile Handling"),
    SIGNATURE_REQUIRED("Signature Required"),
    PRIORITY("Priority Delivery");

    private final String type;

    private TypeService(String displayName) {
        this.type = displayName;
    }

    public String getType() {
        return this.type;
    }

    public double getCost() {
        return (double)0.0F;
    }
}
