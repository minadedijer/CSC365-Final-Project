package com.example.demo;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;


public class ReservationController {

    @FXML
    private TableView<Reservation> table;
    @FXML
    private TableColumn<Reservation, Integer> c1;
    @FXML
    private TableColumn<Reservation, Integer> c2;
    @FXML
    private TableColumn<Reservation, String> c3;
    @FXML
    private TableColumn<Reservation, LocalDate> c4;
    @FXML
    private TableColumn<Reservation, LocalTime> c5;
    @FXML
    private TableColumn<Reservation, LocalTime> c6;
    @FXML
    private Connection connect;
    @FXML
    private String username;
    @FXML
    private Button addButton;

    /**
     * Initializes the controller class.
     */
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


    public void populateReservations(String username) {

        System.out.println("inside initialize");

        List<Reservation> ResforUser = JDBCDao.getReservations(makeConnection(), username);

        c1.setCellValueFactory(new PropertyValueFactory("id"));
        c2.setCellValueFactory(new PropertyValueFactory("fId"));
        c3.setCellValueFactory(new PropertyValueFactory("groupName"));
        c4.setCellValueFactory(new PropertyValueFactory("date"));
        c5.setCellValueFactory(new PropertyValueFactory("startTime"));
        c6.setCellValueFactory(new PropertyValueFactory("endTime"));
        try {
            table.getItems().addAll(ResforUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onAddButtonClick(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Scheduler.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
        stage.setTitle("Cal Poly Fishbowl Scheduler");
        stage.setScene(scene);
        stage.show();
    }
}

