package co.edu.uniquindio.poo.deliverx.model;

import java.time.LocalDate;

public class Pay {
    private String paymentId;
    private PaymentMethod paymentMethod;
    private double amount;
    private LocalDate date;
    private TransactionResult result;

    public Pay(String paymentId, PaymentMethod paymentMethod, double amount, LocalDate date, TransactionResult result) {
        this.paymentId = paymentId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.date = date;
        this.result = result;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TransactionResult getResult() {
        return result;
    }

    public void setResult(TransactionResult result) {
        this.result = result;
    }
}
