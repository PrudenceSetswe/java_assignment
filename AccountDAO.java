import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    
    public boolean saveAccount(Account account, String customerId) {
        String sql = "INSERT OR REPLACE INTO accounts (account_number, customer_id, account_type, balance, branch, interest_rate, overdraft_limit, employer, company_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, account.getAccountNumber());
            pstmt.setString(2, customerId);
            pstmt.setString(3, getAccountType(account));
            pstmt.setDouble(4, account.getBalance());
            pstmt.setString(5, account.getBranch());
            
            if (account instanceof SavingsAccount) {
                pstmt.setDouble(6, ((SavingsAccount) account).getInterestRate());
                pstmt.setNull(7, java.sql.Types.DECIMAL);
                pstmt.setNull(8, java.sql.Types.VARCHAR);
                pstmt.setNull(9, java.sql.Types.VARCHAR);
            } else if (account instanceof InvestmentAccount) {
                pstmt.setDouble(6, ((InvestmentAccount) account).getInterestRate());
                pstmt.setNull(7, java.sql.Types.DECIMAL);
                pstmt.setNull(8, java.sql.Types.VARCHAR);
                pstmt.setNull(9, java.sql.Types.VARCHAR);
            } else if (account instanceof ChequeAccount) {
                pstmt.setDouble(6, 0.0);
                pstmt.setDouble(7, ((ChequeAccount) account).getOverdraftLimit());
                pstmt.setString(8, ((ChequeAccount) account).getEmployer());
                pstmt.setString(9, ((ChequeAccount) account).getCompanyAddress());
            } else {
                pstmt.setDouble(6, 0.0);
                pstmt.setNull(7, java.sql.Types.DECIMAL);
                pstmt.setNull(8, java.sql.Types.VARCHAR);
                pstmt.setNull(9, java.sql.Types.VARCHAR);
            }
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Error saving account: " + e.getMessage());
            return false;
        }
    }
    
    public Account findAccountByNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return createAccountFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.out.println("Error finding account: " + e.getMessage());
        }
        return null;
    }
    
    public List<Account> findAccountsByCustomer(String customerId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Account account = createAccountFromResultSet(rs);
                if (account != null) {
                    accounts.add(account);
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Error finding customer accounts: " + e.getMessage());
        }
        return accounts;
    }
    
    public boolean updateAccountBalance(String accountNumber, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, accountNumber);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Error updating account balance: " + e.getMessage());
            return false;
        }
    }
    
    private Account createAccountFromResultSet(ResultSet rs) throws SQLException {
        String accountNumber = rs.getString("account_number");
        String accountType = rs.getString("account_type");
        double balance = rs.getDouble("balance");
        String branch = rs.getString("branch");
        
        switch (accountType) {
            case "SAVINGS":
                SavingsAccount savings = new SavingsAccount(accountNumber, branch);
                savings.deposit(balance);
                return savings;
                
            case "INVESTMENT":
                InvestmentAccount investment = new InvestmentAccount(accountNumber, branch, balance);
                return investment;
                
            case "CHEQUE":
                String employer = rs.getString("employer");
                String companyAddress = rs.getString("company_address");
                double overdraftLimit = rs.getDouble("overdraft_limit");
                ChequeAccount cheque = new ChequeAccount(accountNumber, branch, overdraftLimit, employer, companyAddress);
                cheque.deposit(balance);
                return cheque;
                
            default:
                return null;
        }
    }
    
    private String getAccountType(Account account) {
        if (account instanceof SavingsAccount) return "SAVINGS";
        if (account instanceof InvestmentAccount) return "INVESTMENT";
        if (account instanceof ChequeAccount) return "CHEQUE";
        return "UNKNOWN";
    }
}