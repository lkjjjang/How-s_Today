package com.example.hows_today;

import android.location.Address;
import android.provider.ContactsContract;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

// 공공데이터포털에 접속해 날씨 정보를 받아오는 클래스
// 클래스 생성시 ForecastType 으로 현재날씨, 미래날씨 변경하여 받아옴

public class ApiConnect {

    private String weatherInfo;

    private final String baseURL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/";
    private final String serviceKey = "dkpU%2Bps2gYZHzAxIGg5PhhWwTELac3cyelA%2BiP8gWLwPefQilXdHDPfqbXKaR5FyhGqJ8gI7Y9Oppm89vVo5mA%3D%3D";
    private final String now = "getUltraSrtNcst";    // 실황
    private final String forecast = "getVilageFcst"; // 예보
    private final String encodeType = "UTF-8";
    private final String responseType = "JSON";
    private final String pageNo = "1";
    private String resultCount;
    private String time;
    private String date;
    private String LocationX;
    private String LocationY;
    private ForecastType forecastType;

    public ApiConnect(Address address, ForecastType forecastType) {
        this.forecastType = forecastType;
        setTime();
        setLocation(address);
        connectStart();
    }

    public String getWeatherInfo() {
        return this.weatherInfo;
    }

    private void setTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        String getTime = sdf.format(date);

        this.date = getTime.substring(0, 8);                   // yyyy 년 MM 월 dd 일
        int hour = Integer.parseInt(getTime.substring(8, 10)); // 시
        int minute = Integer.parseInt(getTime.substring(10));  // 분

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
                    } else {
                        --hour;
                    }
                }

                result = Integer.toString(hour) + "00";
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
                } else if (hour == 0 || hour == 1 || hour == 2) { // 하루전 데이터를 가져 와야함
                    hour = 23;
                    int yesterday = Integer.parseInt(this.date);
                    yesterday--;
                    this.date = Integer.toString(yesterday);
                }

                result = Integer.toString(hour) + "00";
                break;
            default:
                break;
        }

        this.time = result;
    }

    private void setLocation(Address address) {
        Location CL = new Location(address);
        this.LocationX = Integer.toString((int) CL.getLocation().getX());
        this.LocationY = Integer.toString((int) CL.getLocation().getY());
    }

    private void connectStart() {
        String type = "";

        switch (this.forecastType) {
            case NOW_WEATHER:
                type = this.now;
                this.resultCount = "8";
                break;
            case WEATHER_FORECAST:
                type = this.forecast;
                this.resultCount = "875";
                break;
            default:
                break;
        }

        try {
            StringBuilder urlBuilder = new StringBuilder(this.baseURL); // baseURL

            urlBuilder.append(type);
            urlBuilder.append("?").append(URLEncoder.encode("ServiceKey", this.encodeType)).append("=" + this.serviceKey); // Service Key
            urlBuilder.append("&").append(URLEncoder.encode("pageNo", this.encodeType)).append("=").append(URLEncoder.encode(this.pageNo, this.encodeType)); // 페이지번호
            urlBuilder.append("&").append(URLEncoder.encode("numOfRows", this.encodeType)).append("=").append(URLEncoder.encode(this.resultCount, this.encodeType)); // 한 페이지 결과 수
            urlBuilder.append("&").append(URLEncoder.encode("dataType", this.encodeType)).append("=").append(URLEncoder.encode(this.responseType, this.encodeType)); // 자료형식(XML/JSON) Default: XML
            urlBuilder.append("&").append(URLEncoder.encode("base_date", this.encodeType)).append("=").append(URLEncoder.encode(this.date, this.encodeType)); // 년 월 일
            urlBuilder.append("&").append(URLEncoder.encode("base_time", this.encodeType)).append("=").append(URLEncoder.encode(this.time, this.encodeType)); // 시간
            urlBuilder.append("&").append(URLEncoder.encode("nx", this.encodeType)).append("=").append(URLEncoder.encode(this.LocationX, this.encodeType)); // X 좌표
            urlBuilder.append("&").append(URLEncoder.encode("ny", this.encodeType)).append("=").append(URLEncoder.encode(this.LocationY, this.encodeType)); // Y 좌표

            URL url = new URL(urlBuilder.toString());
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
}

