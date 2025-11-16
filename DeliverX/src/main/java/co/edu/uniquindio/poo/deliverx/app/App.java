package co.edu.uniquindio.poo.deliverx.app;

import co.edu.uniquindio.poo.deliverx.model.*;
import co.edu.uniquindio.poo.deliverx.model.Strategy.ExpressStrategy;
import co.edu.uniquindio.poo.deliverx.model.Strategy.NormalStrategy;
import co.edu.uniquindio.poo.deliverx.model.Strategy.RateStrategy;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

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

        deliverX.registerAdmin(admin);

        deliverX.loginUser(admin.getUserId(), admin.getPassword());

    }
}


