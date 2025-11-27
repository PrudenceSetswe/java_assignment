public class SavingsAccount extends Account implements InterestBearing {
    private double interestRate;

    // Constructor with custom interest rate
    public SavingsAccount(String accountNumber, String branch, double interestRate) {
        super(accountNumber, branch);
        this.interestRate = interestRate;
    }

    // Constructor with default interest rate (0.05% monthly as per requirements)
    public SavingsAccount(String accountNumber, String branch) {
        this(accountNumber, branch, 0.0005); // 0.05% monthly = 0.0005
    }

    // Override withdraw to prevent withdrawals from savings account
    @Override
    public void withdraw(double amount) {
        addHistory("Withdrawal attempted but denied: Savings account does not allow withdrawals");
        throw new UnsupportedOperationException("Withdrawals not allowed from Savings Account");
    }

    @Override
    public double calculateInterest() {
        return getBalance() * interestRate;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    // Method to apply monthly interest
    public void applyMonthlyInterest() {
        double interest = calculateInterest();
        if (interest > 0) {
            deposit(interest);
            addHistory("Monthly interest applied: " + interest);
        }
    }
}
