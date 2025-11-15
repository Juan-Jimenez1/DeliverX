package co.edu.uniquindio.poo.deliverx.model.Strategy;

import co.edu.uniquindio.poo.deliverx.model.Address;

public class DsitanceBasedStrategy implements RateStrategy {
    private static final double COST_PER_KM = 2000.0;
    private static final double BASE_COST = 5000.0;

    @Override
    public double calculate(Address origin, Address destination, double weight) {
        double distance = calculateDistance(origin, destination);
        return BASE_COST + (distance * COST_PER_KM);
    }

    @Override
    public String getStrategyName() {
        return "Tarifa por Distancia";
    }

    //Pregúntarle a juan José
    // Que buena pregunta
    private double calculateDistance(Address origin, Address destination) {
        double lat1 = Double.parseDouble(origin.getLatitude());
        double lon1 = Double.parseDouble(origin.getLongitude());
        double lat2 = Double.parseDouble(destination.getLatitude());
        double lon2 = Double.parseDouble(destination.getLongitude());
    //double.parseDouble: Sirve para pasar un String a un decimal
        return Math.sqrt(Math.pow(lat2 - lat1, 2) + Math.pow(lon2 - lon1, 2)) * 111; // ~111 km por grado
    //Math pow: Para elevar un número a una potencia
    }
}



