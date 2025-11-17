package co.edu.uniquindio.poo.deliverx.controller;

import co.edu.uniquindio.poo.deliverx.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

public class DeliveryProfile {

    // Cambiar a private - IMPORTANTE
    @FXML private TextField deliveryIdField;
    @FXML private TextField nameField;
    @FXML private TextField documentField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField coverageZoneField;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private Label totalDeliveriesLabel;
    @FXML private Label completedDeliveriesLabel;
    @FXML private Label successRateLabel;
    @FXML private Label messageLabel;
    @FXML private Label currentStatusLabel;

    private DeliverX deliverX;
    private DeliveryMan currentDeliveryMan;

    @FXML
    public void initialize() {
        try {
            deliverX = DeliverX.getInstance();

            // Verificar que deliverX no sea null
            if (deliverX == null) {
                showMessage("Error: Sistema no inicializado", "error");
                return;
            }

            // Obtener el repartidor actualmente logueado
            User loggedUser = deliverX.getUserLoged();

            if (loggedUser == null) {
                showMessage("Error: No hay usuario logueado", "error");
                return;
            }

            if (loggedUser instanceof DeliveryMan) {
                currentDeliveryMan = (DeliveryMan) loggedUser;
                loadDeliveryManData();
                loadStatistics();
            } else {
                showMessage("Error: Debe iniciar sesión como repartidor", "error");
            }

        } catch (Exception e) {
            System.err.println("Error en initialize: " + e.getMessage());
            e.printStackTrace();
            showMessage("Error al inicializar: " + e.getMessage(), "error");
        }
    }

    private void loadDeliveryManData() {
        try {
            if (currentDeliveryMan == null) {
                showMessage("Error: Repartidor no encontrado", "error");
                return;
            }

            // Cargar datos básicos del repartidor con validaciones
            deliveryIdField.setText(currentDeliveryMan.getUserId() != null ?
                    currentDeliveryMan.getUserId() : "");

            nameField.setText(currentDeliveryMan.getName() != null ?
                    currentDeliveryMan.getName() : "");

            emailField.setText(currentDeliveryMan.getEmail() != null ?
                    currentDeliveryMan.getEmail() : "");

            phoneField.setText(currentDeliveryMan.getPhoneNumber() != null ?
                    currentDeliveryMan.getPhoneNumber() : "");

            coverageZoneField.setText(currentDeliveryMan.getZonaCobertura() != null ?
                    currentDeliveryMan.getZonaCobertura() : "");

            // Campo de documento (simulado ya que no está en tu clase)
            documentField.setText("DOC-" +
                    (currentDeliveryMan.getUserId() != null ? currentDeliveryMan.getUserId() : "N/A"));

            // Deshabilitar campo de ID (no editable)
            deliveryIdField.setDisable(true);

            // Mostrar estado actual
            if (currentDeliveryMan.getState() != null) {
                currentStatusLabel.setText("Estado: " + currentDeliveryMan.getState().getStateName());
            } else {
                currentStatusLabel.setText("Estado: NO DEFINIDO");
            }

            showMessage("Perfil cargado correctamente", "info");

        } catch (Exception e) {
            System.err.println("Error en loadDeliveryManData: " + e.getMessage());
            e.printStackTrace();
            showMessage("Error al cargar los datos del perfil: " + e.getMessage(), "error");
        }
    }

    private void loadStatistics() {
        try {
            if (deliverX == null || currentDeliveryMan == null) {
                totalDeliveriesLabel.setText("0");
                completedDeliveriesLabel.setText("0");
                successRateLabel.setText("0%");
                return;
            }

            // Calcular estadísticas basadas en los envíos del sistema
            int totalDeliveries = calculateTotalDeliveries();
            int completedDeliveries = calculateCompletedDeliveries();
            double successRate = totalDeliveries > 0 ?
                    (completedDeliveries * 100.0) / totalDeliveries : 0;

            totalDeliveriesLabel.setText(String.valueOf(totalDeliveries));
            completedDeliveriesLabel.setText(String.valueOf(completedDeliveries));
            successRateLabel.setText(String.format("%.1f%%", successRate));

        } catch (Exception e) {
            System.err.println("Error en loadStatistics: " + e.getMessage());
            e.printStackTrace();
            showMessage("Error al cargar estadísticas: " + e.getMessage(), "error");
            totalDeliveriesLabel.setText("0");
            completedDeliveriesLabel.setText("0");
            successRateLabel.setText("0%");
        }
    }

    private int calculateTotalDeliveries() {
        try {
            if (deliverX.getListShipments() == null) {
                return 0;
            }

            return (int) deliverX.getListShipments().stream()
                    .filter(shipment -> shipment != null &&
                            shipment.getDeliveryMan() != null &&
                            shipment.getDeliveryMan().getUserId().equals(currentDeliveryMan.getUserId()))
                    .count();
        } catch (Exception e) {
            System.err.println("Error calculando total de entregas: " + e.getMessage());
            return 0;
        }
    }

    private int calculateCompletedDeliveries() {
        try {
            if (deliverX.getListShipments() == null) {
                return 0;
            }

            return (int) deliverX.getListShipments().stream()
                    .filter(shipment -> shipment != null &&
                            shipment.getDeliveryMan() != null &&
                            shipment.getDeliveryMan().getUserId().equals(currentDeliveryMan.getUserId()) &&
                            shipment.getCurrentState() != null &&
                            shipment.getCurrentState().getStateName().equals("DELIVERED"))
                    .count();
        } catch (Exception e) {
            System.err.println("Error calculando entregas completadas: " + e.getMessage());
            return 0;
        }
    }

