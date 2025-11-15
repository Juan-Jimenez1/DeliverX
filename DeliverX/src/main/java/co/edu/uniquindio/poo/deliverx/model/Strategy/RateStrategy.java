package co.edu.uniquindio.poo.deliverx.model.Strategy;

import co.edu.uniquindio.poo.deliverx.model.Address;//?

public interface RateStrategy {
    double calculate(Address origin, Address destination, double weight);
    String getStrategyName();

}
