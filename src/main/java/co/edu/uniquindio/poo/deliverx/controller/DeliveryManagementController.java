package co.edu.uniquindio.poo.deliverx.controller;

import co.edu.uniquindio.poo.deliverx.model.DeliverX;
import co.edu.uniquindio.poo.deliverx.model.DeliveryMan;
import co.edu.uniquindio.poo.deliverx.model.state.ActiveState;
import co.edu.uniquindio.poo.deliverx.model.state.InactiveState;
import co.edu.uniquindio.poo.deliverx.model.state.InRouteDeliveryState;
import co.edu.uniquindio.poo.deliverx.model.state.DeliveryManState;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DeliveryManagementController {
    @FXML
    public TextField searchField;
    @FXML
    public ComboBox<String> filterStateComboBox;
    @FXML
    public TableView<DeliveryMan> deliveryTable;
    @FXML
    public TableColumn<DeliveryMan, String> deliveryIdColumn;
    @FXML
    public TableColumn<DeliveryMan, String> nameColumn;
    @FXML
    public TableColumn<DeliveryMan, String> emailColumn;
    @FXML
    public TableColumn<DeliveryMan, String> phoneColumn;
    @FXML
    public TableColumn<DeliveryMan, String> stateColumn;
    @FXML
    public TableColumn<DeliveryMan, String> zonaColumn;
    @FXML
    public Label statusLabel;

    private DeliverX deliverX;
    private ObservableList<DeliveryMan> deliveryList;
    private ObservableList<DeliveryMan> filteredList;

    @FXML
    public void initialize() {
        deliverX = DeliverX.getInstance();

        // Configure table columns
        deliveryIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        stateColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() -> {
                    try {
                        DeliveryManState state = cellData.getValue().getState();
                        return state != null ? state.getStateName() : "NO STATE";
                    } catch (Exception e) {
                        return "ERROR";
                    }
                })
        );
        zonaColumn.setCellValueFactory(new PropertyValueFactory<>("zonaCobertura"));
        filterStateComboBox.setItems(FXCollections.observableArrayList(
                "All", "ACTIVE", "INACTIVE", "ROUTE", "NO STATE"
        ));
        filterStateComboBox.setValue("All");
        loadDeliveryData();
        setupListeners();

        updateStatus("System loaded. " + deliveryList.size() + " delivery men found.");
    }

    private void setupListeners() {
        filterStateComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });
        deliveryTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        updateStatus("Delivery man selected: " + newSelection.getName());
                    }
                });
    }

    private void loadDeliveryData() {
        try {
            List<DeliveryMan> deliveryMans = deliverX.getListDeliveryMans();
            deliveryList = FXCollections.observableArrayList(deliveryMans);
            filteredList = FXCollections.observableArrayList(deliveryList);
            deliveryTable.setItems(filteredList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Data Load Error",
                    "Error loading delivery men data: " + e.getMessage());
            deliveryList = FXCollections.observableArrayList();
            filteredList = FXCollections.observableArrayList();
            deliveryTable.setItems(filteredList);
        }
    }

    @FXML
    public void handleSearch(ActionEvent event) {
        applyFilters();
    }

    @FXML
    public void handleRefresh(ActionEvent event) {
        loadDeliveryData();
        searchField.clear();
        filterStateComboBox.setValue("All");
        updateStatus("Data updated. " + deliveryList.size() + " delivery men loaded.");
    }

    @FXML
    public void handleViewDetails(ActionEvent event) {
        DeliveryMan selected = deliveryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showDeliveryDetails(selected);
        } else {
            showAlert(Alert.AlertType.WARNING, "Selection Required",
                    "Please select a delivery man to view details.");
        }
    }

    @FXML
    public void handleEdit(ActionEvent event) {
        DeliveryMan selected = deliveryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            editDeliveryMan(selected);
        } else {
            showAlert(Alert.AlertType.WARNING, "Selection Required",
                    "Please select a delivery man to edit.");
        }
    }

    @FXML
    public void handleChangeState(ActionEvent event) {
        DeliveryMan selected = deliveryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            changeDeliveryState(selected);
        } else {
            showAlert(Alert.AlertType.WARNING, "Selection Required",
                    "Please select a delivery man to change state.");
        }
    }

    @FXML
    public void handleDelete(ActionEvent event) {
        DeliveryMan selected = deliveryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            deleteDeliveryMan(selected);
        } else {
            showAlert(Alert.AlertType.WARNING, "Selection Required",
                    "Please select a delivery man to delete.");
        }
    }

    private void applyFilters() {
        try {
            String searchText = searchField.getText().toLowerCase();
            String stateFilter = filterStateComboBox.getValue();

            List<DeliveryMan> filtered = deliveryList.stream()
                    .filter(delivery -> {
                        // Search filter
                        boolean matchesSearch = searchText.isEmpty() ||
                                delivery.getUserId().toLowerCase().contains(searchText) ||
                                delivery.getName().toLowerCase().contains(searchText);

                        // State filter
                        boolean matchesState = stateFilter.equals("All");
                        if (!matchesState) {
                            try {
                                DeliveryManState state = delivery.getState();
                                String stateName = state != null ? state.getStateName() : "NO STATE";
                                matchesState = stateName.equalsIgnoreCase(stateFilter);
                            } catch (Exception e) {
                                matchesState = "NO STATE".equalsIgnoreCase(stateFilter);
                            }
                        }

                        return matchesSearch && matchesState;
                    })
                    .collect(Collectors.toList());

            filteredList.setAll(filtered);
            updateStatus(filtered.size() + " delivery men found with applied filters.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Filter Error",
                    "Error applying filters: " + e.getMessage());
        }
    }

    private void showDeliveryDetails(DeliveryMan deliveryMan) {
        try {
            StringBuilder details = new StringBuilder();
            details.append("=== DELIVERY MAN DETAILS ===\n\n");
            details.append("ID: ").append(deliveryMan.getUserId()).append("\n");
            details.append("Name: ").append(deliveryMan.getName()).append("\n");
            details.append("Email: ").append(deliveryMan.getEmail()).append("\n");
            details.append("Phone: ").append(deliveryMan.getPhoneNumber()).append("\n");
            DeliveryManState state = null;
            String stateName = "NO STATE";
            try {
                state = deliveryMan.getState();
                if (state != null) {
                    stateName = state.getStateName();
                }
            } catch (Exception e) {
                stateName = "ERROR: " + e.getMessage();
            }
            details.append("State: ").append(stateName).append("\n");

            details.append("Coverage Zone: ").append(deliveryMan.getZonaCobertura()).append("\n");

            String shipmentInfo = "None";
            try {
                java.lang.reflect.Method getCurrentShipmentMethod = deliveryMan.getClass().getMethod("getCurrentShipment");
                Object currentShipment = getCurrentShipmentMethod.invoke(deliveryMan);
                if (currentShipment != null) {
                    java.lang.reflect.Method getIdMethod = currentShipment.getClass().getMethod("getIdShipment");
                    Object shipmentId = getIdMethod.invoke(currentShipment);
                    shipmentInfo = shipmentId != null ? shipmentId.toString() : "Unknown";
                }
            } catch (Exception e) {
                shipmentInfo = "Unable to retrieve";
            }
            details.append("Current Shipment: ").append(shipmentInfo).append("\n");

            TextArea textArea = new TextArea(details.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefSize(400, 300);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Delivery Man Details");
            alert.setHeaderText("Complete information of " + deliveryMan.getName());
            alert.getDialogPane().setContent(textArea);
            alert.showAndWait();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Details Error",
                    "Error showing details: " + e.getMessage());
        }
    }
