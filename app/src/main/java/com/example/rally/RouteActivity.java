package com.example.rally;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
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
import java.util.List;

public class RouteActivity extends BaseMapActivity {

    public static final String MARKERS_LNG = "markers_lng";
    public static final String MARKERS_LOG = "markers_log";
    public static final String WARNINGS_LNG = "warnings_lng";
    public static final String WARNINGS_LOG = "warnings_log";
    private GeoPoint lastRoutePoint;
    private GeoPoint firstRoutePoint;
    private boolean userCrossedStart = false;
    private boolean userCrossedEnd = false;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        ArrayList<Double> markersLng = (ArrayList<Double>) getIntent().getExtras().get(MARKERS_LNG);
        ArrayList<Double> markersLog = (ArrayList<Double>) getIntent().getExtras().get(MARKERS_LOG);
        ArrayList<Double> warningsLng = (ArrayList<Double>) getIntent().getExtras().get(WARNINGS_LNG);
        ArrayList<Double> warningsLog = (ArrayList<Double>) getIntent().getExtras().get(WARNINGS_LOG);

        firstRoutePoint = new GeoPoint(markersLng.get(0), markersLog.get(0));
        Marker startPosition = new Marker(mapView);
        startPosition.setPosition(firstRoutePoint);
        startPosition.setTextIcon(getString(R.string.start));
        mapView.getOverlays().add(startPosition);

        for (int i = 1; i < markersLng.size(); i++) {
            GeoPoint geoPoint = new GeoPoint(markersLng.get(i - 1), markersLog.get(i - 1));
            lastRoutePoint = new GeoPoint(markersLng.get(i), markersLog.get(i));
            Polyline polyline = new Polyline();
            polyline.setColor(getResources().getColor(R.color.green));
            polyline.setWidth(5f);
            polyline.addPoint(geoPoint);
            polyline.addPoint(new GeoPoint(markersLng.get(i), markersLog.get(i)));
            mapView.getOverlays().add(polyline);
            mapView.getController().setCenter(geoPoint);
        }

        Marker endPosition = new Marker(mapView);
        endPosition.setPosition(lastRoutePoint);
        endPosition.setTextIcon(getString(R.string.end));
        mapView.getOverlays().add(endPosition);

        if (warningsLog != null && warningsLng != null) {
            for (int i = 0; i < warningsLog.size(); i++) {
                Marker newMarker = new Marker(mapView);
                newMarker.setPosition(new GeoPoint(warningsLng.get(i), warningsLog.get(i)));
                newMarker.setIcon(AppCompatResources.getDrawable(RouteActivity.this, R.drawable.warningmarker));
                mapView.getOverlays().add(newMarker);
            }
        }

        mapView.getController().setCenter(firstRoutePoint);

        findViewById(R.id.backButton).setOnClickListener(view -> finish());
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);
        if (mapView != null) {
            currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());

            userCrossedStart = userCrossedStart || currentLocation.distanceToAsDouble(firstRoutePoint) < 100;

            if (userCrossedStart && currentLocation.distanceToAsDouble(lastRoutePoint) < 100.0 && !userCrossedEnd) {
                User newUser = App.cacheUtils.getUser();
                newUser.routesTraveled += 1;
                databaseUtils.updateUser(newUser);
                userCrossedEnd = true;
                Toast.makeText(RouteActivity.this, getString(R.string.route_finished), Toast.LENGTH_SHORT).show();
                finish();
            }

            if (!userCrossedStart) {
                navigateTo(firstRoutePoint);
            } else {
                removePolylineToDestination();
            }
        }
    }

    @Override
    int getContentView() {
        return R.layout.activity_route;
    }

    @Override
    protected void initializeMapView() {
        mapView = findViewById(R.id.osmmap);
    }
}
