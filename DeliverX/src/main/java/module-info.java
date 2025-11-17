module co.edu.uniquindio.poo.deliverx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    requires org.apache.pdfbox;


    exports co.edu.uniquindio.poo.deliverx.app;
    opens co.edu.uniquindio.poo.deliverx.app to javafx.fxml;
}