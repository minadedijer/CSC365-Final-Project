package com.example.demo;

import java.net.URL;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class AvailableResController implements Initializable {

    @FXML
    private TableView<AvailableRes> table;
    @FXML
    private TableColumn<AvailableRes, String> c1;
    @FXML
    private TableColumn<AvailableRes, String> c2;
    @FXML
    private TableColumn<AvailableRes, String> c3;
    @FXML
    private TableColumn<AvailableRes, String> c4;
    @FXML
    private Connection connect;
    @FXML
    private LocalDate date;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        List<AvailableRes> availableRes = JDBCDao.availableFishbowls(connect, date);

        c1.setCellValueFactory(new PropertyValueFactory("ID"));
        c2.setCellValueFactory(new PropertyValueFactory("Capacity"));
        c3.setCellValueFactory(new PropertyValueFactory("Loudness"));
        c4.setCellValueFactory(new PropertyValueFactory("Available Times"));
        try {
            table.getItems().addAll(availableRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
