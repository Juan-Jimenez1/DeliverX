package co.edu.uniquindio.poo.deliverx.model.pdfGenerator.adapter;

import co.edu.uniquindio.poo.deliverx.model.Shipment;
import co.edu.uniquindio.poo.deliverx.model.pdfGenerator.PdfShipmentsPerMonthReportGenerator;

import java.util.List;

public class MonthPdfAdapter implements PdfReportGenerator{
    private PdfShipmentsPerMonthReportGenerator monthPDF;

    public MonthPdfAdapter( List<Shipment> shipments) {
        this.monthPDF = new PdfShipmentsPerMonthReportGenerator(shipments);
    }
    @Override
    public void generatePDFReport(String filename, String month) {
        monthPDF.generatePDFReport(filename, month);
    }
}
