package co.edu.uniquindio.poo.deliverx.model.Strategy;

import co.edu.uniquindio.poo.deliverx.model.Address;

public class CombinedStrategy implements RateStrategy {
    private static final double WEIGHT_FACTOR = 1000.0;
    private static final double DISTANCE_FACTOR = 1500.0;
    private static final double BASE_COST = 6000.0;

    @Override
    public double calculate(Address origin, Address destination, double weight) {
        double distance = calculateDistance(origin, destination);
        return BASE_COST + (weight * WEIGHT_FACTOR) + (distance * DISTANCE_FACTOR);
    }

    @Override
    public String getStrategyName() {
        return "Combinate Rate (Weight + Distance)";
    }

    private double calculateDistance(Address origin, Address destination) {
        double lat1 = Double.parseDouble(origin.getLatitude());
        double lon1 = Double.parseDouble(origin.getLongitude());
        double lat2 = Double.parseDouble(destination.getLatitude());
        double lon2 = Double.parseDouble(destination.getLongitude());

        return Math.sqrt(Math.pow(lat2 - lat1, 2) + Math.pow(lon2 - lon1, 2)) * 111;
    }

}
