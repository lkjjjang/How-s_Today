package com.example.hows_today;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Time {

    private String time;

    public Time() {
        setTime();
    }

    public String getYear() {
        return this.time.substring(0, 4);
    }

    public String getMonth() {
        return this.time.substring(4, 6);
    }

    public String getDay() {
        return this.time.substring(6, 8);
    }

    public String getHour() {
        return this.time.substring(8, 10);
    }

    public String getMinute() {
        return this.time.substring(10);
    }

    private void setTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        this.time = sdf.format(date);
    }
}
