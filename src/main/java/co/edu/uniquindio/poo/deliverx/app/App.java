package co.edu.uniquindio.poo.deliverx.app;

import co.edu.uniquindio.poo.deliverx.model.*;
import co.edu.uniquindio.poo.deliverx.model.Strategy.RateStrategy;
import co.edu.uniquindio.poo.deliverx.model.facade.DeliverXFacade;
import co.edu.uniquindio.poo.deliverx.model.factory.ShipmentFactory;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {


        DeliverX deliverX = DeliverX.getInstance();

        Admin admin = new Admin.Builder<>()
                .userId("0")
                .name("Juan")
                .password("1")
                .email("admin@deliverx.com")
                .phoneNumber("962600000")
                .build();

        deliverX.registerAdmin(admin);
        System.out.println("Admin registrado: " + admin.getName() + " - ID: " + admin.getUserId());

        Customer customer1 = new Customer.Builder<>().userId("1").name("Jose").password("1").email("jose@email.com").phoneNumber("962600000").build();

        Customer customer2 = new Customer.Builder<>().userId("2").name("Maria").password("2").email("maria@email.com").phoneNumber("962600001").build();

        Customer customer3 = new Customer.Builder<>().userId("3").name("Carlos").password("3").email("carlos@email.com").phoneNumber("962600002").build();

        deliverX.registerCustomer(customer1);
        deliverX.registerCustomer(customer2);
        deliverX.registerCustomer(customer3);
        System.out.println("Clientes registrados");

        DeliveryMan deliveryMan = new DeliveryMan.Builder<>().userId("100").name("Pedro Repartidor").password("1").email("pedro@deliverx.com").phoneNumber("962600100").build();

        deliverX.registerDeliveryMan(deliveryMan);
        System.out.println("Repartidor registrado: " + deliveryMan.getName() + " - ID: " + deliveryMan.getUserId());

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

        Address origin1 = new Address("1", "cra9", "armenia", "house", 12, 11);
        Address destination1 = new Address("2", "cra10", "armenia", "house", 50, 100);
        Address origin2 = new Address("3", "cra15", "pereira", "apartment", 20, 30);
        Address destination2 = new Address("4", "cra20", "pereira", "office", 60, 70);
        Address origin3 = new Address("5", "cra25", "manizales", "store", 35, 45);
        Address destination3 = new Address("6", "cra30", "manizales", "building", 80, 90);

        List<String> services2 = List.of("FRAGILE", "INSURANCE");
        List<String> services3 = List.of("SIGNATURE", "PACKAGING");

        Shipment shipment1 = ShipmentFactory.createShipment("normal", "01",
                customer1, origin1, destination1, 1, rateStrategy, services, PaymentMethod.DEBIT_CARD);
        Shipment shipment2 = ShipmentFactory.createShipment("express", "02",
                customer1, origin2, destination2, 2, rateStrategy, services2, PaymentMethod.CREDIT_CARD);
        Shipment shipment3 = ShipmentFactory.createShipment("normal", "03",
                customer2, origin3, destination3, 1.5, rateStrategy, services3, PaymentMethod.CASH);
        Shipment shipment4 = ShipmentFactory.createShipment("express", "04",
                customer2, origin1, destination2, 3, rateStrategy, services, PaymentMethod.DEBIT_CARD);
        Shipment shipment5 = ShipmentFactory.createShipment("normal", "05",
                customer3, origin2, destination3, 2.5, rateStrategy, services2, PaymentMethod.CREDIT_CARD);

        System.out.println("Shipment 1: " + shipment1.getPrice());
        System.out.println("Shipment 2: " + shipment2.getPrice());
        System.out.println("Shipment 3: " + shipment3.getPrice());
        System.out.println("Shipment 4: " + shipment4.getPrice());
        System.out.println("Shipment 5: " + shipment5.getPrice());


        FXMLLoader loader = new FXMLLoader(App.class.getResource("/co/edu/uniquindio/poo/deliverx/loggin/home.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        stage.setScene(scene);
        stage.setTitle("DeliverX - Login");
        stage.setResizable(false);
        stage.show();

        System.out.println("\n=== USUARIOS DISPONIBLES PARA LOGIN ===");
        System.out.println("Admin: ID=0, Password=1");
        System.out.println("Cliente Jose: ID=1, Password=1");
        System.out.println("Cliente Maria: ID=2, Password=2");
        System.out.println("Cliente Carlos: ID=3, Password=3");
        System.out.println("Repartidor Pedro: ID=100, Password=1");
        System.out.println("=======================================\n");
    }


    public static void main(String[] args) {
        launch(args);
    }
}