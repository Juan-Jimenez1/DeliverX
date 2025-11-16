package co.edu.uniquindio.poo.deliverx.model.Strategy;

import co.edu.uniquindio.poo.deliverx.model.Address;

public class NormalStrategy implements RateStrategy{
    private static final double BASE_RATE = 5000.0;  // Tarifa base
    private static final double COST_PER_KG_EXTRA = 800.0;   // Costo por kg adicional
    private static final double FREE_WEIGHT_KG = 5.0;         // Peso incluido sin cargo extra
    private static final double COST_PER_KM = 100.0; // costo por km

    @Override
    public double calculate(Address origin, Address destination, double weight) {
        // Validaciones de entrada
        if (origin == null || destination == null) {
            throw new IllegalArgumentException("Source and destination addresses cannot be null");
        }

        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be greater than zero");
        }

        double totalCost = BASE_RATE;

        // Agregar cargo por peso adicional si excede el peso gratuito
        if (weight > FREE_WEIGHT_KG) {
            double extraWeight = weight - FREE_WEIGHT_KG;
            totalCost += extraWeight * COST_PER_KG_EXTRA;
        }

        double distanceKm = calculateCartesianDistance(origin, destination);
        totalCost += distanceKm * COST_PER_KM;

        // Redondear a centenas
        return Math.ceil(totalCost / 100) * 100;
    }

    private double calculateCartesianDistance(Address origin, Address destination) {
        double lat1 = origin.getLatitude();
        double lon1 = origin.getLongitude();
        double lat2 = destination.getLatitude();
        double lon2 = destination.getLongitude();

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        // Distancia euclidiana
        double distance = Math.sqrt(dLat * dLat + dLon * dLon);

        return distance;
    }

    @Override
    public String getStrategyName() {
        return "Express Rate";
    }
}
