package co.edu.uniquindio.poo.deliverx.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AdminController {
    public Label adminNameLabel;
    public AnchorPane contentArea;

    public void loadRegisterDelivery(ActionEvent event) {

    }

    public void loadRegisterUser(ActionEvent event) {
        navegarVentana("/co/edu/uniquindio/poo/deliverx/view/admin/register_delivery.fxml","");
    }

    public void loadUserManagement(ActionEvent event) {

    }

    public void loadDeliveryManagement(ActionEvent event) {
    }

    public void loadEnvioManagement(ActionEvent event) {
    }

    public void loadMetrics(ActionEvent event) {
    }

    public void loadReports(ActionEvent event) {
    }

    public void logout(ActionEvent event) {
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

        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
