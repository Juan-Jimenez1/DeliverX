package co.edu.uniquindio.poo.deliverx.controller;

import javafx.event.ActionEvent;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MetricsController {
    public Label lastUpdateLabel;
    public Label totalUsersLabel;
    public Label usersGrowthLabel;
    public Label activeDeliveriesLabel;
    public Label deliveriesStatusLabel;
    public Label avgDeliveryTimeLabel;
    public Label timeComparisonLabel;
    public Label monthlyRevenueLabel;
    public Label revenueGrowthLabel;
    public ComboBox revenuePeriodCombo;
    public LineChart revenueChart;
    public CategoryAxis revenueXAxis;
    public NumberAxis revenueYAxis;
    public PieChart additionalServicesChart;
    public BarChart deliveryTimeChart;
    public CategoryAxis timeXAxis;
    public BarChart incidentsByZoneChart;
    public NumberAxis zoneYAxis;
    public CategoryAxis zoneXAxis;
    public TableView zoneStatsTable;
    public TableColumn zoneNameColumn;
    public TableColumn zoneRatingColumn;
    public TableColumn zoneRevenueColumn;
    public TableColumn zoneIncidentsColumn;
    public TableColumn zoneAvgTimeColumn;
    public TableColumn zoneDeliveriesColumn;
    public Label adminsLabel;
    public Label inactiveDeliveriesLabel;
    public Label avgSatisfactionLabel;
    public NumberAxis timeYAxis;

    public void handleRefresh(ActionEvent event) {
    }

    public void handleExportPDF(ActionEvent event) {
    }

    public void handleViewDetails(ActionEvent event) {
    }
}
