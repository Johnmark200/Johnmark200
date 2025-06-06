package Eclinic;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

public class LoginPageController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        // Clear error message when user starts typing
        usernameField.setOnKeyTyped(e -> clearErrorMessage());
        passwordField.setOnKeyTyped(e -> clearErrorMessage());
        
        // Enable Enter key to submit form
        usernameField.setOnKeyPressed(this::handleKeyPressed);
        passwordField.setOnKeyPressed(this::handleKeyPressed);
        
        // Set focus to username field on startup
        usernameField.requestFocus();
    }

    @FXML
    private void handleLoginAction(ActionEvent event) {
        performLogin();
    }
    
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            performLogin();
        }
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Clear previous error messages
        clearErrorMessage();

        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        // Authenticate user
        if (authenticateUser(username, password)) {
            showSuccess("Login Successful!");
            
            // Disable the login button to prevent multiple clicks
            loginButton.setDisable(true);
            
            // Navigate to dashboard after successful login
            navigateToDashboard();
        } else {
            showError("Invalid username or password. Please try again.");
            
            // Clear password field for security
            passwordField.clear();
            
            // Focus back to username field
            usernameField.requestFocus();
        }
    }

    private boolean authenticateUser(String username, String password) {
        // TODO: Replace with actual database authentication
        // This is just a temporary hardcoded check
        
        // You can add multiple user accounts here
        return ("admin".equals(username) && "1234".equals(password)) ||
               ("doctor".equals(username) && "doctor123".equals(password)) ||
               ("nurse".equals(username) && "nurse123".equals(password));
    }

    private void navigateToDashboard() {
        try {
            // Get the current stage
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            
            // Load the dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Eclinic/dashboard.fxml"));
            Parent dashboardRoot = loader.load();
            
            // Create new scene
            Scene dashboardScene = new Scene(dashboardRoot, 1024, 768);
            
            // Apply CSS if available
            try {
                dashboardScene.getStylesheets().add(getClass().getResource("/design/Pagedesign.css").toExternalForm());
            } catch (Exception e) {
                System.out.println("CSS file not found, continuing without styles");
            }
            
            // Set the new scene
            currentStage.setScene(dashboardScene);
            currentStage.setTitle("E-Clinic Dashboard");
            currentStage.centerOnScreen();
            
        } catch (Exception e) {
            showError("Error loading dashboard: " + e.getMessage());
            loginButton.setDisable(false); // Re-enable login button
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
    }
    
    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
    }

    private void clearErrorMessage() {
        errorLabel.setText("");
        loginButton.setDisable(false);
    }

    @FXML
    private void handleForgotPassword() {
        // TODO: Implement forgot password functionality
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot Password");
        alert.setHeaderText(null);
        alert.setContentText("Password recovery feature will be implemented soon.\n\nFor now, please contact the system administrator.");
        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Application");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to exit the application?");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            System.exit(0);
        }
    }
}