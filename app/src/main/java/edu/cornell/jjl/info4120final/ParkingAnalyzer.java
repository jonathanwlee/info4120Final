package edu.cornell.jjl.info4120final;

import android.location.Location;
import android.text.format.DateUtils;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParkingAnalyzer {
    String parkingLot = "206 College";
    ParkingParser parser;

    protected boolean exitsOnFoot;
    protected int numberOfLoops;
    protected double distanceFromPOI;
    protected long timeInLot;

    protected LinkedHashMap<Date, LatLng> locationData = new LinkedHashMap<Date, LatLng>();
    protected LinkedHashMap<Date, Accel> accelerometerData = new LinkedHashMap<Date, Accel>();
    protected LinkedHashMap<Date, ActivityRecog> activityRecogData = new LinkedHashMap<Date, ActivityRecog>();

    public ParkingAnalyzer(String date) {
        parser = new ParkingParser(date);
        this.locationData = parser.locationData;
        this.accelerometerData = parser.accelerometerData;
        this.activityRecogData = parser.activityRecogData;
        init();
    }

    public void init() {
        this.exitsOnFoot = exitsOnFoot();
        this.numberOfLoops = numberOfLoops();
        this.distanceFromPOI = distFromPOI(findParkingLocation(),Constants.PARKING_LOTS_POI.get(parkingLot));
        this.timeInLot = timeInLot();

        Log.i("Variables", Boolean.toString(exitsOnFoot));
        Log.i("Variables", Integer.toString(numberOfLoops));
        Log.i("Variables", Double.toString(distanceFromPOI));
        Log.i("Variables", Long.toString(timeInLot));

    }

    public int numberOfStops() {
        return 0;
    }

    public LatLng findParkingLocation() {
        long minDiff = -1, stillTimestamp = findParkedTimestamp().getTime();
        Date nearestDate = null;

        for (Date date : locationData.keySet()) {
            long diff = Math.abs(stillTimestamp - date.getTime());
            if ((minDiff == -1) || (diff < minDiff)) {
                minDiff = diff;
                nearestDate = date;
            }
        }
        return locationData.get(nearestDate);
    }

    public boolean exitsOnFoot() {
        //Get last three activity recognition.
        int walkingThreshold = 50;
        List keyList = new ArrayList<Date>(activityRecogData.keySet());
        Log.i("Length",Integer.toString(keyList.toArray().length));
        //If last three activity recognition don't have on foot > 50,
        for (int i=1; i < 4; i++) {
            if (activityRecogData.get(keyList.toArray()[keyList.size()-i]).walking > 50
                    && activityRecogData.get(keyList.toArray()[keyList.size()-i]).vehicle < 40){
                return true;
            }
            //Log.i("ARRAYACTIVITYCONTENTS",Integer.toString(activityRecogData.get(keyList.toArray()[keyList.size()-i]).vehicle));
        }

        return false;
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
        Date start = new Date(Long.MAX_VALUE);
        Date end = new Date(Long.MIN_VALUE);

        //Find Earliest Time
        for (int i = 0; i < keyset.size() - 1; i++) {
            if (((Date) (keyset.toArray()[i])).compareTo(start) < 0) {
                start = (Date) keyset.toArray()[i];
            }
        }

        //Find when still
        end = findParkedTimestamp();
        return (end.getTime() - start.getTime()) / DateUtils.SECOND_IN_MILLIS;
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

    public int numberOfLoops() {
        int loops = 0;
        double thresholdClose = 5;
        double thresholdFar = 5;
        boolean entered = false;
        boolean far = false;
        for (Map.Entry<Date, LatLng> entry : locationData.entrySet()) {
            Date key = entry.getKey();
            LatLng value = entry.getValue();

            //Starts checking. It has passed the loop / entered
            if (distFromPOI(Constants.PARKING_LOTS_LOOPS.get(parkingLot),value) < thresholdClose) {
                entered = true;
            }

            if (entered) {
                //If entered, make sure far enough away.
                if (distFromPOI(Constants.PARKING_LOTS_LOOPS.get(parkingLot),value) > thresholdFar) {
                    far = true;
                }
            }
            //Close again. Must have been a loop. Add one. Reset.
            if (distFromPOI(Constants.PARKING_LOTS_LOOPS.get(parkingLot),value) < thresholdClose && far) {
                loops++;
                entered = false;
                far = false;
            }
            Log.i("DISTANCE FROM", Double.toString(distFromPOI(Constants.PARKING_LOTS_LOOPS.get(parkingLot),value)));
            //Log.i("ARRAY CONTENTS:",value.toString());
         }
        Log.i("Loops Number",Integer.toString(loops));
    return loops;
    }


    /**
    Implements Rule tree for determining parking availability.
     */
    public int determineParkingAvailability() {

        double thresholdPOIClose = 50;
        double thresholdPOIFar = 50;

        if (!exitsOnFoot) {
            return 0;
        }
        else {
            //Walking and Number loops == 0
            if (numberOfLoops == 0 ) {

                //Distance Close
                if (distanceFromPOI > thresholdPOIClose) {
                }

                //Distance Far
                else {
                }

            }



        }

        return 0;

    }
}
