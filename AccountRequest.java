public class AccountRequest {

    private Customer customer;   
    private String accountType;  
    private boolean approved = false;

    public AccountRequest(Customer customer, String accountType) {
        this.customer = customer;
        this.accountType = accountType;
    }

    public Customer getCustomer() { return customer; }
    public String getAccountType() { return accountType; }
    public boolean isApproved() { return approved; }
    public void approve() { approved = true; }
}

