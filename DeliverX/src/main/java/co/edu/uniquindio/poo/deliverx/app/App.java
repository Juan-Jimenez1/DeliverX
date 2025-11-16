package co.edu.uniquindio.poo.deliverx.app;

import co.edu.uniquindio.poo.deliverx.model.*;
import co.edu.uniquindio.poo.deliverx.model.Strategy.ExpressStrategy;
import co.edu.uniquindio.poo.deliverx.model.Strategy.NormalStrategy;
import co.edu.uniquindio.poo.deliverx.model.Strategy.RateStrategy;
import co.edu.uniquindio.poo.deliverx.model.adapter.PDFReportAdapter;
import co.edu.uniquindio.poo.deliverx.model.facade.DeliverXFacade;
import co.edu.uniquindio.poo.deliverx.model.factory.ShipmentFactory;
import co.edu.uniquindio.poo.deliverx.pdfGenerator.pdfReceiptAdapter;
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

        List<String>services= new ArrayList<>();
        services.add("FRAGILE");
        services.add("SIGNATURE");
        services.add("PACKAGING");
        services.add("INSURANCE");

        DeliverXFacade facade= new DeliverXFacade();
        Customer customer = new Customer.Builder<>().userId("1").name("Jose").password("1").email("").phoneNumber("962600000").build();
        Address origin = new Address("1","cra9","armenia","house",12,11);
        Address destination = new Address("2","cra10","armenia","house",50,100);
        Shipment shipment = ShipmentFactory.createShipment("normal","01",customer,origin,destination,1, rateStrategy, services, PaymentMethod.DEBIT_CARD);
        System.out.println(shipment.getPrice());
        System.out.println(shipment.getAdditionalServices());
        System.out.println(shipment.getPay());

         pdfReceiptAdapter pdfReceiptAdapter = new pdfReceiptAdapter(shipment);
         pdfReceiptAdapter.generatePDFReport("receipt");

    }
}


