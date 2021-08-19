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

    private final int nowTemperatureCount = 1;
    private final int nowWeatherResponseCount = 8;

    private final int threeDaysInfoCount = 48; // 1시간단위 3일치 정보
    private final int futureWeatherResponseCount = 14 - 1 - 2; // 1 = WAV 자료가 안옴, 2 = TMN TMX 규칙적으로 오지않음

    public ArrayList<Weather> JsonParse(String weatherJson, ForecastType forecastType) {
        if (weatherJson == null) {
            Log.e(">>", "weatherJson is Null!");
            assert false;
        }

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
        }

        try {

            HashSet<String> codeSample = new HashSet<>();
            ResponseCode[] responseCodes = ResponseCode.values();

            for (ResponseCode responseCode : responseCodes) {
                codeSample.add(responseCode.toString());
            }

            ArrayList<Weather> results = new ArrayList<>();

            switch (forecastType) {
                case MIN_MAX:
                    results = getMinMax(valueStr, weatherJson);
                    break;
                case NOW_WEATHER:
                    break;
                case WEATHER_FORECAST:
                    results = getFuture(outSideLoop, inSideLoop, valueStr, weatherJson, codeSample);
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

    private ArrayList<Weather> getMinMax(String valueStr, String weatherJson) throws JSONException {
        JSONObject jsonObject = new JSONObject(weatherJson);
        JSONObject item = jsonObject.getJSONObject(this.RESPONSE).getJSONObject(this.BODY).getJSONObject(this.ITEMS);
        JSONArray jsonArray = item.getJSONArray(this.OBJECT_TITLE);

        String min = "";
        String max = "";
        String dateTime = "";

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject weatherObject = jsonArray.getJSONObject(i);

            if (dateTime.length() == 0) {
                dateTime = weatherObject.get("fcstDate").toString() + weatherObject.get("fcstTime").toString();
            }

            if (weatherObject.get(this.KEY).equals("TMN")) {
                if (min.length() == 0) {
                    min = weatherObject.get(valueStr).toString();
                }
            }

            if (weatherObject.get(this.KEY).equals("TMX")) {
                if (max.length() == 0) {
                    max = weatherObject.get(valueStr).toString();
                }
            }
        }

        assert min.length() != 0 && max.length() != 0 && dateTime.length() != 0;

        ArrayList<Weather> result = new ArrayList<>();
        MinMaxTemperature minMaxTemperature = new MinMaxTemperature(min, max, dateTime);
        result.add(minMaxTemperature);

        return result;
    }

    private ArrayList<Weather> getFuture(int outSideLoop, int inSideLoop, String valueStr, String weatherJson, HashSet<String> codeSample) throws JSONException {
        int objCount = 0;

        FutureWeather futureWeather;
        ArrayList<Weather> futureWeathers = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(weatherJson);
        JSONObject item = jsonObject.getJSONObject(this.RESPONSE).getJSONObject(this.BODY).getJSONObject(this.ITEMS);
        JSONArray jsonArray = item.getJSONArray(this.OBJECT_TITLE);

        for (int i = 0; i < outSideLoop; i++) {
            HashMap<String, String> responseValues = new HashMap<>();

            for (int j = 0; j < inSideLoop; j++) {
                JSONObject weatherObject = jsonArray.getJSONObject(objCount++);

                String dateKey = weatherObject.get("fcstDate").toString() + weatherObject.get("fcstTime").toString();
                responseValues.put(ResponseCode.DATE.toString(), dateKey);

                if (weatherObject.get(this.KEY).equals("TMN") || weatherObject.get(this.KEY).equals("TMX")) {
                        j--; // -- 하지 않으면 순서가 밀림
                }

                String key = weatherObject.get(this.KEY).toString();
                String value = weatherObject.get(valueStr).toString();

                if (codeSample.contains(weatherObject.get(this.KEY).toString())) {
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
}
/*ArrayList<Weather> futureWeathers = new ArrayList<>();

            // 필요한 응답코드만 뽑아 배열을 만들어  JSONObject 가 배열에 포함되어 있을경우 자료 저장
            HashSet<String> codeSample = new HashSet<>();
            ResponseCode[] responseCodes = ResponseCode.values();

            for (ResponseCode responseCode : responseCodes) {
                codeSample.add(responseCode.toString());
            }

            // json 분할
            JSONObject jsonObject = new JSONObject(weatherJson);
            JSONObject item = jsonObject.getJSONObject(this.RESPONSE).getJSONObject(this.BODY).getJSONObject(this.ITEMS);
            JSONArray jsonArray = item.getJSONArray(this.OBJECT_TITLE);

            FutureWeather futureWeather;

            int objCount = 0;

            for (int i = 0; i < outSideCount; i++) {
                HashMap<String, String> responseValues = new HashMap<>();

                for (int j = 0; j < inSideLoop; j++) {
                    JSONObject weatherObject = jsonArray.getJSONObject(objCount++);

                    String dateKey = weatherObject.get("fcstDate").toString() + weatherObject.get("fcstTime").toString();
                    responseValues.put(ResponseCode.DATE.toString(), dateKey);

                    // 최저, 최고 기온 별도로 저장해두고  getMinMaxTemp() 로 호출
                    if (weatherObject.get(this.KEY).equals("TMN")) {
                        if (this.minTemp == null) {
                            this.minTemp = weatherObject.get(valueStr).toString();
                            this.minMaxTemp = dateKey;
                            j--; // -- 하지 않으면 순서가 밀림
                        }
                    }

                    if (weatherObject.get(this.KEY).equals("TMX")) {
                        if (this.maxTemp == null) {
                            this.maxTemp = weatherObject.get(valueStr).toString();
                            j--;
                        }
                    }

                    String key = weatherObject.get(this.KEY).toString();
                    String value = weatherObject.get(valueStr).toString();

                    if (codeSample.contains(weatherObject.get(this.KEY).toString())) {
                        responseValues.put(key, value);
                    }
                }

                //TMP, SKY, PTY, POP, PCP, REH, SNO, DATE
                futureWeather = new FutureWeather(responseValues.get(ResponseCode.TMP.toString()), responseValues.get(ResponseCode.SKY.toString()),
                        responseValues.get(ResponseCode.PTY.toString()), responseValues.get(ResponseCode.POP.toString()),
                        responseValues.get(ResponseCode.PCP.toString()), responseValues.get(ResponseCode.REH.toString()),
                        responseValues.get(ResponseCode.SNO.toString()), responseValues.get(ResponseCode.DATE.toString()));

                futureWeathers.add(futureWeather);
            }*/
