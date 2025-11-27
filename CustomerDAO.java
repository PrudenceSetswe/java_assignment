import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    
    public boolean saveCustomer(Customer customer) {
        String sql = "INSERT OR REPLACE INTO customers (customer_id, first_name, last_name, company_name, address, password, customer_type, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customer.getCustomerID());
            pstmt.setString(2, customer instanceof PersonCustomer ? ((PersonCustomer) customer).getFirstName() : null);
            pstmt.setString(3, customer instanceof PersonCustomer ? ((PersonCustomer) customer).getLastName() : null);
            pstmt.setString(4, customer instanceof CompanyCustomer ? ((CompanyCustomer) customer).getCompanyName() : null);
            pstmt.setString(5, customer.getAddress());
            pstmt.setString(6, customer.getPassword());
            pstmt.setString(7, customer instanceof PersonCustomer ? "PERSON" : "COMPANY");
            pstmt.setString(8, customer.getStatus());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Error saving customer: " + e.getMessage());
            return false;
        }
    }
    
    public Customer findCustomerById(String customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String customerType = rs.getString("customer_type");
                String address = rs.getString("address");
                String password = rs.getString("password");
                String status = rs.getString("status");
                
                if ("PERSON".equals(customerType)) {
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    PersonCustomer customer = new PersonCustomer(customerId, address, password, firstName, lastName);
                    customer.setStatus(status);
                    return customer;
                } else {
                    String companyName = rs.getString("company_name");
                    CompanyCustomer customer = new CompanyCustomer(customerId, address, password, companyName);
                    customer.setStatus(status);
                    return customer;
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error finding customer: " + e.getMessage());
        }
        return null;
    }
    
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String customerId = rs.getString("customer_id");
                String customerType = rs.getString("customer_type");
                String address = rs.getString("address");
                String password = rs.getString("password");
                String status = rs.getString("status");
                
                Customer customer;
                if ("PERSON".equals(customerType)) {
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    customer = new PersonCustomer(customerId, address, password, firstName, lastName);
                } else {
                    String companyName = rs.getString("company_name");
                    customer = new CompanyCustomer(customerId, address, password, companyName);
                }
                customer.setStatus(status);
                customers.add(customer);
            }
            
        } catch (SQLException e) {
            System.out.println("Error getting all customers: " + e.getMessage());
        }
        return customers;
    }
    
    public boolean updateCustomerStatus(String customerId, String status) {
        String sql = "UPDATE customers SET status = ? WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setString(2, customerId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Error updating customer status: " + e.getMessage());
            return false;
        }
    }
}