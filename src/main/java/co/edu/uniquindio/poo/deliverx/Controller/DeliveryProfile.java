package co.edu.uniquindio.poo.deliverx.Controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class DeliveryProfile {
    public TextField deliveryIdField;
    public TextField nameField;
    public TextField documentField;
    public TextField emailField;
    public TextField phoneField;
    public TextField coverageZoneField;
    public PasswordField currentPasswordField;
    public PasswordField newPasswordField;
    public Label totalDeliveriesLabel;
    public Label completedDeliveriesLabel;
    public Label successRateLabel;
    public Label messageLabel;
    public Label currentStatusLabel;

    public void handleSaveProfile(ActionEvent event) {
    }

    public void handleCancel(ActionEvent event) {
    }
}
