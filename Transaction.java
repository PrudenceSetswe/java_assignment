import java.time.LocalDateTime;

public class Transaction {
    private final String transactionID;
    private final LocalDateTime date;
    private final String type;     
    private final double amount;

    public Transaction(String transactionID, String type, double amount) {
        this.transactionID = transactionID;
        this.type = type;
        this.amount = amount;
        this.date = LocalDateTime.now(); 
    }

    public String getTransactionID() {
        return transactionID;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public void record() {
        System.out.println("Transaction recorded: " +
                "ID=" + transactionID +
                ", Type=" + type +
                ", Amount=" + amount +
                ", Date=" + date);
    }
}