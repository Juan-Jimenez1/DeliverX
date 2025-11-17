package co.edu.uniquindio.poo.deliverx.controller;

import co.edu.uniquindio.poo.deliverx.model.*;
import co.edu.uniquindio.poo.deliverx.model.Strategy.RateStrategy;
import co.edu.uniquindio.poo.deliverx.model.factory.ShipmentFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

public class UserNewOrderController {

    @FXML
    public TextField weightField;
    @FXML
    public TextField widthField;
    @FXML
    public TextField heightField;
    @FXML
    public TextField depthField;
    @FXML
    public Label messageLabel;
    @FXML
    public Label estimatedCostLabel;
    @FXML
    public ComboBox<String> paymentMethodComboBox;
    @FXML
    public CheckBox prioritarioCheckBox;
    @FXML
    public CheckBox firmaCheckBox;
    @FXML
    public CheckBox expressCheckBox;
    @FXML
    public CheckBox seguroCheckBox;
    @FXML
    public TextField destinationPostalField;
    @FXML
    public TextField destinationCityField;
    @FXML
    public TextField destinationZoneField;
    @FXML
    public TextField destinationStreetField;
    @FXML
    public ComboBox<String> originAddressComboBox;
    @FXML
    public TextArea descriptionArea;

    private DeliverX deliverX;
    private Customer currentCustomer;
    private List<Address> customerAddresses;

    @FXML
    public void initialize() {
        deliverX = DeliverX.getInstance();

        // Obtener el usuario logueado
        User userLogged = deliverX.getUserLoged();
        if (userLogged instanceof Customer) {
            currentCustomer = (Customer) userLogged;
        }

        // Inicializar ComboBox de métodos de pago
        paymentMethodComboBox.getItems().addAll("CASH", "CREDIT_CARD", "DEBIT_CARD");
        paymentMethodComboBox.setValue("CASH");

        // Cargar direcciones del cliente
        loadCustomerAddresses();

        // Limpiar mensajes
        messageLabel.setText("");
        estimatedCostLabel.setText("Costo estimado: $0.00");
    }

    private void loadCustomerAddresses() {
        // Aquí deberías cargar las direcciones guardadas del cliente
        // Por ahora, agregamos una opción para crear nueva dirección
        originAddressComboBox.getItems().add("-- Nueva Dirección --");

        // Si el cliente tiene direcciones guardadas, agrégalas aquí
        // Por ejemplo:
        // for (Address addr : currentCustomer.getAddresses()) {
        //     originAddressComboBox.getItems().add(addr.toString());
        // }

        originAddressComboBox.setValue("-- Nueva Dirección --");
    }

    @FXML
    public void handleCalculateCost(ActionEvent event) {
        try {
            // Validar campos básicos
            if (!validateBasicFields()) {
                return;
            }

            double weight = Double.parseDouble(weightField.getText().trim());

            // Crear direcciones temporales para calcular
            Address tempOrigin = new Address("temp_origin", "temp_street", "temp_city",
                    "temp_type", 0, 0);
            Address tempDestination = createDestinationAddress("temp_dest");

            // Determinar tipo de envío
            String shipmentType = expressCheckBox.isSelected() ? "express" : "normal";

            // Crear estrategia de tarifa (puedes usar la estrategia real de tu sistema)
            RateStrategy rateStrategy = new RateStrategy() {
                @Override
                public double calculate(Address origin, Address destination, double weight) {
                    // Tarifa base según peso
                    double baseRate = weight * 5000; // 5000 por kg

                    // Multiplicador por tipo
                    if (shipmentType.equals("express")) {
                        baseRate *= 2; // Express cuesta el doble
                    }

                    return baseRate;
                }

                @Override
                public String getStrategyName() {
                    return shipmentType.equals("express") ? "Express Rate" : "Normal Rate";
                }
            };

            // Calcular tarifa base
            double baseCost = rateStrategy.calculate(tempOrigin, tempDestination, weight);

            // Agregar servicios adicionales
            double additionalCost = 0;
            if (prioritarioCheckBox.isSelected()) additionalCost += 5000;
            if (firmaCheckBox.isSelected()) additionalCost += 3000;
            if (seguroCheckBox.isSelected()) additionalCost += 8000;

            double totalCost = baseCost + additionalCost;

            estimatedCostLabel.setText(String.format("Costo estimado: $%.2f", totalCost));
            estimatedCostLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 14px;");

        } catch (NumberFormatException e) {
            showError("Error en el formato de los números");
        } catch (Exception e) {
            showError("Error al calcular el costo: " + e.getMessage());
        }
    }

