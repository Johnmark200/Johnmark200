package Eclinic;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.*;

public class LoginSignupApp extends Application {

    // Database connection constants
    private static final String DB_URL = "jdbc:mysql://localhost/eclinic";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("E-Clinic Login");
        primaryStage.setScene(createLoginScene(primaryStage));
        primaryStage.show();
    }

    private Scene createLoginScene(Stage stage) {
        // Header
        Label headerLabel = new Label("E-Clinic System");
        headerLabel.setFont(Font.font("Arial Black", 26));
        headerLabel.setTextFill(Color.WHITE);
        HBox header = new HBox(headerLabel);
        header.setPadding(new Insets(15));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #34495e;");

        // Form fields
        Label loginTitle = new Label("Sign In");
        loginTitle.setFont(Font.font("Segoe UI", 24));
        loginTitle.setTextFill(Color.web("#2c3e50"));

        Label userLabel = new Label("Username");
        userLabel.setFont(Font.font("Segoe UI Semibold", 14));
        TextField userField = new TextField();
        userField.setPromptText("Enter username");
        userField.setPrefHeight(40);

        Label passLabel = new Label("Password");
        passLabel.setFont(Font.font("Segoe UI Semibold", 14));
        PasswordField passField = new PasswordField();
        passField.setPromptText("Enter password");
        passField.setPrefHeight(40);

        Label errorMsg = new Label();
        errorMsg.setTextFill(Color.RED);

        // Buttons
        Button loginBtn = new Button("Login");
        loginBtn.setPrefSize(280, 45);
        loginBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 16px;");

        Button signUpLink = new Button("Create an account");
        signUpLink.setStyle("-fx-background-color: transparent; -fx-text-fill: #2980b9; -fx-underline: true; -fx-font-size: 12px;");

        Button exitBtn = new Button("Exit");
        exitBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #c0392b; -fx-font-size: 12px;");

        VBox buttonsBox = new VBox(10, loginBtn, signUpLink, exitBtn);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox formBox = new VBox(15, loginTitle, userLabel, userField, passLabel, passField, errorMsg, buttonsBox);
        formBox.setPadding(new Insets(20));
        formBox.setMaxWidth(320);
        formBox.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setTop(header);
        root.setCenter(formBox);
        root.setStyle("-fx-background-color: #ecf0f1;");

        // Button Actions
        loginBtn.setOnAction(e -> {
            String username = userField.getText().trim();
            String password = passField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                errorMsg.setText("Please fill in both username and password.");
                return;
            }

            login(stage, username, password);
        });

        signUpLink.setOnAction(e -> {
            // TODO: Implement your createSignupScene() if needed
            showAlert(Alert.AlertType.INFORMATION, "Signup", "Signup screen not implemented.");
        });

        exitBtn.setOnAction(e -> stage.close());

        return new Scene(root, 400, 450);
    }

    private void login(Stage stage, String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String sql = "SELECT * FROM user_login WHERE username=? AND password=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");

                switch (role.toLowerCase()) {
                    case "admin":
                        showAlert(Alert.AlertType.INFORMATION, "Login Success", "Welcome, Admin!");
                        new AdminDashboard().start(new Stage());
                        break;
                    case "staff":
                        showAlert(Alert.AlertType.INFORMATION, "Login Success", "Welcome, Staff!");
                        new StaffDashboard().start(new Stage());
                        break;
                    case "user":
                        showAlert(Alert.AlertType.INFORMATION, "Login Success", "Welcome, User!");
                        new PatientDashboard().start(new Stage());
                        break;
                    default:
                        showAlert(Alert.AlertType.ERROR, "Unknown Role", "Contact system administrator.");
                        break;
                }

                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Wrong username or password.");
            }

            conn.close();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
