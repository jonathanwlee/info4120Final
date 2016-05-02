package edu.cornell.jjl.info4120final;

import android.location.Location;
import android.text.format.DateUtils;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParkingAnalyzer {
    String parkingLot = "Sage";
    ParkingParser parser;

    protected boolean exitsOnFoot;
    protected int numberOfLoops;
    protected double distanceFromPOI;
    protected long timeInLot;
    protected int numberOfStops;

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
        this.distanceFromPOI = distFromPOI(Constants.PARKING_LOTS_POI.get(parkingLot),findParkingLocation());
        this.timeInLot = timeInLot();
        this.numberOfStops = numberOfStops();

        Log.i("VariablesOnFoot", Boolean.toString(exitsOnFoot));
        Log.i("VariablesLoops", Integer.toString(numberOfLoops));
        Log.i("VariablesDistancePOI", Double.toString(distanceFromPOI));
        Log.i("VariablesNumberOfStop", Integer.toString(numberOfStops));
    }

    public int numberOfStops() {
        for (Map.Entry<Date, Accel> entry : accelerometerData.entrySet()) {
            Date key = entry.getKey();
            Accel value = entry.getValue();
            //Magnitude
        }

        int stops = 0;
        double gravity = 9.8;
        double accelerationThreshold = 1.5;
        boolean wasMoving = false;
        boolean isMoving = false;

        Deque<Double> last100 = new LinkedList<Double>();

        for (Map.Entry<Date, Accel> entry : accelerometerData.entrySet()) {
            Date key = entry.getKey();
            Accel value = entry.getValue();
            //Magnitude
            double magnitude = Math.abs(findMagnitude(value.x, value.y, value.z) - gravity);
            //System.out.println(magnitude);
            last100.addLast(magnitude);
            if (last100.size() < 100) {
                continue;
            }

            double averageMagnitude = average(last100);
            if (averageMagnitude < accelerationThreshold) {
                //System.out.println(magnitude);
                if (isMoving) {
                    wasMoving = true;
                    isMoving = false;
                }
            }
            else {
                if (!isMoving) {
                    if (wasMoving) {
                        stops++;
                        //System.out.println("---------------------------------");
                        //for (Map.Entry<Date, Double> reading : queue.entrySet()) {
                        //    System.out.println(reading);
                        //}
                    }
                    wasMoving = false;
                    isMoving = true;
                }
            }
            last100.removeFirst();
        }

        return stops;
    }

    private double average(Deque<Double> magnitudes) {
        double sum = 0.0;
        for (Double x : magnitudes) {
            sum += x;
        }
        return sum / magnitudes.size();
    }

    public double findMagnitude(float x, float y, float z) {
        return Math.sqrt(x*x + y*y + z*z);
    }

    public LatLng findParkingLocation() {
        //If never parked, return Preset 0,0 LatLng
        if (findParkedTimestamp().equals(new Date(Long.MIN_VALUE))) {
            return new LatLng(0,0);
        }
        long minDiff = -1, stillTimestamp = findParkedTimestamp().getTime();
        Date nearestDate = null;

        for (Date date : locationData.keySet()) {
            long diff = Math.abs(stillTimestamp - date.getTime());
            if ((minDiff == -1) || (diff < minDiff)) {
                minDiff = diff;
                nearestDate = date;
            }
        }
        Log.i("VariableParkingLocation",locationData.get(nearestDate).toString());
        return locationData.get(nearestDate);
    }

    public boolean exitsOnFoot() {
        //Get last two activity recognition.
        int walkingThreshold = 50;
        List keyList = new ArrayList<Date>(activityRecogData.keySet());
        Log.i("Length",Integer.toString(keyList.toArray().length));
        //If last two activity recognition don't have on foot > 50,
        for (int i=1; i < 3; i++) {
            if (activityRecogData.get(keyList.toArray()[keyList.size()-i]).walking > 50
                    && activityRecogData.get(keyList.toArray()[keyList.size()-i]).vehicle < 40){
                return true;
            }
        }
        return false;
    }

    public Date findParkedTimestamp() {
        Date parkingTimestamp = new Date(Long.MIN_VALUE);
        boolean foundIndex = false;

        List keyList = new ArrayList<Date>(activityRecogData.keySet());
        for (int i = 1; i < keyList.size(); i++) {
        }

        for (Map.Entry<Date, ActivityRecog> entry : activityRecogData.entrySet()) {
            int walkingIndex;
            Date key = entry.getKey();
            ActivityRecog value = entry.getValue();

            if (!foundIndex ) {
                if (value.walking > 20) {
                    walkingIndex = keyList.indexOf(key);
                    foundIndex = true;

                    for (int i = walkingIndex - 1; i >= 0; i--) {
                        ActivityRecog valueBeforeWalking = activityRecogData.get(keyList.toArray()[i]);

                        if (valueBeforeWalking.vehicle < 40) {
                            parkingTimestamp = (Date) keyList.toArray()[i];
                        }
                    }

                    //If not long enough to be still
                    if (parkingTimestamp.equals(new Date(Long.MIN_VALUE))) {
                        parkingTimestamp = (Date) keyList.toArray()[walkingIndex-1];

                        if (((((Date) keyList.toArray()[walkingIndex]).getTime()
                                - parkingTimestamp.getTime()) / DateUtils.SECOND_IN_MILLIS) > 15) {

                            long average = ((((Date) keyList.toArray()[walkingIndex]).getTime()
                                    - parkingTimestamp.getTime()) / 2 );
                            Log.i("average",Long.toString(average));

                            Calendar cal = Calendar.getInstance();
                            cal.setTime(((Date) keyList.toArray()[walkingIndex-1]));
                            cal.add(Calendar.MILLISECOND, ((int) average));
                            parkingTimestamp = cal.getTime();
                        }
                    }
                }
            }
        }
        Log.i("ParkingTimestamp",parkingTimestamp.toString());
        return parkingTimestamp;
    }

    /**
     * @return seconds using accelerometer data to track how long user was in the parking lot from
     * beginning of recording to end.
     */
    public long timeInLot() {
        Set<Date> keyset = accelerometerData.keySet();
        Date start = new Date(Long.MAX_VALUE);
        Date end;

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

        //If never parked. Return MAX VALUE
        if (locParking.getLatitude() ==0 && locParking.getLongitude() ==0) {
            return Double.MAX_VALUE;
        }

        return locPOI.distanceTo(locParking);
    }

    public int numberOfLoops() {
        int loops = 0;
        double thresholdClose = 10;
        double thresholdFar = 10;
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
                if(key.before(findParkedTimestamp())) {
                    loops++;
                }

                entered = false;
                far = false;
            }
            Log.i("DISTANCE FROM", Double.toString(distFromPOI(Constants.PARKING_LOTS_LOOPS.get(parkingLot),value)));
         }
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
                    if (numberOfStops == 0) {
                        //yellow
                        return 30;
                    }

                    else if (numberOfStops > 1) {
                        return 70;
                    }

                }

                //Distance Far
                else {
                }
            }
            //# of Loops is equal to 1.
            else if (numberOfLoops ==1 ) {
                //Distance Close
                if (distanceFromPOI > thresholdPOIClose) {
                    //yellow
                    return 30;
                }

                //Distance Far
                else {
                    //yellow
                    return 30;
                }
            }
            //# of Loops is greater than one.
            else if (numberOfLoops > 1) {
                //red
                return 0;
            }

        }

        return 0;

    }
}
