package co.edu.uniquindio.poo.deliverx.controller;

import co.edu.uniquindio.poo.deliverx.model.DeliverX;
import co.edu.uniquindio.poo.deliverx.model.Shipment;
import co.edu.uniquindio.poo.deliverx.model.pdfGenerator.PdfShipmentsByStateReport;
import co.edu.uniquindio.poo.deliverx.model.pdfGenerator.PdfShipmentsPerMonthReportGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class PdfGeneratorControllerAdmin {

    @FXML
    private ComboBox<String> cmbReportType;
    @FXML
    private ComboBox<String> cmbState;
    @FXML
    private ComboBox<String> cmbMonth;
    @FXML
    private TextField txtFileName;
    @FXML
    private TextArea txtPreview;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label lblStatus;

    private DeliverX deliverX;

    @FXML
    public void initialize() {
        deliverX = DeliverX.getInstance();

        // Inicializar ComboBox de tipos de reporte
        cmbReportType.getItems().addAll(
                "Shipments by State",
                "Shipments by Month"
        );
        cmbReportType.setValue("Shipments by State");

        // Inicializar ComboBox de estados
        cmbState.getItems().addAll(
                "PENDING",
                "IN_TRANSIT",
                "DELIVERED",
                "CANCELLED"
        );
        cmbState.setValue("PENDING");

        // Inicializar ComboBox de meses
        cmbMonth.getItems().addAll(
                "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
                "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER"
        );
        cmbMonth.setValue("JANUARY");

        // Generar nombre de archivo por defecto
        generateDefaultFileName();

        // Actualizar visibilidad de controles según el tipo de reporte
        cmbReportType.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateControlsVisibility();
            generateDefaultFileName();
        });

        updateControlsVisibility();
        progressBar.setVisible(false);
    }

    private void updateControlsVisibility() {
        String reportType = cmbReportType.getValue();
        boolean isStateReport = "Shipments by State".equals(reportType);

        cmbState.setVisible(isStateReport);
        cmbMonth.setVisible(!isStateReport);
    }

    private void generateDefaultFileName() {
        String reportType = cmbReportType.getValue();
        String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        if ("Shipments by State".equals(reportType)) {
            String state = cmbState.getValue();
            txtFileName.setText("shipments_" + state.toLowerCase() + "_" + timestamp);
        } else {
            String month = cmbMonth.getValue();
            txtFileName.setText("shipments_" + month.toLowerCase() + "_" + timestamp);
        }
    }

    @FXML
    private void generatePdf(ActionEvent event) {
        try {
            String fileName = txtFileName.getText().trim();
            if (fileName.isEmpty()) {
                showError("Please enter a file name");
                return;
            }

            progressBar.setVisible(true);
            lblStatus.setText("Generating PDF...");

            // Simular proceso asíncrono
            new Thread(() -> {
                try {
                    String reportType = cmbReportType.getValue();
                    List<Shipment> shipments = deliverX.getListShipments();

                    if ("Shipments by State".equals(reportType)) {
                        String state = cmbState.getValue();
                        generateStateReport(shipments, fileName, state);
                    } else {
                        String month = cmbMonth.getValue();
                        generateMonthReport(shipments, fileName, month);
                    }

                    javafx.application.Platform.runLater(() -> {
                        progressBar.setVisible(false);
                        showSuccess("PDF generated successfully!");
                        updatePreview();
                    });

                } catch (Exception e) {
                    javafx.application.Platform.runLater(() -> {
                        progressBar.setVisible(false);
                        showError("Error generating PDF: " + e.getMessage());
                    });
                }
            }).start();

        } catch (Exception e) {
            showError("Error: " + e.getMessage());
            progressBar.setVisible(false);
        }
    }

    private void generateStateReport(List<Shipment> allShipments, String fileName, String state) {
        // Filtrar envíos por estado
        List<Shipment> filteredShipments = allShipments.stream()
                .filter(shipment -> state.equalsIgnoreCase(shipment.getCurrentState().getStateName()))
                .collect(Collectors.toList());

        PdfShipmentsByStateReport report = new PdfShipmentsByStateReport(filteredShipments);
        report.generateShipmentsByStatePDF(fileName, state);
    }

    private void generateMonthReport(List<Shipment> allShipments, String fileName, String month) {
        // Filtrar envíos por mes (aquí necesitarías implementar la lógica de filtrado por mes)
        List<Shipment> filteredShipments = allShipments; // Por ahora todos los envíos

        PdfShipmentsPerMonthReportGenerator report = new PdfShipmentsPerMonthReportGenerator(filteredShipments);
        report.generatePDFReport(fileName, month);
    }

    @FXML
    private void clearFields(ActionEvent event) {
        txtFileName.clear();
        txtPreview.clear();
        lblStatus.setText("");
        generateDefaultFileName();
        updatePreview();
    }

    @FXML
    private void openFolder(ActionEvent event) {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Folder to View PDFs");
            File selectedDirectory = directoryChooser.showDialog(null);

            if (selectedDirectory != null) {
                // Abrir el explorador de archivos en el directorio seleccionado
                java.awt.Desktop.getDesktop().open(selectedDirectory);
                showInfo("Folder opened: " + selectedDirectory.getAbsolutePath());
            }
        } catch (Exception e) {
            showError("Error opening folder: " + e.getMessage());
        }
    }

    @FXML
    private void updatePreview() {
        String reportType = cmbReportType.getValue();
        StringBuilder preview = new StringBuilder();

        if ("Shipments by State".equals(reportType)) {
            String state = cmbState.getValue();
            preview.append("REPORT: Shipments by State\n");
            preview.append("==========================\n");
            preview.append("State: ").append(state).append("\n");
            preview.append("Date: ").append(LocalDate.now()).append("\n");
            preview.append("File: ").append(txtFileName.getText()).append(".pdf\n");
        } else {
            String month = cmbMonth.getValue();
            preview.append("REPORT: Shipments by Month\n");
            preview.append("==========================\n");
            preview.append("Month: ").append(month).append("\n");
            preview.append("Date: ").append(LocalDate.now()).append("\n");
            preview.append("File: ").append(txtFileName.getText()).append(".pdf\n");
        }

        preview.append("\nThis report will include:\n");
        preview.append("- Shipment IDs\n");
        preview.append("- Customer names\n");
        preview.append("- Dates and prices\n");
        preview.append("- Total count\n");

        txtPreview.setText(preview.toString());
    }

    private void showError(String message) {
        lblStatus.setText("❌ " + message);
        lblStatus.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
    }

    private void showSuccess(String message) {
        lblStatus.setText("✓ " + message);
        lblStatus.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
    }

    private void showInfo(String message) {
        lblStatus.setText("ℹ " + message);
        lblStatus.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold;");
    }
}