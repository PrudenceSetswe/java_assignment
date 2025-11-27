import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    
    public boolean saveTransaction(String accountNumber, String type, double amount, String description) {
        String sql = "INSERT INTO transactions (account_number, transaction_type, amount, description) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, type);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, description);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
            return false;
        }
    }
    
    public List<String> getTransactionHistory(String accountNumber) {
        List<String> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY transaction_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String type = rs.getString("transaction_type");
                double amount = rs.getDouble("amount");
                String date = rs.getString("transaction_date");
                String description = rs.getString("description");
                
                String transaction = String.format("%s: %s - P%.2f on %s", 
                    type, description, amount, date);
                transactions.add(transaction);
            }
            
        } catch (SQLException e) {
            System.out.println("Error getting transaction history: " + e.getMessage());
        }
        return transactions;
    }
}