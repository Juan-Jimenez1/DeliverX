module co.edu.uniquindio.poo.deliverx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens co.edu.uniquindio.poo.deliverx.app to javafx.fxml;
    opens co.edu.uniquindio.poo.deliverx.Controller to javafx.fxml;

    exports co.edu.uniquindio.poo.deliverx.app;
    exports co.edu.uniquindio.poo.deliverx.Controller;
}