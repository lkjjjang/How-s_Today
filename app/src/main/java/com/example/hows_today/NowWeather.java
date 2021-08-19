package com.example.hows_today;

import java.util.HashMap;

public class NowWeather {

    protected final String rainForm;        // PTY 강수형태
    protected final String rainHour;        // PCP 강수량
    protected final String humidity;        // REH 습도
    protected final String temperature;     // TMP 기온
    protected final char chTemperature = 'º';
    //public static int resultCount = 4; // 요청 갯수

    public NowWeather(HashMap<String, String> weather) {
        this.rainForm = weather.get(ResponseCode.POP.toString());
        this.rainHour = weather.get(ResponseCode.PCP.toString());
        this.humidity = weather.get(ResponseCode.REH.toString());
        this.temperature = weather.get(ResponseCode.TMP.toString());
    }

    public NowWeather(String rainForm, String rainHour, String humidity, String temperature) {
        this.rainForm = rainForm;
        this.rainHour = rainHour;
        this.humidity = humidity;
        this.temperature = temperature;
    }

    //# 특정 요소의 코드값 및 범주
    //- 하늘상태(SKY) 코드 : 맑음(1), 구름많음(3), 흐림(4)
    //- 강수형태(PTY) 코드 : (초단기) 없음(0), 비(1), 비/눈(2), 눈(3), 빗방울(5), 빗방울눈날림(6), 눈날림(7)
    //                      (단기) 없음(0), 비(1), 비/눈(2), 눈(3), 소나기(4)
    //- 초단기예보, 단기예보 강수량(RN1, PCP) 범주 및 표시방법(값)
    //범주	문자열표시
    //1mm 미만	1mm 미만
    //1mm 이상 30mm 미만	정수값
    //(1mm~29 mm)
    //30 mm 이상 50 mm 미만	30~50mm
    //50 mm 이상	50mm 이상
    //
    //
    //예) PCP = 6 일 경우 강수량은 6mm
    //    PCP = 30 일 경우 강수량은 30~50mm
    protected String getWeatherConditionOrNull() {
        int code = Integer.parseInt(this.rainForm);

        String result = "";

        switch (code) {
            case 0:
                return null;
            case 1:
                result = "비";
                break;
            case 2:
                result = "비 또는 눈";
                break;
            case 3:
                result = "눈";
                break;
            case 4:
                result = "소나기";
                break;
            case 5:
                result = "빗방울";
                break;
            case 6:
                result = "빗방울 또는 눈날림";
                break;
            case 7:
                result = "눈날림";
                break;
            default:
                break;
        }

        return result;
    }

    public String getRainForm() {
        return rainForm;
    }

    public String getRainHour() {
        float rainInt = Float.parseFloat(this.rainHour);

        if (rainInt == 0.0) {
            return "";
        } else if (rainInt < 1.0) {
            return "강수량 1mm 미만";
        } else if (rainInt >= 1.0 && rainInt < 30.0) {
            return "강수량" + Integer.toString((int) rainInt) + "mm";
        } else if (rainInt < 50.0) {
            return "강수량 30mm ~ 50mm";
        } else {
            return "강수량 50mm 이상";
        }
    }

    public String getHumidity() {
        return humidity;
    }

    public String getTemperature() {
        int temp = (int) Double.parseDouble(this.temperature);
        return Integer.toString(temp) + this.chTemperature;
    }
}

