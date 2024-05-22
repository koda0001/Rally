package com.example.rally;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.data.User;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

abstract public class BaseMapActivity extends BaseActivity implements android.location.LocationListener {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private User user;
    double distanceTraveled = 0.0;
    private Integer maxSpeed = 0;
    private double secondsWithSpeedMoreThan120KmPerHour = 0;
    private double maxSecondsWithSpeedMoreThan120KmPerHour = 0;
    private int locationChangeCounter = 0;
    private Long previousTimeInMilis = 0L;
    private Marker currentLocationMarker;
    private Polyline polylineToDestination;

    protected List<GeoPoint> locationHistory = new ArrayList<>();
    protected LocationManager locationManager;
    protected MapView mapView;
    protected GeoPoint currentLocation;
    protected Float currentAngle;

    @Override
    protected void onStop() {
        super.onStop();
        User newUser = user;
        boolean userDataChanged = false;
        if (user.maxSpeed < maxSpeed) {
            userDataChanged = true;
            newUser.maxSpeed = maxSpeed;
        }
        if (user.timeWithMoreThan120KmPerHour < maxSecondsWithSpeedMoreThan120KmPerHour) {
            userDataChanged = true;
            newUser.timeWithMoreThan120KmPerHour = maxSecondsWithSpeedMoreThan120KmPerHour;
        }
        if (distanceTraveled > 0) {
            userDataChanged = true;
            newUser.kilometersTraveled += distanceTraveled;
        }
        if (userDataChanged) {
            databaseUtils.updateUser(newUser);
            App.cacheUtils.commitUser(newUser);
        }
    }

    abstract protected void initializeMapView();

    abstract int getContentView();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        setContentView(getContentView());

        user = App.cacheUtils.getUser();

        initializeMapView();

        mapView.getController().setZoom(17f);

        polylineToDestination = new Polyline(mapView);


        requestPermissionsIfNecessary(
                new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                }
        );

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 5, this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(BaseMapActivity.this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Configuration.getInstance().load(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Configuration.getInstance().save(getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        if (mapView != null) {
            locationChangeCounter++;
            currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
            float speedInMetersPerSecond = location.getSpeed();
            double speedInKilometersPerHour = speedInMetersPerSecond * 3.6;
            locationHistory.add(currentLocation);
            if (locationHistory.size() > 3) {
                locationHistory.remove(0);
                currentAngle = calculateAngle(locationHistory.get(2), locationHistory.get(0));
            }

            maxSpeed = (int) speedInKilometersPerHour;
            if (speedInKilometersPerHour > 120) {
                if (previousTimeInMilis != 0) {
                    Long currentTimeInMilis = Calendar.getInstance().getTimeInMillis();
                    double deltaTimeInMilis = currentTimeInMilis - previousTimeInMilis;
                    secondsWithSpeedMoreThan120KmPerHour += (deltaTimeInMilis / 1000);
                }
                previousTimeInMilis = Calendar.getInstance().getTimeInMillis();
            } else {
                previousTimeInMilis = 0L;
                secondsWithSpeedMoreThan120KmPerHour = 0L;
            }
            if (secondsWithSpeedMoreThan120KmPerHour > maxSecondsWithSpeedMoreThan120KmPerHour) {
                maxSecondsWithSpeedMoreThan120KmPerHour = secondsWithSpeedMoreThan120KmPerHour;
            }

            if (locationChangeCounter % 3 == 0) {
                float[] results = {0f, 0f, 0f};
                Location.distanceBetween(
                        location.getLatitude(),
                        location.getLongitude(),
                        locationHistory.get(0).getLatitude(),
                        locationHistory.get(0).getLongitude(),
                        results
                );

                distanceTraveled += results[0] / 1000;
            }

            if (currentLocationMarker != null) {
                mapView.getOverlays().remove(currentLocationMarker);
            }
            currentLocationMarker = new Marker(mapView);
            currentLocationMarker.setPosition(currentLocation);
            currentLocationMarker.setTitle(getString(R.string.current_location));
            mapView.getOverlays().add(currentLocationMarker);

            mapView.getController().animateTo(currentLocation);

            if (currentAngle != null) {
                mapView.setMapOrientation(currentAngle);
            }
        }
    }

    private float calculateAngle(GeoPoint from, GeoPoint to) {
        double toLongitude = to.getLongitude();
        double toLatitude = to.getLatitude();
        double fromLongitude = from.getLongitude();
        double fromLatitude = from.getLatitude();

        double angle = Math.toDegrees(Math.atan2(toLatitude - fromLatitude, toLongitude - fromLongitude));

        if ((toLongitude < 0 && toLatitude < 0 && fromLongitude < 0 && fromLatitude < 0) || (toLongitude < 0 && toLatitude < 0 && fromLongitude > 0 && fromLatitude > 0)) {
            angle += 360;
        } else if (toLongitude > 0 && toLatitude > 0 && fromLongitude > 0 && fromLatitude > 0) {
            angle += 90;
            if (angle < 0) {
                angle += 360;
            }
        }
        return (float) angle;
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

    protected void requestPermissionsIfNecessary(String[] permissions) {
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

    protected void navigateTo(GeoPoint destinationPoint) {
        String MY_USER_AGENT = getApplicationContext().getPackageName();
        RoadManager roadManager = new OSRMRoadManager(this, MY_USER_AGENT);

        new Thread(() -> {
            ArrayList<GeoPoint> waypoints = new ArrayList<>();
            waypoints.add(currentLocation);
            waypoints.add(destinationPoint);

            final Road road = roadManager.getRoad(waypoints);

            // Remove the existing roadOverlay
            if (polylineToDestination != null) {
                mapView.getOverlays().remove(polylineToDestination);
            }
            runOnUiThread(() -> {
                polylineToDestination = RoadManager.buildRoadOverlay(road);
                mapView.getOverlays().add(polylineToDestination);
                mapView.invalidate();

            });
        }).start();
    }

    protected void removePolylineToDestination() {
        if (polylineToDestination != null) {
            mapView.getOverlays().remove(polylineToDestination);
        }
    }
}
