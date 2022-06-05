package com.example.demo;

import java.sql.*;

public class JDBCDao {
    static   Connection connect;
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connect = DriverManager.getConnection(
                    "jdbc:mysql://ambari-node5.csc.calpoly.edu:3306/aarsky?user=aarsky&password=14689801");
        } catch (Exception e) {
            e.printStackTrace();
        }

        executeQuery("SELECT * FROM Students", connect);
    }

    public static void executeQuery(String query, Connection con) {
        ResultSet rs;
        try {
            Statement statement = con.createStatement();
            rs = statement.executeQuery(query);

            while (rs.next()) {
                String studentName = rs.getString(4);
                System.out.println("Student name = " +
                        studentName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}


