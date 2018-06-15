package com.cowbell.cordova.geofence;

import android.app.PendingIntent;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import org.apache.cordova.LOG;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddGeofenceCommand extends AbstractGoogleServiceCommand {
    private List<Geofence> geofencesToAdd;
    private PendingIntent pendingIntent;

    public AddGeofenceCommand(Context context, PendingIntent pendingIntent,
                              List<Geofence> geofencesToAdd) {
        super(context);
        this.geofencesToAdd = geofencesToAdd;
        this.pendingIntent = pendingIntent;
    }

    @Override
    public void ExecuteCustomCode() {
        logger.log(Log.DEBUG, "Adding new geofences...");
        if (geofencesToAdd != null && geofencesToAdd.size() > 0) try {
            GeofencingClient client = LocationServices.getGeofencingClient(context);

            GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                    .addGeofences(geofencesToAdd)
                    .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                    .build();


            client.addGeofences(geofencingRequest, pendingIntent)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            logger.log(Log.DEBUG, "Geofences successfully added");
                            CommandExecuted();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof ApiException) {
                                try {
                                    ApiException apiException = (ApiException)e;
                                    Map<Integer, String> errorCodeMap = new HashMap<Integer, String>();
                                    errorCodeMap.put(GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE, GeofencePlugin.ERROR_GEOFENCE_NOT_AVAILABLE);
                                    errorCodeMap.put(GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES, GeofencePlugin.ERROR_GEOFENCE_LIMIT_EXCEEDED);

                                    Integer statusCode = apiException.getStatusCode();
                                    String message = "Adding geofences failed - SystemCode: " + statusCode;
                                    JSONObject error = new JSONObject();
                                    error.put("message", message);

                                    if (statusCode == GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE) {
                                        error.put("code", GeofencePlugin.ERROR_GEOFENCE_NOT_AVAILABLE);
                                    } else if (statusCode == GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES) {
                                        error.put("code", GeofencePlugin.ERROR_GEOFENCE_LIMIT_EXCEEDED);
                                    } else if (statusCode == GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS) {
                                        error.put("code", GeofencePlugin.ERROR_GEOFENCE_INTENT_LIMIT_EXCEEDED);
                                    } else {
                                        error.put("code", GeofencePlugin.ERROR_UNKNOWN);
                                    }

                                    logger.log(Log.ERROR, message);
                                    CommandExecuted(error);
                                } catch (JSONException exception) {
                                    CommandExecuted(exception);
                                }
                            }
                        }
                    });
        } catch (Exception exception) {
            logger.log(LOG.ERROR, "Exception while adding geofences");
            exception.printStackTrace();
            CommandExecuted(exception);
        }
    }
}
