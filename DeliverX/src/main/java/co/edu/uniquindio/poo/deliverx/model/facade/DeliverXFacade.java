package co.edu.uniquindio.poo.deliverx.model.facade;

import co.edu.uniquindio.poo.deliverx.model.*;
import co.edu.uniquindio.poo.deliverx.model.Strategy.RateCalculator;
import co.edu.uniquindio.poo.deliverx.model.Strategy.RateStrategy;
import co.edu.uniquindio.poo.deliverx.model.decorator.*;
import co.edu.uniquindio.poo.deliverx.model.observer.ObservableShipment;
import co.edu.uniquindio.poo.deliverx.model.observer.SMSNotificationObserver;
import co.edu.uniquindio.poo.deliverx.model.observer.ShipmentObserver;
import co.edu.uniquindio.poo.deliverx.model.observer.EmailNotificationObserver;
import co.edu.uniquindio.poo.deliverx.model.state.AssignedState;
import co.edu.uniquindio.poo.deliverx.model.state.CancelledState;
import co.edu.uniquindio.poo.deliverx.model.state.RequestedState;
import co.edu.uniquindio.poo.deliverx.model.state.ShipmentState;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DeliverXFacade {
    private DeliverX deliverX;
    private RateCalculator rateCalculator;
    private List<ShipmentObserver> defaultObservers;

    public DeliverXFacade() {
        this.deliverX = DeliverX.getInstance();
        this.defaultObservers = new ArrayList<>();
        initializeDefaultObservers();
    }

    private void initializeDefaultObservers() {
        defaultObservers.add(new EmailNotificationObserver());
        defaultObservers.add(new SMSNotificationObserver());
    }

    /**
     * Crea un envío completo
     */
    public Shipment createCompleteShipment(
            String shipmentId,
            Customer customer,
            Address origin,
            Address destination,
            double weight,
            RateStrategy rateStrategy,
            List<String> additionalServices,
            PaymentMethod paymentMethod) {

        System.out.println("=== CREATING COMPLETE SHIPMENT ===\n");

        // Calcular tarifa base usando Strategy
        rateCalculator = new RateCalculator(rateStrategy);
        double baseCost = rateCalculator.calculateRate(origin, destination, weight);
        System.out.println("  Calculated base rate: $" + baseCost);
        System.out.println("  Strategy: " + rateCalculator.getStrategyDescription() + "\n");

        // 2. Crear envío básico
        Shipment shipment = new Shipment(
                shipmentId, origin, destination, weight,
                null, customer, null, LocalDate.now()
        );

        // 3. Aplicar servicios adicionales con Decorator
        ShipmentComponent shipmentComponent = new BasicShipment(shipment, baseCost);
        shipmentComponent = applyAdditionalServices(shipmentComponent, additionalServices);

        double finalCost = shipmentComponent.calculateCost();
        System.out.println("  Additional services applied:");
        System.out.println("  " + shipmentComponent.getDescription());
        System.out.println("  Total Amount: $" + finalCost + "\n");

        // 4. Procesar pago usando Adapter
        Pay payment = processPayment(shipmentId, finalCost, paymentMethod);
        shipment.setPay(payment);

        if (payment.getResult() == TransactionResult.APPROVED) {
            System.out.println(" Payment processed successfully\n");
        } else {
            System.out.println(" Payment rejected\n");
            return null;
        }

        // 5. Configurar observadores para notificaciones
        ObservableShipment observableShipment = new ObservableShipment(shipment);
        for (ShipmentObserver observer : defaultObservers) {
            observableShipment.attach(observer);
        }

        // 6. Establecer estado inicial usando State
        ShipmentState initialState = new RequestedState();
        shipment.setCurrentState(initialState);
        System.out.println(" Shipment created in status: " + initialState.getStateName());

        // Notificar observadores
        observableShipment.notifyObservers(null, initialState);

        // 7. Registrar en el sistema
        deliverX.getListShipments().add(shipment);
        customer.getShipmentList().add(shipment);

        System.out.println("=== SHIPMENT SUCCESSFULLY CREATED ===\n");
        return shipment;
    }

    /**
     * Asigna un repartidor y actualiza el estado del envío
     */
    public void assignDeliveryManToShipment(Shipment shipment, DeliveryMan deliveryMan) {
        System.out.println("=== ASSIGNING DELIVERYMAN ===\n");

        shipment.setDeliveryMan(deliveryMan);

        ObservableShipment observableShipment = new ObservableShipment(shipment);
        for (ShipmentObserver observer : defaultObservers) {
            observableShipment.attach(observer);
        }

        ShipmentState oldState = shipment.getCurrentState();
        ShipmentState newState = new AssignedState();
        shipment.changeState(newState);

        observableShipment.notifyObservers(oldState, newState);

        System.out.println(" Deliveryman " + deliveryMan.getName() + " assigned\n");
    }

    /**
     * Actualiza el estado del envío y notifica a todos los observadores
     */
    public void updateShipmentState(Shipment shipment, ShipmentState newState) {
        System.out.println("=== UPDATING SHIPPING STATUS ===\n");

        ObservableShipment observableShipment = new ObservableShipment(shipment);
        for (ShipmentObserver observer : defaultObservers) {
            observableShipment.attach(observer);
        }

        ShipmentState oldState = shipment.getCurrentState();
        shipment.changeState(newState);
        observableShipment.notifyObservers(oldState, newState);
    }

    /**
     * Obtiene una cotización de envío (sin crear el envío)
     */
    public double getShipmentQuote(
            Address origin,
            Address destination,
            double weight,
            RateStrategy rateStrategy,
            List<String> additionalServices) {

        rateCalculator = new RateCalculator(rateStrategy);
        double baseCost = rateCalculator.calculateRate(origin, destination, weight);

        // Simular decoradores para calcular costo total
        ShipmentComponent component = new BasicShipment(null, baseCost);
        component = applyAdditionalServices(component, additionalServices);

        return component.calculateCost();
    }

    /**
     * Cancela un envío si está en estado válido
     */
    public boolean cancelShipment(Shipment shipment) {
        System.out.println("=== CANCELLED SHIPMENT ===\n");

        ObservableShipment observableShipment = new ObservableShipment(shipment);
        for (ShipmentObserver observer : defaultObservers) {
            observableShipment.attach(observer);
        }

        ShipmentState oldState = shipment.getCurrentState();
        ShipmentState cancelledState = new CancelledState();

        boolean cancelled = shipment.changeState(cancelledState);

        if (cancelled) {
            observableShipment.notifyObservers(oldState, cancelledState);
            deliverX.getListShipments().remove(shipment);
            System.out.println(" Shipment successfully cancelled\n");
            return true;
        }

        System.out.println(" The shipment could not be cancelled\n");
        return false;
    }

    private ShipmentComponent applyAdditionalServices(
            ShipmentComponent component, List<String> services) {

        if (services == null || services.isEmpty()) {
            return component;
        }

        for (String service : services) {
            component = switch (service.toUpperCase()) {
                case "INSURANCE SHIPMENT", "INSURANCE" -> new InsuranceDecorator(component);
                case "FRAGILE SHIPMENT", "FRAGILE" -> new FragileDecorator(component);
                case "SIGNATURE SHIPMENT", "SIGNATURE" -> new SignatureRequiredDecorator(component);
                case "PRIORITY SHIPMENT", "PRIORITY" -> new PriorityDecorator(component);
                case "SPECIAL PACKAGING", "PACKAGING" -> new SpecialPackagingDecorator(component);
                default -> component;
            };
        }

        return component;
    }

//    private Pay processPayment(String paymentId, double amount, PaymentMethod method) {
//        System.out.println(" Processing payment...");
//        PaymentProcessor processor = PaymentAdapterFactory.getAdapter(method);
//        return processor.processPayment(paymentId, amount, method);
//    }

private Pay processPayment(String paymentId, double amount, PaymentMethod method) {
    System.out.println(" Processing payment via " + method + ": $" + amount);

    TransactionResult result;

    if (method == PaymentMethod.CASH) {
        // Efectivo requiere confirmación física
        result = TransactionResult.PENDING;
        System.out.println(" Cash payment registered - Pending confirmation");
    } else {
        // Tarjetas de crédito/débito se aprueban inmediatamente
        result = TransactionResult.APPROVED;
        System.out.println( method + " payment approved");
    }

    // Crear el pago con todos los parámetros requeridos
    Pay payment = new Pay(
            paymentId,
            method,
            amount,
            LocalDate.now(),
            result
    );

    return payment;
}

    public DeliverX getDeliverX() {
        return deliverX;
    }

}
