package co.edu.uniquindio.poo.deliverx.model;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeliverX {
    private String name;
    private String nit;
    private List<Customer> listCustomers;
    private List<DeliveryMan> listDeliveryMans;
    private List<Admin> listAdmins;
    private List<Shipment> listShipments;
    private List<User> listUsers;
    private static DeliverX instance;
    private User userLoged;


    private DeliverX() {
        this.name ="DeliverX" ;
        this.nit = "1139322-1";
        this.listCustomers = new ArrayList<>();
        this.listDeliveryMans = new ArrayList<>();
        this.listAdmins = new ArrayList<>();
        this.listShipments = new ArrayList<>();
        this.listUsers = new ArrayList<>();
        this.userLoged = null;
    }

    public static DeliverX getInstance() {
        if(instance == null) {
            instance = new DeliverX();
        }
        return instance;
    }
    public void updateUsersList() {
        listUsers.clear();
        if (listCustomers != null) {
            listUsers.addAll(listCustomers);
        }
        if (listDeliveryMans != null) {
            listUsers.addAll(listDeliveryMans);
        }
        if (listAdmins != null) {
            listUsers.addAll(listAdmins);
        }
    }


    public boolean userExists(String userId) {
        return listUsers.stream().anyMatch(user -> user.getUserId().equals(userId));
    }

    public void registerCustomer(Customer customer) {
        if(!userExists(customer.getUserId())) {
            listCustomers.add(customer);
            updateUsersList();
        }else{
            JOptionPane.showMessageDialog(null, "The user already exists");
        }
    }

    public void deleteCustomer(String customerId) {
        for (int i = 0; i < listCustomers.size(); i++) {
            if (listCustomers.get(i).getUserId().equalsIgnoreCase(customerId)) {
                listCustomers.remove(i);
                updateUsersList();
                break;
            }
        }
    }

    public boolean updateCustomer(String customerIdToUpdate, Customer customer) {
        for (int i = 0; i < listCustomers.size(); i++) {
            if (listCustomers.get(i).getUserId().equalsIgnoreCase(customerIdToUpdate)) {
                listCustomers.set(i, customer);
                updateUsersList();
                return true;
            }
        }
        return false;
    }

    public Shipment getShipment (String shipmentID){
        return listShipments.stream().filter(shipment -> shipment.getIdShipment().equals(shipmentID)).findFirst().orElse(null);
    }
//Juan revisa este método
    public List<Shipment> getShipmentsByCustomer(String customerId){
        Customer found= null;
        for (Customer customer : listCustomers) {
            if (customer.getUserId().equals(customerId)) {
                found= customer;
                break;
            }
        }
       if(found!=null){
            return found.getShipmentList();
        }else{
            return new ArrayList<>();
        }
    }

    public List<Shipment> getShipmentsByState(String stateName) {
        return listShipments.stream()
                .filter(s -> s.getCurrentState().getStateName().equalsIgnoreCase(stateName))
                .collect(Collectors.toList());
    }

    public List<Shipment> getShipmentsByMonth(int year, int month) {
        return listShipments.stream()
                .filter(s -> s.getDateTime().getYear() == year &&
                        s.getDateTime().getMonthValue() == month)
                .collect(Collectors.toList());
    }

    public boolean updateShipment(String shipmentId, Shipment updatedShipment) {
        for (int i = 0; i < listShipments.size(); i++) {
            if (listShipments.get(i).getIdShipment().equals(shipmentId)) {
                listShipments.set(i, updatedShipment);
                System.out.println(" Updated shipment " + shipmentId);
                return true;
            }
        }
        System.out.println(" The shipment could not be updated: " + shipmentId);
        return false;
    }

    public boolean deleteShipment(String shipmentId) {
        Shipment shipment = getShipment(shipmentId);
        if (shipment == null) {
            System.out.println(" Shipment not found: " + shipmentId);
            return false;
        }

        // Solo se pueden eliminar envíos solicitados o cancelados
        String state = shipment.getCurrentState().getStateName();
        if (!state.equals("REQUESTED") && !state.equals("CANCELLED")) {
            System.out.println(" Can not be deleted: The shipment is on " + state +"state");
            return false;
        }

        listShipments.remove(shipment);
        if (shipment.getCustomer() != null) {
            shipment.getCustomer().getShipmentList().remove(shipment);
        }

        System.out.println(" Deleted shipment: " + shipmentId);
        return true;
    }

    public Address createAddress(String addressId, String street, String city,
                                String type, double latitude, double longitude) {
        Address address = new Address(addressId, street, city, type, latitude,longitude);
        System.out.println(" Address Created: " + addressId);
        return address;
    }

    public boolean addAddressToCustomer(Customer customer, Address address) {
        if (customer == null || address == null) {
            System.out.println(" Client or address are null");
            return false;
        }

        boolean exists = customer.getListAddresses().stream()
                .anyMatch(a -> a.getAddressId().equals(address.getAddressId()));

        if (exists) {
            System.out.println(" The address already exists for this client");
            return false;
        }

        customer.getListAddresses().add(address);
        System.out.println(" Address added to client: " + customer.getName());
        return true;
    }

    public Address getAddressFromCustomer(Customer customer, String addressId) {
        if (customer == null) {
            return null;
        }
        return customer.getListAddresses().stream()
                .filter(a -> a.getAddressId().equals(addressId))
                .findFirst()
                .orElse(null);
    }

    public List<Address> getCustomerAddresses(Customer customer) {
        return customer != null ? new ArrayList<>(customer.getListAddresses()) : new ArrayList<>();
    }

    public boolean updateAddressFromCustomer(Customer customer, String addressId, Address updatedAddress) {
        if (customer == null) {
            System.out.println(" Client not found");
            return false;
        }

        for (int i = 0; i < customer.getListAddresses().size(); i++) {
            if (customer.getListAddresses().get(i).getAddressId().equals(addressId)) {
                customer.getListAddresses().set(i, updatedAddress);
                System.out.println(" Updated address: " + addressId);
                return true;
            }
        }
        System.out.println(" Address not found: " + addressId);
        return false;
    }

    public boolean deleteAddressFromCustomer(Customer customer, String addressId) {
        if (customer == null) {
            System.out.println(" Client not found");
            return false;
        }

        Address address = getAddressFromCustomer(customer, addressId);
        if (address == null) {
            System.out.println(" Address not found: " + addressId);
            return false;
        }

        customer.getListAddresses().remove(address);
        System.out.println(" Address deleted: " + addressId);
        return true;
    }

    public void registerDeliveryMan(DeliveryMan deliveryMan) {
        if(!userExists(deliveryMan.getUserId())) {
            listDeliveryMans.add(deliveryMan);
            updateUsersList();
        }else{
            JOptionPane.showMessageDialog(null, "The user already exists");
        }
    }
    public void deleteDeliveryMan(String deliveryManId) {
        for (int i = 0; i < listDeliveryMans.size(); i++) {
            if (listDeliveryMans.get(i).getUserId().equalsIgnoreCase(deliveryManId)) {
                listDeliveryMans.remove(i);
                updateUsersList();
                break;
            }
        }
    }
    public boolean updateDeliveryMan(String deliveryManIdToUpdate, DeliveryMan deliveryMan) {
        for (int i = 0; i < listDeliveryMans.size(); i++) {
            if (listDeliveryMans.get(i).getUserId().equalsIgnoreCase(deliveryManIdToUpdate)) {
                listDeliveryMans.set(i, deliveryMan);
                updateUsersList();
                return true;
            }
        }
        return false;
    }
    public void registerAdmin(Admin admin) {
        if(!userExists(admin.getUserId())) {
            listAdmins.add(admin);
            updateUsersList();
        }else{
            JOptionPane.showMessageDialog(null, "The user already exists");
        }
    }
    public void deleteAdmin(String adminId) {
        for (int i = 0; i < listAdmins.size(); i++) {
            if (listAdmins.get(i).getUserId().equalsIgnoreCase(adminId)) {
                listAdmins.remove(i);
                updateUsersList();
                break;
            }
        }
    }
    public boolean updateAdmin(String adminIdToUpdate, Admin admin) {
        for (int i = 0; i < listAdmins.size(); i++) {
            if (listAdmins.get(i).getUserId().equalsIgnoreCase(adminIdToUpdate)) {
                listAdmins.set(i, admin);
                updateUsersList();
                return true;
            }
        }
        return false;
    }
    public boolean loginUser(String userId, String password) {
        if (userExists(userId)) {
            for (User user : listUsers) {
                if (user.getUserId().equalsIgnoreCase(userId) && user.getPassword().equalsIgnoreCase(password)) {
                    userLoged = user;
                    JOptionPane.showMessageDialog(null, "You have logged in successfully");
                    return true;
                }
            }
            JOptionPane.showMessageDialog(null, "The password is incorrect", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "The ID is incorrect or the user does not exist", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public Pay processPayment(String paymentId, double amount, PaymentMethod method) {
        System.out.println(" Processing payment via " + method + ": $" + amount);

        TransactionResult result;

        if (method == PaymentMethod.CASH) {
            // Efectivo requiere confirmación física
            result = TransactionResult.PENDING;
            System.out.println(" Cash payment registered - Pending confirmation");
        } else {
            // Tarjetas de crédito/débito se aprueban inmediatamente
            result = TransactionResult.APPROVED;
            System.out.println( method + " payment approved");
        }

        // Crear el pago con todos los parámetros requeridos
        Pay payment = new Pay(
                paymentId,
                method,
                amount,
                LocalDate.now(),
                result
        );

        return payment;
    }



    


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public List<Customer> getListCustomers() {
        return listCustomers;
    }

    public void setListCustomers(List<Customer> listCustomers) {
        this.listCustomers = listCustomers;
    }

    public List<DeliveryMan> getListDeliveryMans() {
        return listDeliveryMans;
    }

    public void setListDeliveryMans(List<DeliveryMan> listDeliveryMans) {
        this.listDeliveryMans = listDeliveryMans;
    }

    public List<Admin> getListAdmins() {
        return listAdmins;
    }

    public void setListAdmins(List<Admin> listAdmins) {
        this.listAdmins = listAdmins;
    }

    public List<Shipment> getListShipments() {
        return listShipments;
    }

    public void setListShipments(List<Shipment> listShipments) {
        this.listShipments = listShipments;
    }

    public User getUserLoged() {
        return userLoged;
    }

    public void setUserLoged(User userLoged) {
        this.userLoged = userLoged;
    }

    public List<User> getListUsers() {
        return listUsers;
    }

    public void setListUsers(List<User> listUsers) {
        this.listUsers = listUsers;
    }
}


