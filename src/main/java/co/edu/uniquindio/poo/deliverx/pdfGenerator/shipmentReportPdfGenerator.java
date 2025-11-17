package co.edu.uniquindio.poo.deliverx.pdfGenerator;

import co.edu.uniquindio.poo.deliverx.model.Shipment;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;

import javax.swing.text.Document;
import java.util.List;

public class shipmentReportPdfGenerator implements pdfGenerator{

    @Override
    public void generatePDFReport(String filename, Object data) {
        List<Shipment> shipments = (List<Shipment>) data;

        try {
            PdfWriter writer = new PdfWriter(path);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            doc.add(new Paragraph("SHIPMENT REPORT\n\n"));

            for (Shipment s : shipments) {
                doc.add(new Paragraph(
                        "ID: " + s.getId() +
                                " | Client: " + s.getClient().getName() +
                                " | Cost: $" + s.getFinalPrice()
                ));
            }

            doc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
