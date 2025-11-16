package co.edu.uniquindio.poo.deliverx.app;

import co.edu.uniquindio.poo.deliverx.model.*;
import co.edu.uniquindio.poo.deliverx.model.Strategy.RateStrategy;
import co.edu.uniquindio.poo.deliverx.model.facade.DeliverXFacade;
import co.edu.uniquindio.poo.deliverx.model.factory.ShipmentFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Admin admin = new Admin.Builder<>().userId("0").name("Juan").password("1").email("").phoneNumber("962600000").build();
        System.out.println(admin);
        DeliverX deliverX = DeliverX.getInstance();

        //deliverX.registerAdmin(admin);
        //deliverX.loginUser(admin.getUserId(), admin.getPassword());
        RateStrategy rateStrategy = new RateStrategy() {
            @Override
            public double calculate(Address origin, Address destination, double weight) {
                return 0;
            }

            @Override
            public String getStrategyName() {
                return "";
            }
        };

        List<String> services = new ArrayList<>();
        services.add("FRAGILE");
        services.add("SIGNATURE");
        services.add("PACKAGING");
        services.add("INSURANCE");

        DeliverXFacade facade = new DeliverXFacade();

        Customer customer1 = new Customer.Builder<>().userId("1").name("Jose").password("1").email("jose@email.com").phoneNumber("962600000").build();
        Customer customer2 = new Customer.Builder<>().userId("2").name("Maria").password("2").email("maria@email.com").phoneNumber("962600001").build();
        Customer customer3 = new Customer.Builder<>().userId("3").name("Carlos").password("3").email("carlos@email.com").phoneNumber("962600002").build();

        Address origin1 = new Address("1", "cra9", "armenia", "house", 12, 11);
        Address destination1 = new Address("2", "cra10", "armenia", "house", 50, 100);
        Address origin2 = new Address("3", "cra15", "pereira", "apartment", 20, 30);
        Address destination2 = new Address("4", "cra20", "pereira", "office", 60, 70);
        Address origin3 = new Address("5", "cra25", "manizales", "store", 35, 45);
        Address destination3 = new Address("6", "cra30", "manizales", "building", 80, 90);

        List<String> services2 = new ArrayList<>(List.of("FRAGILE", "INSURANCE"));
        List<String> services3 = new ArrayList<>(List.of("SIGNATURE", "PACKAGING"));

        Shipment shipment1 = ShipmentFactory.createShipment("normal", "01", customer1, origin1, destination1, 1, rateStrategy, services, PaymentMethod.DEBIT_CARD);
        Shipment shipment2 = ShipmentFactory.createShipment("express", "02", customer1, origin2, destination2, 2, rateStrategy, services2, PaymentMethod.CREDIT_CARD);
        Shipment shipment3 = ShipmentFactory.createShipment("normal", "03", customer2, origin3, destination3, 1.5, rateStrategy, services3, PaymentMethod.CASH);
        Shipment shipment4 = ShipmentFactory.createShipment("express", "04", customer2, origin1, destination2, 3, rateStrategy, services, PaymentMethod.DEBIT_CARD);
        Shipment shipment5 = ShipmentFactory.createShipment("normal", "05", customer3, origin2, destination3, 2.5, rateStrategy, services2, PaymentMethod.CREDIT_CARD);

        System.out.println("Shipment 1: " + shipment1.getPrice() + " " + shipment1.getAdditionalServices() + " " + shipment1.getPay());
        System.out.println("Shipment 2: " + shipment2.getPrice() + " " + shipment2.getAdditionalServices() + " " + shipment2.getPay());
        System.out.println("Shipment 3: " + shipment3.getPrice() + " " + shipment3.getAdditionalServices() + " " + shipment3.getPay());
        System.out.println("Shipment 4: " + shipment4.getPrice() + " " + shipment4.getAdditionalServices() + " " + shipment4.getPay());
        System.out.println("Shipment 5: " + shipment5.getPrice() + " " + shipment5.getAdditionalServices() + " " + shipment5.getPay());

        List<Shipment> shipments = new ArrayList<>(List.of(shipment1, shipment2, shipment3, shipment4, shipment5));

        admin.generateReportShipmentsPerMonth(2025,11);

        admin.generateReportShipmentsByState("REQUESTED");
    }
}


