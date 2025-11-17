package co.edu.uniquindio.poo.deliverx.model.adapter;

public interface PDFReportInterface {
    boolean generatePDFReport(String filename, ReportData data);
    String getFormatDescription();
}
