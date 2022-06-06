package com.example.demo;

import java.time.LocalDateTime;

public class AvailableRes {
    public int fId;
    public String capacity;
    public String loudness;
    public LocalDateTime time;

    public AvailableRes(int fId, String capacity, String loudness, LocalDateTime time) {
        this.fId = fId;
        this.capacity = capacity;
        this.loudness = loudness;
        this.time = time;
    }
}
