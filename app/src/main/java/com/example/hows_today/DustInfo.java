package com.example.hows_today;

import android.location.Address;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class DustInfo {

    private String dustGrade;
    private String microDustGrade;

    private final String SERVICE_KEY = "dkpU%2Bps2gYZHzAxIGg5PhhWwTELac3cyelA%2BiP8gWLwPefQilXdHDPfqbXKaR5FyhGqJ8gI7Y9Oppm89vVo5mA%3D%3D";
    private final String BASE_URL = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/";
    private final String ENCODE_TYPE = "UTF-8";
    private final String RESPONSE_TYPE = "json";
    private final String SUB_BASE_URL = "getMsrstnAcctoRltmMesureDnsty";
    private final String PAGE_NO = "1";
    private final String RESPONSE = "response";
    private final String BODY = "body";
    private final String ITEMS = "items";
    private final String DATA_TERM = "DAILY";
    private final String RESULT_COUNT = "1";
    private final String VER = "1.0";
    private final String stationName;
    private final String json;

    public DustInfo(CreateAddress address) {
        this.stationName = address.getGu();
        this.json = getJson();
        setGrade();
    }

    public String getDustInfo() {
        return this.dustGrade;
    }

    public String getMicroDustGrade() {
        return this.microDustGrade;
    }

    private void setGrade() {
        try {
            jsonParse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jsonParse() throws JSONException {
        JSONObject jsonObject = new JSONObject(this.json);
        JSONObject item = jsonObject.getJSONObject(this.RESPONSE).getJSONObject(this.BODY);
        JSONArray jsonArray = item.getJSONArray(this.ITEMS);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject weatherObject = jsonArray.getJSONObject(i);
            this.dustGrade = weatherObject.get("pm10Grade").toString();
            this.microDustGrade = weatherObject.get("pm25Grade").toString();
        }
    }

    private String getJson() {
        String result = "";
        try {
            URL url = new URL(createURL());
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
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();;
        }

        return result;
    }

    private String createURL() throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder(this.BASE_URL); /*URL*/

        sb.append(this.SUB_BASE_URL);
        sb.append("?").append(URLEncoder.encode("ServiceKey", this.ENCODE_TYPE)).append("=" + this.SERVICE_KEY); /*Service Key*/
        sb.append("&").append(URLEncoder.encode("returnType", this.ENCODE_TYPE)).append("=").append(URLEncoder.encode(this.RESPONSE_TYPE, this.ENCODE_TYPE));
        sb.append("&").append(URLEncoder.encode("numOfRows", this.ENCODE_TYPE)).append("=").append(URLEncoder.encode(this.RESULT_COUNT, this.ENCODE_TYPE));
        sb.append("&").append(URLEncoder.encode("pageNo", this.ENCODE_TYPE)).append("=").append(URLEncoder.encode(this.PAGE_NO, this.ENCODE_TYPE));
        sb.append("&").append(URLEncoder.encode("dataTerm", this.ENCODE_TYPE)).append("=").append(URLEncoder.encode(this.DATA_TERM, this.ENCODE_TYPE));
        sb.append("&").append(URLEncoder.encode("stationName", this.ENCODE_TYPE)).append("=").append(URLEncoder.encode(this.stationName, this.ENCODE_TYPE));
        sb.append("&").append(URLEncoder.encode("ver", this.ENCODE_TYPE)).append("=").append(URLEncoder.encode(this.VER, this.ENCODE_TYPE));

        return sb.toString();
    }
}
