package com.example.hows_today;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class GpsTracker extends Service implements LocationListener {

    private final Context context;
    Location location;
    double latitude;
    double longitude;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60;
    protected LocationManager locationManager;

    public GpsTracker(Context context) {
        this.context = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            this.locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            boolean isGPSEnabled = this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = this.locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {

            } else {
                int hasFineLocationPermission = ContextCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION);
                int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION);

                if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED
                        && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

                } else {
                    return null;
                }

                if (isNetworkEnabled) {
                    this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (this.locationManager != null) {
                        this.location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        if (this.location != null) {
                            this.latitude = this.location.getLatitude();
                            this.longitude = this.location.getLongitude();
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (this.location == null) {
                        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                        if (this.locationManager != null) {
                            this.location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            if (this.location != null) {
                                this.latitude = location.getLatitude();
                                this.longitude = location.getLongitude();
                            }
                        }
                    }
                }

            }
        } catch (Exception e) {
            Log.d("@@@", "" + e.toString());
        }

        return this.location;
    }

    public double getLatitude() {
        if (this.location != null) {
            this.latitude = this.location.getLatitude();
        }
        return this.latitude;
    }

    public double getLongitude() {
        if (this.location != null) {
            this.longitude = this.location.getLongitude();
        }
        return this.longitude;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    public void stopUsingGPS() {
        if (this.locationManager != null) {
            locationManager.removeUpdates(GpsTracker.this);
        }
    }
}

