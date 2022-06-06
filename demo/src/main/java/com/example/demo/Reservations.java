package com.example.demo;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reservations {
    public int id;
    public String username;
    public int fId;
    public String groupName;
    public LocalDate date;
    public LocalTime startTime;
    public LocalTime endTime;

    public Reservations(int id, String username, int fId, String groupName, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.username = username;
        this.fId = fId;
        this.groupName = groupName;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
