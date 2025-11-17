package co.edu.uniquindio.poo.deliverx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import co.edu.uniquindio.poo.deliverx.model.DeliverX;
import co.edu.uniquindio.poo.deliverx.model.Shipment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class DeliveryHistoryController {
    @FXML
    public DatePicker dateFromPicker;
    @FXML
    public DatePicker dateToPicker;
    @FXML
    public ComboBox<String> statusFilterComboBox;
    @FXML
    public TableView<Shipment> historyTable;
    @FXML
    public TableColumn<Shipment, String> orderIdColumn;
    @FXML
    public TableColumn<Shipment, String> destinationColumn;
    @FXML
    public TableColumn<Shipment, String> statusColumn;
    @FXML
    public TableColumn<Shipment, String> deliveryDateColumn;
    @FXML
    public TableColumn<Shipment, String> durationColumn;
    @FXML
    public Label totalDeliveriesLabel;
    @FXML
    public Label completedLabel;
    @FXML
    public Label incidentsLabel;
    @FXML
    public Label avgTimeLabel;
    @FXML
    public Label successRateLabel;
    @FXML
    public Label statusLabel;

    private ObservableList<Shipment> shipmentsData;
    private FilteredList<Shipment> filteredData;
    private DeliverX deliverX;

    @FXML
    public void initialize() {
        deliverX = DeliverX.getInstance();

        // Initialize table columns
        orderIdColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getIdShipment()));
        destinationColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDestination() != null ?
                                cellData.getValue().getDestination().getStreet() + ", " +
                                        cellData.getValue().getDestination().getCity() : "No destination"));
        statusColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getCurrentState().getStateName()));
        deliveryDateColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getDateTime() != null ?
                                cellData.getValue().getDateTime().toString() : "No date"));
        durationColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(calculateDuration(cellData.getValue())));

        // Initialize status filter
        statusFilterComboBox.setItems(FXCollections.observableArrayList(
                "All", "REQUESTED", "ASSIGNED", "ROUTE", "DELIVERED", "CANCELLED"
        ));
        statusFilterComboBox.setValue("All");

        // Set default date range (last 30 days)
        dateToPicker.setValue(LocalDate.now());
        dateFromPicker.setValue(LocalDate.now().minusDays(30));

        // Load data
        loadShipmentsData();

        // Set up filtering
        setupFiltering();

        // Update statistics
        updateStatistics();
    }

    private void loadShipmentsData() {
        shipmentsData = FXCollections.observableArrayList();

        // Load shipments from DeliverX system
        if (deliverX.getListShipments() != null) {
            shipmentsData.addAll(deliverX.getListShipments());
        }
    }

    private void setupFiltering() {
        filteredData = new FilteredList<>(shipmentsData, p -> true);

        // Date filter
        dateFromPicker.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        dateToPicker.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        // Status filter
        statusFilterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        // Wrap the FilteredList in a SortedList
        SortedList<Shipment> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(historyTable.comparatorProperty());
        historyTable.setItems(sortedData);
    }

    private void applyFilters() {
        filteredData.setPredicate(shipment -> {
            // Date filter
            if (dateFromPicker.getValue() != null && dateToPicker.getValue() != null) {
                if (shipment.getDateTime() == null) {
                    return false;
                }
                LocalDate shipmentDate = shipment.getDateTime();
                if (shipmentDate.isBefore(dateFromPicker.getValue()) ||
                        shipmentDate.isAfter(dateToPicker.getValue())) {
                    return false;
                }
            }

            // Status filter
            String selectedStatus = statusFilterComboBox.getValue();
            if (selectedStatus != null && !selectedStatus.equals("All")) {
                if (!shipment.getCurrentState().getStateName().equals(selectedStatus)) {
                    return false;
                }
            }

            return true;
        });

        updateStatistics();
        updateStatusLabel();
    }

    private String calculateDuration(Shipment shipment) {
        if (shipment.getDateTime() == null) {
            return "N/A";
        }

        long days = ChronoUnit.DAYS.between(shipment.getDateTime(), LocalDate.now());
        if (days == 0) {
            return "Today";
        } else if (days == 1) {
            return "1 day";
        } else {
            return days + " days";
        }
    }

    private void updateStatistics() {
        int total = filteredData.size();
        int completed = (int) filteredData.stream()
                .filter(s -> "DELIVERED".equals(s.getCurrentState().getStateName()))
                .count();
        int incidents = (int) filteredData.stream()
                .filter(s -> "CANCELLED".equals(s.getCurrentState().getStateName()))
                .count();

        // Calculate average "duration" (simplified - using days since creation)
        double avgDays = filteredData.stream()
                .filter(s -> s.getDateTime() != null)
                .mapToLong(s -> ChronoUnit.DAYS.between(s.getDateTime(), LocalDate.now()))
                .average()
                .orElse(0.0);

        double successRate = total > 0 ? (double) completed / total * 100 : 0;

        totalDeliveriesLabel.setText(String.valueOf(total));
        completedLabel.setText(String.valueOf(completed));
        incidentsLabel.setText(String.valueOf(incidents));
        avgTimeLabel.setText(String.format("%.1f days", avgDays));
        successRateLabel.setText(String.format("%.1f%%", successRate));
    }

    private void updateStatusLabel() {
        int total = shipmentsData.size();
        int filtered = filteredData.size();
        statusLabel.setText("Showing " + filtered + " of " + total + " shipments in history");
    }

    @FXML
    public void handleFilter(ActionEvent event) {
        applyFilters();
        showAlert("Information", "Filters applied successfully");
    }

    @FXML
    public void handleClear(ActionEvent event) {
        // Reset filters
        dateFromPicker.setValue(LocalDate.now().minusDays(30));
        dateToPicker.setValue(LocalDate.now());
        statusFilterComboBox.setValue("All");

        applyFilters();
        showAlert("Information", "Filters cleared");
    }

    @FXML
    public void handleViewDetails(ActionEvent event) {
        Shipment selectedShipment = historyTable.getSelectionModel().getSelectedItem();
        if (selectedShipment != null) {
            showShipmentDetails(selectedShipment);
        } else {
            showAlert("Warning", "Please select a shipment to view details");
        }
    }

    @FXML
    public void handleExportReport(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Delivery History Report");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );
            fileChooser.setInitialFileName("delivery_history_" + LocalDate.now() + ".csv");

            File file = fileChooser.showSaveDialog(new Stage());
            if (file != null) {
                exportToCSV(file);
                showAlert("Success", "Report exported successfully to: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to export report: " + e.getMessage());
        }
    }

    private void exportToCSV(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            // Write header
            writer.write("Shipment ID,Customer,Origin,Destination,Status,Weight,Price,Date,Duration\n");

            // Write data
            for (Shipment shipment : filteredData) {
                writer.write(String.format("%s,%s,%s,%s,%s,%.2f,%.2f,%s,%s\n",
                        shipment.getIdShipment(),
                        shipment.getCustomer() != null ? shipment.getCustomer().getName() : "No Customer",
                        shipment.getOrigin() != null ? shipment.getOrigin().getStreet() : "No Origin",
                        shipment.getDestination() != null ? shipment.getDestination().getStreet() : "No Destination",
                        shipment.getCurrentState().getStateName(),
                        shipment.getWeight(),
                        shipment.getPrice(),
                        shipment.getDateTime() != null ? shipment.getDateTime().toString() : "No Date",
                        calculateDuration(shipment)
                ));
            }
        }
    }

    private void showShipmentDetails(Shipment shipment) {
        StringBuilder details = new StringBuilder();
        details.append("=== SHIPMENT DETAILS ===\n\n");
        details.append("ID: ").append(shipment.getIdShipment()).append("\n");
        details.append("Customer: ").append(shipment.getCustomer() != null ? shipment.getCustomer().getName() : "No customer").append("\n");
        details.append("Email: ").append(shipment.getCustomer() != null ? shipment.getCustomer().getEmail() : "No email").append("\n");
        details.append("Origin: ").append(shipment.getOrigin() != null ?
                shipment.getOrigin().getStreet() + ", " + shipment.getOrigin().getCity() : "No origin").append("\n");
        details.append("Destination: ").append(shipment.getDestination() != null ?
                shipment.getDestination().getStreet() + ", " + shipment.getDestination().getCity() : "No destination").append("\n");
        details.append("Status: ").append(shipment.getCurrentState().getStateName()).append("\n");
        details.append("Weight: ").append(shipment.getWeight()).append(" kg\n");
        details.append("Price: $").append(String.format("%,.2f", shipment.getPrice())).append("\n");
        details.append("Date: ").append(shipment.getDateTime() != null ? shipment.getDateTime().toString() : "No date").append("\n");
        details.append("Duration: ").append(calculateDuration(shipment)).append("\n");

        if (shipment.getDeliveryMan() != null) {
            details.append("Delivery Man: ").append(shipment.getDeliveryMan().getName()).append("\n");
        }

        if (shipment.getExtraServices() != null && !shipment.getExtraServices().isEmpty()) {
            details.append("Extra Services: ").append(String.join(", ", shipment.getExtraServices())).append("\n");
        }

        TextArea textArea = new TextArea(details.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        ScrollPane scrollPane = new ScrollPane(textArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefSize(500, 400);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Shipment Details");
        alert.setHeaderText("Complete Details for Shipment " + shipment.getIdShipment());
        alert.getDialogPane().setContent(scrollPane);
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}