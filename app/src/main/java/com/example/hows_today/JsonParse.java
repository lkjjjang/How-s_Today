package com.example.hows_today;

import org.json.JSONArray;
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
    private ArrayList<HashMap<String, String>> weatherList = new ArrayList<>();

    public HashMap<String, String> JsonParse(String weatherJson, ForecastType forecastType) {
        String valueStr = "";

        switch (forecastType) {
            case NOW_WEATHER:
                valueStr = this.NOW_VALUE;
                break;
            case WEATHER_FORECAST:
                valueStr = this.FUTURE_VALUE;
                break;
            default:
                break;
        }

        try {

            ArrayList<FutureWeather> futureWeathers = new ArrayList<>();
            HashSet<String> codeSample = new HashSet<>();
            ResponseCode[] responseCodes = ResponseCode.values();

            for (ResponseCode responseCode : responseCodes) {
                codeSample.add(responseCode.toString());
            }

            JSONObject jsonObject = new JSONObject(weatherJson);
            JSONObject item = jsonObject.getJSONObject(this.RESPONSE).getJSONObject(this.BODY).getJSONObject(this.ITEMS);
            JSONArray jsonArray = item.getJSONArray(this.OBJECT_TITLE);

            FutureWeather futureWeather;

            for (int i = 0; i < 48; i++) {
                HashMap<String, String> code = new HashMap<>();
                boolean isBool = false;

                for (int j = 0; j < 11; j++) {
                    JSONObject weatherObject = jsonArray.getJSONObject(i);

                    if (weatherObject.get(this.KEY).equals("TMN") || weatherObject.get(this.KEY).equals("TMX")) {
                        isBool = true;
                    }

                    if (codeSample.contains(weatherObject.get(this.KEY).toString())) {
                        code.put(weatherObject.get(this.KEY).toString(), weatherObject.get(valueStr).toString());
                    }
                }

                //String TMP, String SKY, String PTY, String POP, String PCP, String REH, String SNO, String TMN, String TMX
                if (isBool) {
                    futureWeather = new FutureWeather(code.get(ResponseCode.TMP.toString()), code.get(ResponseCode.SKY.toString()),
                            code.get(ResponseCode.PTY.toString()), code.get(ResponseCode.POP.toString()),
                            code.get(ResponseCode.PCP.toString()), code.get(ResponseCode.REH.toString()),
                            code.get(ResponseCode.SNO.toString()), code.get(ResponseCode.TMN.toString()),
                            code.get(ResponseCode.TMX.toString()));
                } else {
                    futureWeather = new FutureWeather(code.get(ResponseCode.TMP.toString()), code.get(ResponseCode.SKY.toString()),
                            code.get(ResponseCode.PTY.toString()), code.get(ResponseCode.POP.toString()),
                            code.get(ResponseCode.PCP.toString()), code.get(ResponseCode.REH.toString()),
                            code.get(ResponseCode.SNO.toString()));
                }

                futureWeathers.add(futureWeather);

            }




            /*
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject weatherObject = jsonArray.getJSONObject(i);

                String key = weatherObject.get(this.KEY).toString();

                if (key.equals(ResponseCode.T1H.toString())) {
                    key = ResponseCode.TMP.toString();
                } else if (key.equals(ResponseCode.RN1.toString())) {
                    key = ResponseCode.PCP.toString();
                }

                String value = weatherObject.get(valueStr).toString();
                weatherInfo.put(key, value);
            }*/

            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
