package com.example.gpstemplate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private TextView tvGPS;
    private TextView tvNetWork;
    private TextView tvOnOffGPS;
    private TextView tvOnOffNetWork;
    private TextView tvLength;
    private TextView tvSpeed;

    private LocationManager locationManager;

    double latGPS = 0, lonGPS = 0, latNW = 0, lonNW = 0, length = 0, x, y;
    int speed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvGPS = findViewById(R.id.tvGPS);
        tvNetWork = findViewById(R.id.tvNetWork);
        tvOnOffGPS = findViewById(R.id.tvOnOffGPS);
        tvOnOffNetWork = findViewById(R.id.tvOnOffNetWork);
        tvLength = findViewById(R.id.tvLength);
        tvSpeed = findViewById(R.id.tvSpeed);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        requestPermissions();

        checkProviderEnabled();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkProviderEnabled();

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        locationManager.removeUpdates(locationListener);
    }

    public void requestPermissions() {
        final int LOCATION_PERMISSION = 1;
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                }, LOCATION_PERMISSION);
    }

    private void getSpeed(Location location) throws InterruptedException {
        long timeStart, timeEnd = 0, timeElapsed;

        if(location == null)
            return;

        if(location.getProvider().equals(LocationManager.GPS_PROVIDER) && location.hasSpeed()){

            timeStart = location.getTime();
            if(length != length)
            timeEnd = location.getTime();

            timeElapsed = (timeEnd - timeStart) / 1000 ;

            Log.d("TEST", String.valueOf(timeElapsed));

            speed = (int) (length / timeElapsed);

            tvSpeed.setText(Integer.toString(speed));
        }
    }


    private void distance(Location location){
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            tvGPS.setText(location.getLatitude() + "\n" + location.getLongitude());

            latGPS = location.getLatitude();
            lonGPS = location.getLongitude();
        }
        if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            tvNetWork.setText(location.getLatitude() + "\n" + location.getLongitude());

            latNW = location.getLatitude();
            lonNW = location.getLongitude();
        }

        x = Math.pow(latGPS - latNW, 2);
        y = Math.pow(lonGPS - lonNW, 2);

        length = Math.sqrt(x + y) * 6371000;

        tvLength.setText(String.format("Разница: %.2f метров", length));
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void showLocation(Location location) throws InterruptedException {
        distance(location);
        getSpeed(location);
    }

    @SuppressLint("SetTextI18n")
    private void checkProviderEnabled(){
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            tvOnOffGPS.setText("true");
        else
            tvOnOffGPS.setText("false");

        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            tvOnOffNetWork.setText("true");
        else
            tvOnOffNetWork.setText("false");
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            try {
                showLocation(location);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            checkProviderEnabled();
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            checkProviderEnabled();
        }
    };
}