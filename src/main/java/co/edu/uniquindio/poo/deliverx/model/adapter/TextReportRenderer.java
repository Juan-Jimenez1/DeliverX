package co.edu.uniquindio.poo.deliverx.model.adapter;

import co.edu.uniquindio.poo.deliverx.model.Shipment;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class TextReportRenderer {
    public boolean renderToFile(String filename, ReportData data) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {

            // Encabezado del reporte
            writer.println("╔════════════════════════════════════════════════════════════════════════════╗");
            writer.println("║                           " + centerText(data.getTitle(), 44) + "                           ║");
            writer.println("╚════════════════════════════════════════════════════════════════════════════╝");
            writer.println();

            if (data.getSubtitle() != null) {
                writer.println(data.getSubtitle());
                writer.println();
            }

            writer.println("Fecha de generación: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            writer.println("═══════════════════════════════════════════════════════════════════════════════");
            writer.println();

            // Contenido
            if (data.getShipments() != null) {
                renderShipments(writer, data.getShipments());
            } else if (data.getCustomData() != null) {
                renderCustomData(writer, data.getCustomData());
            }

            // Pie de página
            writer.println();
            writer.println("═══════════════════════════════════════════════════════════════════════════════");
            writer.println("                          Generado por DeliverX System");
            writer.println("═══════════════════════════════════════════════════════════════════════════════");

            return true;

        } catch (IOException e) {
            System.err.println(" Error renderizando reporte: " + e.getMessage());
            return false;
        }
    }

    private void renderShipments(PrintWriter writer, List<Shipment> shipments) {
        writer.println("RESUMEN DE ENVÍOS");
        writer.println("───────────────────────────────────────────────────────────────────────────────");
        writer.printf("Total de envíos: %d%n%n", shipments.size());

        // Tabla de envíos
        writer.println(String.format("%-12s %-20s %-15s %-15s %-10s",
                "ID", "Cliente", "Origen", "Destino", "Estado"));
        writer.println("───────────────────────────────────────────────────────────────────────────────");

        for (Shipment s : shipments) {
            writer.printf("%-12s %-20s %-15s %-15s %-10s%n",
                    truncate(s.getIdShipment(), 12),
                    truncate(s.getCustomer().getName(), 20),
                    truncate(s.getOrigin().getCity(), 15),
                    truncate(s.getDestination().getCity(), 15),
                    s.getCurrentState() != null ? s.getCurrentState().getStateName() : "N/A"
            );
        }

        writer.println();

        // Estadísticas
        renderStatistics(writer, shipments);
    }

    private void renderStatistics(PrintWriter writer, List<Shipment> shipments) {
        writer.println("ESTADÍSTICAS");
        writer.println("───────────────────────────────────────────────────────────────────────────────");

        // Total por estado
        writer.println("Envíos por estado:");
        shipments.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getCurrentState() != null ? s.getCurrentState().getStateName() : "N/A",
                        Collectors.counting()
                ))
                .forEach((estado, cantidad) ->
                        writer.printf("  - %s: %d%n", estado, cantidad)
                );

        // Monto total
        double totalAmount = shipments.stream()
                .filter(s -> s.getPay() != null)
                .mapToDouble(s -> s.getPay().getAmount())
                .sum();
        writer.printf("%nMonto total: $%,.2f%n", totalAmount);
    }

    private void renderCustomData(PrintWriter writer, List<String[]> data) {
        writer.println("DATOS DEL REPORTE");
        writer.println("───────────────────────────────────────────────────────────────────────────────");

        for (String[] row : data) {
            writer.println(String.join(" | ", row));
        }
    }

    private String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text;
    }

    private String truncate(String text, int maxLength) {
        return text.length() > maxLength ? text.substring(0, maxLength - 3) + "..." : text;
    }
}
