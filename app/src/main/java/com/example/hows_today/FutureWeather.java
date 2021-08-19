package com.example.hows_today;

import java.util.HashMap;

public class FutureWeather extends NowWeather {

    private final String rainProbability; // POP 강수확률
    private final String snowHour;        // SNO 1시간 적설량
    private final String skyCondition;// SKY 하늘상태

    //public static int resultCount = 5 + NowWeather.resultCount; // 요청 갯수

    public FutureWeather(String TMP, String SKY, String PTY, String POP, String PCP, String REH, String SNO, String date) {
        super(PTY, PCP, REH, TMP, date);
        this.rainProbability = POP;
        this.snowHour = SNO;
        this.skyCondition = SKY;
    }

    public String getRainProbability() {
        return rainProbability;
    }

    public String getSnowHour() {
        return snowHour;
    }

    public String getWeatherCondition() {
        String result = super.getWeatherConditionOrNull();

        if (result != null) {
            return result;
        }

        result = weatherConditionParse(this.skyCondition);
        return result;
    }

    private String weatherConditionParse(String weatherCondition) {
        String weather = super.getWeatherConditionOrNull();

        if (weather != null) {
            return weather;
        }

        int skyCode = Integer.parseInt(weatherCondition);
        String result = "";

        if (skyCode >= 0 && skyCode <= 5) {
            result = "맑음";
        } else if (skyCode >= 6 && skyCode <= 8) {
            result = "구름많음";
        } else {
            result = "흐림";
        }

        return result;
    }
}

