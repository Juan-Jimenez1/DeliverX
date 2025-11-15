package co.edu.uniquindio.poo.deliverx.model.Strategy;

import co.edu.uniquindio.poo.deliverx.model.Address;

public class WeightBasedStrategy implements RateStrategy {
    private static final double COST_PER_KG = 1500.0;
    private static final double BASE_COST = 4000.0;

    @Override
    public double calculate(Address origin, Address destination, double weight) {
        return BASE_COST + (weight * COST_PER_KG);
    }

    @Override
    public String getStrategyName() {
        return "Rate per weight";
    }
}
