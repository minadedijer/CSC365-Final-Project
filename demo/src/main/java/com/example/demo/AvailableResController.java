package com.example.demo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDateTime;


public class AvailableResController {

    private String username;
    @FXML
    private TableView<AvailableRes> table;
    @FXML
    private TableColumn<AvailableRes, Integer> c1;
    @FXML
    private TableColumn<AvailableRes, String> c2;
    @FXML
    private TableColumn<AvailableRes, String> c3;
    @FXML
    private TableColumn<AvailableRes, LocalDateTime> c4;
    @FXML
    private Connection connect;
    @FXML
    private DatePicker date;
    @FXML
    private Label SelectedDate;
    @FXML
    private DatePicker DatePicked;
    @FXML
    private Button makeResButton;
    @FXML
    private TextField groupName;
    @FXML
    private ChoiceBox startTime;
    @FXML
    private ChoiceBox endTime;
    @FXML
    private ChoiceBox fId;

    /**
     * Initializes the controller class.
     */
    public void setUsername(String username) {
        this.username = username;
    }

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


    public void populateReservations(LocalDate date) {

        System.out.println("inside initialize");

        List<AvailableRes> availableRes = JDBCDao.availableFishbowls(makeConnection(), date);

        c1.setCellValueFactory(new PropertyValueFactory("fId"));
        c2.setCellValueFactory(new PropertyValueFactory("capacity"));
        c3.setCellValueFactory(new PropertyValueFactory("loudness"));
        c4.setCellValueFactory(new PropertyValueFactory("time"));
        try {
            table.getItems().addAll(availableRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML //clicking the date on the calendar triggers the population of the reservation table, method above
    protected void onDateSelection()
    {
        String datePicked0 = DatePicked.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        SelectedDate.setText(datePicked0);
        populateReservations(DatePicked.getValue());
    }

    @FXML
    protected void onMakeResButtonClick(ActionEvent event) throws IOException {
        /* Currently shows errors in createReservation() b/c in unsure of what value fId, startTime, and endTime will be
        JDBCDao.createReservation(makeConnection(), username, fId.getValue(), groupName.getText(), date.getValue(), startTime.getValue(), endTime.getValue());
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("HomePage.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 1000, 700);
        stage.setTitle("Current Cal Poly Fishbowl Reservations");
        stage.setScene(scene);
        stage.show();
         */
    }
}
