package com.rvsoftlab.helpopolice.helper;

public class Constants {
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL = 60 * 1000;
    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value, but they may be less frequent.
     */
    public static final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL / 2;
    /**
     * The max time before batched results are delivered by location services. Results may be
     * delivered sooner than this interval.
     */
    public static final long MAX_WAIT_TIME = UPDATE_INTERVAL * 2;

    public interface FIREBASE_CHILD{
        String USER = "user";
        String LOCATION = "location";
        String HELPO_REQUEST = "request";
        String HELP_DETAIL = "helpo";
    }
}
