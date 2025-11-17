package co.edu.uniquindio.poo.deliverx.controller;

import co.edu.uniquindio.poo.deliverx.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert.AlertType;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserOrdersController {
    @FXML public ComboBox<String> statusFilterComboBox;
    @FXML public TextField searchField;
    @FXML public TableView<Shipment> ordersTable;
    @FXML public TableColumn<Shipment, String> orderIdColumn;
    @FXML public TableColumn<Shipment, String> destinationColumn;
    @FXML public TableColumn<Shipment, String> statusColumn;
    @FXML public TableColumn<Shipment, Double> costColumn;
    @FXML public TableColumn<Shipment, String> dateColumn;
    @FXML public Label statusLabel;

    private DeliverX deliverX;
    private Customer currentCustomer;
    private ObservableList<Shipment> ordersList;
    private ObservableList<Shipment> filteredList;

    @FXML
    public void initialize() {
        deliverX = DeliverX.getInstance();
        
        // Obtener el cliente actualmente logueado
        User loggedUser = deliverX.getUserLoged();
        if (loggedUser instanceof Customer) {
            currentCustomer = (Customer) loggedUser;
            setupComponents();
            loadOrdersData();
        } else {
            showMessage("Error: Debe iniciar sesión como cliente", "error");
        }
    }

    private void setupComponents() {
        // Configurar ComboBox de filtros de estado
        statusFilterComboBox.setItems(FXCollections.observableArrayList(
            "Todos", "SOLICITADO", "ASIGNADO", "EN_RUTA", "ENTREGADO", "CANCELADO"
        ));
        statusFilterComboBox.setValue("Todos");

        // Configurar columnas de la tabla
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("idShipment"));
        
        destinationColumn.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(() -> {
                Address dest = cellData.getValue().getDestination();
                return dest != null ? dest.getStreet() + ", " + dest.getCity() : "N/A";
            })
        );
        
        statusColumn.setCellValueFactory(cellData -> 
            javafx.beans.binding.Bindings.createStringBinding(() -> {
                return cellData.getValue().getCurrentState().getStateName();
            })
        );
        
        costColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        costColumn.setCellFactory(column -> new TableCell<Shipment, Double>() {
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

        // Inicializar listas
        ordersList = FXCollections.observableArrayList();
        filteredList = FXCollections.observableArrayList();
        ordersTable.setItems(filteredList);

        // Configurar listeners
        setupListeners();
        
        updateStatus("Sistema cargado. Cargando pedidos...");
    }

    private void setupListeners() {
        // Listener para filtro de estado
        statusFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });

        // Listener para selección de tabla
        ordersTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    updateStatus("Pedido seleccionado: " + newSelection.getIdShipment());
                }
            });
    }

    private void loadOrdersData() {
        try {
            if (currentCustomer != null && currentCustomer.getShipmentList() != null) {
                ordersList.setAll(currentCustomer.getShipmentList());
                filteredList.setAll(ordersList);
                updateStatus(ordersList.size() + " pedidos encontrados");
            } else {
                updateStatus("No se encontraron pedidos");
            }
        } catch (Exception e) {
            showMessage("Error al cargar los pedidos: " + e.getMessage(), "error");
            updateStatus("Error al cargar pedidos");
        }
    }

    @FXML
    public void handleSearch(ActionEvent event) {
        applyFilters();
    }

    @FXML
    public void handleClear(ActionEvent event) {
        searchField.clear();
        statusFilterComboBox.setValue("Todos");
        filteredList.setAll(ordersList);
        updateStatus("Filtros limpiados. " + ordersList.size() + " pedidos mostrados");
    }

    @FXML
    public void handleTrackOrder(ActionEvent event) {
        Shipment selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showOrderTracking(selected);
        } else {
            showAlert(AlertType.WARNING, "Selección requerida", 
                     "Por favor seleccione un pedido para rastrear.");
        }
    }

    @FXML
    public void handleViewDetails(ActionEvent event) {
        Shipment selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showOrderDetails(selected);
        } else {
            showAlert(AlertType.WARNING, "Selección requerida", 
                     "Por favor seleccione un pedido para ver detalles.");
        }
    }

    @FXML
    public void handleCancelOrder(ActionEvent event) {
        Shipment selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cancelOrder(selected);
        } else {
            showAlert(AlertType.WARNING, "Selección requerida", 
                     "Por favor seleccione un pedido para cancelar.");
        }
    }

    private void applyFilters() {
        try {
            String searchText = searchField.getText().toLowerCase();
            String statusFilter = statusFilterComboBox.getValue();

            List<Shipment> filtered = ordersList.stream()
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
            showMessage("Error al aplicar filtros: " + e.getMessage(), "error");
        }
    }

    private void showOrderTracking(Shipment shipment) {
        try {
            StringBuilder tracking = new StringBuilder();
            tracking.append("=== RASTREO DE PEDIDO ===\n\n");
            tracking.append("ID del Pedido: ").append(shipment.getIdShipment()).append("\n");
            tracking.append("Estado Actual: ").append(shipment.getCurrentState().getStateName()).append("\n");
            tracking.append("Fecha: ").append(shipment.getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
            
            if (shipment.getOrigin() != null) {
                tracking.append("Origen: ").append(shipment.getOrigin().getStreet()).append(", ").append(shipment.getOrigin().getCity()).append("\n");
            }
            
            if (shipment.getDestination() != null) {
                tracking.append("Destino: ").append(shipment.getDestination().getStreet()).append(", ").append(shipment.getDestination().getCity()).append("\n");
            }
            
            tracking.append("Peso: ").append(shipment.getWeight()).append(" kg\n");
            tracking.append("Costo: $").append(String.format("%,.0f", shipment.getPrice())).append("\n");
            
            if (shipment.getDeliveryMan() != null) {
                tracking.append("Repartidor: ").append(shipment.getDeliveryMan().getName()).append("\n");
            }
            
            if (shipment.getAdditionalServices() != null && !shipment.getAdditionalServices().isEmpty()) {
                tracking.append("\nServicios Adicionales:\n");
                for (String service : shipment.getAdditionalServices()) {
                    tracking.append("• ").append(service).append("\n");
                }
            }

            TextArea textArea = new TextArea(tracking.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefSize(500, 400);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Rastreo de Pedido");
            alert.setHeaderText("Información de seguimiento - " + shipment.getIdShipment());
            alert.getDialogPane().setContent(textArea);
            alert.showAndWait();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error de Rastreo", 
                     "Error al mostrar información de rastreo: " + e.getMessage());
        }
    }

    private void showOrderDetails(Shipment shipment) {
        try {
            StringBuilder details = new StringBuilder();
            details.append("=== DETALLES COMPLETOS DEL PEDIDO ===\n\n");
            details.append("INFORMACIÓN BÁSICA:\n");
            details.append("• ID: ").append(shipment.getIdShipment()).append("\n");
            details.append("• Estado: ").append(shipment.getCurrentState().getStateName()).append("\n");
            details.append("• Fecha: ").append(shipment.getDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
            details.append("• Peso: ").append(shipment.getWeight()).append(" kg\n");
            details.append("• Costo: $").append(String.format("%,.0f", shipment.getPrice())).append("\n");
            details.append("• Tipo: ").append(shipment.getType()).append("\n\n");
            
            details.append("DIRECCIONES:\n");
            if (shipment.getOrigin() != null) {
                details.append("• Origen: ").append(shipment.getOrigin().getStreet())
                       .append(", ").append(shipment.getOrigin().getCity()).append("\n");
            }
            if (shipment.getDestination() != null) {
                details.append("• Destino: ").append(shipment.getDestination().getStreet())
                       .append(", ").append(shipment.getDestination().getCity()).append("\n");
            }
            
            details.append("\nINFORMACIÓN DE PAGO:\n");
            if (shipment.getPay() != null) {
                details.append("• Método: ").append(shipment.getPay().getPaymentMethod()).append("\n");
                details.append("• Estado: ").append(shipment.getPay().getResult()).append("\n");
            }

            if (shipment.getDeliveryMan() != null) {
                details.append("\nINFORMACIÓN DEL REPARTIDOR:\n");
                details.append("• Nombre: ").append(shipment.getDeliveryMan().getName()).append("\n");
                details.append("• Teléfono: ").append(shipment.getDeliveryMan().getPhoneNumber()).append("\n");
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
            alert.setTitle("Detalles del Pedido");
            alert.setHeaderText("Información completa - " + shipment.getIdShipment());
            alert.getDialogPane().setContent(textArea);
            alert.showAndWait();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error de Detalles",
                     "Error al mostrar detalles del pedido: " + e.getMessage());
        }
    }

    private void cancelOrder(Shipment shipment) {
        try {
            String currentState = shipment.getCurrentState().getStateName();

            // Verificar si el pedido puede ser cancelado
            if (!currentState.equals("SOLICITADO")) {
                showAlert(AlertType.WARNING, "Cancelación no permitida",
                         "Solo se pueden cancelar pedidos en estado SOLICITADO.\n" +
                         "Estado actual: " + currentState);
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar Cancelación");
            alert.setHeaderText("¿Está seguro de cancelar este pedido?");
            alert.setContentText("Pedido: " + shipment.getIdShipment() +
                               "\nDestino: " + (shipment.getDestination() != null ?
                                   shipment.getDestination().getStreet() : "N/A") +
                               "\nCosto: $" + String.format("%,.0f", shipment.getPrice()));

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Aquí deberías implementar la lógica para cancelar el pedido
                // Por ejemplo: shipment.changeState(new CancelledState());

                // Por ahora, simulamos la cancelación
                showMessage("Pedido " + shipment.getIdShipment() + " cancelado exitosamente", "success");
                loadOrdersData(); // Recargar datos
            }
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error de Cancelación",
                     "Error al cancelar el pedido: " + e.getMessage());
        }
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    private void showMessage(String message, String type) {
        // Mostrar mensaje temporal en la etiqueta de estado
        updateStatus(message);

        // También podrías mostrar un alert si es error
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