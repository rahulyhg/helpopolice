package com.rvsoftlab.helpopolice;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.rvsoftlab.helpopolice.helper.PermissionHelper;
import com.rvsoftlab.helpopolice.interfaces.OnPermissionListener;
import com.rvsoftlab.helpopolice.services.LocationUpdatesBroadcastReceiver;

import static com.rvsoftlab.helpopolice.helper.Constants.FASTEST_UPDATE_INTERVAL;
import static com.rvsoftlab.helpopolice.helper.Constants.MAX_WAIT_TIME;
import static com.rvsoftlab.helpopolice.helper.Constants.UPDATE_INTERVAL;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private PermissionHelper permission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permission = new PermissionHelper(this);
        askForPermission();
        buildGoogleApiClient();
    }

    private void askForPermission() {
        permission.checkAndAskPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100, new OnPermissionListener() {
            @Override
            public void onPermissionGranted() {
                buildGoogleApiClient();
            }

            @Override
            public void onPermissionDenied() {
                Toast.makeText(MainActivity.this, "Permission Canceled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buildGoogleApiClient() {
        if (mGoogleApiClient != null) {
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(UPDATE_INTERVAL);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Sets the maximum time when batched location updates are delivered. Updates may be
        // delivered sooner than this interval.
        mLocationRequest.setMaxWaitTime(MAX_WAIT_TIME);

        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        try {
            Log.i("MainActivity", "Starting location updates");
            //LocationRequestHelper.setRequesting(this, true);
            LocationServices.getFusedLocationProviderClient(this)
                    .requestLocationUpdates(mLocationRequest,getPendingIntent());
            /*LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, getPendingIntent());*/
        } catch (SecurityException e) {
            //LocationRequestHelper.setRequesting(this, false);
            e.printStackTrace();
        }
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationUpdatesBroadcastReceiver.class);
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permission.onRequestPermissionsResult(requestCode,permissions,grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
