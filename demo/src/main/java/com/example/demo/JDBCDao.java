package com.example.demo;

import org.javatuples.Pair;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
        System.out.println(checkFishbowls(connect));
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 1. finds the fId, date, startTime, and endTime for all Reservations
    // 2. creates a Hashmap of reserved times by fId: for each (key, value) pair, fId is the key,
    // and a list of LocalDateTime pairs (essentially just the combination of date+startTime and
    // date+endTime) is the value
    // 3. iterates through this Hashmap, and for each fId, finds the available times (with getAvailableTimes())
    // 4. returns a Hashmap of available times by fId: fId is the key and a List of available
    // LocalDateTimes is the value
    public static HashMap<Integer, List<LocalDateTime>> checkFishbowls(Connection con) {
        ResultSet rs;
        HashMap<Integer, List<LocalDateTime>> AvailTimesByFishbowl = new HashMap<>();
        try {
            Statement statement = con.createStatement();
            String query = "SELECT fId, date, startTime, endTime FROM Reservations;";
            rs = statement.executeQuery(query);

            HashMap<Integer, List<Pair<LocalDateTime, LocalDateTime>>> ResTimesByFishbowl = new HashMap<>();
            while (rs.next()) {
                Integer fId = rs.getInt("fId");
                LocalDate date = rs.getDate("date").toLocalDate();
                LocalTime startTime = rs.getTime("startTime").toLocalTime();
                LocalTime endTime = rs.getTime("endTime").toLocalTime();
                System.out.println("fId: " + fId + ", date: " + date + ", start: " + startTime + ", end: " + endTime);

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

            for (Map.Entry reserve : ResTimesByFishbowl.entrySet()) {
                Integer fId = (Integer) reserve.getKey();
                List<Pair<LocalDateTime, LocalDateTime>> booked = (List<Pair<LocalDateTime, LocalDateTime>>) reserve.getValue();
                List<LocalDateTime> available = getAvailableTimes(booked);
                AvailTimesByFishbowl.put(fId, available);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return AvailTimesByFishbowl;
    }

    // given a list of LocalDateTime pairs, finds available LocalDateTimes (ranging from 24 hours from today
    // to 2 weeks from then) that aren't booked (i.e. are before the first LocalDateTime pair and after the
    // second one for all pairs)
    public static List<LocalDateTime> getAvailableTimes(List<Pair<LocalDateTime, LocalDateTime>> booked) {
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).truncatedTo(ChronoUnit.HOURS);
        LocalDateTime twoWeeks = tomorrow.plusDays(14);

        long numOfDays = ChronoUnit.DAYS.between(tomorrow, twoWeeks);

        List<LocalDateTime> potentials = Stream.iterate(tomorrow, date -> date.plusHours(1))
                .limit(numOfDays*12)
                .collect(Collectors.toList());

        List<LocalDateTime> available = new ArrayList<>();
        Integer totalRes = booked.size();
        for (LocalDateTime potential : potentials) {
            Integer count = 0;
            for (Pair<LocalDateTime, LocalDateTime> reserve: booked) {
                boolean outsideRes = (potential.isBefore(reserve.getValue0()) || potential.isAfter(reserve.getValue1()));
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


