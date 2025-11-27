import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Banking System Login");

        Label titleLabel = new Label("Banking System Login");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label userLabel = new Label("Username:");
        TextField userField = new TextField();
        userField.setPromptText("Enter username");

        Label roleLabel = new Label("Login as:");
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Customer", "Employee");
        roleCombo.setValue("Customer");

        Button loginBtn = new Button("Login");
        loginBtn.setStyle("-fx-background-color: #2E8B57; -fx-text-fill: white;");

        loginBtn.setOnAction(e -> {
            String role = roleCombo.getValue().toLowerCase();
            primaryStage.close();
            
            if (role.equals("employee")) {
                new EmployeeDashboard().start(new Stage());
            } else if (role.equals("customer")) {
                new CustomerDashboard().start(new Stage());
            } else {
                showAlert("Error", "Please select 'employee' or 'customer'");
            }
        });

        VBox layout = new VBox(10, titleLabel, userLabel, userField, roleLabel, roleCombo, loginBtn);
        layout.setStyle("-fx-padding: 20; -fx-background-color: #f5f5f5;");

        Scene scene = new Scene(layout, 350, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
