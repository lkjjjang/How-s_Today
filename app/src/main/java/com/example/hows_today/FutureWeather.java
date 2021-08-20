package com.example.hows_today;

import java.util.HashMap;

public class FutureWeather extends NowWeather {

    private final String rainProbability; // POP 강수확률
    private final String snowHour;        // SNO 1시간 적설량
    private final String skyCondition;    // SKY 하늘상태

    //public static int resultCount = 5 + NowWeather.resultCount; // 요청 갯수

    public FutureWeather(String TMP, String SKY, String PTY, String POP, String PCP, String REH, String SNO, String DATE) {
        super(PTY, PCP, REH, TMP, DATE);
        this.rainProbability = POP;
        this.snowHour = SNO;
        this.skyCondition = SKY;
    }

    public String getSkyCondition() {
        return this.skyCondition;
    }

    public String getDate() {
        // 202108182400 형식
        return this.date;
    }

    public String getRainProbability() {
        return rainProbability;
    }

    public String getSnowHour() {
        return snowHour;
    }


}

