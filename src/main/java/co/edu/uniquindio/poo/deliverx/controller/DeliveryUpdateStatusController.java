package co.edu.uniquindio.poo.deliverx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import co.edu.uniquindio.poo.deliverx.model.DeliverX;
import co.edu.uniquindio.poo.deliverx.model.Shipment;
import co.edu.uniquindio.poo.deliverx.model.DeliveryMan;
import co.edu.uniquindio.poo.deliverx.model.User;
import co.edu.uniquindio.poo.deliverx.model.state.*;

public class DeliveryUpdateStatusController {
    @FXML
    public ComboBox<Shipment> orderComboBox;
    @FXML
    public Label orderIdLabel;
    @FXML
    public Label destinationLabel;
    @FXML
    public Label currentStatusLabel;
    @FXML
    public Label messageLabel;
    @FXML
    public TextArea notesArea;
    @FXML
    public RadioButton asignadoRadio;
    @FXML
    public RadioButton enRutaRadio;
    @FXML
    public RadioButton entregadoRadio;
    @FXML
    public RadioButton incidenciaRadio;

    private ObservableList<Shipment> shipments;
    private ToggleGroup statusToggleGroup;
    private DeliverX deliverX;
    private DeliveryMan currentDeliveryMan;

    @FXML
    public void initialize() {
        deliverX = DeliverX.getInstance();

        // Obtener el repartidor logueado
        User userLogged = deliverX.getUserLoged();
        if (userLogged instanceof DeliveryMan) {
            currentDeliveryMan = (DeliveryMan) userLogged;
            System.out.println("Repartidor actual: " + currentDeliveryMan.getName());
        } else {
            showMessage("Error: No delivery man logged in", "error");
            return;
        }

        // Initialize toggle group for radio buttons
        statusToggleGroup = new ToggleGroup();
        asignadoRadio.setToggleGroup(statusToggleGroup);
        enRutaRadio.setToggleGroup(statusToggleGroup);
        entregadoRadio.setToggleGroup(statusToggleGroup);
        incidenciaRadio.setToggleGroup(statusToggleGroup);

        // Load shipments data - solo los asignados a este repartidor
        loadAssignedShipments();

        // Set up combo box
        orderComboBox.setItems(shipments);
        orderComboBox.setCellFactory(param -> new ListCell<Shipment>() {
            @Override
            protected void updateItem(Shipment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("Shipment #" + item.getIdShipment() + " - " +
                            (item.getDestination() != null ? item.getDestination().getStreet() : "No destination") +
                            " (" + item.getCurrentState().getStateName() + ")");
                }
            }
        });

