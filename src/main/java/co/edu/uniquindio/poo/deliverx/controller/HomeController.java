package co.edu.uniquindio.poo.deliverx.controller;

import co.edu.uniquindio.poo.deliverx.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class HomeController {

    public TextField nameId;
    public PasswordField passwordId;
    public Label errorLabel;
    public Button loginId;

    private DeliverX deliverX;

    public void initialize() {
        deliverX = DeliverX.getInstance();
        errorLabel.setText("");
    }

    public void handleLogin(ActionEvent event) {

        String userId = nameId.getText().trim();
        String password = passwordId.getText().trim();

        if (userId.isEmpty() || password.isEmpty()) {
            mostrarError("Please enter username and password");
            return;
        }

        boolean loginExitoso = deliverX.loginUser(userId, password);

        if (!loginExitoso) {
            mostrarError("Incorrect username or password");
            return;
        }

        User userLogged = deliverX.getUserLoged();

        if (userLogged instanceof Admin admin) {
            irPantalla("/co/edu/uniquindio/poo/deliverx/admin/admin.fxml", "Admin");
        }
        else if (userLogged instanceof DeliveryMan deliveryMan) {
            irPantalla("/co/edu/uniquindio/poo/deliverx/delivery/delivery.fxml", "Delivery");
        }
        else if (userLogged instanceof Customer customer) {
            irPantalla("/co/edu/uniquindio/poo/deliverx/user/userDashboard.fxml", "Customer");
        }
        else {
            mostrarError("Unknown user type");
        }
    }

    private void irPantalla(String fxml, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(titulo);
            stage.setResizable(false);
            stage.show();

            // Cerrar ventana actual
            cerrarVentana();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al cargar la ventana: " + titulo);
        }
    }

    private void mostrarError(String mensaje) {
        errorLabel.setText(mensaje);
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
    }

    public void cerrarVentana() {
        Stage stage = (Stage) loginId.getScene().getWindow();
        stage.close();
    }

    public void handleRegisterUser(ActionEvent event) {
        irPantalla("/co/edu/uniquindio/poo/deliverx/loggin/registerUser.fxml","Register User");
    }
}
