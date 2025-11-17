package co.edu.uniquindio.poo.deliverx.controller;

import co.edu.uniquindio.poo.deliverx.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class MetricsController {

    // KPI Labels
    @FXML private Label totalUsersLabel;
    @FXML private Label usersGrowthLabel;
    @FXML private Label activeDeliveriesLabel;
    @FXML private Label deliveriesStatusLabel;
    @FXML private Label avgDeliveryTimeLabel;
    @FXML private Label timeComparisonLabel;
    @FXML private Label monthlyRevenueLabel;
    @FXML private Label revenueGrowthLabel;
    @FXML private Label lastUpdateLabel;

    // Additional Metrics Labels
    @FXML private Label adminsLabel;
    @FXML private Label inactiveDeliveriesLabel;
    @FXML private Label avgSatisfactionLabel;

    // Charts
    @FXML private LineChart<String, Number> revenueChart;
    @FXML private CategoryAxis revenueXAxis;
    @FXML private NumberAxis revenueYAxis;
    @FXML private ComboBox<String> revenuePeriodCombo;

    @FXML private PieChart additionalServicesChart;

    @FXML private BarChart<String, Number> deliveryTimeChart;
    @FXML private CategoryAxis timeXAxis;
    @FXML private NumberAxis timeYAxis;

    @FXML private BarChart<String, Number> incidentsByZoneChart;
    @FXML private CategoryAxis zoneXAxis;
    @FXML private NumberAxis zoneYAxis;

    // Table
    @FXML private TableView<ZoneStatistics> zoneStatsTable;
    @FXML private TableColumn<ZoneStatistics, String> zoneNameColumn;
    @FXML private TableColumn<ZoneStatistics, Integer> zoneDeliveriesColumn;
    @FXML private TableColumn<ZoneStatistics, String> zoneAvgTimeColumn;
    @FXML private TableColumn<ZoneStatistics, Integer> zoneIncidentsColumn;
    @FXML private TableColumn<ZoneStatistics, String> zoneRevenueColumn;
    @FXML private TableColumn<ZoneStatistics, Double> zoneRatingColumn;

    private DeliverX deliverX;
    private Admin currentAdmin;

    @FXML
    public void initialize() {
        deliverX = DeliverX.getInstance();

        // Configurar ComboBox de períodos
        revenuePeriodCombo.setItems(FXCollections.observableArrayList(
                "Última Semana", "Último Mes", "Último Trimestre", "Último Año"
        ));
        revenuePeriodCombo.setValue("Último Mes");
        revenuePeriodCombo.setOnAction(e -> updateRevenueChart());

        // Configurar columnas de la tabla
        setupTableColumns();

        // Cargar datos iniciales
        loadAllMetrics();
        updateLastUpdateTime();
    }

    private void setupTableColumns() {
        zoneNameColumn.setCellValueFactory(new PropertyValueFactory<>("zoneName"));
        zoneDeliveriesColumn.setCellValueFactory(new PropertyValueFactory<>("totalDeliveries"));
        zoneAvgTimeColumn.setCellValueFactory(new PropertyValueFactory<>("averageTime"));
        zoneIncidentsColumn.setCellValueFactory(new PropertyValueFactory<>("incidents"));
        zoneRevenueColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        zoneRatingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
    }

    private void loadAllMetrics() {
        updateKPIs();
        updateRevenueChart();
        updateAdditionalServicesChart();
        updateDeliveryTimeChart();
        updateIncidentsByZoneChart();
        updateZoneStatisticsTable();
    }

    private void updateKPIs() {
        // Total de usuarios (clientes + repartidores + admins)
        int totalUsers = deliverX.getListCustomers().size() +
                deliverX.getListDeliveryMans().size() +
                deliverX.getListAdmins().size();
        totalUsersLabel.setText(String.valueOf(totalUsers));

        // Calcular crecimiento de usuarios (simulado)
        double usersGrowth = calculateGrowthRate(totalUsers);
        usersGrowthLabel.setText(String.format("↑ %.1f%% vs mes anterior", usersGrowth));
        usersGrowthLabel.setStyle(usersGrowth >= 0 ?
                "-fx-text-fill: #27ae60; -fx-font-size: 11px;" :
                "-fx-text-fill: #e74c3c; -fx-font-size: 11px;");

        // Repartidores activos
        long activeDeliveries = deliverX.getListDeliveryMans().stream()
                .filter(dp ->dp.getState()).count();
        activeDeliveriesLabel.setText(String.valueOf(activeDeliveries));

        // Repartidores inactivos
        long inactiveDeliveries = deliverX.getListDeliveryMans().size() - activeDeliveries;
        inactiveDeliveriesLabel.setText(String.valueOf(inactiveDeliveries));

        // Tiempo promedio de entrega (simulado basado en peso y distancia)
        double avgTime = calculateAverageDeliveryTime();
        avgDeliveryTimeLabel.setText(String.format("%.0f min", avgTime));

        // Comparación de tiempo
        timeComparisonLabel.setText("↓ 5% más rápido");

        // Ingresos del mes
        double monthlyRevenue = calculateMonthlyRevenue();
        monthlyRevenueLabel.setText(String.format("$%.2f", monthlyRevenue));

        // Crecimiento de ingresos
        double revenueGrowth = calculateGrowthRate((int) monthlyRevenue);
        revenueGrowthLabel.setText(String.format("↑ %.1f%% vs mes anterior", revenueGrowth));

        // Administradores
        adminsLabel.setText(String.valueOf(deliverX.getListAdmins().size()));

        // Satisfacción promedio (simulada)
        double avgSatisfaction = 4.2 + (Math.random() * 0.8); // Entre 4.2 y 5.0
        avgSatisfactionLabel.setText(String.format("%.1f", avgSatisfaction));
    }

    private void updateRevenueChart() {
        revenueChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ingresos");

        String period = revenuePeriodCombo.getValue();
        Map<String, Double> revenueData = getRevenueByPeriod(period);

        revenueData.forEach((key, value) ->
                series.getData().add(new XYChart.Data<>(key, value))
        );

        revenueChart.getData().add(series);
    }

    private void updateAdditionalServicesChart() {
        additionalServicesChart.getData().clear();

        // Recopilar todos los servicios adicionales
        Map<String, Long> servicesCount = new HashMap<>();

        for (Shipment shipment : deliverX.getListShipments()) {
            if (shipment.getAdditionalServices() != null) {
                for (String service : shipment.getAdditionalServices()) {
                    servicesCount.merge(service, 1L, Long::sum);
                }
            }
        }

        // Si no hay servicios, mostrar mensaje
        if (servicesCount.isEmpty()) {
            servicesCount.put("Sin servicios adicionales", 1L);
        }

        servicesCount.forEach((serviceName, count) -> {
            PieChart.Data slice = new PieChart.Data(serviceName, count);
            additionalServicesChart.getData().add(slice);
        });
    }

    private void updateDeliveryTimeChart() {
        deliveryTimeChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        Map<String, Long> timeDistribution = new LinkedHashMap<>();
        timeDistribution.put("0-15", 0L);
        timeDistribution.put("15-30", 0L);
        timeDistribution.put("30-45", 0L);
        timeDistribution.put("45-60", 0L);
        timeDistribution.put("60+", 0L);

        // Calcular tiempo estimado basado en peso y distancia
        for (Shipment shipment : deliverX.getListShipments()) {
            double estimatedTime = estimateDeliveryTime(shipment);

            if (estimatedTime <= 15) timeDistribution.merge("0-15", 1L, Long::sum);
            else if (estimatedTime <= 30) timeDistribution.merge("15-30", 1L, Long::sum);
            else if (estimatedTime <= 45) timeDistribution.merge("30-45", 1L, Long::sum);
            else if (estimatedTime <= 60) timeDistribution.merge("45-60", 1L, Long::sum);
            else timeDistribution.merge("60+", 1L, Long::sum);
        }

        timeDistribution.forEach((range, count) ->
                series.getData().add(new XYChart.Data<>(range, count))
        );

        deliveryTimeChart.getData().add(series);
    }

    private void updateIncidentsByZoneChart() {
        incidentsByZoneChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Contar envíos por zona
        Map<String, Long> shipmentsByZone = deliverX.getListShipments().stream()
                .collect(Collectors.groupingBy(
                        shipment -> getZoneFromAddress(shipment.getOrigin()),
                        Collectors.counting()
                ));

        // Simular incidencias (5-15% de los envíos por zona)
        Map<String, Long> incidentsByZone = new HashMap<>();
        shipmentsByZone.forEach((zone, count) -> {
            long incidents = (long) (count * (0.05 + Math.random() * 0.1));
            incidentsByZone.put(zone, incidents);
        });

        incidentsByZone.forEach((zone, count) ->
                series.getData().add(new XYChart.Data<>(zone, count))
        );

        incidentsByZoneChart.getData().add(series);
    }

    private void updateZoneStatisticsTable() {
        ObservableList<ZoneStatistics> zoneStats = FXCollections.observableArrayList();

        Map<String, List<Shipment>> shipmentsByZone = deliverX.getListShipments().stream()
                .collect(Collectors.groupingBy(s -> getZoneFromAddress(s.getOrigin())));

        shipmentsByZone.forEach((zone, shipments) -> {
            int totalDeliveries = shipments.size();

            // Calcular tiempo promedio estimado
            double avgTime = shipments.stream()
                    .mapToDouble(this::estimateDeliveryTime)
                    .average()
                    .orElse(0.0);

            // Simular incidencias (5-15% de los envíos)
            int incidents = (int) (totalDeliveries * (0.05 + Math.random() * 0.1));

            // Sumar ingresos (precio de los envíos)
            double revenue = shipments.stream()
                    .mapToDouble(Shipment::getPrice)
                    .sum();

            // Simular calificación (entre 3.5 y 5.0)
            double rating = 3.5 + (Math.random() * 1.5);

            zoneStats.add(new ZoneStatistics(
                    zone,
                    totalDeliveries,
                    String.format("%.0f min", avgTime),
                    incidents,
                    String.format("$%.2f", revenue),
                    rating
            ));
        });

        zoneStatsTable.setItems(zoneStats);
    }

    // Métodos auxiliares de cálculo
    private double calculateAverageDeliveryTime() {
        if (deliverX.getListShipments().isEmpty()) {
            return 0.0;
        }

        return deliverX.getListShipments().stream()
                .mapToDouble(this::estimateDeliveryTime)
                .average()
                .orElse(0.0);
    }

    private double estimateDeliveryTime(Shipment shipment) {
        // Tiempo base: 20 minutos
        double baseTime = 20.0;

        // Agregar tiempo por peso (1 min por cada 5 kg)
        double weightTime = (shipment.getWeight() / 5.0);

        // Agregar tiempo por distancia (simulada basada en coordenadas)
        double distanceTime = calculateDistance(shipment.getOrigin(), shipment.getDestination()) * 2;

        // Agregar tiempo por servicios adicionales (5 min por servicio)
        double servicesTime = shipment.getAdditionalServices().size() * 5.0;

        return baseTime + weightTime + distanceTime + servicesTime;
    }

    private double calculateDistance(Address origin, Address destination) {
        // Calcular distancia euclidiana entre coordenadas
        double latDiff = origin.getLatitude() - destination.getLatitude();
        double lonDiff = origin.getLongitude() - destination.getLongitude();
        return Math.sqrt(latDiff * latDiff + lonDiff * lonDiff) * 100; // Escalar a km aproximados
    }

    private double calculateMonthlyRevenue() {
        LocalDate now = LocalDate.now();
        return deliverX.getListShipments().stream()
                .filter(s -> s.getDateTime().getMonth() == now.getMonth() &&
                        s.getDateTime().getYear() == now.getYear())
                .mapToDouble(Shipment::getPrice)
                .sum();
    }

    private double calculateGrowthRate(int currentValue) {
        // Simulación de crecimiento basado en valor actual
        return (currentValue * 0.05) + (Math.random() * 10 - 5);
    }

    private Map<String, Double> getRevenueByPeriod(String period) {
        Map<String, Double> revenueMap = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();

        switch (period) {
            case "Última Semana":
                for (int i = 6; i >= 0; i--) {
                    LocalDate date = now.minusDays(i);
                    String dayLabel = date.format(DateTimeFormatter.ofPattern("EEE"));
                    double revenue = getRevenueForDay(date);
                    revenueMap.put(dayLabel, revenue);
                }
                break;

            case "Último Mes":
                for (int i = 29; i >= 0; i -= 5) {
                    LocalDate date = now.minusDays(i);
                    String dayLabel = date.format(DateTimeFormatter.ofPattern("dd/MM"));
                    double revenue = getRevenueForDay(date);
                    revenueMap.put(dayLabel, revenue);
                }
                break;

            case "Último Trimestre":
                for (int i = 2; i >= 0; i--) {
                    LocalDate date = now.minusMonths(i);
                    String monthLabel = date.format(DateTimeFormatter.ofPattern("MMM"));
                    double revenue = getRevenueForMonth(date);
                    revenueMap.put(monthLabel, revenue);
                }
                break;

            case "Último Año":
                for (int i = 11; i >= 0; i--) {
                    LocalDate date = now.minusMonths(i);
                    String monthLabel = date.format(DateTimeFormatter.ofPattern("MMM"));
                    double revenue = getRevenueForMonth(date);
                    revenueMap.put(monthLabel, revenue);
                }
                break;
        }

        return revenueMap;
    }

    private double getRevenueForDay(LocalDate date) {
        return deliverX.getListShipments().stream()
                .filter(s -> s.getDateTime().equals(date))
                .mapToDouble(Shipment::getPrice)
                .sum();
    }

    private double getRevenueForMonth(LocalDate date) {
        return deliverX.getListShipments().stream()
                .filter(s -> s.getDateTime().getMonth() == date.getMonth() &&
                        s.getDateTime().getYear() == date.getYear())
                .mapToDouble(Shipment::getPrice)
                .sum();
    }

    private String getZoneFromAddress(Address address) {
        if (address == null) return "Zona Desconocida";

        String city = address.getCity();
        if (city == null) return "Zona Desconocida";

        // Extraer zona basándose en la ciudad o tipo
        if (city.toLowerCase().contains("norte") || address.getType().toLowerCase().contains("norte"))
            return "Zona Norte";
        if (city.toLowerCase().contains("sur") || address.getType().toLowerCase().contains("sur"))
            return "Zona Sur";
        if (city.toLowerCase().contains("este") || address.getType().toLowerCase().contains("este"))
            return "Zona Este";
        if (city.toLowerCase().contains("oeste") || address.getType().toLowerCase().contains("oeste"))
            return "Zona Oeste";

        return "Zona Centro";
    }

    private void updateLastUpdateTime() {
        LocalDateTime now = LocalDateTime.now();
        String timeString = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        lastUpdateLabel.setText("Última actualización: " + timeString);
    }

    // Métodos de acción
    @FXML
    private void handleRefresh() {
        loadAllMetrics();
        updateLastUpdateTime();
        showAlert("Datos Actualizados", "Las métricas se han actualizado correctamente.", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleExportPDF() {
        if (currentAdmin == null) {
            showAlert("Error", "No hay administrador autenticado.", Alert.AlertType.ERROR);
            return;
        }

        // Mostrar diálogo de selección de tipo de reporte
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Por Mes", "Por Mes", "Por Estado");
        dialog.setTitle("Tipo de Reporte");
        dialog.setHeaderText("Seleccione el tipo de reporte a generar");
        dialog.setContentText("Tipo de reporte:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(type -> {
            try {
                if (type.equals("Por Mes")) {
                    LocalDate now = LocalDate.now();
                    currentAdmin.generateReportShipmentsPerMonth(now.getYear(), now.getMonthValue());
                } else {
                    // Mostrar diálogo para seleccionar estado
                    TextInputDialog stateDialog = new TextInputDialog("DELIVERED");
                    stateDialog.setTitle("Estado del Envío");
                    stateDialog.setHeaderText("Ingrese el estado de los envíos");
                    stateDialog.setContentText("Estado:");

                    Optional<String> stateResult = stateDialog.showAndWait();
                    stateResult.ifPresent(state -> {
                        currentAdmin.generateReportShipmentsByState(state);
                    });
                }
                showAlert("Reporte Generado", "El reporte PDF se ha generado exitosamente.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Error", "No se pudo generar el reporte: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    @FXML
    private void handleViewDetails() {
        // Implementar navegación a vista de detalles
        showAlert("Detalles", "Función de detalles en desarrollo.", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setCurrentAdmin(Admin admin) {
        this.currentAdmin = admin;
    }

    // Clase interna para las estadísticas de zona
    public static class ZoneStatistics {
        private String zoneName;
        private Integer totalDeliveries;
        private String averageTime;
        private Integer incidents;
        private String revenue;
        private Double rating;

        public ZoneStatistics(String zoneName, Integer totalDeliveries, String averageTime,
                              Integer incidents, String revenue, Double rating) {
            this.zoneName = zoneName;
            this.totalDeliveries = totalDeliveries;
            this.averageTime = averageTime;
            this.incidents = incidents;
            this.revenue = revenue;
            this.rating = rating;
        }

        // Getters
        public String getZoneName() { return zoneName; }
        public Integer getTotalDeliveries() { return totalDeliveries; }
        public String getAverageTime() { return averageTime; }
        public Integer getIncidents() { return incidents; }
        public String getRevenue() { return revenue; }
        public Double getRating() { return rating; }
    }
}