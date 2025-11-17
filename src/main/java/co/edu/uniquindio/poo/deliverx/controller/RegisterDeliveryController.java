package co.edu.uniquindio.poo.deliverx.controller;

import co.edu.uniquindio.poo.deliverx.model.DeliverX;
import co.edu.uniquindio.poo.deliverx.model.DeliveryMan;
import co.edu.uniquindio.poo.deliverx.model.state.ActiveState;
import co.edu.uniquindio.poo.deliverx.model.state.InactiveState;
import co.edu.uniquindio.poo.deliverx.model.state.InRouteDeliveryState;
import co.edu.uniquindio.poo.deliverx.model.state.DeliveryManState;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class RegisterDeliveryController {
    @FXML
    public Label messageLabel;
    @FXML
    public TextField zonaCoberturaField;
    @FXML
    public ComboBox<String> stateComboBox;
    @FXML
    public PasswordField passwordField;
    @FXML
    public TextField phoneField;
    @FXML
    public TextField emailField;
    @FXML
    public TextField deliveryIdField;
    @FXML
    public TextField nameField;

    private DeliverX deliverX;

    @FXML
    public void initialize() {
        deliverX = DeliverX.getInstance();
        messageLabel.setText("");

        // Initialize state ComboBox
        stateComboBox.setItems(FXCollections.observableArrayList(
                "ACTIVE", "INACTIVE"
        ));
        stateComboBox.setValue("ACTIVE"); // Default value
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        try {
            // Validate fields
            if (!validateFields()) {
                return;
            }

            // Get form data
            String userId = deliveryIdField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String zonaCobertura = zonaCoberturaField.getText().trim();
            String password = passwordField.getText();
            String selectedState = stateComboBox.getValue();

            // Create appropriate state object
            DeliveryManState state;
            switch (selectedState) {
                case "ACTIVE":
                    state = new ActiveState();
                    break;
                case "INACTIVE":
                    state = new InactiveState();
                    break;
                default:
                    state = new ActiveState(); // Default fallback
            }

            // Check if user already exists
            if (deliverX.userExists(userId)) {
                showMessage("User ID already exists. Please choose a different one.", "error");
                deliveryIdField.requestFocus();
                return;
            }

            // Create and register delivery man using Builder pattern
            DeliveryMan deliveryMan = new DeliveryMan.Builder<>()
                    .userId(userId)
                    .name(name)
                    .email(email)
                    .phoneNumber(phone)
                    .password(password)
                    .state(state)
                    .zonaCobertura(zonaCobertura)
                    .build();

            deliverX.registerDeliveryMan(deliveryMan);
            showMessage("Delivery man registered successfully!", "success");

            // Clear form after successful registration
            clearForm();

        } catch (Exception e) {
            showMessage("Error registering delivery man: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleClear(ActionEvent event) {
        clearForm();
        showMessage("Form cleared", "info");
    }

    @FXML
    public void handleBackToHome(ActionEvent event) {
        try {
            // Navigate back to admin home - delivery management
            AdminController adminController = new AdminController();
            adminController.loadDeliveryManagement(event);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Navigation Error",
                    "Error returning to home: " + e.getMessage());
        }
    }

    private boolean validateFields() {
        // Validate Delivery ID
        if (deliveryIdField.getText().trim().isEmpty()) {
            showMessage("Delivery ID is required", "error");
            deliveryIdField.requestFocus();
            return false;
        }

        // Validate Name
        if (nameField.getText().trim().isEmpty()) {
            showMessage("Name is required", "error");
            nameField.requestFocus();
            return false;
        }

        // Validate Email
        if (emailField.getText().trim().isEmpty()) {
            showMessage("Email is required", "error");
            emailField.requestFocus();
            return false;
        }

        if (!isValidEmail(emailField.getText().trim())) {
            showMessage("Please enter a valid email address", "error");
            emailField.requestFocus();
            return false;
        }

        // Validate Phone
        if (phoneField.getText().trim().isEmpty()) {
            showMessage("Phone number is required", "error");
            phoneField.requestFocus();
            return false;
        }

        if (!phoneField.getText().trim().matches("\\d+")) {
            showMessage("Phone number should contain only numbers", "error");
            phoneField.requestFocus();
            return false;
        }

        // Validate Coverage Zone
        if (zonaCoberturaField.getText().trim().isEmpty()) {
            showMessage("Coverage zone is required", "error");
            zonaCoberturaField.requestFocus();
            return false;
        }

        // Validate Password
        if (passwordField.getText().isEmpty()) {
            showMessage("Password is required", "error");
            passwordField.requestFocus();
            return false;
        }

        if (passwordField.getText().length() < 4) {
            showMessage("Password must be at least 4 characters long", "error");
            passwordField.requestFocus();
            return false;
        }

        // Validate State
        if (stateComboBox.getValue() == null || stateComboBox.getValue().isEmpty()) {
            showMessage("Please select a state", "error");
            stateComboBox.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        // Basic email validation
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private void clearForm() {
        deliveryIdField.clear();
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        zonaCoberturaField.clear();
        passwordField.clear();
        stateComboBox.setValue("ACTIVE");
        messageLabel.setText("");
    }

    private void showMessage(String message, String type) {
        messageLabel.setText(message);
        switch (type.toLowerCase()) {
            case "success":
                messageLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                break;
            case "error":
                messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                break;
            case "info":
                messageLabel.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
                break;
            default:
                messageLabel.setStyle("-fx-text-fill: #2c3e50;");
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