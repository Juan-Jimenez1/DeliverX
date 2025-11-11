package co.edu.uniquindio.poo.deliverx.Controller;


import co.edu.uniquindio.poo.deliverx.model.Admin;
import co.edu.uniquindio.poo.deliverx.model.Database;
import co.edu.uniquindio.poo.deliverx.model.DeliveryMan;
import co.edu.uniquindio.poo.deliverx.model.User;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

    public class HomeController implements Initializable {
        @FXML
        private Button loginId;
        @FXML
        private TextField nameId;
        @FXML
        private TextField passwordId;

        public HomeController() {
        }

        public void initialize(URL url, ResourceBundle rb) {
            this.configurarEventos();
        }

        @FXML
        private void registerId() throws IOException {
            try {
                FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/co/edu/uniquindio/poo/deliverx/view/RegisterUser.fxml"));
                Parent root = (Parent)loader.load();
                Stage stage = (Stage)this.loginId.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("DeliverX - Registro de Usuario");
                stage.centerOnScreen();
            } catch (IOException e) {
                this.mostrarError("Error al cargar la pantalla de registro: " + e.getMessage());
                e.printStackTrace();
            }

        }

        private void configurarEventos() {
            this.loginId.setOnAction((event) -> this.validarLogin());
            this.nameId.setOnAction((event) -> this.validarLogin());
            this.passwordId.setOnAction((event) -> this.validarLogin());
        }

        @FXML
        private void validarLogin() {
            String username = this.nameId.getText().trim();
            String password = this.passwordId.getText().trim();
            if (!username.isEmpty() && !password.isEmpty()) {
                Object usuarioEncontrado = this.buscarUsuarioEnTodasLasListas(username, password);
                if (usuarioEncontrado != null) {
                    String tipoUsuario = this.determinarTipoUsuario(usuarioEncontrado);
                    String nombreUsuario = this.obtenerNombreUsuario(usuarioEncontrado);
                    this.mostrarAlerta(AlertType.INFORMATION, "Login exitoso", "Bienvenido " + nombreUsuario + " (" + tipoUsuario + ")");
                    this.redirigirSegunTipo(usuarioEncontrado, tipoUsuario);
                } else {
                    this.mostrarAlerta(AlertType.ERROR, "Login fallido", "Credenciales incorrectas.");
                }

            } else {
                this.mostrarAlerta(AlertType.WARNING, "Campos incompletos", "Por favor, complete todos los campos.");
            }
        }

        private Object buscarUsuarioEnTodasLasListas(String username, String password) {
            for(Admin admin : Database.ADMINS) {
                if (admin.getUserId().equals(username) && admin.getPassword().equals(password)) {
                    return admin;
                }
            }

            for(User user : Database.USERS) {
                if (user.getUserId().equals(username) && user.getPassword().equals(password)) {
                    return user;
                }
            }

            for(DeliveryMan delivery : Database.DELIVERIES) {
                if (delivery.getUserId().equals(username) && delivery.getPassword().equals(password)) {
                    return delivery;
                }
            }

            return null;
        }

        private String determinarTipoUsuario(Object usuario) {
            if (usuario instanceof Admin) {
                return "Administrador";
            } else if (usuario instanceof User) {
                return "Cliente";
            } else {
                return usuario instanceof DeliveryMan ? "Repartidor" : "Usuario";
            }
        }

        private String obtenerNombreUsuario(Object usuario) {
            if (usuario instanceof Admin) {
                return ((Admin)usuario).getName();
            } else if (usuario instanceof User) {
                return ((User)usuario).getName();
            } else {
                return usuario instanceof DeliveryMan ? ((DeliveryMan)usuario).getName() : "Usuario";
            }
        }

        private void redirigirSegunTipo(Object usuario, String tipoUsuario) {
            try {
                Stage stage = (Stage)this.loginId.getScene().getWindow();
                String rutaFXML = "";
                switch (tipoUsuario) {
                    case "Administrador":
                        rutaFXML = "/co/edu/uniquindio/poo/deliverx/view/Admin.fxml";
                        break;
                    case "Cliente":
                        rutaFXML = "/co/edu/uniquindio/poo/deliverx/User.fxml";
                        break;
                    case "Repartidor":
                        rutaFXML = "/co/edu/uniquindio/poo/deliverx/Diverly.fxml";
                        break;
                    default:
                        this.mostrarAlerta(AlertType.ERROR, "Error", "Tipo de usuario no válido");
                        return;
                }

                FXMLLoader loader = new FXMLLoader(this.getClass().getResource(rutaFXML));
                Parent root = (Parent)loader.load();
                this.pasarDatosUsuario(loader.getController(), usuario, tipoUsuario);
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("DeliverX - " + tipoUsuario);
                stage.centerOnScreen();
            } catch (IOException e) {
                this.mostrarError("Error al cargar la interfaz: " + e.getMessage());
                e.printStackTrace();
            }

            this.limpiarCampos();
        }

        private void pasarDatosUsuario(Object controlador, Object usuario, String tipoUsuario) {
        }

        private void limpiarCampos() {
            this.nameId.clear();
            this.passwordId.clear();
        }

        private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
            Alert alerta = new Alert(tipo);
            alerta.setTitle(titulo);
            alerta.setHeaderText((String)null);
            alerta.setContentText(mensaje);
            alerta.showAndWait();
        }

        private void mostrarError(String mensaje) {
            Alert alerta = new Alert(AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText((String)null);
            alerta.setContentText(mensaje);
            alerta.showAndWait();
        }
    }

}
