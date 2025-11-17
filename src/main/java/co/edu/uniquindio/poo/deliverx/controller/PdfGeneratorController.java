package co.edu.uniquindio.poo.deliverx.controller;

import co.edu.uniquindio.poo.deliverx.model.DeliverX;
import co.edu.uniquindio.poo.deliverx.model.Shipment;
import co.edu.uniquindio.poo.deliverx.model.pdfGenerator.PdfReceiptShipmentReport;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class PdfGeneratorController {

    @FXML
    private TextField txtShipmentId;

    @FXML
    private TextField txtFilename;

    @FXML
    private TextArea txtInfo;

    @FXML
    private Button btnGeneratePdf;

    @FXML
    private Label lblStatus;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TextArea txtPreview;

    private DeliverX deliverX;
    private static final String OUTPUT_PATH = "C:\\Users\\Asus\\lol\\";

    @FXML
    public void initialize() {
        deliverX = DeliverX.getInstance();
        lblStatus.setText("Complete los campos y genere el recibo PDF");
        progressBar.setVisible(false);
    }

    @FXML
    public void generatePdf(ActionEvent event) {
        // Validar campos
        String shipmentId = txtShipmentId.getText().trim();
        String filename = txtFilename.getText().trim();

        if (shipmentId.isEmpty()) {
            showError("Por favor ingrese el ID del envío");
            return;
        }

        if (filename.isEmpty()) {
            filename = "recibo_" + shipmentId;
            txtFilename.setText(filename);
        }

        // Buscar el envío
        Shipment shipment = deliverX.getShipment(shipmentId);

        if (shipment == null) {
            showError("No se encontró el envío con ID: " + shipmentId);
            return;
        }

        // Mostrar progreso
        progressBar.setVisible(true);
        progressBar.setProgress(0.3);
        lblStatus.setText("Generando PDF...");
        lblStatus.setStyle("-fx-text-fill: #3498db;");

        // Generar vista previa
        generatePreview(shipment, shipmentId);

        // Generar PDF en un hilo separado
        String finalFilename = filename;
        new Thread(() -> {
            try {
                PdfReceiptShipmentReport pdfReport = new PdfReceiptShipmentReport(shipment);
                pdfReport.generatePDFReport(finalFilename, shipmentId);

                // Actualizar UI en el hilo de JavaFX
                Platform.runLater(() -> {
                    progressBar.setProgress(1.0);
                    lblStatus.setText("✓ PDF generado exitosamente: " + finalFilename + ".pdf");
                    lblStatus.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

                    // Ocultar barra de progreso después de 2 segundos
                    new Thread(() -> {
                        try {
                            Thread.sleep(2000);
                            Platform.runLater(() -> {
                                progressBar.setVisible(false);
                                progressBar.setProgress(0);
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Error al generar el PDF: " + e.getMessage());
                    progressBar.setVisible(false);
                    progressBar.setProgress(0);
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void generatePreview(Shipment shipment, String id) {
        StringBuilder preview = new StringBuilder();
        preview.append("═══════════════════════════════════════\n");
        preview.append("       ORDER RECEIPT ID ").append(id).append("\n");
        preview.append("═══════════════════════════════════════\n\n");

        preview.append("Shipment ID: ").append(id).append("\n");
        preview.append("Date: ").append(shipment.getDateTime()).append("\n");
        preview.append("Status: ").append(shipment.getCurrentState().getStateName()).append("\n");
        preview.append("Customer: ").append(shipment.getCustomer().getName()).append("\n");
        preview.append("Type: ").append(shipment.getType()).append("\n");
        preview.append("Payment Method: ").append(shipment.getPay().getPaymentMethod()).append("\n");
        preview.append("Payment Status: ").append(shipment.getPay().getResult()).append("\n\n");

        if (!shipment.getAdditionalServices().isEmpty()) {
            preview.append("Additional Services:\n");
            for (String service : shipment.getAdditionalServices()) {
                preview.append("  • ").append(service).append("\n");
            }
            preview.append("\n");
        }

        preview.append("───────────────────────────────────────\n");
        preview.append("TOTAL: $").append(shipment.getPrice()).append("\n");
        preview.append("═══════════════════════════════════════\n");

        txtPreview.setText(preview.toString());
    }

    @FXML
    public void clearFields(ActionEvent event) {
        txtShipmentId.clear();
        txtFilename.clear();
        txtInfo.clear();
        txtPreview.clear();
        lblStatus.setText("Complete los campos y genere el recibo PDF");
        lblStatus.setStyle("-fx-text-fill: #7f8c8d; -fx-font-weight: normal;");
        progressBar.setVisible(false);
        progressBar.setProgress(0);
    }

    @FXML
    public void openFolder(ActionEvent event) {
        try {
            File folder = new File(OUTPUT_PATH);

            if (!folder.exists()) {
                showError("La carpeta no existe: " + OUTPUT_PATH);
                return;
            }

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(folder);
                lblStatus.setText("Carpeta abierta exitosamente");
                lblStatus.setStyle("-fx-text-fill: #3498db;");
            } else {
                showError("No se puede abrir la carpeta en este sistema");
            }

        } catch (IOException e) {
            showError("Error al abrir la carpeta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        lblStatus.setText("✗ " + message);
        lblStatus.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}