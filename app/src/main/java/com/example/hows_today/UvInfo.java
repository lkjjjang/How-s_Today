package com.example.hows_today;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UvInfo {

    private String grade;

    private final String BASE_URL = "https://api.openuv.io/api/v1/uv";
    private final String SERVICE_KEY = "91ebb421f28adebcdc572b32053ddfee";
    private final String HEADER_NAME = "x-access-token";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public UvInfo(CreateAddress address) throws IOException, JSONException {
        setGrade(address);
    }

    public String getGrade() {
        return this.grade;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setGrade(CreateAddress address) throws IOException, JSONException {
        String timePoint = LocalDateTime.now().toString();

        StringBuilder sb = new StringBuilder(this.BASE_URL);
        sb.append("?");
        sb.append("lat=");
        sb.append(address.getLat());
        sb.append("&");
        sb.append("lng=");
        sb.append(address.getLon());
        sb.append("&");
        sb.append("dt=");
        sb.append(timePoint);

        String url = sb.toString();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader(this.HEADER_NAME, this.SERVICE_KEY)
                .build();

        Response response = client.newCall(request).execute();

        assert response.body() != null;

        String uvInfo = response.body().string(); // response.body().string() 한번 이상 호출하면 예외발생

        System.out.println(uvInfo);

        JSONObject jsonObject = new JSONObject(uvInfo);
        JSONObject item = jsonObject.getJSONObject("result");
        String str = item.getString("uv_max");

        double rounds = Double.parseDouble(str);
        int uvGarde = (int) (rounds + 0.5);

        // 미세먼지 등급과 함께 사용하기 위해 2로 나눔
        this.grade = Integer.toString(uvGarde / 2);
    }

}
