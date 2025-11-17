package co.edu.uniquindio.poo.deliverx.model.adapter;

import co.edu.uniquindio.poo.deliverx.model.Shipment;

import java.time.LocalDate;
import java.util.List;

public class ReportData {
    private String title;
    private String subtitle;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Shipment> shipments;
    private List<String[]> customData; // Para datos personalizados

    public ReportData(String title, LocalDate startDate, LocalDate endDate, List<Shipment> shipments) {
        this.title = title;
        this.subtitle = "Periodo: " + startDate + " - " + endDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.shipments = shipments;
    }

    public ReportData(String title, List<String[]> customData) {
        this.title = title;
        this.customData = customData;
    }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public List<Shipment> getShipments() { return shipments; }
    public List<String[]> getCustomData() { return customData; }
}

