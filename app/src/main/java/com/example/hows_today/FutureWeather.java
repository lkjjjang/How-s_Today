package com.example.hows_today;

import java.util.HashMap;

public class FutureWeather extends NowWeather {

    private final String rainProbability; // POP 강수확률
    private final String snowHour;        // SNO 1시간 적설량
    private final String weatherCondition;// SKY 하늘상태
    private String minTemperature;  // TMN 최저기온
    private String maxTemperature;  // TMX 최고기온
    //public static int resultCount = 5 + NowWeather.resultCount; // 요청 갯수

    public FutureWeather(HashMap<String, String> weather) {
        super(weather.get(ResponseCode.PTY.toString()), weather.get(ResponseCode.PCP.toString()),
                weather.get(ResponseCode.REH.toString()), weather.get(ResponseCode.TMP.toString()));

        this.rainProbability = weather.get(ResponseCode.POP.toString());
        this.snowHour = weather.get(ResponseCode.SNO.toString());
        this.weatherCondition = weather.get(ResponseCode.SKY.toString());
        this.minTemperature = weather.get(ResponseCode.TMN.toString());
        this.maxTemperature = weather.get(ResponseCode.TMX.toString());
    }

    public FutureWeather(String TMP, String SKY, String PTY, String POP, String PCP, String REH, String SNO, String TMN, String TMX) {
        super(PTY, PCP, REH, TMP);
        this.rainProbability = POP;
        this.snowHour = SNO;
        this.weatherCondition = SKY;
        this.minTemperature = TMN;
        this.maxTemperature = TMX;
    }

    public FutureWeather(String TMP, String SKY, String PTY, String POP, String PCP, String REH, String SNO) {
        super(PTY, PCP, REH, TMP);
        this.rainProbability = POP;
        this.snowHour = SNO;
        this.weatherCondition = SKY;
    }

    public String getMaxMinTMP() {
        int min = (int) Double.parseDouble(this.minTemperature);
        int max = (int) Double.parseDouble(this.maxTemperature);

        StringBuilder sb = new StringBuilder();

        sb.append("최저 ").append(min).append(chTemperature);
        sb.append(" / ");
        sb.append("최고 ").append(max).append(chTemperature);

        return sb.toString();
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

        result = weatherConditionParse(this.weatherCondition);
        return result;
    }

    public String getMinTemperature() {
        return minTemperature;
    }

    public String getMaxTemperature() {
        return maxTemperature;
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

