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

public class PdfShipmentsByStateReport {
    private List<Shipment> shipments;

    public PdfShipmentsByStateReport(List<Shipment> shipments) {
        this.shipments = shipments;
    }

    public void generateShipmentsByStatePDF(String filename, String state) {

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            PDRectangle pageSize = page.getMediaBox();
            float pageWidth = pageSize.getWidth();

            final PDPageContentStream[] content = {new PDPageContentStream(document, page)};

            final float[] y = {750};
            int fontSize = 12;

            // ==== SALTO DE PÁGINA ====
            Runnable newPage = () -> {
                try {
                    content[0].close();
                    PDPage newP = new PDPage();
                    document.addPage(newP);
                    content[0] = new PDPageContentStream(document, newP);
                    y[0] = 750;
                    content[0].setFont(PDType1Font.HELVETICA, fontSize);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            // ====== EMPTY CASE ======
            if (shipments == null || shipments.isEmpty()) {

                String msg = "No shipments with status " + state;
                float msgWidth = PDType1Font.HELVETICA.getStringWidth(msg) / 1000 * fontSize;
                float msgX = (pageWidth - msgWidth) / 2;

                content[0].setFont(PDType1Font.HELVETICA, fontSize);
                content[0].beginText();
                content[0].newLineAtOffset(msgX, y[0]);
                content[0].showText(msg);
                content[0].endText();

                content[0].close();
                document.save("C:\\Users\\Asus\\lol\\" + filename + ".pdf");
                return;
            }

            // ====== TITLE ======
            String title = "Shipments by State: " + state.toUpperCase();
            int titleFont = 16;
            float titleWidth = PDType1Font.HELVETICA_BOLD.getStringWidth(title) / 1000 * titleFont;
            float titleX = (pageWidth - titleWidth) / 2;

            content[0].setFont(PDType1Font.HELVETICA_BOLD, titleFont);
            content[0].beginText();
            content[0].newLineAtOffset(titleX, y[0]);
            content[0].showText(title);
            content[0].endText();

            y[0] -= 35;

            // ====== SEPARATOR ======
            String sep = "----------------------------------";
            float sepWidth = PDType1Font.HELVETICA.getStringWidth(sep) / 1000 * fontSize;
            float sepX = (pageWidth - sepWidth) / 2;

            content[0].setFont(PDType1Font.HELVETICA_BOLD, fontSize);
            content[0].beginText();
            content[0].newLineAtOffset(sepX, y[0]);
            content[0].showText(sep);
            content[0].endText();

            y[0] -= 20;

            // ====== LIST ======
            content[0].setFont(PDType1Font.HELVETICA, fontSize);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (Shipment s : shipments) {

                if (y[0] < 70) newPage.run();

                String line = "• ID: " + s.getIdShipment() +
                        " / Customer: " + s.getCustomer().getName() +
                        " / Price: $" + s.getPrice() +
                        " / Date: " + s.getDateTime().format(formatter);

                float lw = PDType1Font.HELVETICA.getStringWidth(line) / 1000 * fontSize;
                float lx = (pageWidth - lw) / 2;

                content[0].beginText();
                content[0].newLineAtOffset(lx, y[0]);
                content[0].showText(line);
                content[0].endText();

                y[0] -= 20;
            }

            // === TOTAL ===
            if (y[0] < 70) newPage.run();

            String total = "TOTAL: " + shipments.size() + " shipments";
            float tw = PDType1Font.HELVETICA_BOLD.getStringWidth(total) / 1000 * fontSize;
            float tx = (pageWidth - tw) / 2;

            content[0].setFont(PDType1Font.HELVETICA_BOLD, fontSize);
            content[0].beginText();
            content[0].newLineAtOffset(tx, y[0]);
            content[0].showText(total);
            content[0].endText();

            content[0].close();
            document.save("C:\\Users\\Asus\\lol\\" + filename + ".pdf");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
