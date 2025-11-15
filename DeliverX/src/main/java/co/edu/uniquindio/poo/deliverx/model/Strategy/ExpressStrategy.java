package co.edu.uniquindio.poo.deliverx.model.Strategy;

import co.edu.uniquindio.poo.deliverx.model.Address;

public class ExpressStrategy implements RateStrategy{
    private static final double EXPRESS_FLAT_RATE = 25000.0;
    // No tiene sentido.
    @Override
    public double calculate(Address origin, Address destination, double weight) {
        return EXPRESS_FLAT_RATE;
    }

    @Override
    public String getStrategyName() {
        return "Express Rate";
    }
}
