public class InvestmentAccount extends Account implements InterestBearing {
    private double interestRate;

    public InvestmentAccount(String accountNumber, String branch, double interestRate, double initialDeposit) {
        super(accountNumber, branch);
        if (initialDeposit < 500.00) {
            throw new IllegalArgumentException("Investment account requires minimum initial deposit of BWP500.00");
        }
        this.interestRate = interestRate;
        this.balance = initialDeposit;
        addHistory("Account opened with initial deposit: " + initialDeposit);
    }

    public InvestmentAccount(String accountNumber, String branch, double initialDeposit) {
        this(accountNumber, branch, 0.05, initialDeposit); // 5% monthly = 0.05
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

    public void applyMonthlyInterest() {
        double interest = calculateInterest();
        if (interest > 0) {
            deposit(interest);
            addHistory("Monthly interest applied: " + interest);
        }
    }
}
