package com.example.demo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;


public class ReservationController {

    private String username;
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
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private ChoiceBox<Integer> selectDelete;



    public void setUsername(String username) {
        this.username = username;
        System.out.println(username);
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


    public void populateReservations(String username) {

        System.out.println("inside initialize");
        setUsername(username);

        Connection con = makeConnection();

        List<Reservation> ResforUser = JDBCDao.getReservations(con, this.username);

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
        //call fids method in jdbcdao
        List<Integer> availResIds = JDBCDao.getResIds(con, this.username);
        System.out.println(availResIds);
        //List<Integer> availFids = (List<Integer>) c1;
        selectDelete.getItems().clear(); //clear old fids
        selectDelete.getItems().addAll(availResIds);
    }


    @FXML
    protected void onAddButtonClick(ActionEvent event) throws IOException {
        Stage stage = (Stage) addButton.getScene().getWindow();
        stage.close();

        Stage newStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Scheduler.fxml"));
        Parent root = fxmlLoader.load();

        /* Pass username to AvailableResController */
        AvailableResController availResController = fxmlLoader.getController();
        availResController.setUsername(this.username);

        Scene scene = new Scene(root, 1000, 700);
        newStage.setTitle("Cal Poly Fishbowl Scheduler");
        newStage.setScene(scene);
        newStage.show();
    }

    public void populateDelete() throws ParseException {
        //call fids method in jdbcdao
        //List<Integer> availFids = JDBCDao.getResIds(con, username);
        //List<Integer> availFids = (List<Integer>) c1;
        //selectDelete.getItems().clear(); //clear old fids
        //System.out.println("delete");
        //selectDelete.getItems().addAll(availFids);
    }

    @FXML
    protected void onDelete(ActionEvent event)
    {
        JDBCDao.deleteReservation(makeConnection(), username, selectDelete.getSelectionModel().getSelectedItem());
        System.out.println("Deleted a reservation");
        table.getItems().clear();
        populateReservations(username);
    }

}

