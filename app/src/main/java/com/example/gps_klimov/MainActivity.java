package com.example.gps_klimov;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    LocationManager _LocationManager;
    int ACCESS_FINE_LOCATION;
    int ACCESS_COARSE_LOCATION;
    double HomeLatitude = 57.98088510225732 * (Math.PI / 180);
    double HomeLongitude = 56.28759114418027 * (Math.PI / 180);
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        result = findViewById(R.id.text);

        _LocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    LocationListener _LocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            if (location == null) return;
            else {
                double ThisLatitude = location.getLatitude() * (Math.PI / 180);
                double ThisLongitude = location.getLongitude() * (Math.PI / 180);

                double distance = CalculateDistance(HomeLatitude, HomeLongitude, ThisLatitude, ThisLongitude);

                double time = CalculateTimeMin(distance);

                String message = "";
                if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                    message += "\nМестоположение определено с помощью GPS: долгота - " +
                            location.getLongitude() + " широта - " + location.getLatitude();
                }
                if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
                    message += "\nМестоположение определено с помощью интернета: долгота - " +
                            location.getLongitude() + " широта - " + location.getLatitude();
                }

                message += "\nРасстояние до дома: " + distance + " км.";
                message += "\nВремя пути (6 км/ч): " + time + " мин.";

                result.setText(message);
            }
        }
    };

    private double CalculateDistance(double homeLatitude, double homeLongitude, double thisLatitude, double thisLongitude) {
        var R = 6371;
        var difLatitude = thisLatitude - homeLatitude;
        var difLongitude = thisLongitude - homeLongitude;

        var a = Math.sin(difLatitude / 2) * Math.sin(difLatitude / 2) + Math.cos(homeLatitude) * Math.cos(thisLatitude) *
                Math.sin(difLongitude / 2) * Math.sin(difLongitude / 2);
        var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private double CalculateTimeMin(double distance) {
        return (distance / 6.0) * 60;
    }

    public Boolean GetPermissionGPS() {
        ACCESS_FINE_LOCATION = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION);
        ACCESS_COARSE_LOCATION = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION);

        return ACCESS_FINE_LOCATION == PackageManager.PERMISSION_GRANTED ||
                ACCESS_COARSE_LOCATION == PackageManager.PERMISSION_GRANTED;
    }

    public void OnGetGPS(View view) {
        if (GetPermissionGPS() == false) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (GetPermissionGPS() == false) {
            return;
        }
        else {
            _LocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, 10, _LocationListener);
            _LocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    1000, 10, _LocationListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        _LocationManager.removeUpdates(_LocationListener);
    }
}