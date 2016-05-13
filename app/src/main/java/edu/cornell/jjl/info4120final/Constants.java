package edu.cornell.jjl.info4120final;

/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.model.LatLng;
import java.util.LinkedHashMap;

/**
 * Constants used in this sample.
 */
public final class Constants {

    private Constants() {
    }

    public static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";

    public static final String BROADCAST_ACTION = PACKAGE_NAME + ".BROADCAST_ACTION";

    public static final String ACTIVITY_EXTRA    = PACKAGE_NAME + ".ACTIVITY_EXTRA";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";

    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";

    public static final String DETECTED_ACTIVITIES = PACKAGE_NAME + ".DETECTED_ACTIVITIES";
    public static final String ACTIVITY_UPDATES_REQUESTED_KEY = PACKAGE_NAME +
            ".ACTIVITY_UPDATES_REQUESTED";

    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 150; // 1 mile, 1.6 km

    /**
     * Map for storing information about parking lots.
     */
    public static final LinkedHashMap<String, LatLng> PARKING_LOTS = new LinkedHashMap<String, LatLng>();
    public static final LinkedHashMap<String, LatLng> PARKING_LOTS_ENTRANCE = new LinkedHashMap<String,LatLng>();
    public static final LinkedHashMap<String, LatLng> PARKING_LOTS_POI = new LinkedHashMap<String,LatLng>();
    public static final LinkedHashMap<String, LatLng> PARKING_LOTS_LOOPS = new LinkedHashMap<String,LatLng>();
    public static final LinkedHashMap<String, String> PARKING_LOTS_SPACES = new LinkedHashMap<String,String>();
    public static final LinkedHashMap<String, String> PARKING_LOTS_STATUS = new LinkedHashMap<String,String>();
    public static final LinkedHashMap<String, String> PARKING_LOTS_LAST_UPDATE = new LinkedHashMap<String,String>();


    static {
        // Sage Hall
        PARKING_LOTS.put("Sage", new LatLng(42.4451613, -76.484061));
        PARKING_LOTS_POI.put("Sage", new LatLng(42.445544, -76.483390));
        PARKING_LOTS_LOOPS.put("Sage", new LatLng(42.445321, -76.483498));
        PARKING_LOTS_ENTRANCE.put("Sage", new LatLng(42.445321, -76.483498));
        PARKING_LOTS_SPACES.put("Sage","40");
        PARKING_LOTS_STATUS.put("Sage","Full");
        PARKING_LOTS_LAST_UPDATE.put("Sage","N/A");

        // WSH.
        PARKING_LOTS.put("WSH", new LatLng(42.446450, -76.486055));
        PARKING_LOTS_POI.put("WSH", new LatLng(42.446550, -76.485892));
        PARKING_LOTS_LOOPS.put("WSH", new LatLng(42.446305, -76.486038));
        PARKING_LOTS_ENTRANCE.put("WSH", new LatLng(42.445886, -76.485961));
        PARKING_LOTS_SPACES.put("WSH","40");
        PARKING_LOTS_STATUS.put("WSH","Semi-Empty");
        PARKING_LOTS_LAST_UPDATE.put("WSH","N/A");


        // Engineering.
        PARKING_LOTS.put("Upson", new LatLng(42.443602, -76.483565));
        PARKING_LOTS_POI.put("Upson", new LatLng(42.443818, -76.483644));
        PARKING_LOTS_LOOPS.put("Upson", new LatLng(42.443595, -76.483870));
        PARKING_LOTS_ENTRANCE.put("Upson", new LatLng(42.443510, -76.484163));
        PARKING_LOTS_SPACES.put("Upson","40");
        PARKING_LOTS_STATUS.put("Upson","Semi-Full");
        PARKING_LOTS_LAST_UPDATE.put("Upson","N/A");

        // Bartels.
        PARKING_LOTS.put("Bartels", new LatLng(42.444716, -76.476646));
        PARKING_LOTS_POI.put("Bartels", new LatLng(42.445142, -76.476630));
        PARKING_LOTS_LOOPS.put("Bartels", new LatLng(42.445142, -76.476630));
        PARKING_LOTS_ENTRANCE.put("Bartels", new LatLng(42.445170, -76.475821));
        PARKING_LOTS_SPACES.put("Bartels","60");
        PARKING_LOTS_STATUS.put("Bartels","Empty");
        PARKING_LOTS_LAST_UPDATE.put("Bartels","N/A");

        // Vet.
        PARKING_LOTS.put("Veterinary", new LatLng(42.445732, -76.468948));
        PARKING_LOTS_POI.put("Veterinary", new LatLng(42.445939, -76.469457));
        PARKING_LOTS_LOOPS.put("Veterinary", new LatLng(42.445939, -76.469457));
        PARKING_LOTS_ENTRANCE.put("Veterinary", new LatLng(42.445939, -76.469592));
        PARKING_LOTS_SPACES.put("Veterinary","80");
        PARKING_LOTS_STATUS.put("Veterinary","Semi-Empty");
        PARKING_LOTS_LAST_UPDATE.put("Veterinary","N/A");

        // Kite.
        PARKING_LOTS.put("Kite", new LatLng(42.445435, -76.473621));
        PARKING_LOTS_POI.put("Kite", new LatLng(42.445413, -76.474245));
        PARKING_LOTS_LOOPS.put("Kite", new LatLng(42.445413, -76.474245));
        PARKING_LOTS_ENTRANCE.put("Kite", new LatLng(42.445245, -76.474172));
        PARKING_LOTS_SPACES.put("Kite","100");
        PARKING_LOTS_STATUS.put("Kite","Full");
        PARKING_LOTS_LAST_UPDATE.put("Kite","N/A");

        // Law.
        PARKING_LOTS.put("Law", new LatLng(42.444360, -76.486353));
        PARKING_LOTS_POI.put("Law", new LatLng(42.444360, -76.486353));
        PARKING_LOTS_LOOPS.put("Law", new LatLng(42.444360, -76.486353));
        PARKING_LOTS_ENTRANCE.put("Law", new LatLng(42.444360, -76.486353));
        PARKING_LOTS_SPACES.put("Law","100");
        PARKING_LOTS_STATUS.put("Law","Empty");
        PARKING_LOTS_LAST_UPDATE.put("Law","N/A");
    }

    /**
     * The desired time between activity detections. Larger values result in fewer activity
     * detections while improving battery life. A value of 0 results in activity detections at the
     * fastest possible rate. Getting frequent updates negatively impact battery life and a real
     * app may prefer to request less frequent updates.
     */
    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 0;

    /**
     * List of DetectedActivity types that we monitor in this sample.
     */
    protected static final int[] MONITORED_ACTIVITIES = {
            DetectedActivity.STILL,
            DetectedActivity.ON_FOOT,
            DetectedActivity.WALKING,
            DetectedActivity.RUNNING,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.IN_VEHICLE,
            DetectedActivity.TILTING,
            DetectedActivity.UNKNOWN
    };

    /**
     * Returns a human readable String corresponding to a detected activity type.
     */
    public static String getActivityString(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unidentifiable_activity, detectedActivityType);
        }
    }
}
