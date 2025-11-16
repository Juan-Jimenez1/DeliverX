package co.edu.uniquindio.poo.deliverx.pdfGenerator;

import co.edu.uniquindio.poo.deliverx.model.Shipment;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;

public class pdfReceiptAdapter implements exportable{

    private Shipment shipment;

    public pdfReceiptAdapter(Shipment shipment) {
        this.shipment = shipment;
    }

    public void generatePDFReport(String filename) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDRectangle pageSize = page.getMediaBox();
            float pageWidth = pageSize.getWidth();

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {

                // --- Title ---
                String title = "ORDER RECEIPT";
                int fontSizeTitle = 16;
                float titleWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(title) / 1000 * fontSizeTitle;
                float titleX = (pageWidth - titleWidth) / 2;

                content.setFont(PDType1Font.HELVETICA_BOLD, fontSizeTitle);
                content.beginText();
                content.newLineAtOffset(titleX, 750);
                content.showText(title);
                content.endText();

                // --- Shipment info ---
                float y = 700;
                content.setFont(PDType1Font.HELVETICA, 12);

                String[] info = {
                        "Shipment ID: " + shipment.getIdShipment(),
                        "Date: " + shipment.getDateTime(),
                        "Shipment Status: " + shipment.getCurrentState().getStateName(),
                        "Customer: " + shipment.getCustomer().getName(),
                        "Shipment Type: " + shipment.getType(), // Normal o Express
                        "Payment method: " + shipment.getPay().getPaymentMethod(),
                        "Payment Status: " + shipment.getPay().getResult()
                };

                for (String line : info) {
                    float textWidth = PDType1Font.HELVETICA.getStringWidth(line) / 1000 * 12;
                    float startX = (pageWidth - textWidth) / 2;
                    content.beginText();
                    content.newLineAtOffset(startX, y);
                    content.showText(line);
                    content.endText();
                    y -= 20;
                }

                // --- Additional services ---
                if (!shipment.getAdditionalServices().isEmpty()) {
                    content.setFont(PDType1Font.HELVETICA, 12);
                    y -= 10; // un pequeño espacio
                    String extrasTitle = "Additional Services:";
                    float extrasTitleWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(extrasTitle) / 1000 * 12;
                    float extrasX = (pageWidth - extrasTitleWidth) / 2;
                    content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    content.beginText();
                    content.newLineAtOffset(extrasX, y);
                    content.showText(extrasTitle);
                    content.endText();
                    y -= 20;

                    for (String extra : shipment.getAdditionalServices()) {
                        float lineWidth = PDType1Font.HELVETICA.getStringWidth(extra) / 1000 * 12;
                        float lineX = (pageWidth - lineWidth) / 2;
                        content.setFont(PDType1Font.HELVETICA, 12);
                        content.beginText();
                        content.newLineAtOffset(lineX, y);
                        content.showText(extra);
                        content.endText();
                        y -= 20;
                    }
                }

                // --- Total ---
                String totalLine = "TOTAL: $" + shipment.getPrice();
                float totalWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(totalLine) / 1000 * 12;
                float totalX = (pageWidth - totalWidth) / 2;

                content.setFont(PDType1Font.HELVETICA_BOLD, 12);
                content.beginText();
                content.newLineAtOffset(totalX, y - 20);
                content.showText(totalLine);
                content.endText();
            }

            document.save("C:\\Users\\JUAN JOSE\\OneDrive\\Desktop\\" + filename + ".pdf");
            System.out.println("PDF receipt generated: " + filename);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
