package com.rvsoftlab.helpopolice.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.body.JSONObjectBody;
import com.koushikdutta.ion.Ion;

import java.lang.reflect.Method;
import java.security.acl.LastOwnerException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationUpdatesBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "LUBroadcastReceiver";

    public static final String ACTION_PROCESS_UPDATES =
            "com.rvsoftlab.helpopolice.services.action" +
                    ".PROCESS_UPDATES";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent!=null){
            final String action = intent.getAction();
            if (ACTION_PROCESS_UPDATES.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    List<Location> locations = result.getLocations();
                    Log.i(TAG, String.valueOf(locations.get(0).getLatitude()));
                    if (!locations.isEmpty())
                        updateLocation(locations.get(0),context);
                }
            }
        }
    }

    /*private void updateLocation(final Location location, Context context) {
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://rvsoft.esy.es/Android/helpo/Location.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i(TAG,response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i(TAG,error.toString());
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String > param = new HashMap<>();
                    param.put("tag","update_loc");
                    param.put("mobile","8286903263");
                    param.put("lat",String.valueOf(location.getLatitude()));
                    param.put("long",String.valueOf(location.getLongitude()));
                    return param;
                }
            };
            Volley.newRequestQueue(context).add(stringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/

    private void updateLocation(final Location location, Context context){
        try {
            /*JsonObject json = new JsonObject();
            json.addProperty("tag","update_loc");
            json.addProperty("mobile","8286903263");
            json.addProperty("lat",String.valueOf(location.getLatitude()));
            json.addProperty("long",String.valueOf(location.getLongitude()));

            Ion.with(context)
                    .load("http://rvsoft.esy.es/Android/helpo/Location.php")
                    .setBodyParameter("tag","update_loc")
                    .setBodyParameter("mobile","8286903263")
                    .setBodyParameter("lat",String.valueOf(location.getLatitude()))
                    .setBodyParameter("long",String.valueOf(location.getLongitude()))
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            if (e!=null)
                                e.printStackTrace();
                            else
                                Log.i(TAG,result.toString());
                        }
                    });*/
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/");
            GeoFire geoFire = new GeoFire(ref);
            geoFire.setLocation("1234567890", new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
                @Override
                public void onComplete(String key, DatabaseError error) {
                    if (error!=null){
                        Log.d(TAG,error.toString());
                    }else {
                        Log.d(TAG,"Success GeoFire");
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
