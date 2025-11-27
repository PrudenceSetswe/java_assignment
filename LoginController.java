public class LoginController {
    private BankController bankController;
    
    public LoginController() {
        this.bankController = new BankController();
    }
    
    public LoginResult authenticate(String username, String password, String role) {
        if (username == null || username.trim().isEmpty()) {
            return new LoginResult(false, "Username cannot be empty", null, null);
        }
        
        boolean authenticated = false;
        Customer customer = null;
        BankEmployee employee = null;
        
        if ("customer".equalsIgnoreCase(role)) {
            authenticated = bankController.authenticateCustomer(username, password);
            if (authenticated) {
                customer = bankController.getCurrentCustomer();
            }
        } else if ("employee".equalsIgnoreCase(role)) {
            authenticated = bankController.authenticateEmployee(username, password);
            if (authenticated) {
                employee = bankController.getCurrentEmployee();
            }
        } else {
            return new LoginResult(false, "Invalid role selected", null, null);
        }
        
        if (authenticated) {
            String message = "Welcome " + username + "!";
            return new LoginResult(true, message, customer, employee);
        } else {
            return new LoginResult(false, "Invalid credentials. Please check your username, password, and role.", null, null);
        }
    }
    
    public BankController getBankController() {
        return bankController;
    }
}

class LoginResult {
    private boolean success;
    private String message;
    private Customer customer;
    private BankEmployee employee;
    
    public LoginResult(boolean success, String message, Customer customer, BankEmployee employee) {
        this.success = success;
        this.message = message;
        this.customer = customer;
        this.employee = employee;
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Customer getCustomer() { return customer; }
    public BankEmployee getEmployee() { return employee; }
}