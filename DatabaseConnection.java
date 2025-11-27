import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:banking_system.db";
    private static Connection connection = null;
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL);
                System.out.println("SQLite database connection established.");
            }
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
        return connection;
    }
    
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create tables
            String schemaSQL = 
                "CREATE TABLE IF NOT EXISTS customers (" +
                "    customer_id VARCHAR(20) PRIMARY KEY," +
                "    first_name VARCHAR(50)," +
                "    last_name VARCHAR(50)," +
                "    company_name VARCHAR(100)," +
                "    address VARCHAR(200)," +
                "    password VARCHAR(100)," +
                "    customer_type VARCHAR(10)," +
                "    status VARCHAR(20) DEFAULT 'PENDING'" +
                ");" +
                
                "CREATE TABLE IF NOT EXISTS accounts (" +
                "    account_number VARCHAR(50) PRIMARY KEY," +
                "    customer_id VARCHAR(20)," +
                "    account_type VARCHAR(20)," +
                "    balance DECIMAL(15,2) DEFAULT 0.00," +
                "    branch VARCHAR(100)," +
                "    interest_rate DECIMAL(5,4)," +
                "    overdraft_limit DECIMAL(15,2)," +
                "    employer VARCHAR(100)," +
                "    company_address VARCHAR(200)," +
                "    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    FOREIGN KEY (customer_id) REFERENCES customers(customer_id)" +
                ");" +
                
                "CREATE TABLE IF NOT EXISTS transactions (" +
                "    transaction_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    account_number VARCHAR(50)," +
                "    transaction_type VARCHAR(20)," +
                "    amount DECIMAL(15,2)," +
                "    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    description TEXT," +
                "    FOREIGN KEY (account_number) REFERENCES accounts(account_number)" +
                ");" +
                
                "CREATE TABLE IF NOT EXISTS employees (" +
                "    username VARCHAR(50) PRIMARY KEY," +
                "    password VARCHAR(100)," +
                "    role VARCHAR(50)" +
                ");";
            
            // Execute each CREATE statement
            String[] statements = schemaSQL.split(";");
            for (String statement : statements) {
                if (!statement.trim().isEmpty()) {
                    stmt.executeUpdate(statement);
                }
            }
            
            // Insert sample data
            insertSampleData(conn);
            System.out.println("SQLite database initialized successfully.");
            
        } catch (SQLException e) {
            System.out.println("Database initialization failed: " + e.getMessage());
        }
    }
    
    private static void insertSampleData(Connection conn) throws SQLException {
        // Insert employees
        String insertEmployees = "INSERT OR IGNORE INTO employees (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertEmployees)) {
            pstmt.setString(1, "admin");
            pstmt.setString(2, "admin123");
            pstmt.setString(3, "Manager");
            pstmt.executeUpdate();
            
            pstmt.setString(1, "manager");
            pstmt.setString(2, "manager123");
            pstmt.setString(3, "Supervisor");
            pstmt.executeUpdate();
        }
        
        // Insert sample customers
        String insertCustomers = "INSERT OR IGNORE INTO customers (customer_id, first_name, last_name, address, password, customer_type, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertCustomers)) {
            pstmt.setString(1, "C001");
            pstmt.setString(2, "John");
            pstmt.setString(3, "Doe");
            pstmt.setString(4, "Gaborone");
            pstmt.setString(5, "pass123");
            pstmt.setString(6, "PERSON");
            pstmt.setString(7, "APPROVED");
            pstmt.executeUpdate();
            
            pstmt.setString(1, "C002");
            pstmt.setString(2, "Mary");
            pstmt.setString(3, "Smith");
            pstmt.setString(4, "Francistown");
            pstmt.setString(5, "pass123");
            pstmt.setString(6, "PERSON");
            pstmt.setString(7, "PENDING");
            pstmt.executeUpdate();
        }
        
        // Insert sample accounts
        String insertAccounts = "INSERT OR IGNORE INTO accounts (account_number, customer_id, account_type, balance, branch, interest_rate) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertAccounts)) {
            pstmt.setString(1, "SAV-001");
            pstmt.setString(2, "C001");
            pstmt.setString(3, "SAVINGS");
            pstmt.setDouble(4, 1500.00);
            pstmt.setString(5, "Main Branch");
            pstmt.setDouble(6, 0.0005);
            pstmt.executeUpdate();
            
            pstmt.setString(1, "CHQ-001");
            pstmt.setString(2, "C001");
            pstmt.setString(3, "CHEQUE");
            pstmt.setDouble(4, 800.00);
            pstmt.setString(5, "Main Branch");
            pstmt.setDouble(6, 0.0000);
            pstmt.executeUpdate();
        }
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}