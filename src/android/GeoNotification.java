package com.cowbell.cordova.geofence;

import com.google.android.gms.location.Geofence;
import com.google.gson.annotations.Expose;

public class GeoNotification {
    @Expose public String id;
    @Expose public double latitude;
    @Expose public double longitude;
    @Expose public int radius;
    @Expose public int transitionType;

    @Expose public Notification notification;
    
    // Allows storage of arbitrary data that doesn't fit in standard properties
    @Expose public Object data;

    public GeoNotification() {
    }

    public Geofence toGeofence() {
        return new Geofence.Builder()
            .setRequestId(id)
            .setTransitionTypes(transitionType)
            .setCircularRegion(latitude, longitude, radius)
            .setExpirationDuration(Long.MAX_VALUE).build();
    }

    public String toJson() {
        return Gson.get().toJson(this);
    }

    public static GeoNotification fromJson(String json) {
        if (json == null) return null;
        return Gson.get().fromJson(json, GeoNotification.class);
    }
}
