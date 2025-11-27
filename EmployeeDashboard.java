import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

public class EmployeeDashboard extends Application {

    private ObservableList<Customer> customerData;
    private TableView<Customer> customerTable;
    private List<Customer> allCustomers;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Employee Dashboard - Bank Management System");

        // Initialize sample data
        initializeSampleData();
        
        // Create tab pane for different functionalities
        TabPane tabPane = new TabPane();

        // Tab 1: Customer Management
        Tab customerTab = new Tab("Customer Management");
        customerTab.setClosable(false);
        customerTab.setContent(createCustomerManagementContent());

        // Tab 2: Account Creation
        Tab accountTab = new Tab("Create Accounts");
        accountTab.setClosable(false);
        accountTab.setContent(createAccountCreationContent());

        tabPane.getTabs().addAll(customerTab, accountTab);

        // Logout button
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        logoutBtn.setOnAction(e -> {
            primaryStage.close();
            new LoginView().start(new Stage());
        });

        VBox mainLayout = new VBox(10, new Label("🏦 Bank Employee Dashboard"), tabPane, logoutBtn);
        mainLayout.setStyle("-fx-padding: 20;");

        primaryStage.setScene(new Scene(mainLayout, 900, 700));
        primaryStage.show();
    }

    private void initializeSampleData() {
        // Create some sample customers for the bank
        allCustomers = List.of(
            new PersonCustomer("C001", "123 Main St, Gaborone", "John", "Doe"),
            new PersonCustomer("C002", "456 Broad St, Francistown", "Mary", "Smith"),
            new CompanyCustomer("C003", "789 Business Park", "Tech Solutions Ltd"),
            new PersonCustomer("C004", "321 River View", "David", "Brown")
        );
        
        customerData = FXCollections.observableArrayList(allCustomers);
    }

    private VBox createCustomerManagementContent() {
        VBox customerContent = new VBox(15);
        
        Label title = new Label("Customer Management");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Create customer table
        customerTable = new TableView<>();
        
        TableColumn<Customer, String> idCol = new TableColumn<>("Customer ID");
        idCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCustomerID()));
        
        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cellData -> {
            Customer cust = cellData.getValue();
            if (cust instanceof PersonCustomer) {
                return new SimpleStringProperty(((PersonCustomer) cust).getFullName());
            } else {
                return new SimpleStringProperty(((CompanyCustomer) cust).getCompanyName());
            }
        });
        
        TableColumn<Customer, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAddress()));
        
        TableColumn<Customer, String> accountsCol = new TableColumn<>("Accounts");
        accountsCol.setCellValueFactory(cellData -> new SimpleStringProperty(
            String.valueOf(cellData.getValue().getAccounts().size())
        ));
        
        TableColumn<Customer, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        customerTable.getColumns().addAll(idCol, nameCol, addressCol, accountsCol, statusCol);
        customerTable.setItems(customerData);

        // Action buttons
        HBox buttonBox = new HBox(10);
        Button approveBtn = new Button("Approve Customer");
        approveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        
        Button viewAccountsBtn = new Button("View Customer Accounts");
        viewAccountsBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        approveBtn.setOnAction(e -> {
            Customer selected = customerTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.setStatus("Approved");
                customerTable.refresh();
                showAlert("Success", "Customer " + selected.getCustomerID() + " has been approved!");
            } else {
                showAlert("Error", "Please select a customer first");
            }
        });

        viewAccountsBtn.setOnAction(e -> {
            Customer selected = customerTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showCustomerAccounts(selected);
            } else {
                showAlert("Error", "Please select a customer first");
            }
        });

        buttonBox.getChildren().addAll(approveBtn, viewAccountsBtn);

        customerContent.getChildren().addAll(title, customerTable, buttonBox);
        return customerContent;
    }

    private VBox createAccountCreationContent() {
        VBox accountContent = new VBox(15);
        accountContent.setStyle("-fx-padding: 20;");
        
        Label title = new Label("Create Bank Account for Customer");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Customer selection
        Label customerLabel = new Label("Select Customer:");
        ComboBox<Customer> customerCombo = new ComboBox<>();
        customerCombo.setItems(customerData);
        customerCombo.setButtonCell(new ListCell<Customer>() {
            @Override
            protected void updateItem(Customer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Select a customer");
                } else {
                    if (item instanceof PersonCustomer) {
                        setText(((PersonCustomer) item).getFullName() + " (" + item.getCustomerID() + ")");
                    } else {
                        setText(((CompanyCustomer) item).getCompanyName() + " (" + item.getCustomerID() + ")");
                    }
                }
            }
        });

        // Account type selection
        Label typeLabel = new Label("Account Type:");
        ComboBox<String> accountTypeCombo = new ComboBox<>();
        accountTypeCombo.getItems().addAll("Savings Account", "Investment Account", "Cheque Account");
        accountTypeCombo.setValue("Savings Account");

        // Initial deposit
        Label depositLabel = new Label("Initial Deposit (P):");
        TextField depositField = new TextField();
        depositField.setPromptText("Enter initial deposit amount");

        // Additional info for specific account types
        VBox additionalInfoBox = new VBox(5);
        
        // Investment account info
        Label investmentInfo = new Label("💡 Investment accounts require minimum P500 deposit");
        investmentInfo.setStyle("-fx-text-fill: #e67e22; -fx-font-size: 12px;");
        
        // Cheque account info
        VBox chequeInfoBox = new VBox(5);
        Label employerLabel = new Label("Employer Name:");
        TextField employerField = new TextField();
        employerField.setPromptText("Customer's employer");
        
        Label companyAddressLabel = new Label("Company Address:");
        TextField companyAddressField = new TextField();
        companyAddressField.setPromptText("Employer's address");

        chequeInfoBox.getChildren().addAll(employerLabel, employerField, companyAddressLabel, companyAddressField);
        chequeInfoBox.setVisible(false);

        // Show/hide additional fields based on account type
        accountTypeCombo.setOnAction(e -> {
            String accountType = accountTypeCombo.getValue();
            investmentInfo.setVisible("Investment Account".equals(accountType));
            chequeInfoBox.setVisible("Cheque Account".equals(accountType));
        });

        // Create account button
        Button createAccountBtn = new Button("Create Account");
        createAccountBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");

        // Result area
        TextArea resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefHeight(150);
        resultArea.setStyle("-fx-font-family: monospace;");

        createAccountBtn.setOnAction(e -> {
            try {
                Customer selectedCustomer = customerCombo.getValue();
                String accountType = accountTypeCombo.getValue();
                double initialDeposit = depositField.getText().isEmpty() ? 0 : Double.parseDouble(depositField.getText());
                
                if (selectedCustomer == null) {
                    showAlert("Error", "Please select a customer");
                    return;
                }

                Account newAccount = null;
                String accountNumber = generateAccountNumber(accountType);

                switch(accountType) {
                    case "Savings Account":
                        newAccount = new SavingsAccount(accountNumber, "Main Branch");
                        if (initialDeposit > 0) newAccount.deposit(initialDeposit);
                        break;
                        
                    case "Investment Account":
                        if (initialDeposit < 500) {
                            showAlert("Error", "Investment account requires minimum P500 initial deposit");
                            return;
                        }
                        newAccount = new InvestmentAccount(accountNumber, "Main Branch", initialDeposit);
                        break;
                        
                    case "Cheque Account":
                        String employer = employerField.getText().trim();
                        String companyAddress = companyAddressField.getText().trim();
                        if (employer.isEmpty() || companyAddress.isEmpty()) {
                            showAlert("Error", "Cheque account requires employment information");
                            return;
                        }
                        newAccount = new ChequeAccount(accountNumber, "Main Branch", employer, companyAddress);
                        if (initialDeposit > 0) newAccount.deposit(initialDeposit);
                        break;
                }

                if (newAccount != null) {
                    selectedCustomer.addAccount(newAccount);
                    
                    String customerName = (selectedCustomer instanceof PersonCustomer) 
                        ? ((PersonCustomer) selectedCustomer).getFullName()
                        : ((CompanyCustomer) selectedCustomer).getCompanyName();
                    
                    resultArea.setText("✅ ACCOUNT CREATED SUCCESSFULLY!\n" +
                                     "──────────────────────────────\n" +
                                     "Customer: " + customerName + "\n" +
                                     "Account Number: " + accountNumber + "\n" +
                                     "Account Type: " + accountType + "\n" +
                                     "Initial Deposit: P" + initialDeposit + "\n" +
                                     "Branch: Main Branch\n" +
                                     "Status: Active\n\n" +
                                     "Total accounts for customer: " + selectedCustomer.getAccounts().size());
                    
                    // Clear fields
                    depositField.clear();
                    employerField.clear();
                    companyAddressField.clear();
                    
                    // Refresh customer table to show updated account count
                    customerTable.refresh();
                }
            } catch (Exception ex) {
                showAlert("Error", "Failed to create account: " + ex.getMessage());
            }
        });

        additionalInfoBox.getChildren().addAll(investmentInfo, chequeInfoBox);

        accountContent.getChildren().addAll(
            title, customerLabel, customerCombo, typeLabel, accountTypeCombo,
            depositLabel, depositField, additionalInfoBox, createAccountBtn, resultArea
        );
        
        return accountContent;
    }

    private void showCustomerAccounts(Customer customer) {
        Stage accountsStage = new Stage();
        accountsStage.setTitle("Accounts for " + customer.getCustomerID());
        
        VBox accountsContent = new VBox(15);
        accountsContent.setStyle("-fx-padding: 20;");
        
        String customerName = (customer instanceof PersonCustomer) 
            ? ((PersonCustomer) customer).getFullName()
            : ((CompanyCustomer) customer).getCompanyName();
            
        Label title = new Label("Accounts for: " + customerName);
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        TextArea accountsInfo = new TextArea();
        accountsInfo.setEditable(false);
        accountsInfo.setStyle("-fx-font-family: monospace;");
        accountsInfo.setText(getFormattedAccountsInfo(customer));
        accountsInfo.setPrefSize(500, 400);
        
        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> accountsStage.close());
        
        accountsContent.getChildren().addAll(title, accountsInfo, closeBtn);
        
        Scene scene = new Scene(accountsContent);
        accountsStage.setScene(scene);
        accountsStage.show();
    }

    private String getFormattedAccountsInfo(Customer customer) {
        if (customer.getAccounts().isEmpty()) {
            return "No accounts found for this customer.\nUse 'Create Accounts' tab to add accounts.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== ACCOUNT SUMMARY ===\n\n");
        
        double totalBalance = 0;
        for (Account account : customer.getAccounts()) {
            String type = account.getClass().getSimpleName();
            double balance = account.getBalance();
            totalBalance += balance;
            
            sb.append("Account: ").append(account.getAccountNumber()).append("\n");
            sb.append("Type: ").append(type).append("\n");
            sb.append("Balance: P").append(String.format("%.2f", balance)).append("\n");
            sb.append("Branch: ").append(account.getBranch()).append("\n");
            
            if (account instanceof SavingsAccount) {
                sb.append("Interest: 0.05% monthly\n");
            } else if (account instanceof InvestmentAccount) {
                sb.append("Interest: 5% monthly\n");
            } else if (account instanceof ChequeAccount) {
                sb.append("Employer: ").append(((ChequeAccount) account).getEmployer()).append("\n");
            }
            
            sb.append("────────────────────────\n");
        }
        
        sb.append("\n💰 TOTAL BALANCE: P").append(String.format("%.2f", totalBalance));
        sb.append("\n📊 TOTAL ACCOUNTS: ").append(customer.getAccounts().size());
        
        return sb.toString();
    }

    private String generateAccountNumber(String accountType) {
        String prefix = "";
        switch(accountType) {
            case "Savings Account": prefix = "SAV"; break;
            case "Investment Account": prefix = "INV"; break;
            case "Cheque Account": prefix = "CHQ"; break;
        }
        return prefix + "-" + System.currentTimeMillis();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
