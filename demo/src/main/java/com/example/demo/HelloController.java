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
import java.sql.Connection;
import java.sql.DriverManager;
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
    private Connection connect;


    public Connection makeConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connect = DriverManager.getConnection(
                    "jdbc:mysql://ambari-node5.csc.calpoly.edu:3306/aarsky?user=aarsky&password=14689801");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connect;
    }


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
        else if (!JDBCDao.validateStudent(makeConnection(), loginUsernameTextField.getText(), loginPasswordPasswordField.getText())) {
            invalidLoginCredentials.setText("Invalid username or password!");
            invalidLoginCredentials.setStyle(errorMessage);
        }
        else
        {
            /*
            invalidLoginCredentials.setText("Login Successful!");
            invalidLoginCredentials.setStyle(successMessage);
            loginUsernameTextField.setStyle(successStyle);
            loginPasswordPasswordField.setStyle(successStyle);
            */

            /* Straight to Scheduler:
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Scheduler.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
            stage.setTitle("Cal Poly Fishbowl Scheduler");
            stage.setScene(scene);
            stage.show();
             */

            /* For Reservations page: */
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("HomePage.fxml"));
            Parent root = fxmlLoader.load();

            /* Pass username to ReservationController */
            ReservationController resController = fxmlLoader.getController();
            resController.populateReservations(loginUsernameTextField.getText());

            Scene scene = new Scene(root, 1000, 700);
            stage.setTitle("Current Cal Poly Fishbowl Reservations");
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