package com.example.demo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Reservation {
    public int id;
    public String username;
    public int fId;
    public String groupName;
    public LocalDate date;
    public LocalTime startTime;
    public LocalTime endTime;

    public Reservation(int id, String username, int fId, String groupName, LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.username = username;
        this.fId = fId;
        this.groupName = groupName;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() { return this.id; }
    public int getFId() { return this.fId; }
    public String getGroupName() { return this.groupName; }
    public LocalDate getDate() { return this.date; }
    public LocalTime getStartTime() { return this.startTime; }
    public LocalTime getEndTime() { return this.endTime; }
}
