package com.example.rally;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.config.Configuration;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Monitoring_Pojazdu extends BaseActivity implements LocationListener {

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private TextView obdDataTextView;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        setContentView(R.layout.monitoring_pojazdu);

        obdDataTextView = findViewById(R.id.obdDataTextView);

        // Request location permissions if necessary
        requestPermissionsIfNecessary(
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE});

        // Request location updates
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            ((LocationManager) getSystemService(Context.LOCATION_SERVICE)).requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, this);
        }

        findViewById(R.id.backButton).setOnClickListener(view -> {finish();});

        findViewById(R.id.connectOBDButton).setOnClickListener(view -> {
            // Connect to OBD in a separate thread
            new ConnectOBDTask().execute();
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Handle status changes if needed
    }

    @Override
    public void onProviderEnabled(String provider) {
        // Handle provider enabled
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Handle provider disabled
    }

    private class ConnectOBDTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            if (location != null) {
                float speedInMetersPerSecond = location.getSpeed();
                double speedInKilometersPerHour = speedInMetersPerSecond * 3.6;

                TextView SpeedTextView = findViewById(R.id.speedTextView);
                DecimalFormat decimalFormat = new DecimalFormat("#.#");

                String formatedSpeedInKilometersPerHour = decimalFormat.format(speedInKilometersPerHour);

                // Simulate OBD connection and data retrieval (replace with actual implementation)
                try {
                    Thread.sleep(2000); // Simulating OBD connection delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "Speed: " + formatedSpeedInKilometersPerHour + " km/h\nRPM: 2000\nPSI:\nBattery:";
            } else {
                return "Location not available";
            }
        }

        @Override
        protected void onPostExecute(String obdData) {
            // Update obdDataTextView with retrieved OBD data
            obdDataTextView.setText(String.format(getResources().getString(R.string.obd_data), obdData));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            permissionsToRequest.add(permissions[i]);
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}
