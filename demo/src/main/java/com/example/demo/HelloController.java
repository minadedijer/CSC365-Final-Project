package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.TitledPane;
import javafx.scene.Parent;
import javafx.scene.control.Label;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class HelloController
{

    protected
    String successMessage = String.format("-fx-text-fill: GREEN;");
    String errorMessage = String.format("-fx-text-fill: RED;");
    String errorStyle = String.format("-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;");
    String successStyle = String.format("-fx-border-color: #A9A9A9; -fx-border-width: 2; -fx-border-radius: 5;");

    @FXML
    private Button loginButton;
    @FXML
    private TextField loginUsernameTextField;
    @FXML
    private TextField loginPasswordPasswordField;
    @FXML
    private Label invalidLoginCredentials;
    @FXML
    private Label SelectedDate;
    @FXML
    private DatePicker DatePicked;

    @FXML
    protected void onLoginButtonClick() throws IOException
    {
        if (loginUsernameTextField.getText().isBlank() || loginPasswordPasswordField.getText().isBlank())
        {
            invalidLoginCredentials.setText("The Login fields are required!");
            invalidLoginCredentials.setStyle(errorMessage);

            if (loginUsernameTextField.getText().isBlank())
            {
                loginUsernameTextField.setStyle(errorStyle);
            }
            else if (loginPasswordPasswordField.getText().isBlank())
            {
                loginPasswordPasswordField.setStyle(errorStyle);
            }
        }
        else
        {
            /*
            invalidLoginCredentials.setText("Login Successful!");
            invalidLoginCredentials.setStyle(successMessage);
            loginUsernameTextField.setStyle(successStyle);
            loginPasswordPasswordField.setStyle(successStyle);
            */
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Scheduler.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
            stage.setTitle("Cal Poly Fishbowl Scheduler");
            stage.setScene(scene);
            stage.show();
        }
    }
    /*
    protected void onLoginButtonClick() {
        if (loginUsernameTextField.getText().isBlank() || loginPasswordPasswordField.getText().isBlank()) {
            invalidLoginCredentials.setText("The Login fields are required!");
            invalidLoginCredentials.setStyle(errorMessage);

            if (loginUsernameTextField.getText().isBlank()) {
                loginUsernameTextField.setStyle(errorStyle);
            } else if (loginPasswordPasswordField.getText().isBlank()) {
                loginPasswordPasswordField.setStyle(errorStyle);
            }
        } else {
            invalidLoginCredentials.setText("Login Successful!");
            invalidLoginCredentials.setStyle(successMessage);
            loginUsernameTextField.setStyle(successStyle);
            loginPasswordPasswordField.setStyle(successStyle);
        }
    }
     */

    @FXML
    protected void onDateSelection()
    {
        String datePicked0 = DatePicked.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        SelectedDate.setText(datePicked0);
    }

}