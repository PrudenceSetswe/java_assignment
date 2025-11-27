public class TestDatabase {
    public static void main(String[] args) {
        System.out.println("Testing Database...");
        
        // Initialize database
        DatabaseConnection.initializeDatabase();
        
        // Test CustomerDAO
        CustomerDAO customerDAO = new CustomerDAO();
        Customer customer = customerDAO.findCustomerById("C001");
        
        if (customer != null) {
            System.out.println("Customer found in database: " + 
                (customer instanceof PersonCustomer ? 
                 ((PersonCustomer) customer).getFullName() : 
                 ((CompanyCustomer) customer).getCompanyName()));
        } else {
            System.out.println("Customer NOT found in database");
        }
        
        // Test AccountDAO - without using List
        AccountDAO accountDAO = new AccountDAO();
        java.util.List<Account> accounts = accountDAO.findAccountsByCustomer("C001");
        System.out.println("Customer has " + accounts.size() + " accounts in database");
        
        // Check if database file was created
        java.io.File dbFile = new java.io.File("banking_system.db");
        if (dbFile.exists()) {
            System.out.println("Database file created: " + dbFile.getAbsolutePath());
        } else {
            System.out.println(" Database file NOT found");
        }
    }
}