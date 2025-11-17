package co.edu.uniquindio.poo.deliverx.model;

import co.edu.uniquindio.poo.deliverx.model.pdfGenerator.adapter.MonthPdfAdapter;
import co.edu.uniquindio.poo.deliverx.model.pdfGenerator.adapter.StatePdfAdapter;

public class Admin extends User{
    private DeliverX deliverX;

    private Admin(Builder<?> builder){
        super(builder);
        this.deliverX= DeliverX.getInstance();
    }

    public static class Builder<T extends Builder<T>> extends User.Builder<T>{
        @Override
        public Admin build() {
            return new Admin(this);
        }
    }

    public void generateReportShipmentsByState(String state){
        StatePdfAdapter statePdfAdapter = new StatePdfAdapter(deliverX.getShipmentsByState(state));
        statePdfAdapter.generatePDFReport("Shipments by state report",state);
    }

    public void generateReportShipmentsPerMonth(int year, int month){
        MonthPdfAdapter monthPdfAdapter = new MonthPdfAdapter(deliverX.getShipmentsByMonth(year, month));
        monthPdfAdapter.generatePDFReport("Shipments per month report",month+" "+"OF"+" "+year);
    }

    public DeliverX getDeliverX() {
        return deliverX;
    }
    public void setDeliverX(DeliverX deliverX) {
        this.deliverX = deliverX;
    }
}
