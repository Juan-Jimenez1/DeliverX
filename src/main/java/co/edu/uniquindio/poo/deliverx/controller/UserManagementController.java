package co.edu.uniquindio.poo.deliverx.controller;

import co.edu.uniquindio.poo.deliverx.model.DeliverX;
import co.edu.uniquindio.poo.deliverx.model.Customer;
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

public class UserManagementController {
    @FXML
    public TextField searchField;
    @FXML
    public TableColumn<Customer, String> nameColumn;
    @FXML
    public TableColumn<Customer, String> emailColumn;
    @FXML
    public TableColumn<Customer, String> phoneColumn;
    @FXML
    public Label statusLabel;
    @FXML
    public TableView<Customer> userTable;
    @FXML
    public TableColumn<Customer, String> userIdColumn;

    private DeliverX deliverX;
    private ObservableList<Customer> customerList;
    private ObservableList<Customer> filteredList;

    @FXML
    public void initialize() {
        deliverX = DeliverX.getInstance();

        // Configure table columns
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        // Load initial data
        loadCustomerData();

        // Configure listeners
        setupListeners();

        updateStatus("System loaded. " + customerList.size() + " customers found.");
    }

    private void setupListeners() {
        // Table selection listener
        userTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        updateStatus("Customer selected: " + newSelection.getName() + " (" + newSelection.getUserId() + ")");
                    }
                });
    }

    private void loadCustomerData() {
        try {
            List<Customer> customers = deliverX.getListCustomers();
            customerList = FXCollections.observableArrayList(customers);
            filteredList = FXCollections.observableArrayList(customerList);
            userTable.setItems(filteredList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Data Load Error",
                    "Error loading customers data: " + e.getMessage());
            customerList = FXCollections.observableArrayList();
            filteredList = FXCollections.observableArrayList();
            userTable.setItems(filteredList);
        }
    }

    @FXML
    public void handleSearch(ActionEvent event) {
        applyFilters();
    }

    @FXML
    public void handleRefresh(ActionEvent event) {
        loadCustomerData();
        searchField.clear();
        updateStatus("Data updated. " + customerList.size() + " customers loaded.");
    }

    @FXML
    public void handleViewDetails(ActionEvent event) {
        Customer selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showCustomerDetails(selected);
        } else {
            showAlert(Alert.AlertType.WARNING, "Selection Required",
                    "Please select a customer to view details.");
        }
    }

    @FXML
    public void handleEdit(ActionEvent event) {
        Customer selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            editCustomer(selected);
        } else {
            showAlert(Alert.AlertType.WARNING, "Selection Required",
                    "Please select a customer to edit.");
        }
    }

    @FXML
    public void handleDelete(ActionEvent event) {
        Customer selected = userTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            deleteCustomer(selected);
        } else {
            showAlert(Alert.AlertType.WARNING, "Selection Required",
                    "Please select a customer to delete.");
        }
    }

    private void applyFilters() {
        try {
            String searchText = searchField.getText().toLowerCase();

            List<Customer> filtered = customerList.stream()
                    .filter(customer ->
                            searchText.isEmpty() ||
                                    customer.getUserId().toLowerCase().contains(searchText) ||
                                    customer.getName().toLowerCase().contains(searchText) ||
                                    customer.getEmail().toLowerCase().contains(searchText) ||
                                    customer.getPhoneNumber().toLowerCase().contains(searchText)
                    )
                    .collect(Collectors.toList());

            filteredList.setAll(filtered);
            updateStatus(filtered.size() + " customers found with applied filters.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Filter Error",
                    "Error applying filters: " + e.getMessage());
        }
    }

    private void showCustomerDetails(Customer customer) {
        try {
            StringBuilder details = new StringBuilder();
            details.append("=== CUSTOMER DETAILS ===\n\n");
            details.append("Customer ID: ").append(customer.getUserId()).append("\n");
            details.append("Name: ").append(customer.getName()).append("\n");
            details.append("Email: ").append(customer.getEmail()).append("\n");
            details.append("Phone: ").append(customer.getPhoneNumber()).append("\n");
            details.append("Addresses: ").append(customer.getListAddresses().size()).append("\n");
            details.append("Shipments: ").append(customer.getShipmentList().size()).append("\n");

            // Show addresses if available
            if (!customer.getListAddresses().isEmpty()) {
                details.append("\n=== ADDRESSES ===\n");
                customer.getListAddresses().forEach(address -> {
                    details.append("- ").append(address.getStreet())
                            .append(", ").append(address.getCity())
                            .append(" (").append(address.getType()).append(")\n");
                });
            }

            // Show shipments if available
            if (!customer.getShipmentList().isEmpty()) {
                details.append("\n=== RECENT SHIPMENTS ===\n");
                customer.getShipmentList().stream()
                        .limit(5) // Show only last 5 shipments
                        .forEach(shipment -> {
                            details.append("- Shipment #").append(shipment.getIdShipment())
                                    .append(": $").append(shipment.getPrice())
                                    .append(" (").append(shipment.getCurrentState().getStateName()).append(")\n");
                        });
            }

            TextArea textArea = new TextArea(details.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefSize(500, 400);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Customer Details");
            alert.setHeaderText("Complete information of " + customer.getName());
            alert.getDialogPane().setContent(textArea);
            alert.showAndWait();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Details Error",
                    "Error showing details: " + e.getMessage());
        }
    }

    private void editCustomer(Customer customer) {
        try {
            Dialog<Customer> dialog = new Dialog<>();
            dialog.setTitle("Edit Customer");
            dialog.setHeaderText("Editing: " + customer.getName());

            // Configure buttons
            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            // Create form
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

            TextField nameField = new TextField(customer.getName());
            TextField emailField = new TextField(customer.getEmail());
            TextField phoneField = new TextField(customer.getPhoneNumber());

            grid.add(new Label("Name:"), 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(new Label("Email:"), 0, 1);
            grid.add(emailField, 1, 1);
            grid.add(new Label("Phone:"), 0, 2);
            grid.add(phoneField, 1, 2);

            dialog.getDialogPane().setContent(grid);

            // Convert result
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    try {
                        // Create updated customer
                        Customer updatedCustomer = new Customer.Builder<>()
                                .userId(customer.getUserId())
                                .name(nameField.getText())
                                .email(emailField.getText())
                                .phoneNumber(phoneField.getText())
                                .password(customer.getPassword()) // Keep current password
                                .build();
                        return updatedCustomer;
                    } catch (Exception e) {
                        showAlert(Alert.AlertType.ERROR, "Build Error",
                                "Error creating updated customer: " + e.getMessage());
                        return null;
                    }
                }
                return null;
            });

            Optional<Customer> result = dialog.showAndWait();
            result.ifPresent(updatedCustomer -> {
                try {
                    boolean success = deliverX.updateCustomer(customer.getUserId(), updatedCustomer);
                    if (success) {
                        loadCustomerData();
                        updateStatus("Customer updated successfully: " + updatedCustomer.getName());
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Update Error",
                                "Could not update the customer in the system.");
                    }
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Update Error",
                            "Error updating customer: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Edit Error",
                    "Error opening edit dialog: " + e.getMessage());
        }
    }

    private void deleteCustomer(Customer customer) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete this customer?");
            alert.setContentText("This action cannot be undone.\nCustomer: " +
                    customer.getName() + " (" + customer.getUserId() + ")\n\n" +
                    "This will also delete:\n" +
                    "- " + customer.getListAddresses().size() + " addresses\n" +
                    "- " + customer.getShipmentList().size() + " shipments");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                deliverX.deleteCustomer(customer.getUserId());
                loadCustomerData();
                updateStatus("Customer deleted: " + customer.getName());
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Deletion Error",
                    "Error deleting customer: " + e.getMessage());
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