        orderComboBox.setButtonCell(new ListCell<Shipment>() {
            @Override
            protected void updateItem(Shipment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Select assigned shipment");
                } else {
                    setText("Shipment #" + item.getIdShipment() + " - " +
                            (item.getDestination() != null ? item.getDestination().getStreet() : "No destination"));
                }
            }
        });

        // Add listener for combo box selection
        orderComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateShipmentDetails(newValue);
                updateAvailableStatusOptions(newValue);
            }
        });

        // Mostrar mensaje si no hay envíos asignados
        if (shipments.isEmpty()) {
            showMessage("No shipments assigned to you", "info");
        }
    }

    private void loadAssignedShipments() {
        shipments = FXCollections.observableArrayList();

        // Cargar solo los envíos asignados a este repartidor
        if (deliverX.getListShipments() != null) {
            for (Shipment shipment : deliverX.getListShipments()) {
                if (isShipmentAssignedToCurrentDeliveryMan(shipment)) {
                    shipments.add(shipment);
                }
            }
        }

        System.out.println("Envíos asignados cargados: " + shipments.size());
    }

    private boolean isShipmentAssignedToCurrentDeliveryMan(Shipment shipment) {
        // Verificar si el envío está asignado al repartidor actual
        if (shipment.getDeliveryMan() == null) {
            return false;
        }

        // Comparar por ID del repartidor
        return shipment.getDeliveryMan().getUserId().equals(currentDeliveryMan.getUserId());
    }

    @FXML
    public void handleOrderSelected(ActionEvent event) {
        Shipment selectedShipment = orderComboBox.getValue();
        if (selectedShipment != null) {
            updateShipmentDetails(selectedShipment);
            updateAvailableStatusOptions(selectedShipment);
        }
    }

    private void updateShipmentDetails(Shipment shipment) {
        orderIdLabel.setText(shipment.getIdShipment());
        destinationLabel.setText(shipment.getDestination() != null ?
                shipment.getDestination().getStreet() + ", " + shipment.getDestination().getCity() : "No destination");
        currentStatusLabel.setText(shipment.getCurrentState().getStateName());

        // Clear previous selection and notes
        statusToggleGroup.selectToggle(null);
        notesArea.clear();
        messageLabel.setText("");
    }

    private void updateAvailableStatusOptions(Shipment shipment) {
        String currentState = shipment.getCurrentState().getStateName();

        // Reset all buttons
        asignadoRadio.setDisable(true);
        enRutaRadio.setDisable(true);
        entregadoRadio.setDisable(true);
        incidenciaRadio.setDisable(true);

        // Set tooltips to explain transitions
        asignadoRadio.setTooltip(new Tooltip("Assign to delivery man"));
        enRutaRadio.setTooltip(new Tooltip("Mark as in route"));
        entregadoRadio.setTooltip(new Tooltip("Mark as delivered"));
        incidenciaRadio.setTooltip(new Tooltip("Cancel shipment"));

        // Enable only valid transitions based on current state
        switch (currentState) {
            case "REQUESTED":
                // REQUESTED can go to ASSIGNED or CANCELLED
                asignadoRadio.setDisable(false);
                incidenciaRadio.setDisable(false);
                break;
            case "ASSIGNED":
                // ASSIGNED can go to IN_ROUTE or CANCELLED
                enRutaRadio.setDisable(false);
                incidenciaRadio.setDisable(false);
                break;
            case "ROUTE":
                // IN_ROUTE can go to DELIVERED
                entregadoRadio.setDisable(false);
                break;
            case "DELIVERED":
            case "CANCELLED":
                // Final states - no transitions allowed
                showMessage("This shipment is in a final state and cannot be modified", "info");
                break;
        }
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        // Close the window
        Stage stage = (Stage) orderComboBox.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleUpdateStatus(ActionEvent event) {
        Shipment selectedShipment = orderComboBox.getValue();
        if (selectedShipment == null) {
            showMessage("Please select a shipment", "error");
            return;
        }

        RadioButton selectedRadio = (RadioButton) statusToggleGroup.getSelectedToggle();
        if (selectedRadio == null) {
            showMessage("Please select a status", "error");
            return;
        }

        try {
            boolean success = false;
            ShipmentState newState = null;
            String currentState = selectedShipment.getCurrentState().getStateName();

            // Handle state transitions according to your state pattern
            if (selectedRadio == asignadoRadio && "REQUESTED".equals(currentState)) {
                newState = new AssignedState();
            } else if (selectedRadio == enRutaRadio && "ASSIGNED".equals(currentState)) {
                newState = new InRouteState();
            } else if (selectedRadio == entregadoRadio && "ROUTE".equals(currentState)) {
                newState = new DeliveredState();
            } else if (selectedRadio == incidenciaRadio) {
                // CANCELLED can happen from REQUESTED or ASSIGNED states
                if ("REQUESTED".equals(currentState) || "ASSIGNED".equals(currentState)) {
                    newState = new CancelledState();
                }
            }

            if (newState != null) {
                success = selectedShipment.changeState(newState);

                if (success) {
                    // Save notes if provided
                    if (!notesArea.getText().isEmpty()) {
                        // You might want to add a notes field to your Shipment class
                        System.out.println("Notes saved: " + notesArea.getText());
                    }

                    // Update the shipment in the system
                    deliverX.updateShipment(selectedShipment.getIdShipment(), selectedShipment);

                    // Show success message
                    showMessage("Shipment status updated successfully from " + currentState + " to " + newState.getStateName() + "!", "success");

                    // Update UI
                    currentStatusLabel.setText(newState.getStateName());
                    updateAvailableStatusOptions(selectedShipment);

                    // Clear selection after successful update
                    statusToggleGroup.selectToggle(null);

                    // Recargar la lista por si hay cambios
                    loadAssignedShipments();
                } else {
                    showMessage("Invalid state transition from " + currentState, "error");
                }
            } else {
                showMessage("Invalid transition from current state: " + currentState, "error");
            }
        } catch (Exception e) {
            showMessage("Error updating status: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    private void showMessage(String message, String type) {
        messageLabel.setText(message);
        if ("error".equals(type)) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else if ("success".equals(type)) {
            messageLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else {
            messageLabel.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
        }
    }
}