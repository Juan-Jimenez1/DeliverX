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

import java.io.IOException;

public class HomeController {
    public TextField nameId;
    public PasswordField passwordId;
    public Label errorLabel;
    public Button loginId;

    private DeliverX deliverX;

    public void initialize() {
        deliverX = DeliverX.getInstance();
        // Limpiar mensaje de error al inicio
        errorLabel.setText("");
    }

    public void handleLogin(ActionEvent event) {
        String userId = nameId.getText().trim();
        String password = passwordId.getText().trim();

        // Validar campos vacíos
        if (userId.isEmpty() || password.isEmpty()) {
            mostrarError("Por favor ingrese usuario y contraseña");
            return;
        }

        // Intentar login
        boolean loginExitoso = deliverX.loginUser(userId, password);

        if (loginExitoso) {
            User userLogged = deliverX.getUserLoged();

            // Redirigir según el tipo de usuario
            if (userLogged instanceof Admin) {
                loginComoAdmin((Admin) userLogged);
            } else if (userLogged instanceof DeliveryMan) {
                loginComoDeliveryMan((DeliveryMan) userLogged);
            } else if (userLogged instanceof Customer) {
                loginComoCustomer((Customer) userLogged);
            } else {
                mostrarError("Tipo de usuario no reconocido");
            }
        } else {
            mostrarError("Usuario o contraseña incorrectos");
        }
    }

    private void loginComoAdmin(Admin admin) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/poo/deliverx/view/MetricsView.fxml"));
            Parent root = loader.load();

            // Obtener el controller y pasarle el admin
            MetricsController controller = loader.getController();
            controller.setCurrentAdmin(admin);

            // Crear la escena
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Panel de Administración - DeliverX");
            stage.setMaximized(true); // Maximizar para ver mejor el dashboard
            stage.show();

            // Cerrar ventana de login
            cerrarVentana();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al cargar el panel de administración");
        }
    }

    private void loginComoDeliveryMan(DeliveryMan deliveryMan) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/poo/deliverx/view/DeliveryManView.fxml"));
            Parent root = loader.load();

            // Si tienes un controller para DeliveryMan, pásale los datos
            // DeliveryManController controller = loader.getController();
            // controller.setCurrentDeliveryMan(deliveryMan);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Panel de Repartidor - " + deliveryMan.getName());
            stage.show();

            cerrarVentana();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al cargar el panel de repartidor");
        }
    }

    private void loginComoCustomer(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/poo/deliverx/view/CustomerView.fxml"));
            Parent root = loader.load();

            // Si tienes un controller para Customer, pásale los datos
            // CustomerController controller = loader.getController();
            // controller.setCurrentCustomer(customer);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Panel de Cliente - " + customer.getName());
            stage.show();

            cerrarVentana();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al cargar el panel de cliente");
        }
    }

    public void handleRegisterUser(ActionEvent event) {
        // Navegar a la ventana de registro
        navegarVentana("/co/edu/uniquindio/poo/deliverx/view/RegisterView.fxml", "Registro de Usuario");
    }

    private void mostrarError(String mensaje) {
        errorLabel.setText(mensaje);
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

        // Opcional: Limpiar el mensaje después de 5 segundos
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                javafx.application.Platform.runLater(() -> errorLabel.setText(""));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void navegarVentana(String nombreArchivoFxml, String tituloVentana) {
        try {
            // Cargar la vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource(nombreArchivoFxml));
            Parent root = loader.load();

            // Crear la escena
            Scene scene = new Scene(root);

            // Crear un nuevo escenario (ventana)
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle(tituloVentana);

            // Mostrar la nueva ventana
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al cargar la ventana: " + tituloVentana);
        }
    }

    public void cerrarVentana() {
        Stage stage = (Stage) loginId.getScene().getWindow();
        stage.close();
    }
}