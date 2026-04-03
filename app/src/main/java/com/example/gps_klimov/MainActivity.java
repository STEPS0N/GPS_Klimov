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

public class MainActivity extends AppCompatActivity {

    LocationManager _LocationManager;
    int ACCESS_FINE_LOCATION;
    int ACCESS_COARSE_LOCATION;
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
                String message = "";
                if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                    message += "\nМестоположение определено с помощью GPS: долгота - " +
                            location.getLatitude() + " широта - " + location.getLatitude();
                }
                if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
                    message += "\nМестоположение определено с помощью интернета: долгота - " +
                            location.getLatitude() + " широта - " + location.getLatitude();
                }
                result.setText(message);
            }
        }
    };

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
}