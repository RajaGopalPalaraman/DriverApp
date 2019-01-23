package com.edot.driverapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class UpdateActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            loadMap();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            finish();
                            return;
                        }
                    }
                    loadMap();
                }
                else
                {
                    finish();
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMaxZoomPreference(20.0f);
        mMap.setMinZoomPreference(15.0f);
        findViewById(R.id.footerLayout).setVisibility(View.VISIBLE);
        refreshLocation(null);
        Log.d(AppConstants.LOG_TAG, "Google map loaded");
    }

    private void loadMap() {
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (fragment != null) {
            fragment.getMapAsync(this);
        }
    }

    public void updateLocation(View view) {
        if (currentLocation != null) {
            Log.d(AppConstants.LOG_TAG, "Location : lat - " + currentLocation.getLatitude() + " lon - " + currentLocation.getLongitude());
            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... voids) {
                    try {
                        String url = "http://iotproject2019.000webhostapp.com/location/update.php?lat="
                                + currentLocation.getLatitude() +
                                "&lon=" + currentLocation.getLongitude();
                        HttpURLConnection connection = (HttpURLConnection) new URL(url)
                                .openConnection();
                        connection.disconnect();
                        Log.d(AppConstants.LOG_TAG, "Connection Established : " + url + " : code :" + connection.getResponseCode());
                        return connection.getResponseCode() == 200;
                    } catch (IOException e) {
                        return false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    super.onPostExecute(aBoolean);
                    if (aBoolean) {
                        Toast.makeText(UpdateActivity.this
                                , R.string.loactionUpdated, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UpdateActivity.this,
                                R.string.locationUpdateFailed, Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        }
    }

    public void refreshLocation(View view) {
        if (currentLocation == null) {
            currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if (mMap != null && currentLocation != null) {
            mMap.clear();
            LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(AppConstants.LOG_TAG, "Location updated");
        currentLocation = location;
        refreshLocation(null);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this, R.string.gpsDisabled, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 300, 100, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(this);
    }
}
