package co.edu.uniquindio.poo.deliverx.controller;

import co.edu.uniquindio.poo.deliverx.model.*;
import co.edu.uniquindio.poo.deliverx.model.state.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DeliveryAssignedOrdersController {
    @FXML public ComboBox<String> statusFilterComboBox;
    @FXML public TextField searchField;
    @FXML public TableView<Shipment> assignedOrdersTable;
    @FXML public TableColumn<Shipment, String> orderIdColumn;
    @FXML public TableColumn<Shipment, String> destinationColumn;
    @FXML public TableColumn<Shipment, String> statusColumn;
    @FXML public TableColumn<Shipment, String> zoneColumn;
    @FXML public TableColumn<Shipment, String> dateColumn;
    @FXML public Label totalAssignedLabel;
    @FXML public Label completedTodayLabel;
    @FXML public Label inProgressLabel;
    @FXML public Label statusLabel;

    private DeliverX deliverX;
    private DeliveryMan currentDeliveryMan;
    private ObservableList<Shipment> assignedOrdersList;
    private ObservableList<Shipment> filteredList;

    @FXML
    public void initialize() {
        deliverX = DeliverX.getInstance();

        // Obtener el repartidor actualmente logueado
        User loggedUser = deliverX.getUserLoged();
        if (loggedUser instanceof DeliveryMan) {
            currentDeliveryMan = (DeliveryMan) loggedUser;
            setupComponents();
            loadAssignedOrders();
            updateStatistics();
        } else {
            showMessage("Error: Debe iniciar sesión como repartidor", "error");
        }
    }

    private void setupComponents() {
        // Configurar ComboBox de filtros de estado
        statusFilterComboBox.setItems(FXCollections.observableArrayList(
                "Todos", "ASSIGNED", "IN_ROUTE", "DELIVERED"
        ));
        statusFilterComboBox.setValue("Todos");

        // Configurar columnas de la tabla
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("idShipment"));

        destinationColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() -> {
                    Address destination = cellData.getValue().getDestination();
                    return destination != null ?
                            destination.getStreet() + ", " + destination.getCity() : "N/A";
                })
        );

        statusColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() -> {
                    return cellData.getValue().getCurrentState().getStateName();
                })
        );

        zoneColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() -> {
                    Address destination = cellData.getValue().getDestination();
                    return destination != null ? destination.getCity() : "N/A";
                })
        );

        dateColumn.setCellValueFactory(cellData ->
                javafx.beans.binding.Bindings.createStringBinding(() -> {
                    return cellData.getValue().getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                })
        );

        // Inicializar listas
        assignedOrdersList = FXCollections.observableArrayList();
        filteredList = FXCollections.observableArrayList();
        assignedOrdersTable.setItems(filteredList);

        // Configurar listeners
        setupListeners();

        updateStatus("Sistema cargado. Cargando pedidos asignados...");
    }

    private void setupListeners() {
        // Listener para filtro de estado
        statusFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });

        // Listener para selección de tabla
        assignedOrdersTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        updateStatus("Pedido seleccionado: " + newSelection.getIdShipment());
                    }
                });
    }

    private void loadAssignedOrders() {
        try {
            // Obtener todos los envíos asignados a este repartidor
            List<Shipment> allShipments = deliverX.getListShipments();
            List<Shipment> assignedShipments = allShipments.stream()
                    .filter(shipment -> shipment.getDeliveryMan() != null &&
                            shipment.getDeliveryMan().getUserId().equals(currentDeliveryMan.getUserId()))
                    .collect(Collectors.toList());

            assignedOrdersList.setAll(assignedShipments);
            filteredList.setAll(assignedOrdersList);
            updateStatus(assignedOrdersList.size() + " pedidos asignados cargados");

        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error de Carga",
                    "Error al cargar los pedidos asignados: " + e.getMessage());
            updateStatus("Error al cargar pedidos");
        }
    }

    private void updateStatistics() {
        try {
            int totalAssigned = assignedOrdersList.size();
            int completedToday = calculateCompletedToday();
            int inProgress = calculateInProgress();

            totalAssignedLabel.setText(String.valueOf(totalAssigned));
            completedTodayLabel.setText(String.valueOf(completedToday));
            inProgressLabel.setText(String.valueOf(inProgress));

        } catch (Exception e) {
            showMessage("Error al calcular estadísticas: " + e.getMessage(), "error");
        }
    }

    private int calculateCompletedToday() {
        return (int) assignedOrdersList.stream()
                .filter(shipment -> shipment.getCurrentState().getStateName().equals("DELIVERED") &&
                        shipment.getDateTime().equals(LocalDate.now()))
                .count();
    }

    private int calculateInProgress() {
        return (int) assignedOrdersList.stream()
                .filter(shipment -> shipment.getCurrentState().getStateName().equals("IN_ROUTE"))
                .count();
    }

    @FXML
    public void handleSearch(ActionEvent event) {
        applyFilters();
    }

    @FXML
    public void handleRefresh(ActionEvent event) {
        loadAssignedOrders();
        updateStatistics();
        searchField.clear();
        statusFilterComboBox.setValue("Todos");
        updateStatus("Datos actualizados. " + assignedOrdersList.size() + " pedidos asignados.");
    }

    @FXML
    public void handleViewDetails(ActionEvent event) {
        Shipment selected = assignedOrdersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showOrderDetails(selected);
        } else {
            showAlert(AlertType.WARNING, "Selección Requerida",
                    "Por favor seleccione un pedido para ver los detalles.");
        }
    }

    @FXML
    public void handleStartDelivery(ActionEvent event) {
        Shipment selected = assignedOrdersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            startDelivery(selected);
        } else {
            showAlert(AlertType.WARNING, "Selección Requerida",
                    "Por favor seleccione un pedido para iniciar la entrega.");
        }
    }

    @FXML
    public void handleCompleteDelivery(ActionEvent event) {
        Shipment selected = assignedOrdersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            completeDelivery(selected);
        } else {
            showAlert(AlertType.WARNING, "Selección Requerida",
                    "Por favor seleccione un pedido para completar la entrega.");
        }
    }

    @FXML
    public void handleReportIssue(ActionEvent event) {
        Shipment selected = assignedOrdersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            reportIssue(selected);
        } else {
            showAlert(AlertType.WARNING, "Selección Requerida",
                    "Por favor seleccione un pedido para reportar un problema.");
        }
    }

    private void applyFilters() {
        try {
            String searchText = searchField.getText().toLowerCase();
            String statusFilter = statusFilterComboBox.getValue();

            List<Shipment> filtered = assignedOrdersList.stream()
                    .filter(order ->
                            (searchText.isEmpty() ||
                                    order.getIdShipment().toLowerCase().contains(searchText) ||
                                    (order.getDestination() != null &&
                                            order.getDestination().getStreet().toLowerCase().contains(searchText))) &&
                                    (statusFilter.equals("Todos") ||
                                            order.getCurrentState().getStateName().equalsIgnoreCase(statusFilter))
                    )
                    .collect(Collectors.toList());

            filteredList.setAll(filtered);
            updateStatus(filtered.size() + " pedidos encontrados con los filtros aplicados");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error de Filtro",
                    "Error al aplicar filtros: " + e.getMessage());
        }
    }

    private void showOrderDetails(Shipment shipment) {
        try {
            StringBuilder details = new StringBuilder();
            details.append("=== DETALLES DEL PEDIDO ===\n\n");
            details.append("INFORMACIÓN DEL PEDIDO:\n");
            details.append("• ID: ").append(shipment.getIdShipment()).append("\n");
            details.append("• Estado: ").append(shipment.getCurrentState().getStateName()).append("\n");
            details.append("• Fecha: ").append(shipment.getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
            details.append("• Peso: ").append(shipment.getWeight()).append(" kg\n");
            details.append("• Precio: $").append(String.format("%,.0f", shipment.getPrice())).append("\n");
            details.append("• Tipo: ").append(shipment.getType()).append("\n\n");

            details.append("INFORMACIÓN DEL CLIENTE:\n");
            if (shipment.getCustomer() != null) {
                details.append("• Nombre: ").append(shipment.getCustomer().getName()).append("\n");
                details.append("• Teléfono: ").append(shipment.getCustomer().getPhoneNumber()).append("\n");
                details.append("• Email: ").append(shipment.getCustomer().getEmail()).append("\n");
            }

            details.append("\nDIRECCIONES:\n");
            if (shipment.getOrigin() != null) {
                details.append("• Origen: ").append(shipment.getOrigin().getStreet())
                        .append(", ").append(shipment.getOrigin().getCity()).append("\n");
            }
            if (shipment.getDestination() != null) {
                details.append("• Destino: ").append(shipment.getDestination().getStreet())
                        .append(", ").append(shipment.getDestination().getCity()).append("\n");
                details.append("• Zona: ").append(shipment.getDestination().getCity()).append("\n");
            }

            details.append("\nINSTRUCCIONES ESPECIALES:\n");
            if (shipment.getAdditionalServices() != null && !shipment.getAdditionalServices().isEmpty()) {
                for (String service : shipment.getAdditionalServices()) {
                    details.append("• ").append(service).append("\n");
                }
            } else {
                details.append("• No hay instrucciones especiales\n");
            }

            TextArea textArea = new TextArea(details.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefSize(500, 400);

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Detalles del Pedido");
            alert.setHeaderText("Información de entrega - " + shipment.getIdShipment());
            alert.getDialogPane().setContent(textArea);
            alert.showAndWait();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error de Detalles",
                    "Error al mostrar detalles: " + e.getMessage());
        }
    }

    private void startDelivery(Shipment shipment) {
        try {
            String currentState = shipment.getCurrentState().getStateName();

            if (!currentState.equals("ASSIGNED")) {
                showAlert(AlertType.WARNING, "Acción No Permitida",
                        "Solo puede iniciar entregas de pedidos en estado ASSIGNED.\n" +
                                "Estado actual: " + currentState);
                return;
            }

            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Inicio de Entrega");
            alert.setHeaderText("¿Está seguro de iniciar la entrega de este pedido?");
            alert.setContentText("Pedido: " + shipment.getIdShipment() +
                    "\nDestino: " + (shipment.getDestination() != null ?
                    shipment.getDestination().getStreet() : "N/A") +
                    "\nCliente: " + (shipment.getCustomer() != null ?
                    shipment.getCustomer().getName() : "N/A"));

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Cambiar estado a EN_RUTA
                boolean success = shipment.changeState(new InRouteState());

                if (success) {
                    // Cambiar estado del repartidor a EN_RUTA
                    currentDeliveryMan.changeState(new co.edu.uniquindio.poo.deliverx.model.state.InRouteDeliveryState());
                    deliverX.updateDeliveryMan(currentDeliveryMan.getUserId(), currentDeliveryMan);

                    // Actualizar en el sistema
                    deliverX.updateShipment(shipment.getIdShipment(), shipment);

                    loadAssignedOrders();
                    updateStatistics();
                    updateStatus("Entrega iniciada para el pedido: " + shipment.getIdShipment());
                } else {
                    showAlert(AlertType.ERROR, "Error de Estado",
                            "No se pudo iniciar la entrega. Estado actual: " + currentState);
                }
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error",
                    "Error al iniciar entrega: " + e.getMessage());
        }
    }

    private void completeDelivery(Shipment shipment) {
        try {
            String currentState = shipment.getCurrentState().getStateName();

            if (!currentState.equals("IN_ROUTE")) {
                showAlert(AlertType.WARNING, "Acción No Permitida",
                        "Solo puede completar entregas de pedidos en estado IN_ROUTE.\n" +
                                "Estado actual: " + currentState);
                return;
            }

            // Diálogo para confirmar pago si es en efectivo
            if (shipment.getPay() != null &&
                    shipment.getPay().getPaymentMethod() == PaymentMethod.CASH &&
                    shipment.getPay().getResult() == TransactionResult.PENDING) {

                boolean paymentConfirmed = confirmCashPayment(shipment);
                if (!paymentConfirmed) {
                    return;
                }
            }

            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Entrega Completada");
            alert.setHeaderText("¿Está seguro de marcar esta entrega como completada?");
            alert.setContentText("Pedido: " + shipment.getIdShipment() +
                    "\nEsta acción no se puede deshacer.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Cambiar estado a ENTREGADO
                boolean success = shipment.changeState(new DeliveredState());

                if (success) {
                    // Liberar al repartidor
                    currentDeliveryMan.completeShipment();
                    deliverX.updateDeliveryMan(currentDeliveryMan.getUserId(), currentDeliveryMan);

                    // Actualizar en el sistema
                    deliverX.updateShipment(shipment.getIdShipment(), shipment);

                    loadAssignedOrders();
                    updateStatistics();
                    updateStatus("Entrega completada para el pedido: " + shipment.getIdShipment());
                } else {
                    showAlert(AlertType.ERROR, "Error de Estado",
                            "No se pudo completar la entrega.");
                }
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error",
                    "Error al completar entrega: " + e.getMessage());
        }
    }

    private boolean confirmCashPayment(Shipment shipment) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Pago en Efectivo");
        alert.setHeaderText("Confirmar recepción de pago en efectivo");
        alert.setContentText("Pedido: " + shipment.getIdShipment() +
                "\nMonto: $" + String.format("%,.0f", shipment.getPrice()) +
                "\n\n¿El cliente ha realizado el pago en efectivo?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            currentDeliveryMan.processCashPayment(true);
            return true;
        } else {
            currentDeliveryMan.processCashPayment(false);
            return false;
        }
    }

    private void reportIssue(Shipment shipment) {
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Reportar Problema");
            dialog.setHeaderText("Reportar problema con el pedido: " + shipment.getIdShipment());
            dialog.setContentText("Describa el problema:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(issueDescription -> {
                // Aquí podrías guardar el reporte en el sistema
                showAlert(AlertType.INFORMATION, "Problema Reportado",
                        "Problema reportado para el pedido: " + shipment.getIdShipment() +
                                "\n\nDescripción: " + issueDescription);

                updateStatus("Problema reportado para el pedido: " + shipment.getIdShipment());
            });
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error",
                    "Error al reportar problema: " + e.getMessage());
        }
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    private void showMessage(String message, String type) {
        updateStatus(message);
        if ("error".equals(type)) {
            showAlert(AlertType.ERROR, "Error", message);
        }
    }

    private void showAlert(AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}