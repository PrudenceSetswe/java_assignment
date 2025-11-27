import java.util.List;

public class AccountController {
    private Customer currentCustomer;
    
    public AccountController(Customer customer) {
        this.currentCustomer = customer;
    }
    
    // Account operations
    public TransactionResult deposit(String accountNumber, double amount) {
        if (amount <= 0) {
            return new TransactionResult(false, "Deposit amount must be positive");
        }
        
        Account account = currentCustomer.getAccountByNumber(accountNumber);
        if (account == null) {
            return new TransactionResult(false, "Account not found: " + accountNumber);
        }
        
        try {
            double oldBalance = account.getBalance();
            account.deposit(amount);
            double newBalance = account.getBalance();
            
            String message = String.format("Deposited P%.2f successfully. New balance: P%.2f", amount, newBalance);
            return new TransactionResult(true, message, oldBalance, newBalance);
        } catch (Exception e) {
            return new TransactionResult(false, "Deposit failed: " + e.getMessage());
        }
    }
    
    public TransactionResult withdraw(String accountNumber, double amount) {
        if (amount <= 0) {
            return new TransactionResult(false, "Withdrawal amount must be positive");
        }
        
        Account account = currentCustomer.getAccountByNumber(accountNumber);
        if (account == null) {
            return new TransactionResult(false, "Account not found: " + accountNumber);
        }
        
        // Business logic: Savings accounts cannot withdraw
        if (account instanceof SavingsAccount) {
            return new TransactionResult(false, "Withdrawals are not allowed from Savings accounts");
        }
        
        try {
            double oldBalance = account.getBalance();
            account.withdraw(amount);
            double newBalance = account.getBalance();
            
            String message = String.format("Withdrew P%.2f successfully. New balance: P%.2f", amount, newBalance);
            return new TransactionResult(true, message, oldBalance, newBalance);
        } catch (Exception e) {
            return new TransactionResult(false, "Withdrawal failed: " + e.getMessage());
        }
    }
    
    public InterestResult applyInterest(String accountNumber) {
        Account account = currentCustomer.getAccountByNumber(accountNumber);
        if (account == null) {
            return new InterestResult(false, "Account not found: " + accountNumber, 0);
        }
        
        if (account instanceof SavingsAccount) {
            SavingsAccount savings = (SavingsAccount) account;
            double interest = savings.calculateInterest();
            savings.applyMonthlyInterest();
            return new InterestResult(true, 
                String.format("0.05%% interest applied: P%.4f", interest), 
                interest);
                
        } else if (account instanceof InvestmentAccount) {
            InvestmentAccount investment = (InvestmentAccount) account;
            double interest = investment.calculateInterest();
            investment.applyMonthlyInterest();
            return new InterestResult(true, 
                String.format("5%% interest applied: P%.2f", interest), 
                interest);
                
        } else {
            return new InterestResult(false, "This account type does not earn interest", 0);
        }
    }
    
    // Account information
    public List<Account> getCustomerAccounts() {
        return currentCustomer.getAccounts();
    }
    
    public Account getAccountByNumber(String accountNumber) {
        return currentCustomer.getAccountByNumber(accountNumber);
    }
    
    public String getAccountSummary() {
        if (currentCustomer.getAccounts().isEmpty()) {
            return "No accounts available.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== ACCOUNT SUMMARY ===\n\n");
        
        double totalBalance = 0;
        for (Account account : currentCustomer.getAccounts()) {
            String type = account.getClass().getSimpleName();
            double balance = account.getBalance();
            totalBalance += balance;
            
            sb.append("Account: ").append(account.getAccountNumber()).append("\n");
            sb.append("Type: ").append(type).append("\n");
            sb.append("Balance: P").append(String.format("%.2f", balance)).append("\n");
            
            if (account instanceof SavingsAccount) {
                sb.append("Interest: 0.05% monthly | No withdrawals\n");
            } else if (account instanceof InvestmentAccount) {
                sb.append("Interest: 5% monthly | Withdrawals allowed\n");
            } else if (account instanceof ChequeAccount) {
                sb.append("Interest: None | Withdrawals allowed\n");
            }
            sb.append("--------------------\n");
        }
        
        sb.append("\nTOTAL BALANCE: P").append(String.format("%.2f", totalBalance));
        return sb.toString();
    }
}

class TransactionResult {
    private boolean success;
    private String message;
    private double oldBalance;
    private double newBalance;
    
    public TransactionResult(boolean success, String message) {
        this(success, message, 0, 0);
    }
    
    public TransactionResult(boolean success, String message, double oldBalance, double newBalance) {
        this.success = success;
        this.message = message;
        this.oldBalance = oldBalance;
        this.newBalance = newBalance;
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public double getOldBalance() { return oldBalance; }
    public double getNewBalance() { return newBalance; }
}

class InterestResult {
    private boolean success;
    private String message;
    private double interestAmount;
    
    public InterestResult(boolean success, String message, double interestAmount) {
        this.success = success;
        this.message = message;
        this.interestAmount = interestAmount;
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public double getInterestAmount() { return interestAmount; }
}