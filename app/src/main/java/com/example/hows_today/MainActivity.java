package com.example.hows_today;

import androidx.annotation.NonNull;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {
    private TextView tv_address;
    private TextView tv_weather;
    private TextView tv_temperature;
    private TextView tv_minMaxTemperature;
    private TextView tv_rainAmount;
    private TextView iv_weatherImg;

    private RecyclerView recyclerView;
    private FutureWeatherListAdapter futureWeatherListAdapter;
    private LinearLayoutManager linearLayoutManager;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private Handler handler;
    private Activity activity = this;

    private Address address;
    private ArrayList<Weather> nowWeather = new ArrayList<>();
    private ArrayList<Weather> futureWeathers = new ArrayList<>();
    private ArrayList<Weather> minMaxTemperatures = new ArrayList<>();

    @Override
    @SuppressLint("HandlerLeak")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 위치 권한 설정
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }

        CreateAddress createAddress = new CreateAddress(this);
        this.address = createAddress.getAddress();

        this.tv_address = findViewById(R.id.tv_address);
        this.tv_address.setText(createAddress.getAddressStr());

        handler = new Handler() {
            @SuppressLint("SetTextI18n")
            @Override
            public void handleMessage(Message msg) {
                NowWeather now = (NowWeather) nowWeather.get(0);
                tv_weather = findViewById(R.id.tv_weather);
                tv_weather.setText(now.getWeatherCondition());

                tv_rainAmount = findViewById(R.id.tv_rainAmount);
                tv_rainAmount.setText(now.getRainHour());

                tv_temperature = findViewById(R.id.tv_temperature);
                tv_temperature.setText(now.getTemperature());

                ImageView iv_weatherImg = findViewById(R.id.iv_weatherImg);
                iv_weatherImg.setImageResource(now.getNowImage());

                tv_minMaxTemperature = findViewById(R.id.tv_minMaxTemperature);
                MinMaxTemperature minMaxTemp = (MinMaxTemperature) minMaxTemperatures.get(0);
                tv_minMaxTemperature.setText(minMaxTemp.getMaxMinTMP());

                // 주간예보 recyclerView 관련 부분
                recyclerView = (RecyclerView) findViewById(R.id.rv_futureWeather);
                linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(linearLayoutManager);

                futureWeatherListAdapter = new FutureWeatherListAdapter(futureWeathers);
                recyclerView.setAdapter(futureWeatherListAdapter);
            }
        };

        class NewRunnable implements Runnable {

            @Override
            public void run() {
                JsonParse parse = new JsonParse();

                ApiConnect minMaxTempConn = new ApiConnect(address, ForecastType.MIN_MAX);
                String minMaxWeatherJson = minMaxTempConn.getWeatherInfo();
                minMaxTemperatures = parse.JsonParse(minMaxWeatherJson, ForecastType.MIN_MAX);

                ApiConnect futureWeatherConn = new ApiConnect(address, ForecastType.WEATHER_FORECAST);
                String futureWeatherJson = futureWeatherConn.getWeatherInfo();
                futureWeathers = parse.JsonParse(futureWeatherJson, ForecastType.WEATHER_FORECAST);

                ApiConnect nowWeatherConn = new ApiConnect(address, ForecastType.NOW_WEATHER);
                String nowWeatherJson = nowWeatherConn.getWeatherInfo();
                nowWeather = parse.JsonParse(nowWeatherJson, ForecastType.NOW_WEATHER);

                handler.sendEmptyMessage(0);
            }
        }

        NewRunnable weatherThread = new NewRunnable();
        Thread thread = new Thread(weatherThread);
        thread.start();

    }

    public void onRequestPermissionResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수되었다면
            boolean check_result = true;

            // 모든 퍼미션을 허용 했는지 체크 합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                // 위치 값을 가져올 수 있음
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할수 없는 이유를 설명해주고 앱을 종료
                // 2가지 경우가 있음

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MainActivity.this, "권한설정이 거부 되었습니다. 앱을 다시 실행하여 권한을 허용 해주세요", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "권한설정이 거부 되었습니다. 설정(앱 정보)에서 권한을 허용해야 합니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정 하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create().show();
    }

    private void checkRunTimePermission() {
        // 런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED
                && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된걸로 인식
            // 3. 위치 값을 가져올 수 있음
        } else {
            // 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요
            // 2가지 경우가 있음 ( 3-1, 4-1 )


            // 3-1 사용자가 퍼미션 거부한 경우
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {
                // 3-2 요청전 사용자에게 퍼미션이 필요한 이유를 설명
                Toast.makeText(MainActivity.this, "이 앱을 시랭하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();

                // 3-3 사용자에게 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult 에서 수신됨
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            } else {
                // 4-1 사용자가 퍼미션 거부를 한 적이 없는 경우 바로 퍼미션 요청
                // 요청 결과는 onRequestPermissionResult 에서 수신됨
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    }
}