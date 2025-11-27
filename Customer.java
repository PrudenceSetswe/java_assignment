import java.util.ArrayList;
import java.util.List;

public abstract class Customer {
    private final String customerID;
    private String address;
    private String password;               // for login
    private final List<Account> accounts;
    private String status = "Pending";     // ADDED: Status field

    // Primary constructor (includes password)
    public Customer(String customerID, String address, String password) {
        this.customerID = customerID;
        this.address = address;
        this.password = password;
        this.accounts = new ArrayList<>();
    }

    // Backwards-compatible constructor (no password) - sets empty password
    public Customer(String customerID, String address) {
        this(customerID, address, "");
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // ADDED: Status methods
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Password methods for login
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Account management
    public void addAccount(Account account) {
        accounts.add(account);
        System.out.println("Account " + account.getAccountNumber() + " added for " + customerID);
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    // Find account by account number (returns null if not found)
    public Account getAccountByNumber(String accountNumber) {
        if (accountNumber == null) return null;
        for (Account a : accounts) {
            if (accountNumber.equals(a.getAccountNumber())) return a;
        }
        return null;
    }

    // Return a readable list of accounts (one-per-line)
    public String getAccountListString() {
        if (accounts.isEmpty()) return "No accounts yet.";
        StringBuilder sb = new StringBuilder();
        for (Account a : accounts) {
            sb.append(a.getAccountNumber())
              .append(" | ")
              .append(a.getClass().getSimpleName())
              .append(" | Balance: ")
              .append(a.getBalance())
              .append("\n");
        }
        return sb.toString();
    }
}
