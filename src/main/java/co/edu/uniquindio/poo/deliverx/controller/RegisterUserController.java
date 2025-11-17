package co.edu.uniquindio.poo.deliverx.controller;

import co.edu.uniquindio.poo.deliverx.model.Admin;
import co.edu.uniquindio.poo.deliverx.model.Customer;
import co.edu.uniquindio.poo.deliverx.model.DeliverX;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class RegisterUserController {
    @FXML
    public TextField userIdField;
    @FXML
    public TextField nameField;
    @FXML
    public TextField emailField;
    @FXML
    public TextField phoneField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public PasswordField confirmPasswordField;
    @FXML
    public Label messageLabel;
    public Button comeback;

    private DeliverX deliverX;

    @FXML
    public void initialize() {
        deliverX = DeliverX.getInstance();
        messageLabel.setText("");
    }

    @FXML
    public void handleRegister(ActionEvent event) {
        try {
            // Validate fields
            if (!validateFields()) {
                return;
            }
            if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                showMessage("Passwords do not match", "error");
                return;
            }
            String userId = userIdField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = passwordField.getText();

            // Determine if it's an admin or customer based on some logic
            // For example, if userId starts with "A" it's admin, otherwise customer
            boolean isAdmin = userId.toUpperCase().startsWith("A");

            if (isAdmin) {
                // Register as Admin
                Admin admin = new Admin.Builder<>()
                        .userId(userId)
                        .name(name)
                        .email(email)
                        .phoneNumber(phone)
                        .password(password)
                        .build();

                deliverX.registerAdmin(admin);
                showMessage("Admin registered successfully!", "success");
            } else {
                // Register as Customer
                Customer customer = new Customer.Builder<>()
                        .userId(userId)
                        .name(name)
                        .email(email)
                        .phoneNumber(phone)
                        .password(password)
                        .build();

                deliverX.registerCustomer(customer);
                showMessage("Customer registered successfully!", "success");
            }

            // Clear form after successful registration
            clearForm();

        } catch (Exception e) {
            showMessage("Error registering user: " + e.getMessage(), "error");
        }
    }

    @FXML
    public void handleClear(ActionEvent event) {
        clearForm();
        showMessage("Form cleared", "info");
    }

    @FXML
    public void handleBackToHome(ActionEvent event) {
        irPantalla("/co/edu/uniquindio/poo/deliverx/loggin/home.fxml", "Home");
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
        Stage stage = (Stage) comeback.getScene().getWindow();
        stage.close();
    }

    private boolean validateFields() {
        if (userIdField.getText().trim().isEmpty()) {
            showMessage("User ID is required", "error");
            userIdField.requestFocus();
            return false;
        }

        if (nameField.getText().trim().isEmpty()) {
            showMessage("Name is required", "error");
            nameField.requestFocus();
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            showMessage("Email is required", "error");
            emailField.requestFocus();
            return false;
        }

        if (phoneField.getText().trim().isEmpty()) {
            showMessage("Phone number is required", "error");
            phoneField.requestFocus();
            return false;
        }

        if (passwordField.getText().isEmpty()) {
            showMessage("Password is required", "error");
            passwordField.requestFocus();
            return false;
        }

        if (confirmPasswordField.getText().isEmpty()) {
            showMessage("Please confirm password", "error");
            confirmPasswordField.requestFocus();
            return false;
        }

        // Basic email validation
        if (!isValidEmail(emailField.getText().trim())) {
            showMessage("Please enter a valid email address", "error");
            emailField.requestFocus();
            return false;
        }

        // Basic phone validation (only numbers)
        if (!phoneField.getText().trim().matches("\\d+")) {
            showMessage("Phone number should contain only numbers", "error");
            phoneField.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private void clearForm() {
        userIdField.clear();
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        messageLabel.setText("");
    }

    private void showMessage(String message, String type) {
        messageLabel.setText(message);
        switch (type.toLowerCase()) {
            case "success":
                messageLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                break;
            case "error":
                messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                break;
            case "info":
                messageLabel.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
                break;
            default:
                messageLabel.setStyle("-fx-text-fill: black;");
        }
    }

}