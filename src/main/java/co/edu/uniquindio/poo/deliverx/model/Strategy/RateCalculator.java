package co.edu.uniquindio.poo.deliverx.model.Strategy;
import co.edu.uniquindio.poo.deliverx.model.Address;

public class RateCalculator {
    private RateStrategy strategy;

    public RateCalculator(RateStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("The strategy can't be null");
        }
        this.strategy = strategy;
    }

    public void setStrategy(RateStrategy strategy) {
        this.strategy = strategy;
    }

    public double calculateRate(Address origin, Address destination, double weight) {
        try {
            return strategy.calculate(origin, destination, weight);
        } catch (IllegalArgumentException e) {
            // Relanzar excepciones de validación
            throw e;
        } catch (Exception e) {
            // Manejar otros errores inesperados
            throw new RuntimeException("Error calculating the rate: " + e.getMessage(), e);
        }
    }

    public String getStrategyDescription() {
        return strategy.getStrategyName();
    }
}
