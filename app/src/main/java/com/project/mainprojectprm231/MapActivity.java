package com.project.mainprojectprm231;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapActivity";
    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private int storeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Log.d(TAG, "MapActivity onCreate started");

        // Get coordinates from intent
        Intent intent = getIntent();
        latitude = intent.getDoubleExtra("latitude", 0.0);
        longitude = intent.getDoubleExtra("longitude", 0.0);
        storeId = intent.getIntExtra("storeId", 0);

        Log.d(TAG, "Received coordinates: lat=" + latitude + ", lng=" + longitude + ", storeId=" + storeId);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Store Location");
        }

        // Validate coordinates
        if (latitude == 0.0 || longitude == 0.0 || storeId == 0) {
            Log.e(TAG, "Invalid location data: lat=" + latitude + ", lng=" + longitude + ", storeId=" + storeId);
            Toast.makeText(this, "Invalid location data", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Setup map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);

        if (mapFragment != null) {
            Log.d(TAG, "Getting map async");
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "Map fragment is null");
            Toast.makeText(this, "Error loading map", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d(TAG, "Map is ready");
        mMap = googleMap;

        try {
            // Add marker for store location
            LatLng storePosition = new LatLng(latitude, longitude);
            Log.d(TAG, "Setting marker at: " + storePosition.toString());

            // Clear any existing markers
            mMap.clear();

            // Add marker
            mMap.addMarker(new MarkerOptions()
                    .position(storePosition)
                    .title("Store Location"));

            // Move camera to store location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(storePosition, 15f));

            // Enable zoom controls
            mMap.getUiSettings().setZoomControlsEnabled(true);

        } catch (Exception e) {
            Log.e(TAG, "Error setting up map marker", e);
            Toast.makeText(this, "Error displaying location on map", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item clicks here
        switch (item.getItemId()) {
            case android.R.id.home: // Handle the back button click
                onBackPressed(); // Call the back press method
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
