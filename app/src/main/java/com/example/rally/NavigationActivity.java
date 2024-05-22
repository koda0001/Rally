package com.example.rally;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import com.example.data.User;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;


public class NavigationActivity extends BaseMapActivity implements android.location.LocationListener {

    public static String DATA_IS_FOR_ROUTE_ADD = "is_for_route_add";
    public static String DATA_DESTINATION_LOCATION = "destination_location";
    private TextView compassTextView;
    private TextView locationTextView;
    private Button addRouteButton;
    private Marker destinationLocationMarker;
    private final List<Marker> markers = new ArrayList<>();
    private GeoPoint destinationLocation;
    private TextView speedTextView;

    private List<Double> markersLngListToAdd;
    private List<Double> markersLogListToAdd;
    private List<Double> warningsLngListToAdd;
    private List<Double> warningsLogListToAdd;
    private boolean isDuringRoadCreation = false;
    private final DecimalFormat decimalFormat = new DecimalFormat("#.#");


    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        locationTextView = findViewById(R.id.locationTextView);
        compassTextView = findViewById(R.id.compassTextView);
        speedTextView =  findViewById(R.id.speedTextView);
        Button warningButton = findViewById(R.id.warningButton);
        addRouteButton = findViewById(R.id.addRouteButton);

        boolean isAddingRoute = getIntent().getBooleanExtra(DATA_IS_FOR_ROUTE_ADD, false);
        int additionalButtonsVisible = isAddingRoute ? View.VISIBLE : View.GONE;

        warningButton.setVisibility(additionalButtonsVisible);
        addRouteButton.setVisibility(additionalButtonsVisible);

        if (isAddingRoute) {
            markersLngListToAdd = new ArrayList<>();
            markersLogListToAdd = new ArrayList<>();
            warningsLngListToAdd = new ArrayList<>();
            warningsLogListToAdd = new ArrayList<>();
        }

        destinationLocation = (GeoPoint) getIntent().getSerializableExtra(DATA_DESTINATION_LOCATION);

        findViewById(R.id.BackButton1).setOnClickListener(view -> {
            finish();
        });

        addRouteButton.setOnClickListener(v -> {
                    if (isDuringRoadCreation) {
                        if (markersLngListToAdd.size() > 1) {
                            showInputDialog(NavigationActivity.this);
                        }
                        addRouteButton.setBackgroundColor(getResources().getColor(R.color.green));
                        addRouteButton.setText(getResources().getText(R.string.add_route));
                    } else {
                        addRouteButton.setBackgroundColor(getResources().getColor(R.color.black_transparent));
                        addRouteButton.setText(getResources().getText(R.string.save_route));
                    }
                    isDuringRoadCreation = !isDuringRoadCreation;
                }
        );

        warningButton.setOnClickListener(view -> {
            GeoPoint warning = new GeoPoint(currentLocation.getLatitude(), currentLocation.getLongitude());

            warningsLngListToAdd.add(currentLocation.getLatitude());
            warningsLogListToAdd.add(currentLocation.getLongitude());
            Marker warningmarker = new Marker(mapView);
            warningmarker.setPosition(warning);
            warningmarker.setIcon(AppCompatResources.getDrawable(NavigationActivity.this, R.drawable.warningmarker));
            warningmarker.setTitle(getString(R.string.warning_marker));
            mapView.getOverlays().add(warningmarker);
        });
    }

    @Override
    int getContentView() {
        return R.layout.activity_navigation_to_destiny;
    }

    @Override
    public void onLocationChanged(Location location) {
        super.onLocationChanged(location);

        if (mapView != null) {
            float speedInMetersPerSecond = location.getSpeed();
            double speedInKilometersPerHour = speedInMetersPerSecond * 3.6;

            String formatedSpeedInKilometersPerHour = decimalFormat.format(speedInKilometersPerHour);
            speedTextView.setText(String.format(getApplicationContext().getResources().getText(R.string.speed_with_value).toString(), formatedSpeedInKilometersPerHour));

            if (destinationLocationMarker != null) {
                mapView.getOverlays().remove(destinationLocationMarker);
            }

            if (destinationLocation != null) {
                destinationLocationMarker = new Marker(mapView);
                destinationLocationMarker.setPosition(destinationLocation);
                destinationLocationMarker.setTitle(getString(R.string.destination_location));
                mapView.getOverlays().add(destinationLocationMarker);
            }
            mapView.getController().animateTo(currentLocation);

            locationTextView.setText(decimalFormat.format(location.getLatitude()) + ", " + decimalFormat.format(location.getLongitude()));

            if (destinationLocation != null) {
                addLocationPin(destinationLocation, getString(R.string.destination));
            }

            if (isDuringRoadCreation) {
                markersLngListToAdd.add(currentLocation.getLatitude());
                markersLogListToAdd.add(currentLocation.getLongitude());
            }

            if (currentAngle != null) {
                compassTextView.setText(String.format(getString(R.string.azimuth), decimalFormat.format(currentAngle)));
            }

            if (destinationLocation != null) {
                navigateTo(destinationLocation);
            }
        }
    }

    @Override
    protected void initializeMapView() {
        mapView = findViewById(R.id.osmmap);
    }

    private void addLocationPin(GeoPoint geoPoint, String title) {
        if (mapView != null) {
            // Clear only destination markers
            for (Marker marker : markers) {
                if (marker.getTitle() != null && marker.getTitle().equals(title)) {
                    mapView.getOverlays().remove(marker);
                }
            }
            markers.clear(); // Clear the list

            Marker marker = new Marker(mapView);
            marker.setPosition(geoPoint);
            marker.setTitle(title);
            mapView.getOverlays().add(marker);

            markers.add(marker); // Add the marker to the list
            mapView.invalidate(); // Refresh the map view
        }
    }

    private void showInputDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.route_name);

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_route, null);
        builder.setView(dialogView);

        final EditText editText = dialogView.findViewById(R.id.input_text);

        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            String userInput = editText.getText().toString();
            databaseUtils.addRoute(markersLngListToAdd, markersLogListToAdd, warningsLngListToAdd, warningsLogListToAdd, userInput);
            markersLngListToAdd.clear();
            markersLogListToAdd.clear();
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}