package com.cowbell.cordova.geofence;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class RemoveGeofenceCommand extends AbstractGoogleServiceCommand {
    private List<String> geofencesIds;

    public RemoveGeofenceCommand(Context context, List<String> geofencesIds) {
        super(context);
        this.geofencesIds = geofencesIds;
    }

    @Override
    protected void ExecuteCustomCode() {
        if (geofencesIds != null && geofencesIds.size() > 0) {
            logger.log(Log.DEBUG, "Removing geofences...");
            GeofencingClient client = LocationServices.getGeofencingClient(context);
            client.removeGeofences(geofencesIds)
                .addOnSuccessListener(
                    new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            logger.log(Log.DEBUG, "Geofences successfully removed");
                            CommandExecuted();
                        }
                    })
                .addOnFailureListener(
                    new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof ApiException) {
                                ApiException apiException = (ApiException)e;
                                String message = "Removing geofences failed - " + apiException.getStatusCode();
                                logger.log(Log.ERROR, message);
                                CommandExecuted(new Error(message));
                            }
                        }
                    });
        } else {
            logger.log(Log.DEBUG, "Tried to remove Geofences when there were none");
            CommandExecuted();
        }
    }
}
