package com.rvsoftlab.helpopolice;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.LocationResponse;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rvsoftlab.helpopolice.geofire.GeoFire;
import com.rvsoftlab.helpopolice.geofire.GeoLocation;
import com.rvsoftlab.helpopolice.geofire.GeoQuery;
import com.rvsoftlab.helpopolice.geofire.GeoQueryDataEventListener;
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
        //getNearby();
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
            LocationServices.getFusedLocationProviderClient(this)
                    .requestLocationUpdates(mLocationRequest,getPendingIntent());



        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, LocationUpdatesBroadcastReceiver.class);
        intent.setAction(LocationUpdatesBroadcastReceiver.ACTION_PROCESS_UPDATES);
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @SuppressLint("MissingPermission")
    private void getNearby() {
        Awareness.getSnapshotClient(this)
                .getLocation().addOnCompleteListener(new OnCompleteListener<LocationResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationResponse> task) {
                if (task.isSuccessful()){
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/");
                    GeoFire geoFire = new GeoFire(ref);

                    GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(task.getResult().getLocation().getLatitude(), task.getResult().getLocation().getLongitude()), 0.6);

                    geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                        @Override
                        public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
                            Log.d("TAG ENTERED",dataSnapshot.getKey());
                        }

                        @Override
                        public void onDataExited(DataSnapshot dataSnapshot) {
                            Log.d("TAG EXIT",dataSnapshot.getKey());
                        }

                        @Override
                        public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {


                        }

                        @Override
                        public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {
                            Log.d("TAG CHANGE",""+dataSnapshot.getChildrenCount());

                        }

                        @Override
                        public void onGeoQueryReady() {

                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {
                            Log.d("TAG",error.getDetails());
                        }
                    });
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permission.onRequestPermissionsResult(requestCode,permissions,grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getNearby();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