// for edit delivery
    private void editDeliveryMan(DeliveryMan deliveryMan) {
        try {
            Dialog<DeliveryMan> dialog = new Dialog<>();
            dialog.setTitle("Edit Delivery Man");
            dialog.setHeaderText("Editing: " + deliveryMan.getName());

            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

            TextField nameField = new TextField(deliveryMan.getName());
            TextField emailField = new TextField(deliveryMan.getEmail());
            TextField phoneField = new TextField(deliveryMan.getPhoneNumber());
            TextField zonaField = new TextField(deliveryMan.getZonaCobertura());

            grid.add(new Label("Name:"), 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(new Label("Email:"), 0, 1);
            grid.add(emailField, 1, 1);
            grid.add(new Label("Phone:"), 0, 2);
            grid.add(phoneField, 1, 2);
            grid.add(new Label("Coverage Zone:"), 0, 3);
            grid.add(zonaField, 1, 3);

            dialog.getDialogPane().setContent(grid);

            // Convert result
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    try {
                        DeliveryMan.Builder<?> builder = new DeliveryMan.Builder<>()
                                .userId(deliveryMan.getUserId())
                                .name(nameField.getText())
                                .email(emailField.getText())
                                .phoneNumber(phoneField.getText())
                                .zonaCobertura(zonaField.getText());

                        try {
                            DeliveryManState currentState = deliveryMan.getState();
                            if (currentState != null) {
                                builder.state(currentState);
                            }
                        } catch (Exception e) {
                        }

                        return builder.build();
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Build Error",
                                "Error creating updated delivery man: " + e.getMessage());
                        return null;
                    }
                }
                return null;
            });

            Optional<DeliveryMan> result = dialog.showAndWait();
            result.ifPresent(updatedDeliveryMan -> {
                try {
                    boolean success = deliverX.updateDeliveryMan(deliveryMan.getUserId(), updatedDeliveryMan);
                    if (success) {
                        loadDeliveryData();
                        updateStatus("Delivery man updated successfully: " + updatedDeliveryMan.getName());
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Update Error",
                                "Could not update the delivery man in the system.");
                    }
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Update Error",
                            "Error updating delivery man: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Edit Error",
                    "Error opening edit dialog: " + e.getMessage());
        }
    }

    private void changeDeliveryState(DeliveryMan deliveryMan) {
        try {
            // Get current state safely
            DeliveryManState currentState = null;
            String currentStateName = "NO STATE";
            try {
                currentState = deliveryMan.getState();
                if (currentState != null) {
                    currentStateName = currentState.getStateName();
                }
            } catch (Exception e) {
                currentStateName = "ERROR";
            }

            ChoiceDialog<String> dialog = new ChoiceDialog<>(
                    currentStateName,
                    "ACTIVE", "INACTIVE", "ROUTE"
            );
            dialog.setTitle("Change State");
            dialog.setHeaderText("Changing state of: " + deliveryMan.getName());
            dialog.setContentText("Select the new state:");

            Optional<String> result = dialog.showAndWait();
            DeliveryManState finalCurrentState = currentState;
            String finalCurrentStateName = currentStateName;
            result.ifPresent(newState -> {
                try {
                    DeliveryManState newStateObj;
                    switch (newState) {
                        case "ACTIVE":
                            newStateObj = new ActiveState();
                            break;
                        case "INACTIVE":
                            newStateObj = new InactiveState();
                            break;
                        case "ROUTE":
                            newStateObj = new InRouteDeliveryState();
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid state: " + newState);
                    }

                    boolean success = false;
                    String message;

                    if (finalCurrentState == null) {
                        // if current state is null set the state directly
                        deliveryMan.setState(newStateObj);
                        success = true;
                        message = "State set to: " + newState + " (was previously NO STATE)";
                    } else {
                        success = deliveryMan.changeState(newStateObj);
                        message = success ? "State changed to: " + newState :
                                "Cannot change from " + finalCurrentStateName + " to " + newState;
                    }

                    if (success) {
                        boolean systemUpdate = deliverX.updateDeliveryMan(deliveryMan.getUserId(), deliveryMan);
                        if (systemUpdate) {
                            loadDeliveryData();
                            updateStatus(message + " for " + deliveryMan.getName());
                        } else {
                            showAlert(Alert.AlertType.ERROR, "System Update Error",
                                    "Could not update delivery man in system.");
                        }
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Invalid Transition", message);
                    }
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "State Change Error",
                            "Error changing state: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Dialog Error",
                    "Error opening state change dialog: " + e.getMessage());
        }
    }

    private void deleteDeliveryMan(DeliveryMan deliveryMan) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete this delivery man?");
            alert.setContentText("This action cannot be undone.\nDelivery Man: " +
                    deliveryMan.getName() + " (" + deliveryMan.getUserId() + ")");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                deliverX.deleteDeliveryMan(deliveryMan.getUserId());
                loadDeliveryData();
                updateStatus("Delivery man deleted: " + deliveryMan.getName());
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Deletion Error",
                    "Error deleting delivery man: " + e.getMessage());
        }
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}