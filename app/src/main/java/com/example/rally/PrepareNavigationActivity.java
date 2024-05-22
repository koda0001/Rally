package com.example.rally;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;


public class PrepareNavigationActivity extends BaseMapActivity implements android.location.LocationListener {
    private Marker destinationLocationMarker;
    private double tappedLatitude;
    private double tappedLongitude;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        findViewById(R.id.backButton).setOnClickListener(view -> finish());

        findViewById(R.id.navigateButton).setOnClickListener(view -> {
            GeoPoint DestinationLocation = new GeoPoint(tappedLatitude, tappedLongitude);

            Intent intent = new Intent(PrepareNavigationActivity.this, NavigationActivity.class);
            intent.putExtra(NavigationActivity.DATA_DESTINATION_LOCATION, (Parcelable) DestinationLocation);
            startActivity(intent);
        });

    }

    @Override
    int getContentView() {
        return R.layout.activity_preapre_navigation;
    }
    @Override
    protected void initializeMapView() {
        mapView = findViewById(R.id.osmmap);
        MapEventsOverlay OverlayEvents = new MapEventsOverlay(PrepareNavigationActivity.this, new MapEventsReceiver() {

            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                tappedLatitude = p.getLatitude();
                tappedLongitude = p.getLongitude();
                if (destinationLocationMarker != null) {
                    mapView.getOverlays().remove(destinationLocationMarker);
                }

                GeoPoint DestinationLocation = new GeoPoint(tappedLatitude, tappedLongitude);
                destinationLocationMarker = new Marker(mapView);
                destinationLocationMarker.setPosition(DestinationLocation);
                destinationLocationMarker.setTitle(getString(R.string.destination_location));
                mapView.getOverlays().add(destinationLocationMarker);
                return false;
            }
        });
        mapView.getOverlays().add(OverlayEvents);
    }
}