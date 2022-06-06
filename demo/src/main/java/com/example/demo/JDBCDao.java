package com.example.demo;

import org.javatuples.Pair;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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

        // test executeQuery
        executeQuery("SELECT * FROM Students", connect);

        // test checkStudent
        System.out.println(validateStudent(connect,"jsmith", "123abc"));
        System.out.println(validateStudent(connect,"jsmith", "1234abc"));

        // test checkFishbowls
        LocalDate a = LocalDate.of(2022, 5, 12);
        System.out.println(checkFishbowls(connect, a));
    }

    // just checks to see if you can connect and query
    public static void executeQuery(String query, Connection con) {
        ResultSet rs;
        try {
            Statement statement = con.createStatement();
            rs = statement.executeQuery(query);

            while (rs.next()) {
                String studentName = rs.getString("name");
                System.out.println("Student name = " +
                        studentName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // given the username and password, returns true if student is in the Students database
    // else returns false
    public static boolean validateStudent(Connection con, String username, String password) {
        ResultSet rs;
        String sql = "SELECT COUNT(*) AS Count FROM Students " +
                "WHERE username = ? AND password = ?;";
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            // System.out.println(statement);
            rs = statement.executeQuery();

            rs.next();
            Integer count = rs.getInt("Count");
            if (count == 1) {
                return true;
            }
            else {
                System.out.println("Error: either username or password is incorrect.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 1. finds the fId, date, startTime, and endTime for all Reservations (for a specific date)
    // 2. creates a Hashmap of reserved times by fId: for each (key, value) pair, fId is the key,
    // and a list of LocalDateTime pairs (essentially just the combination of date+startTime and
    // date+endTime) is the value
    // 3. finds the fIds of all Fishbowls and adds fishbowls with no reservations to the Hashmap (with the values as an empty list)
    // 4. iterates through this Hashmap, and for each fId, finds the available times (with getAvailableTimes())
    // 5. returns a Hashmap of available times by fId: fId is the key and a List of available LocalDateTimes is the value
    public static HashMap<Integer, List<LocalDateTime>> checkFishbowls(Connection con, LocalDate resDate) {
        ResultSet res;
        ResultSet fishbowls;
        HashMap<Integer, List<LocalDateTime>> AvailTimesByFishbowl = new HashMap<>();
        try {
            String sql = "SELECT fId, date, startTime, endTime FROM Reservations WHERE date = ?;";
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, resDate.toString());
            res = statement.executeQuery();

            HashMap<Integer, List<Pair<LocalDateTime, LocalDateTime>>> ResTimesByFishbowl = new HashMap<>();
            while (res.next()) {
                Integer fId = res.getInt("fId");
                LocalDate date = res.getDate("date").toLocalDate();
                LocalTime startTime = res.getTime("startTime").toLocalTime();
                LocalTime endTime = res.getTime("endTime").toLocalTime();
                // System.out.println("fId: " + fId + ", date: " + rDate + ", start: " + startTime + ", end: " + endTime);

                LocalDateTime startDate = startTime.atDate(date);
                LocalDateTime endDate = endTime.atDate(date);
                Pair<LocalDateTime, LocalDateTime> reserve = Pair.with(startDate, endDate);
                List<Pair<LocalDateTime, LocalDateTime>> reserves = new ArrayList<>();
                if(ResTimesByFishbowl.containsKey(fId)) {
                    reserves = ResTimesByFishbowl.get(fId);
                    reserves.add(reserve);
                    ResTimesByFishbowl.replace(fId, reserves);
                } else {
                    reserves.add(reserve);
                    ResTimesByFishbowl.put(fId, reserves);
                }
            }

            String query = "SELECT DISTINCT id FROM Fishbowls;";
            Statement statement2 = con.createStatement();
            fishbowls = statement2.executeQuery(query);

            while (fishbowls.next()) {
                Integer fId = fishbowls.getInt("id");
                if (!ResTimesByFishbowl.containsKey(fId)) {
                    ResTimesByFishbowl.put(fId, new ArrayList<>());
                }
            }

            // System.out.println(ResTimesByFishbowl);

            for (Map.Entry reserve : ResTimesByFishbowl.entrySet()) {
                Integer fId = (Integer) reserve.getKey();
                List<Pair<LocalDateTime, LocalDateTime>> booked = (List<Pair<LocalDateTime, LocalDateTime>>) reserve.getValue();
                List<LocalDateTime> available = getAvailableTimes(resDate, booked);
                AvailTimesByFishbowl.put(fId, available);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return AvailTimesByFishbowl;
    }

    // given a list of LocalDateTime pairs, finds available LocalDateTimes that aren't booked (from 8 am to 12 pm on a specific date)
    // (i.e. finds LocalDateTimes that are before the first LocalDateTime pair and after/equal to the second one for all pairs)
    public static List<LocalDateTime> getAvailableTimes(LocalDate resDate, List<Pair<LocalDateTime, LocalDateTime>> booked) {
        LocalDateTime startTime = LocalTime.of(8, 00).atDate(resDate);

        List<LocalDateTime> potentials = Stream.iterate(startTime, d -> d.plusHours(1))
                .limit(16)
                .collect(Collectors.toList());

        List<LocalDateTime> available = new ArrayList<>();
        Integer totalRes = booked.size();
        for (LocalDateTime potential : potentials) {
            Integer count = 0;
            for (Pair<LocalDateTime, LocalDateTime> reserve: booked) {
                boolean outsideRes = (potential.isBefore(reserve.getValue0()) ||
                        (potential.isAfter(reserve.getValue1()) || potential.equals(reserve.getValue1())));
                if (outsideRes) {
                    count++;
                }
            }
            if (count == totalRes) {
                available.add(potential);
            }
        }
        return available;
    }
}


