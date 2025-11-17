package co.edu.uniquindio.poo.deliverx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AdminController {
    public Label adminNameLabel;
    public AnchorPane contentArea;
    public Button logout;

    public void loadRegisterDelivery(ActionEvent event) {
        cargarFXMLEnAnchorPane("/co/edu/uniquindio/poo/deliverx/admin/registerDelivery.fxml", contentArea);

    }

    public void loadRegisterUser(ActionEvent event) {
        cargarFXMLEnAnchorPane("/co/edu/uniquindio/poo/deliverx/loggin/registerUser.fxml", contentArea);
    }

    public void loadUserManagement(ActionEvent event) {
        cargarFXMLEnAnchorPane("/co/edu/uniquindio/poo/deliverx/admin/userManagement.fxml", contentArea);
    }
    public void loadDeliveryManagement(ActionEvent event) {
        cargarFXMLEnAnchorPane("/co/edu/uniquindio/poo/deliverx/admin/deliveryManagement.fxml", contentArea);
    }
    public void loadEnvioManagement(ActionEvent event) {
            cargarFXMLEnAnchorPane("/co/edu/uniquindio/poo/deliverx/admin/shipment_management.fxml", contentArea);}

    public void loadMetrics(ActionEvent event) {
        cargarFXMLEnAnchorPane("/co/edu/uniquindio/poo/deliverx/admin/metrics.fxml", contentArea);
    }

    public void loadReports(ActionEvent event) {
        cargarFXMLEnAnchorPane("/co/edu/uniquindio/poo/deliverx/admin/report_pdf.fxml", contentArea);
    }

    public void logout(ActionEvent event) {
        irPantalla("/co/edu/uniquindio/poo/deliverx/loggin/home.fxml","");
    }
    public void cargarFXMLEnAnchorPane(String nombreArchivoFxml, AnchorPane contenedor) {
        try {
            // Limpiar el contenedor
            contenedor.getChildren().clear();

            // Cargar la vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource(nombreArchivoFxml));
            Parent contenido = loader.load();

            // Anclar el contenido a los bordes del AnchorPane
            AnchorPane.setTopAnchor(contenido, 0.0);
            AnchorPane.setBottomAnchor(contenido, 0.0);
            AnchorPane.setLeftAnchor(contenido, 0.0);
            AnchorPane.setRightAnchor(contenido, 0.0);

            // Añadir el contenido al contenedor
            contenedor.getChildren().add(contenido);

        } catch (Exception e) {
            e.printStackTrace();
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
        }
    }
    public void cerrarVentana() {
        Stage stage = (Stage) logout.getScene().getWindow();
        stage.close();
    }
}
