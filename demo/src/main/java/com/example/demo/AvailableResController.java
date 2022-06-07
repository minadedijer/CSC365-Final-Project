package com.example.demo;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDateTime;


public class AvailableResController {

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
}
