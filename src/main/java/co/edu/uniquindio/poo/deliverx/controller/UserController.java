package co.edu.uniquindio.poo.deliverx.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class UserController {
    public AnchorPane layout;
    public Button logout;

    public void makeOrder(MouseEvent mouseEvent) {cargarFXMLEnAnchorPane("/co/edu/uniquindio/poo/deliverx/user/userNewShipment.fxml", layout);}
    public void viewOrderHistory(MouseEvent mouseEvent) {cargarFXMLEnAnchorPane("/co/edu/uniquindio/poo/deliverx/user/userMyShipments.fxml", layout);}
    public void trackOrder(MouseEvent mouseEvent) {
    }

    public void manageProfile(MouseEvent mouseEvent) {cargarFXMLEnAnchorPane("/co/edu/uniquindio/poo/deliverx/user/userProfile.fxml", layout);}

    public void logout(MouseEvent mouseEvent) {irPantalla( "/co/edu/uniquindio/poo/deliverx/loggin/home.fxml","Home");
    cerrarVentana();}
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
