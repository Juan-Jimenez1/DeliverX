package co.edu.uniquindio.poo.deliverx.model;

public class Admin extends User{


    private Admin(Builder<?> builder){
        super(builder);
    }
//QUITAR BUILDER
    public static class Builder<T extends Builder<T>> extends User.Builder<T>{
        @Override
        public Admin build() {
            return new Admin(this);
        }
    }
}
