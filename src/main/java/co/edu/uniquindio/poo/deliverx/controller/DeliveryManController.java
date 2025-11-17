package co.edu.uniquindio.poo.deliverx.controller;

import co.edu.uniquindio.poo.deliverx.model.DeliverX;
import co.edu.uniquindio.poo.deliverx.model.DeliveryMan;
import co.edu.uniquindio.poo.deliverx.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class DeliveryManController {
    public Label deliveryManNameLabel;
    public Label statusLabel;
    public Button toggleAvailabilityButton;
    public Label coverageZoneLabel;
    public AnchorPane contentArea;
    public Button logOut;

    public void toggleAvailability(ActionEvent event) {
        try {
            System.out.println("=== INICIANDO TOGGLE AVAILABILITY ===");

            // Obtener el repartidor actualmente logueado
            DeliverX deliverX = DeliverX.getInstance();
            User loggedUser = deliverX.getUserLoged();

            System.out.println("Usuario logueado: " + (loggedUser != null ? loggedUser.getClass().getSimpleName() : "NULL"));

            if (loggedUser instanceof DeliveryMan) {
                DeliveryMan deliveryMan = (DeliveryMan) loggedUser;

                // Obtener estado actual - manejar caso null
                String currentState = "NO_STATE";
                if (deliveryMan.getState() != null) {
                    currentState = deliveryMan.getState().getStateName();
                }

                System.out.println("Estado actual del repartidor: " + currentState);

                // Determinar nuevo estado
                String newState = currentState.equals("ACTIVE") ? "INACTIVE" : "ACTIVE";
                System.out.println("Intentando cambiar a: " + newState);

                // Si el estado actual es null, usar setState directamente
                if (deliveryMan.getState() == null) {
                    System.out.println("Estado es NULL - usando setState() directamente");

                    if (newState.equals("ACTIVE")) {
                        deliveryMan.setState(new co.edu.uniquindio.poo.deliverx.model.state.ActiveState());
                    } else {
                        deliveryMan.setState(new co.edu.uniquindio.poo.deliverx.model.state.InactiveState());
                    }

                    // Actualizar interfaz
                    updateUI(newState);

                    // Actualizar en el sistema
                    deliverX.updateDeliveryMan(deliveryMan.getUserId(), deliveryMan);
                    System.out.println("Estado establecido directamente a: " + newState);

                } else {
                    // Intentar cambiar estado usando changeState
                    boolean stateChanged = false;

                    if (newState.equals("ACTIVE")) {
                        stateChanged = deliveryMan.changeState(new co.edu.uniquindio.poo.deliverx.model.state.ActiveState());
                    } else {
                        stateChanged = deliveryMan.changeState(new co.edu.uniquindio.poo.deliverx.model.state.InactiveState());
                    }

                    if (stateChanged) {
                        System.out.println("Estado cambiado exitosamente a: " + newState);
                        updateUI(newState);

                        // Actualizar en el sistema
                        boolean systemUpdated = deliverX.updateDeliveryMan(deliveryMan.getUserId(), deliveryMan);
                        System.out.println("Actualizado en sistema: " + systemUpdated);

                    } else {
                        System.out.println("Fallo el cambio de estado. Usando setState() directamente...");

                        // Fallback: establecer estado directamente
                        if (newState.equals("ACTIVE")) {
                            deliveryMan.setState(new co.edu.uniquindio.poo.deliverx.model.state.ActiveState());
                        } else {
                            deliveryMan.setState(new co.edu.uniquindio.poo.deliverx.model.state.InactiveState());
                        }

                        updateUI(newState);
                        deliverX.updateDeliveryMan(deliveryMan.getUserId(), deliveryMan);
                        System.out.println("Estado establecido directamente a: " + newState);
                    }
                }

            } else {
                System.out.println("ERROR: Usuario no es DeliveryMan");
                showAlert("Error", "Debe iniciar sesión como repartidor para usar esta función");
            }

        } catch (Exception e) {
            System.out.println("EXCEPCIÓN en toggleAvailability: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "No se pudo cambiar la disponibilidad: " + e.getMessage());
        }
    }

    private void updateUI(String newState) {
        if (newState.equals("ACTIVE")) {
            toggleAvailabilityButton.setText("Desactivar Disponibilidad");
            if (statusLabel != null) {
                statusLabel.setText("Estado: ACTIVO - Disponible para entregas");
            }
            toggleAvailabilityButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        } else {
            toggleAvailabilityButton.setText("Activar Disponibilidad");
            if (statusLabel != null) {
                statusLabel.setText("Estado: INACTIVO - No disponible para entregas");
            }
            toggleAvailabilityButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        }

        // Actualizar también el label del nombre si es necesario
        if (deliveryManNameLabel != null) {
            deliveryManNameLabel.setText("Repartidor: " + getDeliveryManName());
        }
    }

    private String getDeliveryManName() {
        try {
            User loggedUser = DeliverX.getInstance().getUserLoged();
            if (loggedUser instanceof DeliveryMan) {
                return ((DeliveryMan) loggedUser).getName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Repartidor";
    }

    private void showAlert(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void viewAssignedOrders(ActionEvent event) {
        cargarFXMLEnAnchorPane("/co/edu/uniquindio/poo/deliverx/delivery/deliveryAssignedOrders.fxml",contentArea);
    }

    public void updateDeliveryStatus(ActionEvent event) {
        cargarFXMLEnAnchorPane("/co/edu/uniquindio/poo/deliverx/delivery/deliveryUpdateStatus.fxml",contentArea);
    }

    public void viewDeliveryHistory(ActionEvent event) {
        cargarFXMLEnAnchorPane("/co/edu/uniquindio/poo/deliverx/delivery/deliveryHistory.fxml",contentArea);}

    public void manageProfile(ActionEvent event) {
        cargarFXMLEnAnchorPane("/co/edu/uniquindio/poo/deliverx/delivery/deliveryProfile.fxml",contentArea);
    }


    public void logout(ActionEvent event) {
        irPantalla("/co/edu/uniquindio/poo/deliverx/loggin/home.fxml","Home");
    cerrarVentana();}
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
        Stage stage = (Stage) logOut.getScene().getWindow();
        stage.close();
    }
    public void cargarFXMLEnAnchorPane(String nombreArchivoFxml, AnchorPane contenedor) {
        try {
            contenedor.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(nombreArchivoFxml));
            Parent contenido = loader.load();
            AnchorPane.setTopAnchor(contenido, 0.0);
            AnchorPane.setBottomAnchor(contenido, 0.0);
            AnchorPane.setLeftAnchor(contenido, 0.0);
            AnchorPane.setRightAnchor(contenido, 0.0);
            contenedor.getChildren().add(contenido);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
