import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;

public class CustomerDashboard extends Application {

    private Customer customer;
    private Label balanceLabel;
    private TextArea historyArea;
    private ComboBox<Account> accountCombo;
    private Label accountInfoLabel;

    @Override
    public void start(Stage primaryStage) {
        // Create a customer with MULTIPLE accounts for testing
        setupCustomerWithMultipleAccounts();
        
        primaryStage.setTitle("Customer Dashboard - Banking System");

        String customerName = (customer instanceof PersonCustomer) 
            ? ((PersonCustomer) customer).getFullName() 
            : ((CompanyCustomer) customer).getCompanyName();

        Label welcomeLabel = new Label("Welcome " + customerName);
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Account selection section
        Label accountLabel = new Label("Select Account:");
        accountLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        accountCombo = new ComboBox<>();
        updateAccountCombo();
        accountCombo.setOnAction(e -> updateAccountDisplay());
        
        // Account info label
        accountInfoLabel = new Label("Please select an account");
        accountInfoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

        balanceLabel = new Label("Balance will appear here");
        balanceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Deposit section
        VBox depositSection = createDepositSection();
        
        // Withdraw section  
        VBox withdrawSection = createWithdrawSection();

        // Interest section
        VBox interestSection = createInterestSection();

        // Transaction history
        historyArea = new TextArea();
        historyArea.setEditable(false);
        historyArea.setPromptText("Transaction history will appear here");
        historyArea.setPrefHeight(200);

        // Account summary
        Button refreshBtn = new Button("Refresh All Accounts");
        refreshBtn.setOnAction(e -> updateAccountCombo());
        
        TextArea summaryArea = new TextArea();
        summaryArea.setEditable(false);
        summaryArea.setText(getAllAccountsSummary());
        summaryArea.setPrefHeight(150);

        // Navigation
        Button backBtn = new Button("Back to Login");
        backBtn.setOnAction(e -> {
            primaryStage.close();
            new LoginView().start(new Stage());
        });

        // Layout - Using Tabs for better organization
        TabPane tabPane = new TabPane();

        // Tab 1: Transactions
        Tab transactionsTab = new Tab("Transactions");
        transactionsTab.setClosable(false);
        VBox transactionsContent = new VBox(15, 
            welcomeLabel,
            new Label("=== Account Selection ==="),
            accountLabel, accountCombo, accountInfoLabel, balanceLabel,
            new Separator(),
            new Label("=== Banking Operations ==="),
            new HBox(20, depositSection, withdrawSection),
            interestSection,
            new Label("=== Transaction History ==="),
            historyArea
        );
        transactionsContent.setStyle("-fx-padding: 20;");
        transactionsTab.setContent(transactionsContent);

        // Tab 2: Account Summary
        Tab summaryTab = new Tab("Account Summary");
        summaryTab.setClosable(false);
        VBox summaryContent = new VBox(15,
            new Label("All Your Accounts"),
            summaryArea,
            refreshBtn
        );
        summaryContent.setStyle("-fx-padding: 20;");
        summaryTab.setContent(summaryContent);

        tabPane.getTabs().addAll(transactionsTab, summaryTab);

        VBox mainLayout = new VBox(10, tabPane, backBtn);
        mainLayout.setStyle("-fx-padding: 10;");

        primaryStage.setScene(new Scene(mainLayout, 800, 700));
        primaryStage.show();
        
        // Auto-select first account
        if (!customer.getAccounts().isEmpty()) {
            accountCombo.getSelectionModel().select(0);
        }
    }

    private void setupCustomerWithMultipleAccounts() {
        // Create a customer with MULTIPLE different accounts
        this.customer = new PersonCustomer("C001", "123 Main St, Gaborone", "John", "Doe");
        
        // Create multiple accounts
        SavingsAccount savings = new SavingsAccount("SAV-001", "Main Branch");
        savings.deposit(1500.00);
        
        InvestmentAccount investment = new InvestmentAccount("INV-001", "Main Branch", 600.00);
        
        ChequeAccount cheque = new ChequeAccount("CHQ-001", "Main Branch", "ABC Company", "Gaborone");
        cheque.deposit(800.00);
        
        // Add all accounts to customer
        customer.addAccount(savings);
        customer.addAccount(investment);
        customer.addAccount(cheque);
        
        System.out.println("Created customer with " + customer.getAccounts().size() + " accounts:");
        for (Account acc : customer.getAccounts()) {
            System.out.println(" - " + acc.getAccountNumber() + " (" + acc.getClass().getSimpleName() + ")");
        }
    }

