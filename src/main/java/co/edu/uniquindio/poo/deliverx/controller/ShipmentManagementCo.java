package co.edu.uniquindio.poo.deliverx.controller;

import co.edu.uniquindio.poo.deliverx.model.*;
import co.edu.uniquindio.poo.deliverx.model.state.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ShipmentManagementCo {
    @FXML public TextField searchField;
    @FXML public ComboBox<String> statusFilterComboBox;
    @FXML public TableView<Shipment> shipmentsTable;
    @FXML public TableColumn<Shipment, String> shipmentIdColumn;
    @FXML public TableColumn<Shipment, String> customerColumn;
    @FXML public TableColumn<Shipment, String> originColumn;
    @FXML public TableColumn<Shipment, String> destinationColumn;
    @FXML public TableColumn<Shipment, String> statusColumn;
    @FXML public TableColumn<Shipment, Double> priceColumn;
    @FXML public TableColumn<Shipment, String> dateColumn;
    @FXML public Label statusLabel;

    private DeliverX deliverX;
    private ObservableList<Shipment> shipmentsList;
    private ObservableList<Shipment> filteredList;

    @FXML
    public void initialize() {
        deliverX = DeliverX.getInstance();

        // Configurar columnas de la tabla
        shipmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("idShipment"));

        customerColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() -> {
                    Customer customer = cellData.getValue().getCustomer();
                    return customer != null ? customer.getName() : "N/A";
                })
        );

        originColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() -> {
                    Address origin = cellData.getValue().getOrigin();
                    return origin != null ? origin.getCity() : "N/A";
                })
        );

        destinationColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() -> {
                    Address destination = cellData.getValue().getDestination();
                    return destination != null ? destination.getCity() : "N/A";
                })
        );

        statusColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() -> {
                    return cellData.getValue().getCurrentState().getStateName();
                })
        );

        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceColumn.setCellFactory(column -> new TableCell<Shipment, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%,.0f", item));
                }
            }
        });

        dateColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() -> {
                    return cellData.getValue().getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                })
        );

        // Configurar ComboBox de estados
        statusFilterComboBox.setItems(FXCollections.observableArrayList(
                "ALL", "SOLICITADO", "ASIGNADO", "EN_RUTA", "ENTREGADO", "CANCELADO"
        ));
        statusFilterComboBox.setValue("ALL");

        // Inicializar listas
        shipmentsList = FXCollections.observableArrayList();
        filteredList = FXCollections.observableArrayList();
        shipmentsTable.setItems(filteredList);

        // Configurar listeners
        setupListeners();

        // Cargar datos iniciales
        loadShipmentsData();

        updateStatus("Sistema cargado. " + shipmentsList.size() + " envíos encontrados.");
    }

    private void setupListeners() {
        // Listener para filtro de estado
        statusFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });

        // Listener para selección de tabla
        shipmentsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        updateStatus("Envío seleccionado: " + newSelection.getIdShipment());
                    }
                });
    }

    private void loadShipmentsData() {
        try {
            List<Shipment> shipments = deliverX.getListShipments();
            shipmentsList.setAll(shipments);
            filteredList.setAll(shipmentsList);
            updateStatus(shipmentsList.size() + " envíos cargados correctamente");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error de Carga",
                    "Error al cargar los envíos: " + e.getMessage());
            updateStatus("Error al cargar envíos");
        }
    }

    @FXML
    public void handleSearch(ActionEvent event) {
        applyFilters();
    }

    @FXML
    public void handleRefresh(ActionEvent event) {
        loadShipmentsData();
        searchField.clear();
        statusFilterComboBox.setValue("Todos");
        updateStatus("Datos actualizados. " + shipmentsList.size() + " envíos cargados.");
    }

    @FXML
    public void handleViewDetails(ActionEvent event) {
        Shipment selected = shipmentsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showShipmentDetails(selected);
        } else {
            showAlert(Alert.AlertType.WARNING, "Selección Requerida",
                    "Por favor seleccione un envío para ver los detalles.");
        }
    }

    @FXML
    public void handleAssignDelivery(ActionEvent event) {
        Shipment selected = shipmentsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            assignDeliveryMan(selected);
        } else {
            showAlert(Alert.AlertType.WARNING, "Selección Requerida",
                    "Por favor seleccione un envío para asignar repartidor.");
        }
    }

    @FXML
    public void handleChangeStatus(ActionEvent event) {
        Shipment selected = shipmentsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            changeShipmentStatus(selected);
        } else {
            showAlert(Alert.AlertType.WARNING, "Selección Requerida",
                    "Por favor seleccione un envío para cambiar estado.");
        }
    }

    @FXML
    public void handleDelete(ActionEvent event) {
        Shipment selected = shipmentsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            deleteShipment(selected);
        } else {
            showAlert(Alert.AlertType.WARNING, "Selección Requerida",
                    "Por favor seleccione un envío para eliminar.");
        }
    }

    private void applyFilters() {
        try {
            String searchText = searchField.getText().toLowerCase();
            String statusFilter = statusFilterComboBox.getValue();

            List<Shipment> filtered = shipmentsList.stream()
                    .filter(shipment ->
                            (searchText.isEmpty() ||
                                    shipment.getIdShipment().toLowerCase().contains(searchText) ||
                                    (shipment.getCustomer() != null &&
                                            shipment.getCustomer().getName().toLowerCase().contains(searchText))) &&
                                    (statusFilter.equals("Todos") ||
                                            shipment.getCurrentState().getStateName().equalsIgnoreCase(statusFilter))
                    )
                    .collect(Collectors.toList());

            filteredList.setAll(filtered);
            updateStatus(filtered.size() + " envíos encontrados con los filtros aplicados");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error de Filtro",
                    "Error al aplicar filtros: " + e.getMessage());
        }
    }

    private void showShipmentDetails(Shipment shipment) {
        try {
            StringBuilder details = new StringBuilder();
            details.append("=== DETALLES COMPLETOS DEL ENVÍO ===\n\n");

            details.append("INFORMACIÓN BÁSICA:\n");
            details.append("• ID: ").append(shipment.getIdShipment()).append("\n");
            details.append("• Estado: ").append(shipment.getCurrentState().getStateName()).append("\n");
            details.append("• Fecha: ").append(shipment.getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
            details.append("• Peso: ").append(shipment.getWeight()).append(" kg\n");
            details.append("• Precio: $").append(String.format("%,.0f", shipment.getPrice())).append("\n");
            details.append("• Tipo: ").append(shipment.getType()).append("\n\n");

            details.append("CLIENTE:\n");
            if (shipment.getCustomer() != null) {
                details.append("• Nombre: ").append(shipment.getCustomer().getName()).append("\n");
                details.append("• Email: ").append(shipment.getCustomer().getEmail()).append("\n");
                details.append("• Teléfono: ").append(shipment.getCustomer().getPhoneNumber()).append("\n");
            }

            details.append("\nDIRECCIONES:\n");
            if (shipment.getOrigin() != null) {
                details.append("• Origen: ").append(shipment.getOrigin().getStreet())
                        .append(", ").append(shipment.getOrigin().getCity()).append("\n");
            }
            if (shipment.getDestination() != null) {
                details.append("• Destino: ").append(shipment.getDestination().getStreet())
                        .append(", ").append(shipment.getDestination().getCity()).append("\n");
            }

            details.append("\nREPARTIDOR:\n");
            if (shipment.getDeliveryMan() != null) {
                details.append("• Nombre: ").append(shipment.getDeliveryMan().getName()).append("\n");
                details.append("• Estado: ").append(shipment.getDeliveryMan().getState().getStateName()).append("\n");
                details.append("• Zona: ").append(shipment.getDeliveryMan().getZonaCobertura()).append("\n");
            } else {
                details.append("• No asignado\n");
            }

            details.append("\nPAGO:\n");
            if (shipment.getPay() != null) {
                details.append("• Método: ").append(shipment.getPay().getPaymentMethod()).append("\n");
                details.append("• Estado: ").append(shipment.getPay().getResult()).append("\n");
            }

            if (shipment.getAdditionalServices() != null && !shipment.getAdditionalServices().isEmpty()) {
                details.append("\nSERVICIOS ADICIONALES:\n");
                for (String service : shipment.getAdditionalServices()) {
                    details.append("• ").append(service).append("\n");
                }
            }

            TextArea textArea = new TextArea(details.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefSize(600, 500);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Detalles del Envío");
            alert.setHeaderText("Información completa - " + shipment.getIdShipment());
            alert.getDialogPane().setContent(textArea);
            alert.showAndWait();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error de Detalles",
                    "Error al mostrar detalles: " + e.getMessage());
        }
    }

    private void assignDeliveryMan(Shipment shipment) {
        try {
            // Obtener lista de repartidores disponibles
            List<DeliveryMan> availableDeliveryMen = deliverX.getListDeliveryMans().stream()
                    .filter(dm -> dm.getState() != null &&
                            dm.getState().getStateName().equals("ACTIVE"))
                    .collect(Collectors.toList());

            if (availableDeliveryMen.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Sin Repartidores",
                        "No hay repartidores disponibles en este momento.");
                return;
            }

            ChoiceDialog<DeliveryMan> dialog = new ChoiceDialog<>(availableDeliveryMen.get(0), availableDeliveryMen);
            dialog.setTitle("Asignar Repartidor");
            dialog.setHeaderText("Asignar repartidor al envío: " + shipment.getIdShipment());
            dialog.setContentText("Seleccione un repartidor:");

            Optional<DeliveryMan> result = dialog.showAndWait();
            result.ifPresent(deliveryMan -> {
                try {
                    shipment.setDeliveryMan(deliveryMan);

                    // Cambiar estado a ASIGNADO si está en SOLICITADO
                    if (shipment.getCurrentState().getStateName().equals("SOLICITADO")) {
                        shipment.changeState(new AssignedState());
                    }

                    // Actualizar en el sistema
                    deliverX.updateShipment(shipment.getIdShipment(), shipment);

                    // Asignar también el envío al repartidor
                    deliveryMan.assignShipment(shipment);
                    deliverX.updateDeliveryMan(deliveryMan.getUserId(), deliveryMan);

                    loadShipmentsData();
                    updateStatus("Repartidor " + deliveryMan.getName() + " asignado al envío " + shipment.getIdShipment());

                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error de Asignación",
                            "Error al asignar repartidor: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Error en asignación: " + e.getMessage());
        }
    }

    private void changeShipmentStatus(Shipment shipment) {
        try {
            String currentState = shipment.getCurrentState().getStateName();

            // Determinar estados posibles según el estado actual
            List<String> possibleStates = getPossibleNextStates(currentState);

            if (possibleStates.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "Estado Final",
                        "Este envío ya está en un estado final y no puede cambiarse.");
                return;
            }

            ChoiceDialog<String> dialog = new ChoiceDialog<>(possibleStates.get(0), possibleStates);
            dialog.setTitle("Cambiar Estado");
            dialog.setHeaderText("Cambiar estado del envío: " + shipment.getIdShipment());
            dialog.setContentText("Estado actual: " + currentState + "\nSeleccione nuevo estado:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newState -> {
                try {
                    ShipmentState newStateObj = createStateFromString(newState);
                    boolean success = shipment.changeState(newStateObj);

                    if (success) {
                        deliverX.updateShipment(shipment.getIdShipment(), shipment);
                        loadShipmentsData();
                        updateStatus("Estado cambiado a: " + newState + " para el envío " + shipment.getIdShipment());
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Transición Inválida",
                                "No se puede cambiar de " + currentState + " a " + newState);
                    }

                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Error",
                            "Error al cambiar estado: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Error en cambio de estado: " + e.getMessage());
        }
    }

    private List<String> getPossibleNextStates(String currentState) {
        switch (currentState) {
            case "REQUESTED":
                return List.of("ASSIGNED", "CANCELLED");
            case "ASSIGNED":
                return List.of("IN_ROUTE");
            case "IN_ROUTE":
                return List.of("DELIVERED");
            case "DELIVERED":
            case "CANCELLED":
                return List.of(); // Estados finales
            default:
                return List.of("REQUESTED", "ASSIGNED", "IN_ROUTE", "DELIVERED", "CANCELLED");
        }
    }

    private ShipmentState createStateFromString(String state) {
        switch (state) {
            case "REQUESTED": return new RequestedState();
            case "ASSIGNED": return new AssignedState();
            case "IN_ROUTE": return new InRouteState();
            case "DELIVERED": return new DeliveredState();
            case "CANCELLED": return new CancelledState();
            default: return new RequestedState();
        }
    }


    private void deleteShipment(Shipment shipment) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Eliminación");
            alert.setHeaderText("¿Está seguro de eliminar este envío?");
            alert.setContentText("Envío: " + shipment.getIdShipment() +
                    "\nCliente: " + (shipment.getCustomer() != null ? shipment.getCustomer().getName() : "N/A") +
                    "\nEstado: " + shipment.getCurrentState().getStateName() +
                    "\nPrecio: $" + String.format("%,.0f", shipment.getPrice()));

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean success = deliverX.deleteShipment(shipment.getIdShipment());
                if (success) {
                    loadShipmentsData();
                    updateStatus("Envío eliminado: " + shipment.getIdShipment());
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error de Eliminación",
                            "No se pudo eliminar el envío. Verifique que esté en estado SOLICITADO o CANCELADO.");
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error de Eliminación",
                    "Error al eliminar envío: " + e.getMessage());
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