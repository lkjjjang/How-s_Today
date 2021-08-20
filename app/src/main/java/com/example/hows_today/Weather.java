package com.example.hows_today;

public class Weather {
    protected final String temperature;     // TMP 기온
    protected final char chTemperature = 'º';

    public Weather(String TMP) {
        this.temperature = TMP;
    }
}
