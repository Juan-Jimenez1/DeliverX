module co.edu.uniquindio.poo.deliverx {
    requires javafx.controls;
    requires javafx.fxml;


    exports co.edu.uniquindio.poo.deliverx.app;
    opens co.edu.uniquindio.poo.deliverx.app to javafx.fxml;
}