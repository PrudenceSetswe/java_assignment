import java.util.ArrayList;
import java.util.List;

public abstract class Account {
    private final String accountNumber;
    protected double balance;
    private final String branch;
    private final List<String> history = new ArrayList<>();

    public Account(String accountNumber, String branch) {
        this.accountNumber = accountNumber;
        this.branch = branch;
        this.balance = 0.0;
        addHistory("Account created: " + accountNumber);
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            addHistory("Deposit: " + amount + " | New balance: " + balance);
        } else {
            addHistory("Failed deposit attempt: " + amount);
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            addHistory("Withdraw: " + amount + " | Remaining balance: " + balance);
        } else {
            addHistory("Failed withdrawal attempt: " + amount);
        }
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getBranch() {
        return branch;
    }

    // CHANGE: Make this protected so subclasses can use it
    protected void addHistory(String event) {
        history.add(event);
    }

    public List<String> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public String toString() {
        return accountNumber + " (" + branch + ")";
    }
}