    @FXML
    public void handleCreateOrder(ActionEvent event) {
        try {
            // Validar todos los campos
            if (!validateAllFields()) {
                return;
            }

            // Obtener datos del formulario
            String shipmentId = "SHIP_" + System.currentTimeMillis();
            double weight = Double.parseDouble(weightField.getText().trim());

            // Crear dirección de origen (temporal o desde el ComboBox)
            Address origin = new Address("origin_" + shipmentId, "Calle Origen",
                    "Ciudad Origen", "house", 0, 0);

            // Crear dirección de destino
            Address destination = createDestinationAddress("dest_" + shipmentId);

            // Determinar tipo de envío
            String shipmentType = expressCheckBox.isSelected() ? "express" : "normal";

            // Obtener método de pago
            String paymentMethodStr = paymentMethodComboBox.getValue();
            PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentMethodStr);

            // Recopilar servicios adicionales
            List<String> services = new ArrayList<>();
            if (prioritarioCheckBox.isSelected()) services.add("FRAGILE");
            if (firmaCheckBox.isSelected()) services.add("SIGNATURE");
            if (seguroCheckBox.isSelected()) services.add("INSURANCE");

            // Crear estrategia de tarifa
            RateStrategy rateStrategy = new RateStrategy() {
                @Override
                public double calculate(Address origin, Address destination, double weight) {
                    double baseRate = weight * 5000;
                    if (shipmentType.equals("express")) {
                        baseRate *= 2;
                    }
                    return baseRate;
                }

                @Override
                public String getStrategyName() {
                    return shipmentType.equals("express") ? "Express Rate" : "Normal Rate";
                }
            };

            // Crear el envío usando la fábrica
            Shipment newShipment = ShipmentFactory.createShipment(
                    shipmentType,
                    shipmentId,
                    currentCustomer,
                    origin,
                    destination,
                    weight,
                    rateStrategy,
                    services,
                    paymentMethod
            );

            // Agregar el envío al sistema
            deliverX.addShipment(newShipment);

            // Mostrar mensaje de éxito
            showSuccess("¡Orden creada exitosamente! ID: " + shipmentId);

            // Limpiar formulario
            handleClear(null);

        } catch (NumberFormatException e) {
            showError("Error en el formato de los números");
        } catch (Exception e) {
            showError("Error al crear la orden: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Address createDestinationAddress(String id) {
        String street = destinationStreetField.getText().trim();
        String city = destinationCityField.getText().trim();
        String zone = destinationZoneField.getText().trim();
        String postal = destinationPostalField.getText().trim();

        // Puedes agregar coordenadas si las tienes
        double latitude = 0;
        double longitude = 0;

        return new Address(id, street, city, zone, latitude, longitude);
    }

    private boolean validateBasicFields() {
        if (weightField.getText().trim().isEmpty()) {
            showError("Por favor ingrese el peso del paquete");
            return false;
        }

        try {
            double weight = Double.parseDouble(weightField.getText().trim());
            if (weight <= 0) {
                showError("El peso debe ser mayor a 0");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("El peso debe ser un número válido");
            return false;
        }

        return true;
    }

    private boolean validateAllFields() {
        if (!validateBasicFields()) {
            return false;
        }

        if (destinationStreetField.getText().trim().isEmpty()) {
            showError("Por favor ingrese la calle de destino");
            return false;
        }

        if (destinationCityField.getText().trim().isEmpty()) {
            showError("Por favor ingrese la ciudad de destino");
            return false;
        }

        if (destinationZoneField.getText().trim().isEmpty()) {
            showError("Por favor ingrese la zona de destino");
            return false;
        }

        if (paymentMethodComboBox.getValue() == null) {
            showError("Por favor seleccione un método de pago");
            return false;
        }

        return true;
    }

    @FXML
    public void handleClear(ActionEvent event) {
        // Limpiar campos de texto
        weightField.clear();
        widthField.clear();
        heightField.clear();
        depthField.clear();
        destinationStreetField.clear();
        destinationCityField.clear();
        destinationZoneField.clear();
        destinationPostalField.clear();
        descriptionArea.clear();

        // Limpiar checkboxes
        prioritarioCheckBox.setSelected(false);
        firmaCheckBox.setSelected(false);
        expressCheckBox.setSelected(false);
        seguroCheckBox.setSelected(false);

        // Resetear ComboBoxes
        paymentMethodComboBox.setValue("CASH");
        originAddressComboBox.setValue("-- Nueva Dirección --");

        // Limpiar labels
        messageLabel.setText("");
        estimatedCostLabel.setText("Costo estimado: $0.00");
        estimatedCostLabel.setStyle("");
    }

    @FXML
    public void handleNewOriginAddress(ActionEvent event) {
        // Aquí podrías abrir un diálogo para agregar una nueva dirección de origen
        // Por ahora solo mostramos un mensaje
        showInfo("Función para agregar nueva dirección de origen");
    }

    private void showError(String message) {
        messageLabel.setText("❌ " + message);
        messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
    }

    private void showSuccess(String message) {
        messageLabel.setText("✓ " + message);
        messageLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
    }

    private void showInfo(String message) {
        messageLabel.setText("ℹ " + message);
        messageLabel.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
    }
}