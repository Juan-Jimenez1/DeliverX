package co.edu.uniquindio.poo.deliverx.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

    public class Database {
        private static final Logger LOGGER = Logger.getLogger(Database.class.getName());
        private static Database instance = new Database();
        private final List<Admin> admins = new ArrayList();
        private final List<User> users = new ArrayList();
        private final List<DeliveryMan> deliveries = new ArrayList();
        private final List<Shipment> shipments = new ArrayList();
        public static final List<Admin> ADMINS;
        public static final List<User> USERS;
        public static final List<DeliveryMan> DELIVERIES;
        public static final List<Shipment> SHIPMENTS;

        private Database() {
        }

        public static Database getInstance() {
            return instance;
        }

        private static void initializeSampleData() {
            Admin admin1 = ((Admin.Builder)((Admin.Builder)((Admin.Builder)((Admin.Builder)((Admin.Builder)(new Admin.Builder()).userId("admin1")).name("Administrador Principal")).password("admin123")).email("admin@deliverx.com")).phoneNumber("123456789")).build();
            User user1 = (new User.Builder()).userId("cliente1").name("Juan Pérez").password("cliente123").email("juan@email.com").phoneNumber("555-1234").build();
            DeliveryMan delivery1 = ((DeliveryMan.Builder)((DeliveryMan.Builder)((DeliveryMan.Builder)((DeliveryMan.Builder)((DeliveryMan.Builder)(new DeliveryMan.Builder()).userId("repartidor1")).name("Carlos López")).password("delivery123")).email("carlos@deliverx.com")).phoneNumber("555-9012")).state(DeliveryManState.ACTIVE).zonaCobertura("Zona Norte").build();
            instance.addAdmin(admin1);
            instance.addUser(user1);
            instance.addDeliveryMan(delivery1);
            Address dir1 = new Address("dir1", "Calle 123", "Zona Norte", "Ciudad", "12345", "Edificio A");
            Address dir2 = new Address("dir2", "Avenida 456", "Zona Sur", "Ciudad", "67890", "Edificio B");
            Shipment envio1 = new Shipment("ENV001", dir1, dir2, "2.5", user1, "Zona Norte", LocalDate.now());
            envio1.calcularTarifaEnvio();
            instance.addEnvio(envio1);
            LOGGER.info("Datos de ejemplo inicializados");
        }

        public boolean addEnvio(Shipment shipment) {
            if (shipment == null) {
                LOGGER.warning("Intento de agregar envío nulo");
                return false;
            } else if (this.findEnvioById(shipment.getIdShipment()) != null) {
                LOGGER.warning("Ya existe un envío con ID: " + shipment.getIdShipment());
                return false;
            } else {
                this.shipments.add(shipment);
                LOGGER.info("Envío agregado: " + shipment.getIdShipment());
                return true;
            }
        }

        public Shipment findEnvioById(String idEnvio) {
            return (Shipment)this.shipments.stream().filter((e) -> e.getIdShipment().equals(idEnvio)).findFirst().orElse((Object)null);
        }

        public List<Shipment> getShipments() {
            return new ArrayList(this.shipments);
        }

        public List<Shipment> filterEnviosByEstado(ShippingStatus status) {
            return (List)this.shipments.stream().filter((e) -> e.getShippingStatus() == status).collect(Collectors.toList());
        }

        public List<Shipment> filterEnviosByZona(String zona) {
            return (List)this.shipments.stream().filter((e) -> e.getZone() != null && e.getZone().equals(zona)).collect(Collectors.toList());
        }

        public List<Shipment> filterEnviosByFecha(LocalDateTime inicio, LocalDateTime fin) {
            return (List)this.shipments.stream().filter((e) -> e.getFechaCreacion() != null && e.getFechaCreacion().isAfter(inicio) && e.getFechaCreacion().isBefore(fin)).collect(Collectors.toList());
        }

        public List<Shipment> getEnviosByUsuario(String userId) {
            return (List)this.shipments.stream().filter((e) -> e.getUser() != null && e.getUser().getUserId().equals(userId)).collect(Collectors.toList());
        }

        public List<Shipment> getEnviosByRepartidor(String repartidorId) {
            return (List)this.shipments.stream().filter((e) -> e.getDeliveryMan() != null && e.getDeliveryMan().getUserId().equals(repartidorId)).collect(Collectors.toList());
        }

        public boolean removeEnvio(String idShipment) {
            Shipment shipment = this.findEnvioById(idShipment);
            if (shipment != null) {
                this.shipments.remove(shipment);
                LOGGER.info("Envío eliminado: " + idShipment);
                return true;
            } else {
                return false;
            }
        }

        public boolean addAdmin(Admin admin) {
            if (admin == null) {
                LOGGER.warning("Intento de agregar administrador nulo");
                return false;
            } else if (this.findUserById(this.admins, admin.getUserId()) != null) {
                LOGGER.warning("Ya existe un administrador con ID: " + admin.getUserId());
                return false;
            } else {
                this.admins.add(admin);
                LOGGER.info("Administrador agregado: " + admin.getUserId());
                return true;
            }
        }

        public boolean addUser(User user) {
            if (user == null) {
                LOGGER.warning("Intento de agregar usuario nulo");
                return false;
            } else if (this.findUserById(this.users, user.getUserId()) != null) {
                LOGGER.warning("Ya existe un usuario con ID: " + user.getUserId());
                return false;
            } else {
                this.users.add(user);
                LOGGER.info("Usuario agregado: " + user.getUserId());
                return true;
            }
        }

        public boolean addDeliveryMan(DeliveryMan deliveryMan) {
            if (deliveryMan == null) {
                LOGGER.warning("Intento de agregar repartidor nulo");
                return false;
            } else if (this.findUserById(this.deliveries, deliveryMan.getUserId()) != null) {
                LOGGER.warning("Ya existe un repartidor con ID: " + deliveryMan.getUserId());
                return false;
            } else {
                this.deliveries.add(deliveryMan);
                LOGGER.info("Repartidor agregado: " + deliveryMan.getUserId());
                return true;
            }
        }

        public User findUserByCredentials(String userId, String password) {
            User user = this.findUserInList(this.admins, userId, password);
            if (user != null) {
                return user;
            } else {
                user = this.findUserInList(this.users, userId, password);
                if (user != null) {
                    return user;
                } else {
                    user = this.findUserInList(this.deliveries, userId, password);
                    return user;
                }
            }
        }

        public Admin findAdminById(String userId) {
            return (Admin)this.findUserById(this.admins, userId);
        }

        public User findRegularUserById(String userId) {
            return this.findUserById(this.users, userId);
        }

        public DeliveryMan findDeliveryManById(String userId) {
            return (DeliveryMan)this.findUserById(this.deliveries, userId);
        }

        public List<Admin> getAdmins() {
            return new ArrayList(this.admins);
        }

        public List<User> getUsers() {
            return new ArrayList(this.users);
        }

        public List<DeliveryMan> getDeliveryMen() {
            return new ArrayList(this.deliveries);
        }

        private User findUserInList(List<? extends User> userList, String userId, String password) {
            for(User user : userList) {
                if (user.getUserId().equals(userId) && user.getPassword().equals(password)) {
                    return user;
                }
            }

            return null;
        }

        private User findUserById(List<? extends User> userList, String userId) {
            for(User user : userList) {
                if (user.getUserId().equals(userId)) {
                    return user;
                }
            }

            return null;
        }

        public boolean userExists(String userId) {
            return this.findUserById(this.admins, userId) != null || this.findUserById(this.users, userId) != null || this.findUserById(this.deliveries, userId) != null;
        }

        public boolean isAdmin(String userId) {
            return this.findUserById(this.admins, userId) != null;
        }

        public boolean isDeliveryMan(String userId) {
            return this.findUserById(this.deliveries, userId) != null;
        }

        public boolean removeAdmin(String userId) {
            Admin admin = this.findAdminById(userId);
            if (admin != null) {
                this.admins.remove(admin);
                LOGGER.info("Administrador eliminado: " + userId);
                return true;
            } else {
                return false;
            }
        }

        public boolean removeUser(String userId) {
            User user = this.findRegularUserById(userId);
            if (user != null) {
                this.users.remove(user);
                LOGGER.info("Usuario eliminado: " + userId);
                return true;
            } else {
                return false;
            }
        }

        public boolean removeDeliveryMan(String userId) {
            DeliveryMan deliveryMan = this.findDeliveryManById(userId);
            if (deliveryMan != null) {
                this.deliveries.remove(deliveryMan);
                LOGGER.info("Repartidor eliminado: " + userId);
                return true;
            } else {
                return false;
            }
        }

        public int getTotalUsers() {
            return this.admins.size() + this.users.size() + this.deliveries.size();
        }

        public int getAdminCount() {
            return this.admins.size();
        }

        public int getUserCount() {
            return this.users.size();
        }

        public int getDeliveryManCount() {
            return this.deliveries.size();
        }

        public int getEnviosCount() {
            return this.shipments.size();
        }

        public void clearAll() {
            this.admins.clear();
            this.users.clear();
            this.deliveries.clear();
            this.shipments.clear();
            LOGGER.info("Todos los datos han sido limpiados");
        }

        public void resetToSampleData() {
            this.clearAll();
            initializeSampleData();
            LOGGER.info("Datos reinicializados con datos de ejemplo");
        }

        static {
            ADMINS = Collections.unmodifiableList(instance.admins);
            USERS = Collections.unmodifiableList(instance.users);
            DELIVERIES = Collections.unmodifiableList(instance.deliveries);
            SHIPMENTS = Collections.unmodifiableList(instance.shipments);
            initializeSampleData();
        }
    }

}