    private VBox createDepositSection() {
        VBox depositSection = new VBox(5);
        depositSection.setStyle("-fx-border-color: #27ae60; -fx-border-width: 1; -fx-padding: 10;");
        
        Label depositLabel = new Label("💰 DEPOSIT");
        depositLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");
        
        TextField depositField = new TextField();
        depositField.setPromptText("Amount to deposit");
        depositField.setPrefWidth(150);
        
        Button depositBtn = new Button("Deposit");
        depositBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        
        depositBtn.setOnAction(e -> {
            try {
                Account selectedAccount = accountCombo.getValue();
                double amount = Double.parseDouble(depositField.getText());
                
                if (selectedAccount == null) {
                    showAlert("Error", "Please select an account first");
                    return;
                }
                
                if (amount > 0) {
                    double oldBalance = selectedAccount.getBalance();
                    selectedAccount.deposit(amount);
                    double newBalance = selectedAccount.getBalance();
                    
                    updateAccountDisplay();
                    historyArea.appendText("✅ DEPOSIT: +P" + amount + " to " + 
                        selectedAccount.getAccountNumber() + " (" + 
                        selectedAccount.getClass().getSimpleName() + ")\n");
                    
                    showAlert("Success", 
                        "Deposited P" + amount + " successfully!\n" +
                        "Account: " + selectedAccount.getAccountNumber() + "\n" +
                        "Old Balance: P" + oldBalance + "\n" +
                        "New Balance: P" + newBalance);
                    
                    depositField.clear();
                } else {
                    showAlert("Error", "Please enter a positive amount");
                }
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid number");
            }
        });
        
        depositSection.getChildren().addAll(depositLabel, depositField, depositBtn);
        return depositSection;
    }

    private VBox createWithdrawSection() {
        VBox withdrawSection = new VBox(5);
        withdrawSection.setStyle("-fx-border-color: #e67e22; -fx-border-width: 1; -fx-padding: 10;");
        
        Label withdrawLabel = new Label("💳 WITHDRAW");
        withdrawLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #e67e22;");
        
        TextField withdrawField = new TextField();
        withdrawField.setPromptText("Amount to withdraw");
        withdrawField.setPrefWidth(150);
        
        Button withdrawBtn = new Button("Withdraw");
        withdrawBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white;");
        
        withdrawBtn.setOnAction(e -> {
            try {
                Account selectedAccount = accountCombo.getValue();
                double amount = Double.parseDouble(withdrawField.getText());
                
                if (selectedAccount == null) {
                    showAlert("Error", "Please select an account first");
                    return;
                }
                
                if (amount > 0) {
                    // Check if it's a savings account (no withdrawals allowed)
                    if (selectedAccount instanceof SavingsAccount) {
                        showAlert("Withdrawal Denied", 
                            "Savings accounts do not allow withdrawals.\n" +
                            "Please use your Cheque or Investment account for withdrawals.");
                        return;
                    }
                    
                    double oldBalance = selectedAccount.getBalance();
                    selectedAccount.withdraw(amount);
                    double newBalance = selectedAccount.getBalance();
                    
                    updateAccountDisplay();
                    historyArea.appendText("✅ WITHDRAW: -P" + amount + " from " + 
                        selectedAccount.getAccountNumber() + " (" + 
                        selectedAccount.getClass().getSimpleName() + ")\n");
                    
                    showAlert("Success", 
                        "Withdrew P" + amount + " successfully!\n" +
                        "Account: " + selectedAccount.getAccountNumber() + "\n" +
                        "Old Balance: P" + oldBalance + "\n" +
                        "New Balance: P" + newBalance);
                    
                    withdrawField.clear();
                } else {
                    showAlert("Error", "Please enter a positive amount");
                }
            } catch (Exception ex) {
                showAlert("Error", "Withdrawal failed: " + ex.getMessage());
            }
        });
        
        withdrawSection.getChildren().addAll(withdrawLabel, withdrawField, withdrawBtn);
        return withdrawSection;
    }