    @FXML
    public void handleSaveProfile(ActionEvent event) {
        try {
            if (!validateForm()) {
                return;
            }

            if (!newPasswordField.getText().isEmpty()) {
                if (!validateCurrentPassword()) {
                    showMessage("La contraseña actual es incorrecta", "error");
                    currentPasswordField.requestFocus();
                    return;
                }
            }

            DeliveryMan updatedDeliveryMan = createUpdatedDeliveryMan();

            // Actualizar en el sistema
            boolean success = deliverX.updateDeliveryMan(
                    currentDeliveryMan.getUserId(),
                    updatedDeliveryMan
            );

            if (success) {
                showMessage("Perfil actualizado exitosamente", "success");
                clearPasswordFields();
                // Actualizar repartidor actual en memoria
                currentDeliveryMan = updatedDeliveryMan;
                // Recargar datos para mostrar cambios
                loadDeliveryManData();

                // Mostrar alerta adicional
                showAlert(AlertType.INFORMATION, "Éxito",
                        "El perfil se ha actualizado correctamente");
            } else {
                showMessage("Error al actualizar el perfil", "error");
                showAlert(AlertType.ERROR, "Error",
                        "No se pudo actualizar el perfil");
            }

        } catch (Exception e) {
            System.err.println("Error en handleSaveProfile: " + e.getMessage());
            e.printStackTrace();
            showMessage("Error al guardar el perfil: " + e.getMessage(), "error");
            showAlert(AlertType.ERROR, "Error",
                    "Error al guardar: " + e.getMessage());
        }
    }

    @FXML
    public void handleCancel(ActionEvent event) {
        try {
            // Recargar datos originales
            loadDeliveryManData();
            clearPasswordFields();
            showMessage("Cambios descartados", "info");
        } catch (Exception e) {
            System.err.println("Error en handleCancel: " + e.getMessage());
            e.printStackTrace();
            showMessage("Error al cancelar: " + e.getMessage(), "error");
        }
    }

    private boolean validateForm() {
        // Validar nombre
        if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
            showMessage("El nombre es requerido", "error");
            showAlert(AlertType.WARNING, "Validación", "El nombre es requerido");
            nameField.requestFocus();
            return false;
        }

        // Validar email
        if (emailField.getText() == null || emailField.getText().trim().isEmpty()) {
            showMessage("El email es requerido", "error");
            showAlert(AlertType.WARNING, "Validación", "El email es requerido");
            emailField.requestFocus();
            return false;
        }

        if (!isValidEmail(emailField.getText().trim())) {
            showMessage("Por favor ingrese un email válido", "error");
            showAlert(AlertType.WARNING, "Validación", "Por favor ingrese un email válido");
            emailField.requestFocus();
            return false;
        }

        // Validar teléfono
        if (phoneField.getText() == null || phoneField.getText().trim().isEmpty()) {
            showMessage("El teléfono es requerido", "error");
            showAlert(AlertType.WARNING, "Validación", "El teléfono es requerido");
            phoneField.requestFocus();
            return false;
        }

        if (!phoneField.getText().trim().matches("\\d+")) {
            showMessage("El teléfono debe contener solo números", "error");
            showAlert(AlertType.WARNING, "Validación",
                    "El teléfono debe contener solo números");
            phoneField.requestFocus();
            return false;
        }

        // Validar zona de cobertura
        if (coverageZoneField.getText() == null || coverageZoneField.getText().trim().isEmpty()) {
            showMessage("La zona de cobertura es requerida", "error");
            showAlert(AlertType.WARNING, "Validación",
                    "La zona de cobertura es requerida");
            coverageZoneField.requestFocus();
            return false;
        }

        // Validar contraseñas si se intenta cambiar
        if (newPasswordField.getText() != null && !newPasswordField.getText().isEmpty()) {
            if (currentPasswordField.getText() == null || currentPasswordField.getText().isEmpty()) {
                showMessage("Debe ingresar la contraseña actual para cambiarla", "error");
                showAlert(AlertType.WARNING, "Validación",
                        "Debe ingresar la contraseña actual para cambiarla");
                currentPasswordField.requestFocus();
                return false;
            }

            if (newPasswordField.getText().length() < 4) {
                showMessage("La nueva contraseña debe tener al menos 4 caracteres", "error");
                showAlert(AlertType.WARNING, "Validación",
                        "La nueva contraseña debe tener al menos 4 caracteres");
                newPasswordField.requestFocus();
                return false;
            }
        }

        return true;
    }

    private boolean validateCurrentPassword() {
        try {
            String currentPassword = currentPasswordField.getText();
            return currentPassword != null &&
                    currentPassword.equals(currentDeliveryMan.getPassword());
        } catch (Exception e) {
            System.err.println("Error validando contraseña: " + e.getMessage());
            return false;
        }
    }

    private DeliveryMan createUpdatedDeliveryMan() {
        String newPassword = newPasswordField.getText().isEmpty() ?
                currentDeliveryMan.getPassword() : newPasswordField.getText();

        return new DeliveryMan.Builder<>()
                .userId(currentDeliveryMan.getUserId())
                .name(nameField.getText().trim())
                .email(emailField.getText().trim())
                .phoneNumber(phoneField.getText().trim())
                .password(newPassword)
                .state(currentDeliveryMan.getState())
                .zonaCobertura(coverageZoneField.getText().trim())
                .build();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    private void clearPasswordFields() {
        currentPasswordField.clear();
        newPasswordField.clear();
    }

    private void showMessage(String message, String type) {
        if (messageLabel == null) {
            System.err.println("messageLabel es null!");
            return;
        }

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
        try {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Error mostrando alerta: " + e.getMessage());
            e.printStackTrace();
        }
    }
}