package com.example.demo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    private List<AvailableRes> overallRes;

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
    private ChoiceBox<LocalTime> startTime;
    @FXML
    private ChoiceBox<LocalTime> endTime;
    @FXML
    private ChoiceBox<Integer> fId;

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

    public void populateFIds(){
        //get user selection of end time
        System.out.println("User Chose End Time: " + endTime.getSelectionModel().getSelectedItem());
        LocalTime userEndTime = endTime.getSelectionModel().getSelectedItem();

        //call fids method in jdbcdao
        List<Integer> availFids = JDBCDao.availableFIds(startTime.getSelectionModel().getSelectedItem(), userEndTime, overallRes);
        fId.getItems().clear(); //clear old fids
        fId.getItems().addAll(availFids);
    }

    public void populateEndTimes(){
        //get user selection of start time
        System.out.println("User Chose Start Time: " + startTime.getSelectionModel().getSelectedItem());
        LocalTime userStartTime = startTime.getSelectionModel().getSelectedItem();

        //call end times method in jbdcdao
        List<LocalTime> availEndTimes = JDBCDao.availableEndTimes(userStartTime, overallRes);
        endTime.getItems().clear(); //clear old times
        endTime.getItems().addAll(availEndTimes);
    }

    public void populateStartTimes(List<AvailableRes> availableRes) {
        //call jdbc method
        List<LocalTime> availStartTimes = JDBCDao.availableStartTimes(availableRes);
        startTime.getItems().clear(); //clear old times
        startTime.getItems().addAll(availStartTimes);

        //wait for user to select a start time, then select it
        /*
        LocalTime finalStartTime;
        startTime.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<LocalTime>() {
            @Override
            public void changed(ObservableValue<? extends LocalTime> observableValue, LocalTime time, LocalTime time2) {
                if(time == time2){
                    System.out.println("no change");}

                }
        });
        //System.out.println("User Chose: " + finalStartTime);

         */
    }

    @FXML //clicking the date on the calendar triggers the population of the reservation table, method above
    protected void onDateSelection()
    {
        String datePicked0 = DatePicked.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        SelectedDate.setText(datePicked0);
        populateReservations(DatePicked.getValue());
        //populate start, end and ids
        overallRes = JDBCDao.availableFishbowls(makeConnection(), DatePicked.getValue());
        populateStartTimes(overallRes);
    }

    @FXML
    protected void onMakeResButtonClick(ActionEvent event) throws IOException {
        //System.out.println(username + ", " + fId.getSelectionModel().getSelectedItem() + ", " + groupName.getText() + ", " + SelectedDate.getText() + ", " + startTime.getSelectionModel().getSelectedItem() + ", " + endTime.getSelectionModel().getSelectedItem());
        try {
            JDBCDao.createReservation(makeConnection(), username, fId.getSelectionModel().getSelectedItem(), groupName.getText(), DatePicked.getValue(), startTime.getValue(), endTime.getValue());
            System.out.println("Created a reservation!");

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Reservation Confirmed!", ButtonType.OK);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.OK) {
                Stage stage = (Stage) makeResButton.getScene().getWindow();
                stage.close();
            }

        } catch(Exception e) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Reservation Failed.\nTry again?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.NO) {
                Stage stage = (Stage) makeResButton.getScene().getWindow();
                stage.close();
            }
        }

    }
}
