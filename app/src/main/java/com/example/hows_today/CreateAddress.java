package com.example.hows_today;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CreateAddress {

    private GpsTracker gpsTracker;
    private Address address;
    private String lat;
    private String lon;

    public CreateAddress(Activity activity) {
        createAddress(activity);
    }

    public Address getAddress() {
        return this.address;
    }

    public String getGu() {
        return this.address.getSubLocality();
    }

    public String getDong() {
        return this.address.getThoroughfare();
    }

    public String getLat() {
        return this.lat;
    }

    public String getLon() {
        return this.lon;
    }

    private void createAddress(Activity activity) {
        // geocoder 주소 예시
        // 대한민국 서울특별시 관악구 보라매동
        this.gpsTracker = new GpsTracker(activity);

        double latitude = this.gpsTracker.getLatitude(); // GpsTracker 에서 위도, 경도 받아옴
        double longitude = this.gpsTracker.getLongitude();

        this.lat = Integer.toString((int) latitude);
        this.lon = Integer.toString((int) longitude);

        setAddress(activity, latitude, longitude);
    }

    private void setAddress(Activity activity, double latitude, double longitude) {
        //지오코더 GPS 를 주소로 변환
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());

        List<Address> addressList = null;

        try {
            addressList = geocoder.getFromLocation(latitude, longitude, 7);
        } catch (IOException e) {
            //네트워크 문제
            Toast.makeText(activity, "지오코더 서비스 사용불가", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException e) {
            Toast.makeText(activity, "잘못된 GPS 좌표", Toast.LENGTH_SHORT).show();
        }

        if (addressList == null || addressList.size() == 0) {
            Toast.makeText(activity, "주소 미발견", Toast.LENGTH_SHORT).show();
        }

        this.address = addressList.get(3); // geocoder 주소 형식 번호(대한민국 서울특별시 관악구 보라매동)
    }
}

