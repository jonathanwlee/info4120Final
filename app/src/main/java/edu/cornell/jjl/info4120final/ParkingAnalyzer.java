package edu.cornell.jjl.info4120final;


import android.app.Activity;
import android.location.Location;
import android.text.format.DateUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;

public class ParkingAnalyzer {

    ParkingParser parser;
    protected LinkedHashMap<Date, LatLng> locationData = new LinkedHashMap<Date, LatLng>();
    protected LinkedHashMap<Date, Accel> accelerometerData = new LinkedHashMap<Date, Accel>();
    protected LinkedHashMap<Date, ActivityRecog> activityRecogData = new LinkedHashMap<Date, ActivityRecog>();

    public ParkingAnalyzer(String date) {
        parser = new ParkingParser(date);
        this.locationData = parser.locationData;
        this.accelerometerData = parser.accelerometerData;
        this.activityRecogData = parser.activityRecogData;
    }

    public int numberOfStops() {
        return 0;
    }


    public LatLng findParkingLocation() {
        long minDiff = -1, walkingTimestamp = findParkedTimestamp().getTime();
        Date nearestDate = null;

        for (Date date : locationData.keySet()) {
            long diff = Math.abs(walkingTimestamp - date.getTime());
            if ((minDiff == -1) || (diff < minDiff)) {
                minDiff = diff;
                nearestDate = date;
            }
        }
        return locationData.get(nearestDate);
    }


    public Date findParkedTimestamp() {
        Date parkingTimestamp = new Date(Long.MIN_VALUE);

        List keyList = new ArrayList<Date>(activityRecogData.keySet());
        for (int i = 1; i < keyList.size(); i++) {
            //Log.i("ARRAY CONTENTS: ", keyList.toArray()[i].toString());
        }

        for (Map.Entry<Date, ActivityRecog> entry : activityRecogData.entrySet()) {
            Date key = entry.getKey();
            ActivityRecog value = entry.getValue();

            if (value.walking > 60) {
                int walkingIndex = keyList.indexOf(key);
                Log.i("WALKING INDEX: ", Integer.toString(walkingIndex));
                for (int i = walkingIndex - 1; i >= 0; i--) {
                    ActivityRecog valueBeforeWalking = activityRecogData.get(keyList.toArray()[i]);
                    if (valueBeforeWalking.still > 60) {
                        parkingTimestamp = (Date) keyList.toArray()[i];
                    }
                }
            }
        }
        Log.i("DATE RETURNED: ", parkingTimestamp.toString());
        return parkingTimestamp;
    }

    /**
     * @return seconds using accelerometer data to track how long user was in the parking lot from
     * beginning of recording to end.
     */
    public long timeInLot() {
        Set<Date> keyset = accelerometerData.keySet();
        Date min = new Date(Long.MAX_VALUE);
        Date max = new Date(Long.MIN_VALUE);

        //Find Earliest Time
        for (int i = 0; i < keyset.size() - 1; i++) {
            if (((Date) (keyset.toArray()[i])).compareTo(min) < 0) {
                min = (Date) keyset.toArray()[i];
            }
        }

        //Find Latest Time
        for (int i = 0; i < keyset.size() - 1; i++) {
            if (((Date) (keyset.toArray()[i])).compareTo(max) > 0) {
                max = (Date) keyset.toArray()[i];
            }
        }

        return (max.getTime() - min.getTime()) / DateUtils.SECOND_IN_MILLIS;
    }

    public double distFromPOI(LatLng latlng1, LatLng latlng2) {
        Location locPOI = new Location("POI");
        locPOI.setLatitude(latlng1.latitude);
        locPOI.setLongitude(latlng1.longitude);

        Location locParking = new Location("PARK");
        locParking.setLatitude(latlng2.latitude);
        locParking.setLongitude(latlng2.longitude);

        return locPOI.distanceTo(locParking);
    }
}
