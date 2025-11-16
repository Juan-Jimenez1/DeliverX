package co.edu.uniquindio.poo.deliverx.pdfGenerator.adapter;

import co.edu.uniquindio.poo.deliverx.model.Shipment;
import co.edu.uniquindio.poo.deliverx.pdfGenerator.PdfReceiptShipmentReport;

public class ReceiptPdfAdapter implements PdfReportGenerator{
    private PdfReceiptShipmentReport receiptPDF;

    public ReceiptPdfAdapter(Shipment shipment) {
        this.receiptPDF = new PdfReceiptShipmentReport(shipment);
    }
    @Override
    public void generatePDFReport(String filename, String id) {
        receiptPDF.generatePDFReport(filename,id);
    }
}
