package com.example.hows_today;

import android.annotation.SuppressLint;
import android.location.Address;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

// 공공데이터포털에 접속해 날씨 정보를 받아오는 클래스
// 클래스 생성시 ForecastType 으로 현재날씨, 미래날씨 변경하여 받아옴

public class WeatherApiConnect {

    private String weatherInfo;

    private final String weatherBaseURL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/";
    private final String serviceKey = "dkpU%2Bps2gYZHzAxIGg5PhhWwTELac3cyelA%2BiP8gWLwPefQilXdHDPfqbXKaR5FyhGqJ8gI7Y9Oppm89vVo5mA%3D%3D";
    private final String now = "getUltraSrtNcst";    // 실황
    private final String forecast = "getVilageFcst"; // 예보
    private final String encodeType = "UTF-8";
    private final String weatherResponseType = "JSON";
    private final String pageNo = "1";

    private final String NOW_RESULT_COUNT = "8";
    private final String FORECAST_RESULT_COUNT = "1000";
    private final String MIN_MAX_RESULT_COUNT = "150";

    private final ForecastType forecastType;
    private final Time dateTime;

    private String resultCount;
    private String time;
    private String date;
    private String LocationX;
    private String LocationY;

    public WeatherApiConnect(CreateAddress address, Time dateTime, ForecastType forecastType) {
        this.forecastType = forecastType;
        this.dateTime = dateTime;
        setTime();
        setLocation(address);
        connectStart();
    }

    public String getWeatherInfo() {
        return this.weatherInfo;
    }

    private void turnBackDate() {
        int result = Integer.parseInt(this.date);
        result--;
        this.date = Integer.toString(result);
    }

    private void setTime() {
        this.date = this.dateTime.getYear() + this.dateTime.getMonth() + this.dateTime.getDay();
        int hour = Integer.parseInt(this.dateTime.getHour());
        int minute = Integer.parseInt(this.dateTime.getMinute());

        String result = "";

        switch (this.forecastType) {
            case NOW_WEATHER:
                // 초단기 실황
                // 매시각 30분 자료 생성, 10분마다 업데이트
                // 매시각 40분 부터 API 제공
                // 매시각 39분 까지는 baseTime 을 한시간 앞으로 변경
                if (minute < 40) {
                    if (hour == 0) {
                        hour = 23;
                        turnBackDate();
                    } else {
                        --hour;
                    }
                }

                String resultStr = Integer.toString(hour);

                if (resultStr.length() == 1) {
                    resultStr = "0" + resultStr;
                }

                result = resultStr + "00";

                break;
            case WEATHER_FORECAST:
                // 단기예보
                // baseTime 0200, 0500 처럼 3시간 단위
                // API 제공은 baseTime + 10분 부터

                if (hour == 3 || hour == 4) {
                    hour = 2;
                } else if (hour == 6 || hour == 7) {
                    hour = 5;
                } else if (hour == 9 || hour == 10) {
                    hour = 8;
                } else if (hour == 12 || hour == 13) {
                    hour = 11;
                } else if (hour == 15 || hour == 16) {
                    hour = 14;
                } else if (hour == 18 || hour == 19) {
                    hour = 17;
                } else if (hour == 21 || hour == 22) {
                    hour = 20;
                } else if (hour == 0 || hour == 1 || hour == 2) {
                    hour = 23;
                    turnBackDate();
                }

                String resultHour = Integer.toString(hour);
                if (resultHour.length() == 1) {
                    resultHour = "0" + resultHour;
                }

                result = resultHour + "00";
                break;
            case MIN_MAX:
                // 단기예보 02시 자료요청 최고, 최고 기온 저장
                if (hour >= 0 && hour <= 2) {
                    turnBackDate(); // 전일 23시 자료 요청
                }

                result = "0200";
            default:
                break;
        }

        this.time = result;
    }

    private void setLocation(CreateAddress address) {
        Location CL = new Location(address.getAddress());
        this.LocationX = Integer.toString((int) CL.getLocation().getX());
        this.LocationY = Integer.toString((int) CL.getLocation().getY());
    }

    private void connectStart() {
        try {
            URL url = new URL(createUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            System.out.println("Response code: " + conn.getResponseCode());

            BufferedReader rd;

            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }

            rd.close();
            conn.disconnect();

            this.weatherInfo = sb.toString();

            System.out.println(this.weatherInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String createUrl() throws UnsupportedEncodingException {
        String type = "";

        switch (this.forecastType) {
            case NOW_WEATHER:
                type = this.now;
                this.resultCount = this.NOW_RESULT_COUNT;
                break;
            case WEATHER_FORECAST:
                type = this.forecast;
                this.resultCount = this.FORECAST_RESULT_COUNT;
                break;
            case MIN_MAX:
                type = this.forecast;
                this.resultCount = this.MIN_MAX_RESULT_COUNT;
                break;
            default:
                assert false;
                break;
        }

        StringBuilder sb = new StringBuilder(this.weatherBaseURL); // baseURL

        sb.append(type);
        sb.append("?").append(URLEncoder.encode("ServiceKey", this.encodeType)).append("=" + this.serviceKey); // Service Key
        sb.append("&").append(URLEncoder.encode("pageNo", this.encodeType)).append("=").append(URLEncoder.encode(this.pageNo, this.encodeType)); // 페이지번호
        sb.append("&").append(URLEncoder.encode("numOfRows", this.encodeType)).append("=").append(URLEncoder.encode(this.resultCount, this.encodeType)); // 한 페이지 결과 수
        sb.append("&").append(URLEncoder.encode("dataType", this.encodeType)).append("=").append(URLEncoder.encode(this.weatherResponseType, this.encodeType)); // 자료형식(XML/JSON) Default: XML
        sb.append("&").append(URLEncoder.encode("base_date", this.encodeType)).append("=").append(URLEncoder.encode(this.date, this.encodeType)); // 년 월 일
        sb.append("&").append(URLEncoder.encode("base_time", this.encodeType)).append("=").append(URLEncoder.encode(this.time, this.encodeType)); // 시간
        sb.append("&").append(URLEncoder.encode("nx", this.encodeType)).append("=").append(URLEncoder.encode(this.LocationX, this.encodeType)); // X 좌표
        sb.append("&").append(URLEncoder.encode("ny", this.encodeType)).append("=").append(URLEncoder.encode(this.LocationY, this.encodeType)); // Y 좌표

        return sb.toString();
    }
}

