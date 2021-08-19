package com.example.hows_today;

public class Weather {
    protected final String temperature;     // TMP 기온
    protected final char chTemperature = 'º';
    protected final String date;

    public Weather(String TMP, String date) {
        this.temperature = TMP;
        this.date = date;
    }
}
