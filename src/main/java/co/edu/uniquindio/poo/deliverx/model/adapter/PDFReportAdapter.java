package co.edu.uniquindio.poo.deliverx.model.adapter;

public class PDFReportAdapter implements PDFReportInterface{
    private final TextReportRenderer renderer;

    public PDFReportAdapter() {
        this.renderer = new TextReportRenderer();
    }

    @Override
    public boolean generatePDFReport(String filename, ReportData data) {
        // Adapta el nombre del archivo a formato .txt (o .pdf si usas PDFBox)
        String adaptedFilename = filename.endsWith(".pdf")
                ? filename.replace(".pdf", ".txt")
                : filename + ".txt";

        System.out.println("📄 Generando reporte PDF...");
        boolean result = renderer.renderToFile(adaptedFilename, data);

        if (result) {
            System.out.println("✓ Reporte PDF generado: " + adaptedFilename);
            System.out.println("ℹ️  Para producción, integrar Apache PDFBox para PDF real");
        }

        return result;
    }

    @Override
    public String getFormatDescription() {
        return "PDF (Portable Document Format) - Adaptado desde TextRenderer";
    }
}
