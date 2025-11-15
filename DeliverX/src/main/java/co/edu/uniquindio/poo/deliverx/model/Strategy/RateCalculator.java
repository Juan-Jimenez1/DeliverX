package co.edu.uniquindio.poo.deliverx.model.Strategy;
import co.edu.uniquindio.poo.deliverx.model.Address;

public class RateCalculator {
    private RateStrategy strategy;

    public RateCalculator(RateStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(RateStrategy strategy) {
        this.strategy = strategy;
    }

    public double calculateRate(Address origin, Address destination, double weight) {
        return strategy.calculate(origin, destination, weight);
    }

    public String getStrategyDescription() {
        return strategy.getStrategyName();
    }
}
