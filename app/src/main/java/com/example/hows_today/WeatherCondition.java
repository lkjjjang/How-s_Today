package com.example.hows_today;

import java.util.ArrayList;

public class WeatherCondition {

    private ArrayList<Weather> weathers = new ArrayList<>();
    private final String ALWAYS = "대체로";
    private final String POINT = "한때";
    private final String MORNING = "오전";
    private final String AFTERNOON = "오후";
    private final String NIGHT = "저녁";
    private final String SUN = "맑음";
    private final String CLOUDY = "흐림";
    private final String RAIN = "비";
    private final int weatherCondition = 0;

    public WeatherCondition(ArrayList<Weather> arrayList) {
        this.weathers = arrayList;
    }

    public String getWeatherCondition() {

        for (Weather list : this.weathers) {
            FutureWeather future = (FutureWeather) list;
            String re = future.rainHour;
        }


        FutureWeather test = (FutureWeather) this.weathers.get(0);

        return "맑음";
    }
}
