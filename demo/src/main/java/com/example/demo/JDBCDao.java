package com.example.demo;

import org.javatuples.Pair;

import java.sql.*;
import java.sql.Date;
import java.text.ParseException;
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

        // test availableFishbowls
        LocalDate a = LocalDate.of(2022, 5, 12);
        List<AvailableRes> availableRes = availableFishbowls(connect, a);
        for (AvailableRes available : availableRes) {
            System.out.println("Fishbowl with fId: " + available.fId + ", capacity: " + available.capacity +
                    " and loudness: " + available.loudness + " is available at " + available.time);
        }

        // test availableStartTimes
        List<LocalTime> availStartTimes = availableStartTimes(availableRes);
        System.out.println("Available Start Times: ");
        for (LocalTime time : availStartTimes) {
            System.out.println(time);
        }

        // test availableEndTimes
        LocalTime startTime = LocalTime.of(8, 00);
        List<LocalTime> availEndTimes = availableEndTimes(startTime, availableRes);
        System.out.println("Available End Times: ");
        for (LocalTime time : availEndTimes) {
            System.out.println(time);
        }

        // test availableFIds
        LocalTime endTime = LocalTime.of(11, 00);
        List<Integer> availFIds = availableFIds(startTime, endTime, availableRes);
        System.out.println("Available FIds: ");
        for (Integer fId : availFIds) {
            System.out.println(fId);
        }

        // test getReservations
        getReservations(connect, "jsmith"); //will output printlns w info
        getReservations(connect, "jdoe"); //no output bc no reservations
        
        // test createReservation
        LocalDate b = LocalDate.of(2022, 5, 12);
        LocalTime start = LocalTime.of(1, 0);
        LocalTime end = LocalTime.of(2, 0);
        createReservation(connect, "jsmith", 2, "geometry exam", b, start, end);
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

    //given a student (username, unique), returns true if at least one reservation exists for said student.
    //print statement contains reservation info, can change return type of this method later if we need that info
    //as opposed to boolean.
    public static List<Reservation> getReservations(Connection con, String username) {
        ResultSet res;
        String sql = "SELECT * FROM Reservations WHERE username = ?;";
        List<Reservation> reservations = new ArrayList<>();
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, username);
            res = statement.executeQuery();

            while (res.next()) {
                Integer id = res.getInt("id");
                Integer fId = res.getInt("fId");
                String groupName = res.getString("groupName");
                LocalDate date = res.getDate("date").toLocalDate();
                LocalTime startTime = res.getTime("startTime").toLocalTime();
                LocalTime endTime = res.getTime("endTime").toLocalTime();
                reservations.add(new Reservation(id, username, fId, groupName, date, startTime, endTime));
                System.out.println("Reservation for student with fId: " + fId + ", date: " + date + ", start: " + startTime + ", end: " + endTime + ", groupName: " + groupName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    //creates a reservation for a student, includes all necessary info for Reservations table
    public static boolean createReservation(Connection con, String username, Integer fId, String groupName, LocalDate date, LocalTime startTime, LocalTime endTime) {
        ResultSet res;
        String sql = "INSERT INTO Reservations (username, fId, groupName, date, startTime, endTime) VALUES (?, ?, ?, ?, ?, ?);";
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, username);
            statement.setInt(2, fId);
            statement.setString(3, groupName);
            statement.setDate(4, Date.valueOf(date));
            statement.setTime(5, Time.valueOf(startTime));
            statement.setTime(6, Time.valueOf(endTime));

            statement.executeUpdate();
            System.out.println("Added reservation for Student: " + username + " in fishbowl: " + fId + " starting at: " + startTime + " until: " + endTime);
        } catch (SQLException e) {
            System.out.println("Unable to add reservation.");

            e.printStackTrace();
        }

        return false;
    }

    public static boolean deleteReservation(Connection con, String username, Integer selectDelete) {
        ResultSet res;
        //String sql = "INSERT INTO Reservations (username, fId, groupName, date, startTime, endTime) VALUES (?, ?, ?, ?, ?, ?);";
        String sql = "DELETE FROM Reservations WHERE id = ?;";
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, selectDelete);
            statement.executeUpdate();
            System.out.println("Deleted Reservation");
        } catch (SQLException e) {
            System.out.println("Unable to remove reservation.");

            e.printStackTrace();
        }

        return false;
    }

    // given the username and password, returns true if student is in the Students database
    // else returns false
    public static boolean validateStudent(Connection con, String username, String password) {
        ResultSet rs;
        String sql = "SELECT COUNT(*) AS Count FROM Students " +
                "WHERE username = ? AND password = ?;";
        Integer count = 0;
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            // System.out.println(statement);
            rs = statement.executeQuery();

            rs.next();
            count = rs.getInt("Count");
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
    public static List<AvailableRes> availableFishbowls(Connection con, LocalDate resDate) {
        ResultSet res;
        ResultSet fishbowls;
        List<AvailableRes> availableRes = new ArrayList<>();
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

            String query = "SELECT DISTINCT id, capacity, loudness FROM Fishbowls;";
            Statement statement2 = con.createStatement();
            fishbowls = statement2.executeQuery(query);

            HashMap<Integer, String> CapacityByFishbowl = new HashMap<>();
            HashMap<Integer, String> LoudnessByFishbowl = new HashMap<>();
            while (fishbowls.next()) {
                Integer fId = fishbowls.getInt("id");
                if (!ResTimesByFishbowl.containsKey(fId)) {
                    ResTimesByFishbowl.put(fId, new ArrayList<>());
                }
                String capacity = fishbowls.getString("capacity");
                CapacityByFishbowl.put(fId, capacity);
                String loudness = fishbowls.getString("loudness");
                LoudnessByFishbowl.put(fId, loudness);
            }

            for (Map.Entry reserve : ResTimesByFishbowl.entrySet()) {
                Integer fId = (Integer) reserve.getKey();
                List<Pair<LocalDateTime, LocalDateTime>> booked = (List<Pair<LocalDateTime, LocalDateTime>>) reserve.getValue();
                List<LocalDateTime> available = getAvailableTimes(resDate, booked);
                for (LocalDateTime time : available) {
                    String capacity = CapacityByFishbowl.get(fId);
                    String loudness = LoudnessByFishbowl.get(fId);
                    availableRes.add(new AvailableRes(fId, capacity, loudness, time));
                    // System.out.println("Fishbowl with fId: " + fId + ", capacity: " + capacity + "and loudness: " + loudness + " is available at " + time);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        availableRes.sort(Comparator.comparing(AvailableRes::getTime));
        return availableRes;
    }

    public static List<LocalTime> availableStartTimes(List<AvailableRes> availableRes) {
        Set<LocalTime> startTimes = availableRes.stream().map(x -> x.getTime().asLocalTime()).collect(Collectors.toSet());
        List<LocalTime> startTimesList = new ArrayList<>(startTimes.stream().toList());
        Collections.sort(startTimesList);
        return startTimesList;
    }

    public static List<LocalTime> availableEndTimes(LocalTime startTime, List<AvailableRes> availableRes) {
        Set<LocalTime> availTimes = availableRes.stream().map(x -> x.getTime().asLocalTime()).collect(Collectors.toSet());
        Set<LocalTime> endTimes = new HashSet<>(Arrays.asList(startTime.plusHours(1)));

        if (availTimes.contains(startTime.plusHours(1))) {
            endTimes.add(startTime.plusHours(2));
            if (availTimes.contains(startTime.plusHours(2))) {
                endTimes.add(startTime.plusHours(3));
            }
        }

        List<LocalTime> endTimesList = new ArrayList<>(endTimes.stream().toList());
        Collections.sort(endTimesList);
        return endTimesList;
    }

    public static List<Integer> availableFIds(LocalTime startTime, LocalTime endTime, List<AvailableRes> availableRes)  {
        List<Integer> allAvailFIds = availableRes.stream().map(x -> x.getFId()).collect(Collectors.toList());
        List<LocalTime> allAvailTimes = availableRes.stream().map(x -> x.getTime().asLocalTime()).collect(Collectors.toList());

        Integer difference = Math.toIntExact(ChronoUnit.HOURS.between(startTime, endTime));
        // System.out.println("Difference: " + difference);

        List<Integer> availFIds = new ArrayList<>();
        for (int i = 0; i < availableRes.size(); i++) {
            LocalTime currStartTime = allAvailTimes.get(i);
            LocalTime currEndTimePlus1 = currStartTime.plusHours(difference - 1);
            if (currStartTime == startTime && allAvailTimes.contains(currEndTimePlus1)) {
                availFIds.add(allAvailFIds.get(i));
            }
        }

        Collections.sort(availFIds);
        return availFIds;
    }

    public static List<Integer> getResIds(Connection con, String username) {
        ResultSet res;
        String sql = "SELECT id FROM Reservations WHERE username = ?;";
        List<Integer> resIds = new ArrayList<>();
        try {
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, username);
            res = statement.executeQuery();

            while (res.next()) {
                Integer id = res.getInt("id");
                resIds.add(id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resIds;
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


