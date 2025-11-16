package co.edu.uniquindio.poo.deliverx.pdfGenerator.adapter;

import co.edu.uniquindio.poo.deliverx.model.Shipment;
import co.edu.uniquindio.poo.deliverx.pdfGenerator.PdfShipmentsByStateReport;

import java.util.List;

public class StatePdfAdapter implements PdfReportGenerator{
    private PdfShipmentsByStateReport statePDF;
    public StatePdfAdapter(List<Shipment> shipments) {
        this.statePDF = new PdfShipmentsByStateReport(shipments);
    }
    @Override
    public void generatePDFReport(String filename, String state) {
        statePDF.generateShipmentsByStatePDF(filename,state);
    }
}
