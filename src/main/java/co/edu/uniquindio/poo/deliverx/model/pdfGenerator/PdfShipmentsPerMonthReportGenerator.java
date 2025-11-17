package co.edu.uniquindio.poo.deliverx.model.pdfGenerator;

import co.edu.uniquindio.poo.deliverx.model.Shipment;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfShipmentsPerMonthReportGenerator {
        private List<Shipment> shipments;

        public PdfShipmentsPerMonthReportGenerator(List<Shipment> shipments) {
            this.shipments = shipments;
        }

    public void generatePDFReport(String filename, String month) {
        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            final PDPageContentStream[] content = {new PDPageContentStream(document, page)};

            PDRectangle pageSize = page.getMediaBox();
            float pageWidth = pageSize.getWidth();

            final float[] y = {750};
            int fontSize = 12;

            Runnable newPage = () -> {
                try {
                    content[0].close();
                    PDPage newPg = new PDPage();
                    document.addPage(newPg);
                    content[0] = new PDPageContentStream(document, newPg);
                    y[0] = 750;
                    content[0].setFont(PDType1Font.HELVETICA, fontSize);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            // ========== IF EMPTY ==========
            if (shipments == null || shipments.isEmpty()) {
                String msg = "No shipments found.";
                float w = PDType1Font.HELVETICA.getStringWidth(msg) / 1000 * fontSize;
                float x = (pageWidth - w) / 2;

                content[0].setFont(PDType1Font.HELVETICA, fontSize);
                content[0].beginText();
                content[0].newLineAtOffset(x, y[0]);
                content[0].showText(msg);
                content[0].endText();

                content[0].close();
                document.save("C:\\Users\\JUAN JOSE\\OneDrive\\Desktop\\" + filename + ".pdf");
                return;
            }

            // ========== TITLE ==========
            String title = "SHIPMENTS IN MONTH " + month;
            int fontSizeTitle = 16;
            float titleWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(title) / 1000 * fontSizeTitle;
            float titleX = (pageWidth - titleWidth) / 2;

            content[0].setFont(PDType1Font.HELVETICA_BOLD, fontSizeTitle);
            content[0].beginText();
            content[0].newLineAtOffset(titleX, y[0]);
            content[0].showText(title);
            content[0].endText();
            y[0] -= 40;

            content[0].setFont(PDType1Font.HELVETICA, fontSize);

            // ========== SHIPMENTS ==========
            for (Shipment s : shipments) {

                String[] info = {
                        "ID: " + s.getIdShipment(),
                        "Customer: " + s.getCustomer().getName(),
                        "Date: " + s.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        "From: " + s.getOrigin().getStreet(),
                        "To: " + s.getDestination().getStreet(),
                        "Type: " + s.getType(),
                        "Weight: " + s.getWeight() + " kg",
                        "Price: $" + s.getPrice(),
                        "State: " + s.getCurrentState().getStateName()
                };

                for (String line : info) {

                    if (y[0] < 70) newPage.run();

                    float tw = PDType1Font.HELVETICA.getStringWidth(line) / 1000 * fontSize;
                    float x = (pageWidth - tw) / 2;

                    content[0].beginText();
                    content[0].newLineAtOffset(x, y[0]);
                    content[0].showText(line);
                    content[0].endText();
                    y[0] -= 20;
                }

                // Extras
                if (s.getAdditionalServices() != null && !s.getAdditionalServices().isEmpty()) {

                    if (y[0] < 100) newPage.run();

                    String extrasTitle = "Additional Services:";
                    float ew = PDType1Font.HELVETICA_BOLD.getStringWidth(extrasTitle) / 1000 * fontSize;
                    float ex = (pageWidth - ew) / 2;

                    content[0].setFont(PDType1Font.HELVETICA_BOLD, fontSize);
                    content[0].beginText();
                    content[0].newLineAtOffset(ex, y[0]);
                    content[0].showText(extrasTitle);
                    content[0].endText();
                    content[0].setFont(PDType1Font.HELVETICA, fontSize);
                    y[0] -= 20;

                    for (String ext : s.getAdditionalServices()) {
                        if (y[0] < 70) newPage.run();

                        float lw = PDType1Font.HELVETICA.getStringWidth(ext) / 1000 * fontSize;
                        float lx = (pageWidth - lw) / 2;

                        content[0].beginText();
                        content[0].newLineAtOffset(lx, y[0]);
                        content[0].showText(ext);
                        content[0].endText();
                        y[0] -= 20;
                    }
                }

                // Separator
                if (y[0] < 70) newPage.run();

                String sep = "----------------------------------------";
                float sepWidth = PDType1Font.HELVETICA.getStringWidth(sep) / 1000 * fontSize;
                float sepX = (pageWidth - sepWidth) / 2;

                content[0].beginText();
                content[0].newLineAtOffset(sepX, y[0]);
                content[0].showText(sep);
                content[0].endText();
                y[0] -= 30;
            }

            // TOTAL SHIPMENTS
            if (y[0] < 70) newPage.run();

            String total = "Total shipments: " + shipments.size();
            float totalWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(total) / 1000 * fontSize;
            float totalX = (pageWidth - totalWidth) / 2;

            content[0].setFont(PDType1Font.HELVETICA_BOLD, fontSize);
            content[0].beginText();
            content[0].newLineAtOffset(totalX, y[0]);
            content[0].showText(total);
            content[0].endText();

            content[0].close();
            document.save("C:\\Users\\JUAN JOSE\\OneDrive\\Desktop\\" + filename + ".pdf");
            System.out.println("Admin PDF report generated: " + filename);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
