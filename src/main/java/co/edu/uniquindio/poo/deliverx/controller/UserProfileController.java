package co.edu.uniquindio.poo.deliverx.controller;

import co.edu.uniquindio.poo.deliverx.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

public class UserProfileController {
    @FXML public TextField userIdField;
    @FXML public TextField nameField;
    @FXML public TextField emailField;
    @FXML public TextField phoneField;
    @FXML public PasswordField currentPasswordField;
    @FXML public PasswordField newPasswordField;
    @FXML public Label messageLabel;

    private DeliverX deliverX;
    private User currentUser;
    private Customer currentCustomer;
    private Admin currentAdmin;

    @FXML
    public void initialize() {
        deliverX = DeliverX.getInstance();

        // Obtener el usuario actualmente logueado
        currentUser = deliverX.getUserLoged();
        if (currentUser != null) {
            loadUserData();
        } else {
            showMessage("Error: No hay usuario logueado", "error");
        }
    }

    private void loadUserData() {
        try {
            // Cargar datos básicos del usuario
            userIdField.setText(currentUser.getUserId());
            nameField.setText(currentUser.getName());
            emailField.setText(currentUser.getEmail());
            phoneField.setText(currentUser.getPhoneNumber());

            // Deshabilitar campo de ID (no editable)
            userIdField.setDisable(true);

            // Determinar el tipo de usuario para operaciones específicas
            if (currentUser instanceof Customer) {
                currentCustomer = (Customer) currentUser;
            } else if (currentUser instanceof Admin) {
                currentAdmin = (Admin) currentUser;
            }

            showMessage("Perfil cargado correctamente", "info");

        } catch (Exception e) {
            showMessage("Error al cargar los datos del perfil: " + e.getMessage(), "error");
        }
    }

    @FXML
    public void handleSaveProfile(ActionEvent event) {
        try {
            if (!validateForm()) {
                return;
            }

            // Verificar contraseña actual si se intenta cambiar la contraseña
            if (!newPasswordField.getText().isEmpty()) {
                if (!validateCurrentPassword()) {
                    showMessage("La contraseña actual es incorrecta", "error");
                    currentPasswordField.requestFocus();
                    return;
                }
            }

            // Crear usuario actualizado según el tipo
            User updatedUser = createUpdatedUser();

            // Actualizar en el sistema según el tipo de usuario
            boolean success = updateUserInSystem(updatedUser);

            if (success) {
                showMessage("Perfil actualizado exitosamente", "success");
                clearPasswordFields();
                // Actualizar usuario actual en memoria
                currentUser = updatedUser;
            } else {
                showMessage("Error al actualizar el perfil", "error");
            }

        } catch (Exception e) {
            showMessage("Error al guardar el perfil: " + e.getMessage(), "error");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        try {
            // Recargar datos originales
            loadUserData();
            clearPasswordFields();
            showMessage("Cambios descartados", "info");
        } catch (Exception e) {
            showMessage("Error al cancelar: " + e.getMessage(), "error");
        }
    }

    private boolean validateForm() {
        // Validar nombre
        if (nameField.getText().trim().isEmpty()) {
            showMessage("El nombre es requerido", "error");
            nameField.requestFocus();
            return false;
        }

        // Validar email
        if (emailField.getText().trim().isEmpty()) {
            showMessage("El email es requerido", "error");
            emailField.requestFocus();
            return false;
        }

        if (!isValidEmail(emailField.getText().trim())) {
            showMessage("Por favor ingrese un email válido", "error");
            emailField.requestFocus();
            return false;
        }

        // Validar teléfono
        if (phoneField.getText().trim().isEmpty()) {
            showMessage("El teléfono es requerido", "error");
            phoneField.requestFocus();
            return false;
        }

        if (!phoneField.getText().trim().matches("\\d+")) {
            showMessage("El teléfono debe contener solo números", "error");
            phoneField.requestFocus();
            return false;
        }

        // Validar contraseñas si se intenta cambiar
        if (!newPasswordField.getText().isEmpty()) {
            if (currentPasswordField.getText().isEmpty()) {
                showMessage("Debe ingresar la contraseña actual para cambiarla", "error");
                currentPasswordField.requestFocus();
                return false;
            }

            if (newPasswordField.getText().length() < 4) {
                showMessage("La nueva contraseña debe tener al menos 4 caracteres", "error");
                newPasswordField.requestFocus();
                return false;
            }
        }

        return true;
    }

    private boolean validateCurrentPassword() {
        // Verificar que la contraseña actual coincida
        String currentPassword = currentPasswordField.getText();
        return currentPassword.equals(currentUser.getPassword());
    }

    private User createUpdatedUser() {
        String newPassword = newPasswordField.getText().isEmpty() ?
                           currentUser.getPassword() : newPasswordField.getText();

        if (currentUser instanceof Customer) {
            return new Customer.Builder<>()
                .userId(currentUser.getUserId())
                .name(nameField.getText().trim())
                .email(emailField.getText().trim())
                .phoneNumber(phoneField.getText().trim())
                .password(newPassword)
                .build();
        } else if (currentUser instanceof Admin) {
            return new Admin.Builder<>()
                .userId(currentUser.getUserId())
                .name(nameField.getText().trim())
                .email(emailField.getText().trim())
                .phoneNumber(phoneField.getText().trim())
                .password(newPassword)
                .build();
        } else {
            // Para otros tipos de usuario (como DeliveryMan)
            return currentUser; // No se puede actualizar, mantener original
        }
    }

    private boolean updateUserInSystem(User updatedUser) {
        try {
            if (updatedUser instanceof Customer) {
                return deliverX.updateCustomer(currentUser.getUserId(), (Customer) updatedUser);
            } else if (updatedUser instanceof Admin) {
                return deliverX.updateAdmin(currentUser.getUserId(), (Admin) updatedUser);
            } else if (updatedUser instanceof DeliveryMan) {
                return deliverX.updateDeliveryMan(currentUser.getUserId(), (DeliveryMan) updatedUser);
            }
            return false;
        } catch (Exception e) {
            showMessage("Error en el sistema al actualizar: " + e.getMessage(), "error");
            return false;
        }
    }

    private boolean isValidEmail(String email) {
        // Validación básica de email
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    private void clearPasswordFields() {
        currentPasswordField.clear();
        newPasswordField.clear();
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