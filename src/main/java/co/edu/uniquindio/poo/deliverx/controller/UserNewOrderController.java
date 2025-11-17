package co.edu.uniquindio.poo.deliverx.controller;

import co.edu.uniquindio.poo.deliverx.model.*;
import co.edu.uniquindio.poo.deliverx.model.Strategy.RateStrategy;
import co.edu.uniquindio.poo.deliverx.model.factory.ShipmentFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

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
    public TextArea descriptionArea;
    @FXML
    public ComboBox<Address> destinationComboBox; // Direcciones guardadas del usuario
    @FXML
    public ComboBox<Address> originComboBox; // Solo Armenia

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
            System.out.println("Cliente actual: " + currentCustomer.getName());
        } else {
            showError("No hay un cliente logueado");
            return;
        }

        // Inicializar ComboBox de métodos de pago
        paymentMethodComboBox.getItems().addAll("CASH", "CREDIT_CARD", "DEBIT_CARD");
        paymentMethodComboBox.setValue("CASH");

        // Cargar direcciones del cliente para DESTINO
        loadCustomerAddresses();

        // Configurar ORIGEN (solo Armenia)
        setupOriginAddress();

        // Limpiar mensajes
        messageLabel.setText("");
        estimatedCostLabel.setText("Costo estimado: $0.00");
    }

    private void loadCustomerAddresses() {
        destinationComboBox.getItems().clear();

        // Crear direcciones predeterminadas del usuario (DESTINO)
        customerAddresses = new ArrayList<>();

        // Direcciones de ejemplo del usuario
        Address armeniaAddress = new Address("1", "cra9", "Armenia", "house", 12, 11);
        customerAddresses.add(armeniaAddress);

        Address pereiraAddress = new Address("3", "cra15", "Pereira", "apartment", 20, 30);
        customerAddresses.add(pereiraAddress);

        Address manizalesAddress = new Address("5", "cra25", "Manizales", "store", 35, 45);
        customerAddresses.add(manizalesAddress);

        // Agregar direcciones al ComboBox de DESTINO
        destinationComboBox.getItems().addAll(customerAddresses);

        // Configurar cómo mostrar las direcciones
        setupAddressComboBox(destinationComboBox);

        // Seleccionar la primera dirección por defecto
        if (!customerAddresses.isEmpty()) {
            destinationComboBox.setValue(customerAddresses.get(0));
        }
    }

    private void setupOriginAddress() {
        originComboBox.getItems().clear();

        // Solo agregar Armenia como dirección de ORIGEN
        Address armeniaOrigin = new Address("origin_1", "cra9", "Armenia", "warehouse", 12, 11);
        originComboBox.getItems().add(armeniaOrigin);

        // Configurar cómo mostrar la dirección
        setupAddressComboBox(originComboBox);

        // Seleccionar Armenia por defecto
        originComboBox.setValue(armeniaOrigin);
        originComboBox.setDisable(true); // Hacerlo de solo lectura
    }

    private void setupAddressComboBox(ComboBox<Address> comboBox) {
        comboBox.setCellFactory(lv -> new ListCell<Address>() {
            @Override
            protected void updateItem(Address address, boolean empty) {
                super.updateItem(address, empty);
                if (empty || address == null) {
                    setText(null);
                } else {
                    setText(formatAddress(address));
                }
            }
        });

        comboBox.setButtonCell(new ListCell<Address>() {
            @Override
            protected void updateItem(Address address, boolean empty) {
                super.updateItem(address, empty);
                if (empty || address == null) {
                    setText("Select address");
                } else {
                    setText(formatAddress(address));
                }
            }
        });
    }

    private String formatAddress(Address address) {
        if (address == null) return "";
        return String.format("%s, %s, %s",
                address.getStreet(),
                address.getCity(),
                address.getType());
    }

    @FXML
    public void handleAddNewAddress(ActionEvent event) {
        try {
            // Crear un diálogo para agregar nueva dirección de DESTINO
            Dialog<Address> dialog = new Dialog<>();
            dialog.setTitle("Add New Destination Address");
            dialog.setHeaderText("Enter the new destination address details");

            // Configurar botones
            ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

            // Crear formulario para la dirección
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

            TextField streetField = new TextField();
            streetField.setPromptText("Street and number");
            TextField cityField = new TextField();
            cityField.setPromptText("City");
            TextField typeField = new TextField();
            typeField.setPromptText("Type (house, apartment, etc.)");

            grid.add(new Label("Street:*"), 0, 0);
            grid.add(streetField, 1, 0);
            grid.add(new Label("City:*"), 0, 1);
            grid.add(cityField, 1, 1);
            grid.add(new Label("Type:*"), 0, 2);
            grid.add(typeField, 1, 2);

            // Validar que los campos obligatorios estén llenos
            Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
            addButton.setDisable(true);

            // Listener para habilitar el botón solo cuando los campos obligatorios estén llenos
            streetField.textProperty().addListener((observable, oldValue, newValue) -> {
                addButton.setDisable(streetField.getText().trim().isEmpty() ||
                        cityField.getText().trim().isEmpty() ||
                        typeField.getText().trim().isEmpty());
            });

            cityField.textProperty().addListener((observable, oldValue, newValue) -> {
                addButton.setDisable(streetField.getText().trim().isEmpty() ||
                        cityField.getText().trim().isEmpty() ||
                        typeField.getText().trim().isEmpty());
            });

            typeField.textProperty().addListener((observable, oldValue, newValue) -> {
                addButton.setDisable(streetField.getText().trim().isEmpty() ||
                        cityField.getText().trim().isEmpty() ||
                        typeField.getText().trim().isEmpty());
            });

            dialog.getDialogPane().setContent(grid);

            // Convertir el resultado a una Address cuando se presiona Add
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    String id = "dest_addr_" + System.currentTimeMillis();
                    return new Address(id,
                            streetField.getText().trim(),
                            cityField.getText().trim(),
                            typeField.getText().trim(),
                            0, 0);
                }
                return null;
            });

            // Mostrar diálogo y procesar resultado
            java.util.Optional<Address> result = dialog.showAndWait();
            result.ifPresent(newAddress -> {
                // Agregar la nueva dirección a la lista de DESTINO
                customerAddresses.add(newAddress);
                destinationComboBox.getItems().add(newAddress);
                destinationComboBox.setValue(newAddress);
                showSuccess("New destination address added: " + formatAddress(newAddress));
            });

        } catch (Exception e) {
            showError("Error adding address: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCalculateCost(ActionEvent event) {
        try {
            // Validar campos básicos
            if (!validateBasicFields()) {
                return;
            }

            double weight = Double.parseDouble(weightField.getText().trim());

            // Obtener direcciones de ORIGEN y DESTINO
            Address origin = originComboBox.getValue();
            Address destination = destinationComboBox.getValue();

            if (origin == null || destination == null) {
                showError("Please select both origin and destination addresses");
                return;
            }

            // Determinar tipo de envío
            String shipmentType = expressCheckBox.isSelected() ? "express" : "normal";

            // Crear estrategia de tarifa
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
            double baseCost = rateStrategy.calculate(origin, destination, weight);

            // Agregar servicios adicionales
            double additionalCost = 0;
            if (prioritarioCheckBox.isSelected()) additionalCost += 6000;
            if (firmaCheckBox.isSelected()) additionalCost += 2000;
            if (seguroCheckBox.isSelected()) additionalCost += 5000;
            if (expressCheckBox.isSelected()) additionalCost += 8000;

            double totalCost = baseCost + additionalCost;

            estimatedCostLabel.setText(String.format("Estimated cost: $%.2f", totalCost));
            estimatedCostLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 14px;");
            showSuccess("Cost calculated successfully");

        } catch (NumberFormatException e) {
            showError("Error in number format. Use dot for decimals.");
        } catch (Exception e) {
            showError("Error calculating cost: " + e.getMessage());
        }
    }

    @FXML
    public void handleCreateOrder(ActionEvent event) {
        try {
            // Validar todos los campos
            if (!validateAllFields()) {
                return;
            }

            // Obtener direcciones de ORIGEN y DESTINO
            Address origin = originComboBox.getValue();
            Address destination = destinationComboBox.getValue();

            if (origin == null || destination == null) {
                showError("Please select both origin and destination addresses");
                return;
            }

            // Obtener datos del formulario
            String shipmentId = "SHIP_" + System.currentTimeMillis();
            double weight = Double.parseDouble(weightField.getText().trim());

            // Determinar tipo de envío
            String shipmentType = expressCheckBox.isSelected() ? "express" : "normal";

            // Obtener método de pago
            String paymentMethodStr = paymentMethodComboBox.getValue();
            PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentMethodStr);

            // Recopilar servicios adicionales
            List<String> services = new ArrayList<>();
            if (prioritarioCheckBox.isSelected()) services.add("PRIORITY");
            if (firmaCheckBox.isSelected()) services.add("SIGNATURE");
            if (seguroCheckBox.isSelected()) services.add("INSURANCE");
            if (expressCheckBox.isSelected()) services.add("EXPRESS");

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

            // Aquí deberías tener un método en DeliverX para agregar el envío
            // deliverX.addShipment(newShipment);

            System.out.println("New shipment created:");
            System.out.println("ID: " + newShipment.getIdShipment());
            System.out.println("Customer: " + newShipment.getCustomer().getName());
            System.out.println("Origin: " + formatAddress(origin));
            System.out.println("Destination: " + formatAddress(destination));
            System.out.println("Weight: " + weight + " kg");
            System.out.println("Type: " + shipmentType);
            System.out.println("Services: " + services);
            System.out.println("Payment method: " + paymentMethod);

            // Mostrar mensaje de éxito
            showSuccess("Order created successfully! ID: " + shipmentId +
                    "\nOrigin: " + formatAddress(origin) +
                    "\nDestination: " + formatAddress(destination));

        } catch (NumberFormatException e) {
            showError("Error in number format. Use dot for decimals.");
        } catch (Exception e) {
            showError("Error creating order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validateBasicFields() {
        if (weightField.getText().trim().isEmpty()) {
            showError("Please enter package weight");
            return false;
        }

        try {
            double weight = Double.parseDouble(weightField.getText().trim());
            if (weight <= 0) {
                showError("Weight must be greater than 0");
                return false;
            }
            if (weight > 100) {
                showError("Weight cannot be greater than 100 kg");
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Weight must be a valid number. Use dot for decimals.");
            return false;
        }

        if (destinationComboBox.getValue() == null) {
            showError("Please select a destination address");
            return false;
        }

        return true;
    }

    private boolean validateAllFields() {
        if (!validateBasicFields()) {
            return false;
        }

        if (paymentMethodComboBox.getValue() == null) {
            showError("Please select a payment method");
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
        descriptionArea.clear();

        // Limpiar checkboxes
        prioritarioCheckBox.setSelected(false);
        firmaCheckBox.setSelected(false);
        expressCheckBox.setSelected(false);
        seguroCheckBox.setSelected(false);

        // Resetear ComboBoxes
        paymentMethodComboBox.setValue("CASH");
        if (!customerAddresses.isEmpty()) {
            destinationComboBox.setValue(customerAddresses.get(0));
        }

        // Limpiar labels
        messageLabel.setText("");
        estimatedCostLabel.setText("Estimated cost: $0.00");
        estimatedCostLabel.setStyle("");

        showInfo("Form cleared. Origin address remains Armenia.");
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