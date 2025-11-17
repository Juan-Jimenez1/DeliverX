module co.edu.uniquindio.poo.deliverx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.apache.pdfbox;

    exports co.edu.uniquindio.poo.deliverx.app;

    // Controladores y vistas
    opens co.edu.uniquindio.poo.deliverx.app to javafx.fxml;

    // Si tienes controladores en un paquete controller:
    opens co.edu.uniquindio.poo.deliverx.controller to javafx.fxml;

    // Modelos que son accedidos desde FXML o instanciados por reflexión
    opens co.edu.uniquindio.poo.deliverx.model to javafx.base, javafx.fxml;

    // Si tienes subpaquetes (factory, facade, strategy):
    opens co.edu.uniquindio.poo.deliverx.model.factory to javafx.base, javafx.fxml;
    opens co.edu.uniquindio.poo.deliverx.model.facade to javafx.base, javafx.fxml;
    opens co.edu.uniquindio.poo.deliverx.model.Strategy to javafx.base, javafx.fxml;
}