    private VBox createInterestSection() {
        VBox interestSection = new VBox(5);
        interestSection.setStyle("-fx-border-color: #9b59b6; -fx-border-width: 1; -fx-padding: 10;");
        
        Label interestLabel = new Label("📈 INTEREST");
        interestLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #9b59b6;");
        
        Button interestBtn = new Button("Apply Monthly Interest");
        interestBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
        
        Label interestInfo = new Label("Savings: 0.05% | Investment: 5% | Cheque: No interest");
        interestInfo.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        
        interestBtn.setOnAction(e -> {
            Account selectedAccount = accountCombo.getValue();
            
            if (selectedAccount == null) {
                showAlert("Error", "Please select an account first");
                return;
            }
            
            if (selectedAccount instanceof SavingsAccount) {
                double interest = ((SavingsAccount) selectedAccount).calculateInterest();
                ((SavingsAccount) selectedAccount).applyMonthlyInterest();
                updateAccountDisplay();
                historyArea.appendText("✅ INTEREST: +P" + interest + " (0.05%) to " + 
                    selectedAccount.getAccountNumber() + "\n");
                showAlert("Interest Applied", 
                    "Monthly interest of P" + interest + " added!\n" +
                    "Account: " + selectedAccount.getAccountNumber() + "\n" +
                    "Rate: 0.05% monthly");
                
            } else if (selectedAccount instanceof InvestmentAccount) {
                double interest = ((InvestmentAccount) selectedAccount).calculateInterest();
                ((InvestmentAccount) selectedAccount).applyMonthlyInterest();
                updateAccountDisplay();
                historyArea.appendText("✅ INTEREST: +P" + interest + " (5%) to " + 
                    selectedAccount.getAccountNumber() + "\n");
                showAlert("Interest Applied", 
                    "Monthly interest of P" + interest + " added!\n" +
                    "Account: " + selectedAccount.getAccountNumber() + "\n" +
                    "Rate: 5% monthly");
                
            } else {
                showAlert("No Interest", 
                    "Cheque accounts do not earn interest.\n" +
                    "Consider opening a Savings or Investment account to earn interest.");
            }
        });
        
        interestSection.getChildren().addAll(interestLabel, interestBtn, interestInfo);
        return interestSection;
    }

    private void updateAccountCombo() {
        accountCombo.getItems().clear();
        if (customer != null && !customer.getAccounts().isEmpty()) {
            accountCombo.getItems().addAll(customer.getAccounts());
            
            // Update the combo box display to show account type and number
            accountCombo.setCellFactory(lv -> new ListCell<Account>() {
                @Override
                protected void updateItem(Account item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        String type = item.getClass().getSimpleName();
                        setText(type + " - " + item.getAccountNumber() + " (P" + item.getBalance() + ")");
                    }
                }
            });
            
            accountCombo.setButtonCell(new ListCell<Account>() {
                @Override
                protected void updateItem(Account item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Select an account");
                    } else {
                        String type = item.getClass().getSimpleName();
                        setText(type + " - " + item.getAccountNumber() + " (P" + item.getBalance() + ")");
                    }
                }
            });
        }
    }

    private void updateAccountDisplay() {
        Account selectedAccount = accountCombo.getValue();
        if (selectedAccount != null) {
            double balance = selectedAccount.getBalance();
            String accountType = selectedAccount.getClass().getSimpleName();
            String accountNumber = selectedAccount.getAccountNumber();
            
            balanceLabel.setText("Current Balance: P" + String.format("%.2f", balance));
            
            // Update account info label
            String info = "Account: " + accountNumber + " | Type: " + accountType;
            if (selectedAccount instanceof SavingsAccount) {
                info += " | Interest: 0.05% monthly | No withdrawals allowed";
            } else if (selectedAccount instanceof InvestmentAccount) {
                info += " | Interest: 5% monthly | Withdrawals allowed";
            } else if (selectedAccount instanceof ChequeAccount) {
                info += " | No interest | Withdrawals allowed";
            }
            accountInfoLabel.setText(info);
            
            // Show transaction history
            historyArea.clear();
            historyArea.appendText("=== TRANSACTION HISTORY ===\n");
            List<String> history = selectedAccount.getHistory();
            for (String entry : history) {
                historyArea.appendText(entry + "\n");
            }
        }
    }

    private String getAllAccountsSummary() {
        if (customer.getAccounts().isEmpty()) {
            return "No accounts available.\nPlease contact the bank to open an account.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== YOUR ACCOUNT SUMMARY ===\n\n");
        
        double totalBalance = 0;
        for (Account account : customer.getAccounts()) {
            String type = account.getClass().getSimpleName();
            double balance = account.getBalance();
            totalBalance += balance;
            
            sb.append("📊 ").append(account.getAccountNumber()).append("\n");
            sb.append("   Type: ").append(type).append("\n");
            sb.append("   Balance: P").append(String.format("%.2f", balance)).append("\n");
            
            if (account instanceof SavingsAccount) {
                sb.append("   Interest: 0.05% monthly\n");
                sb.append("   Withdrawals: Not allowed\n");
            } else if (account instanceof InvestmentAccount) {
                sb.append("   Interest: 5% monthly\n");
                sb.append("   Withdrawals: Allowed\n");
            } else if (account instanceof ChequeAccount) {
                sb.append("   Interest: None\n");
                sb.append("   Withdrawals: Allowed\n");
                sb.append("   Employer: ").append(((ChequeAccount) account).getEmployer()).append("\n");
            }
            sb.append("   --------------------\n");
        }
        
        sb.append("\n💰 TOTAL BALANCE ACROSS ALL ACCOUNTS: P").append(String.format("%.2f", totalBalance));
        sb.append("\n📈 TOTAL ACCOUNTS: ").append(customer.getAccounts().size());
        
        return sb.toString();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
