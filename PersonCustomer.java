public class PersonCustomer extends Customer {
    private String firstName;
    private String lastName;

    // Constructor with password
    public PersonCustomer(String customerID, String address, String password, String firstName, String lastName) {
        super(customerID, address, password);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Backwards-compatible constructor (no password) — matches your previous usage
    public PersonCustomer(String customerID, String address, String firstName, String lastName) {
        super(customerID, address);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    // optional getters
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
}