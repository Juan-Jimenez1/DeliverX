package co.edu.uniquindio.poo.deliverx.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.*;

public class DeliveryHistoryController {
    public DatePicker dateFromPicker;
    public DatePicker dateToPicker;
    public ComboBox statusFilterComboBox;
    public TableView historyTable;
    public TableColumn orderIdColumn;
    public TableColumn destinationColumn;
    public TableColumn statusColumn;
    public TableColumn deliveryDateColumn;
    public TableColumn durationColumn;
    public Label totalDeliveriesLabel;
    public Label completedLabel;
    public Label incidentsLabel;
    public Label avgTimeLabel;
    public Label successRateLabel;
    public Label statusLabel;

    public void handleFilter(ActionEvent event) {
    }

    public void handleClear(ActionEvent event) {
    }

    public void handleViewDetails(ActionEvent event) {
    }

    public void handleExportReport(ActionEvent event) {
    }
}
