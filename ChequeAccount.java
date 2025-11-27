public class ChequeAccount extends Account {
    private double overdraftLimit;
    private String employer;
    private String companyAddress;

    public ChequeAccount(String accountNumber, String branch, double overdraftLimit, String employer, String companyAddress) {
        super(accountNumber, branch);
        if (employer == null || employer.trim().isEmpty()) {
            throw new IllegalArgumentException("Cheque account requires employment information");
        }
        this.overdraftLimit = overdraftLimit;
        this.employer = employer;
        this.companyAddress = companyAddress;
        addHistory("Cheque account created for employee of: " + employer);
    }

    public ChequeAccount(String accountNumber, String branch, String employer, String companyAddress) {
        this(accountNumber, branch, 500.00, employer, companyAddress);
    }

    @Override
    public void withdraw(double amount) {
        if (amount > 0 && (balance + overdraftLimit) >= amount) {
            balance -= amount;
            addHistory("Withdraw: " + amount + " | Remaining balance: " + balance);
        } else {
            addHistory("Withdrawal denied: " + amount + " - Exceeds overdraft limit or invalid amount");
            System.out.println("Withdrawal denied. Exceeds overdraft limit or invalid amount.");
        }
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }

    public String getEmployer() {
        return employer;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    @Override
    public String toString() {
        return super.toString() + " [Employer: " + employer + "]";
    }
}