package com.example.hows_today;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.LocationManager;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {
    private final Time time = new Time();

    private TextView tv_address;
    private TextView tv_weather;
    private TextView tv_temperature;
    private TextView tv_minMaxTemperature;
    private TextView tv_rainAmount;

    private LinearLayout ll_dust;
    private ImageView iv_dust;
    private TextView tv_dust;

    private LinearLayout ll_microDust;
    private ImageView iv_microDust;
    private TextView tv_microDust;

    private LinearLayout ll_uv;
    private ImageView iv_uv;
    private TextView tv_uv;

    private LinearLayout ll_humidity;
    private ImageView iv_humidity;
    private TextView tv_humidity;

    private RecyclerView recyclerView;
    private FutureWeatherListAdapter futureWeatherListAdapter;
    private LinearLayoutManager linearLayoutManager;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private final Activity activity = this;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private Handler nowWeatherHandler;
    private Handler minWeatherHandler;
    private Handler futureHandler;
    private Handler emoticonLayoutHandler;

    private CreateAddress address;
    private ArrayList<Weather> nowWeather = new ArrayList<>();
    private ArrayList<Weather> futureWeathers = new ArrayList<>();
    private ArrayList<Weather> minMaxTemperatures = new ArrayList<>();
    private DustInfo dustInfo;
    private UvInfo uvInfo;

    @Override
    @SuppressLint("HandlerLeak")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ?????? ?????? ??????
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }

        setAddress();
        mainActivityPrint();

    }

    public void onRequestPermissionResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // ?????? ????????? PERMISSIONS_REQUEST_CODE ??????, ????????? ????????? ???????????? ???????????????
            boolean check_result = true;

            // ?????? ???????????? ?????? ????????? ?????? ?????????.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                // ?????? ?????? ????????? ??? ??????
            } else {
                // ????????? ???????????? ????????? ?????? ???????????? ?????? ????????? ??????????????? ?????? ??????
                // 2?????? ????????? ??????

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MainActivity.this, "??????????????? ?????? ???????????????. ?????? ?????? ???????????? ????????? ?????? ????????????", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "??????????????? ?????? ???????????????. ??????(??? ??????)?????? ????????? ???????????? ?????????.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setAddress() {
        CreateAddress createAddress = new CreateAddress(this);
        this.address = createAddress;

        this.tv_address = findViewById(R.id.tv_address);
        String addressPrint = createAddress.getGu() + " " + createAddress.getDong();
        this.tv_address.setText(addressPrint);
    }

    private void mainActivityPrint() {
        nowWeatherHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message m) {
                NowWeather now = (NowWeather) nowWeather.get(0);
                tv_weather = findViewById(R.id.tv_weather);
                tv_weather.setText(now.getWeatherCondition());

                tv_rainAmount = (TextView) findViewById(R.id.tv_rainAmount);
                tv_rainAmount.setText(now.getRainHour());

                tv_temperature = (TextView) findViewById(R.id.tv_temperature);
                tv_temperature.setText(now.getTemperature());

                ImageView iv_weatherImg = (ImageView) findViewById(R.id.iv_weatherImg);
                iv_weatherImg.setImageResource(now.getNowImage());

                ll_humidity = (LinearLayout) findViewById(R.id.ll_humidity);
                iv_humidity = (ImageView) findViewById(R.id.iv_humidity);
                tv_humidity = (TextView) findViewById(R.id.tv_humidity);

                // ?????????
                CenterImage humidity = new CenterImage(((NowWeather) nowWeather.get(0)).getHumidity(), ll_humidity, iv_humidity, tv_humidity);
                humidity.print();
            }
        };

        class NowWeatherThread implements Runnable {
            @Override
            public void run() {
                JsonParse parse = new JsonParse();

                WeatherApiConnect nowWeatherConn = new WeatherApiConnect(address, time, ForecastType.NOW_WEATHER);
                String nowWeatherJson = nowWeatherConn.getWeatherInfo();
                nowWeather = parse.JsonParse(nowWeatherJson, ForecastType.NOW_WEATHER);

                nowWeatherHandler.sendEmptyMessage(0);
            }
        }

        NowWeatherThread nowWeatherThread = new NowWeatherThread();
        Thread nowThread = new Thread(nowWeatherThread);

        minWeatherHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message m) {
                tv_minMaxTemperature = (TextView) findViewById(R.id.tv_minMaxTemperature);
                MinMaxTemperature minMaxTemp = (MinMaxTemperature) minMaxTemperatures.get(0);
                tv_minMaxTemperature.setText(minMaxTemp.getMaxMinTMP());
            }
        };

        class MinWeatherThread implements Runnable {
            @Override
            public void run() {
                JsonParse parse = new JsonParse();

                WeatherApiConnect minMaxTempConn = new WeatherApiConnect(address, time, ForecastType.MIN_MAX);
                String minMaxWeatherJson = minMaxTempConn.getWeatherInfo();
                minMaxTemperatures = parse.JsonParse(minMaxWeatherJson, ForecastType.MIN_MAX);

                minWeatherHandler.sendEmptyMessage(0);
            }
        }

        MinWeatherThread MinWeatherThread = new MinWeatherThread();
        Thread MinThread = new Thread(MinWeatherThread);

        futureHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message m) {
                recyclerView = (RecyclerView) findViewById(R.id.rv_futureWeather);
                linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);

                futureWeatherListAdapter = new FutureWeatherListAdapter(futureWeathers);
                recyclerView.setAdapter(futureWeatherListAdapter);
            }
        };

        class FutureWeatherThread implements Runnable {
            @Override
            public void run() {
                JsonParse parse = new JsonParse();

                WeatherApiConnect futureWeatherConn = new WeatherApiConnect(address, time, ForecastType.WEATHER_FORECAST);
                String futureWeatherJson = futureWeatherConn.getWeatherInfo();
                futureWeathers = parse.JsonParse(futureWeatherJson, ForecastType.WEATHER_FORECAST);

                futureHandler.sendEmptyMessage(0);
            }
        }

        FutureWeatherThread futureWeatherThread = new FutureWeatherThread();
        Thread futureThread = new Thread(futureWeatherThread);

        emoticonLayoutHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message m) {
                ll_dust = (LinearLayout) findViewById(R.id.ll_dust);
                iv_dust = (ImageView) findViewById(R.id.iv_dust);
                tv_dust = (TextView) findViewById(R.id.tv_dust);

                CenterImage dustPrint = new CenterImage(dustInfo.getDustInfo(), ll_dust, iv_dust, tv_dust);
                dustPrint.print();

                ll_microDust = (LinearLayout) findViewById(R.id.ll_microDust);
                iv_microDust = (ImageView) findViewById(R.id.iv_microDust);
                tv_microDust = (TextView) findViewById(R.id.tv_microDust);

                CenterImage microDust = new CenterImage(dustInfo.getMicroDustGrade(), ll_microDust, iv_microDust, tv_microDust);
                microDust.print();

                ll_uv = (LinearLayout) findViewById(R.id.ll_uv);
                iv_uv = (ImageView) findViewById(R.id.iv_uv);
                tv_uv = (TextView) findViewById(R.id.tv_uv);

                CenterImage uv = new CenterImage(uvInfo.getGrade(), ll_uv, iv_uv, tv_uv);
                uv.print();
            }
        };

        class EmoticonLayoutThread implements Runnable {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                dustInfo = new DustInfo(address);

                try {
                    uvInfo = new UvInfo(address);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                emoticonLayoutHandler.sendEmptyMessage(0);
            }
        }

        EmoticonLayoutThread emoticonLayoutThread = new EmoticonLayoutThread();
        Thread emoticonThread = new Thread(emoticonLayoutThread);

        ArrayList<Thread> threads = new ArrayList<>();

        threads.add(nowThread);
        threads.add(MinThread);
        threads.add(futureThread);
        threads.add(emoticonThread);

        for (Thread thread : threads) {
            thread.start();
        }
    }

    private boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("?????? ????????? ????????????");
        builder.setMessage("?????? ???????????? ???????????? ?????? ???????????? ???????????????.\n" + "?????? ????????? ?????? ???????????????????");
        builder.setCancelable(true);

        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });

        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    private void checkRunTimePermission() {
        // ????????? ????????? ??????
        // 1. ?????? ???????????? ????????? ????????? ??????
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED
                && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. ?????? ???????????? ????????? ?????????
            // ??????????????? 6.0 ?????? ????????? ????????? ???????????? ???????????? ????????? ?????? ??????????????? ??????
            // 3. ?????? ?????? ????????? ??? ??????
        } else {
            // ????????? ????????? ????????? ?????? ????????? ????????? ????????? ??????
            // 2?????? ????????? ?????? ( 3-1, 4-1 )


            // 3-1 ???????????? ????????? ????????? ??????
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {
                // 3-2 ????????? ??????????????? ???????????? ????????? ????????? ??????
                Toast.makeText(MainActivity.this, "??? ?????? ?????? ????????? ?????? ?????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();

                // 3-3 ??????????????? ????????? ????????? ?????????. ?????? ????????? onRequestPermissionResult ?????? ?????????
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            } else {
                // 4-1 ???????????? ????????? ????????? ??? ?????? ?????? ?????? ?????? ????????? ??????
                // ?????? ????????? onRequestPermissionResult ?????? ?????????
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    }




}