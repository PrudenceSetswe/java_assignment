public class CompanyCustomer extends Customer {
    private String companyName;

    public CompanyCustomer(String customerID, String address, String password, String companyName) {
        super(customerID, address, password);
        this.companyName = companyName;
    }

    public CompanyCustomer(String customerID, String address, String companyName) {
        super(customerID, address);
        this.companyName = companyName;
    }

    public String getCompanyName() {
        return companyName;
    }
}