package com.example.demo;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class CustomDate extends java.sql.Date {

    public CustomDate(long date) {
        super(date);
    }

    @Override
    public String toString() {
        return new SimpleDateFormat("HH:mm MM/dd/yyyy").format(this);
    }

    public LocalTime asLocalTime() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("HH:mm MM/dd/yyyy");
        LocalDateTime localDateTime = LocalDateTime.parse(this.toString(), df);
        return localDateTime.toLocalTime();
    }
}
