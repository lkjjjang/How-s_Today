package com.example.hows_today;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class JsonParse {

    private final String RESPONSE = "response";
    private final String BODY = "body";
    private final String ITEMS = "items";
    private final String OBJECT_TITLE = "item";
    private final String KEY = "category";
    private final String NOW_VALUE = "obsrValue";
    private final String FUTURE_VALUE = "fcstValue";
    private final String MIN_MAX_VALUE = "fcstValue";
    private final String MIN = "TMN";
    private final String MAX = "TMX";
    private final String NOT_USED_CODE_RN1 = "RN1";
    private final String NOT_USED_CODE_T1H = "T1H";
    private final String BASE_DATE = "baseDate";
    private final String BASE_TIME = "baseTime";

    private String valueStr;
    private int outSideLoop;
    private int inSideLoop;

    private final int nowTemperatureCount = 1;
    private final int nowWeatherResponseCount = 8;

    private final int threeDaysInfoCount = 48; // 1시간단위 3일치 정보
    private final int futureWeatherResponseCount = 14 - 1 - 2; // 1 = WAV 자료가 안옴, 2 = TMN TMX 규칙적으로 오지않음

    public ArrayList<Weather> JsonParse(String weatherJson, ForecastType forecastType) {
        if (weatherJson == null) {
            Log.e(">>", "weatherJson is Null!");
            assert false;
        }

        setBase(forecastType);
        /*
        String valueStr = "";
        int outSideLoop = 0;
        int inSideLoop = 0;

        switch (forecastType) {
            case NOW_WEATHER:
                valueStr = this.NOW_VALUE;
                outSideLoop = this.nowTemperatureCount;
                inSideLoop = this.nowWeatherResponseCount;
                break;
            case WEATHER_FORECAST:
                valueStr = this.FUTURE_VALUE;
                outSideLoop = this.threeDaysInfoCount;
                inSideLoop = this.futureWeatherResponseCount;
                break;
            case MIN_MAX:
                valueStr = this.MIN_MAX_VALUE;
                break;
            default:
                assert false;
                break;
        }*/

        try {
            ArrayList<Weather> results = new ArrayList<>();

            switch (forecastType) {
                case MIN_MAX:
                    results = getMinMax(weatherJson);
                    break;
                case NOW_WEATHER:
                    results = getNow(weatherJson);
                    break;
                case WEATHER_FORECAST:
                    results = getFuture(weatherJson);
                    break;
                default:
                    assert false;
                    break;
            }

            return results;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private ArrayList<Weather> getNow(String weatherJson) throws JSONException {
        JSONObject jsonObject = new JSONObject(weatherJson);
        JSONObject item = jsonObject.getJSONObject(this.RESPONSE).getJSONObject(this.BODY).getJSONObject(this.ITEMS);
        JSONArray jsonArray = item.getJSONArray(this.OBJECT_TITLE);

        HashSet<String> codeSample = new HashSet<>();
        // PTY, PCP, REH, TMP, DATE 필요한 데이터만 담아서 사용
        ResponseCode[] responseCodes = new ResponseCode[] {ResponseCode.PTY, ResponseCode.PCP, ResponseCode.REH, ResponseCode.TMP, ResponseCode.DATE};

        for (ResponseCode responseCode : responseCodes) {
            codeSample.add(responseCode.toString());
        }

        HashMap<String, String> resultMap = new HashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject weatherObject = jsonArray.getJSONObject(i);

            String dateKey = weatherObject.get(this.BASE_DATE).toString() + weatherObject.get(this.BASE_TIME).toString();
            resultMap.put(ResponseCode.DATE.toString(), dateKey);

            String key = weatherObject.get(this.KEY).toString();
            String value = weatherObject.get(valueStr).toString();

            if (key.equals(this.NOT_USED_CODE_RN1)) {
                key = ResponseCode.PCP.toString();
            }

            if (key.equals(this.NOT_USED_CODE_T1H)) {
                key = ResponseCode.TMP.toString();
            }

            if (codeSample.contains(key)) {
                resultMap.put(key, value);
            }
        }

        ArrayList<Weather> result = new ArrayList<>();

        NowWeather nowWeather = new NowWeather(resultMap.get(ResponseCode.PTY.toString()),
                resultMap.get(ResponseCode.PCP.toString()), resultMap.get(ResponseCode.REH.toString()),
                resultMap.get(ResponseCode.TMP.toString()), resultMap.get(ResponseCode.DATE.toString()));

        result.add(nowWeather);

        return result;
    }


    private ArrayList<Weather> getMinMax(String weatherJson) throws JSONException {
        JSONObject jsonObject = new JSONObject(weatherJson);
        JSONObject item = jsonObject.getJSONObject(this.RESPONSE).getJSONObject(this.BODY).getJSONObject(this.ITEMS);
        JSONArray jsonArray = item.getJSONArray(this.OBJECT_TITLE);

        String min = "";
        String max = "";
        String dateTime = "";

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject weatherObject = jsonArray.getJSONObject(i);

            if (dateTime.length() == 0) {
                dateTime = weatherObject.get(this.BASE_DATE).toString() + weatherObject.get(this.BASE_TIME).toString();
            }

            if (weatherObject.get(this.KEY).equals(this.MIN)) {
                if (min.length() == 0) {
                    min = weatherObject.get(this.valueStr).toString();
                }
            }

            if (weatherObject.get(this.KEY).equals(this.MAX)) {
                if (max.length() == 0) {
                    max = weatherObject.get(this.valueStr).toString();
                }
            }
        }

        assert min.length() != 0 && max.length() != 0 && dateTime.length() != 0;

        ArrayList<Weather> result = new ArrayList<>();
        MinMaxTemperature minMaxTemperature = new MinMaxTemperature(min, max, dateTime);
        result.add(minMaxTemperature);

        return result;
    }

    private ArrayList<Weather> getFuture(String weatherJson) throws JSONException {
        int objCount = 0;

        FutureWeather futureWeather;
        ArrayList<Weather> futureWeathers = new ArrayList<>();

        HashSet<String> codeSample = new HashSet<>();
        ResponseCode[] responseCodes = ResponseCode.values();
        // 필요한 데이터만 담아서 사용 해당 메서드에서는 ResponseCode 모두 필요함
        for (ResponseCode responseCode : responseCodes) {
            codeSample.add(responseCode.toString());
        }

        JSONObject jsonObject = new JSONObject(weatherJson);
        JSONObject item = jsonObject.getJSONObject(this.RESPONSE).getJSONObject(this.BODY).getJSONObject(this.ITEMS);
        JSONArray jsonArray = item.getJSONArray(this.OBJECT_TITLE);

        for (int i = 0; i < this.outSideLoop; i++) {
            HashMap<String, String> responseValues = new HashMap<>();

            for (int j = 0; j < this.inSideLoop; j++) {
                JSONObject weatherObject = jsonArray.getJSONObject(objCount++);

                String dateKey = weatherObject.get(this.BASE_DATE).toString() + weatherObject.get(this.BASE_TIME).toString();
                responseValues.put(ResponseCode.DATE.toString(), dateKey);

                if (weatherObject.get(this.KEY).equals(this.MIN) || weatherObject.get(this.KEY).equals(this.MAX)) {
                        j--; // -- 하지 않으면 순서가 밀림
                }

                String key = weatherObject.get(this.KEY).toString();
                String value = weatherObject.get(this.valueStr).toString();

                if (codeSample.contains(key)) {
                    responseValues.put(key, value);
                }
            }

            //TMP, SKY, PTY, POP, PCP, REH, SNO, DATE
            futureWeather = new FutureWeather(responseValues.get(ResponseCode.TMP.toString()), responseValues.get(ResponseCode.SKY.toString()),
                    responseValues.get(ResponseCode.PTY.toString()), responseValues.get(ResponseCode.POP.toString()),
                    responseValues.get(ResponseCode.PCP.toString()), responseValues.get(ResponseCode.REH.toString()),
                    responseValues.get(ResponseCode.SNO.toString()), responseValues.get(ResponseCode.DATE.toString()));

            futureWeathers.add(futureWeather);
        }
        return futureWeathers;
    }

    private void setBase(ForecastType forecastType) {
        switch (forecastType) {
            case NOW_WEATHER:
                this.valueStr = this.NOW_VALUE;
                this.outSideLoop = this.nowTemperatureCount;
                this.inSideLoop = this.nowWeatherResponseCount;
                break;
            case WEATHER_FORECAST:
                this.valueStr = this.FUTURE_VALUE;
                this.outSideLoop = this.threeDaysInfoCount;
                this.inSideLoop = this.futureWeatherResponseCount;
                break;
            case MIN_MAX:
                this.valueStr = this.MIN_MAX_VALUE;
                break;
            default:
                assert false;
                break;
        }
    }
}
