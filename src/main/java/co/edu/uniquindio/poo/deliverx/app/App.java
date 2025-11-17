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
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/co/edu/uniquindio/poo/deiverx/view/loggin/home.fxml" +
                "UserMyShipments.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}


