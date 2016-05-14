package edu.cornell.jjl.info4120final;

import android.location.Location;
import android.text.format.DateUtils;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Class used for feature identification and classification of parking lot availability.
 */
public class ParkingAnalyzer {
    String parkingLot = "";
    ParkingParser parser;

    protected boolean exitsOnFoot;
    protected int numberOfLoops;
    protected double distanceFromPOI;
    protected long timeInLot;
    protected int numberOfStops;
    protected boolean most_recent = true;

    protected LinkedHashMap<Date, LatLng> locationData = new LinkedHashMap<Date, LatLng>();
    protected LinkedHashMap<Date, Accel> accelerometerData = new LinkedHashMap<Date, Accel>();
    protected LinkedHashMap<Date, ActivityRecog> activityRecogData = new LinkedHashMap<Date, ActivityRecog>();

    /**
     * Constructor to populate LinkedHashMaps corresponding to sensor data for analysis.
     * @param date
     */
    public ParkingAnalyzer(String date) {
        parser = new ParkingParser(date);
        this.locationData = parser.locationData;
        this.accelerometerData = parser.accelerometerData;
        this.activityRecogData = parser.activityRecogData;
        Log.i("VariablesLogFile",date);

        //Put lastUpdateTime() behind init() if want to run all files.
        lastUpdateTime(date);
        init();
    }


    /**
     * Runs all feature identification and classification procedures.
     */
    public void init() {
        //Only runs if the file is the most recent file corresponding to that parking lot.
        if (most_recent) {
            this.parkingLot = findParkingLot();
            this.exitsOnFoot = exitsOnFoot();
            this.numberOfLoops = numberOfLoops();
            this.distanceFromPOI = distFromPOI(Constants.PARKING_LOTS_POI.get(parkingLot),findParkingLocation());
            this.timeInLot = timeInLot();
            this.numberOfStops = numberOfStops();
            determineParkingAvailability();

            Log.i("VariablesParkingLot",parkingLot);
            Log.i("VariablesOnFoot", Boolean.toString(exitsOnFoot));
            Log.i("VariablesLoops", Integer.toString(numberOfLoops));
            Log.i("VariablesDistancePOI", Double.toString(distanceFromPOI));
            Log.i("VariablesNumberOfStop", Integer.toString(numberOfStops));
            Log.i("VariablesResult",Constants.PARKING_LOTS_STATUS.get(parkingLot));
        }
    }

    /**
     * Determines whether the current file is the most recent data log of the given parking lot.
     * Converts date to String format that can be displayed in the MapsActivity.
     * @param date
     */
    public void lastUpdateTime(String date) {
        Date updatedDate;
        Date currentDate;
        String[] split_date = date.split("_");

        String[] split_year = split_date[1].split("-");
        String string_date = split_year[1] + "-" + split_year[2] + "_" + split_date[2];

        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd_HH-mm-ss");
        try {
            updatedDate = formatter.parse(string_date);

            try {
                currentDate = formatter.parse(Constants.PARKING_LOTS_LAST_UPDATE.get(parkingLot));
            }

            catch (ParseException e){
                currentDate = new Date(Long.MIN_VALUE);
            }
            if (updatedDate.after(currentDate)) {
                String[] split_update_date = updatedDate.toString().split("EDT");
                Constants.PARKING_LOTS_LAST_UPDATE.put(parkingLot,split_update_date[0]);
                most_recent = true;
            }
            else {
                most_recent = false;
            }

        }
        catch (ParseException e) {
        }

    }
    /**
     * returns string name of parking lot.
     */
    public String findParkingLot() {
        LatLng firstLocation = locationData.get(locationData.keySet().toArray()[0]);
        for (Map.Entry<String,LatLng> entry : Constants.PARKING_LOTS.entrySet()) {
            if (distFromPOI(firstLocation,entry.getValue()) < 150) {
                return entry.getKey();
            }
        }

        //Default. Sage Hall Parking Lot.
        return "Sage";
    }


    /**
     * Returns the number of stops from the given accelerometer data.
     */
    public int numberOfStops() {
        int stops = 0;
        boolean wasMoving = false;
        boolean isMoving = false;

        Deque<Double> last100 = new LinkedList<Double>();

        List<Double> variances = new ArrayList<Double>();
        for (Map.Entry<Date, Accel> entry : accelerometerData.entrySet()) {
            Date key = entry.getKey();
            Accel value = entry.getValue();
            //Magnitude
            double magnitude = findMagnitude(value.x, value.y, value.z);
            last100.addLast(magnitude);
            if (last100.size() < 100) {
                continue;
            }

            double variance = variance(last100);
            variances.add(variance);
            last100.removeFirst();
        }

        double threshold = average(variances);

        last100.clear();
        for (Map.Entry<Date, Accel> entry : accelerometerData.entrySet()) {
            Date key = entry.getKey();
            Accel value = entry.getValue();
            //Magnitude
            double magnitude = findMagnitude(value.x, value.y, value.z);
            last100.addLast(magnitude);
            if (last100.size() < 100) {
                continue;
            }

            double variance = variance(last100);
            if (variance < threshold) {
                if (isMoving) {
                    wasMoving = true;
                    isMoving = false;
                }
            }
            else {
                if (!isMoving) {
                    if (wasMoving) {
                        stops++;
                    }
                    wasMoving = false;
                    isMoving = true;
                }
            }
            last100.removeFirst();
        }

        return stops;
    }

    /**
     * Function to calculate the average. Used for determining number of stops.
     */
    private double average(Collection<Double> magnitudes) {
        double sum = 0.0;
        for (Double x : magnitudes) {
            sum += x;
        }
        return sum / magnitudes.size();
    }

    /**
     * Function to calculate variance of a given number of magnitudes. Used for determining
     * number of stops.
     */
    private double variance(Collection<Double> magnitudes) {
        double avg = average(magnitudes);
        double sum = 0.0;
        for (Double x : magnitudes) {
            sum += (x - avg) * (x - avg);
        }
        return sum / magnitudes.size();
    }

    /**
     * Returns the magnitude of a given x,y,z value.
     */
    public double findMagnitude(float x, float y, float z) {
        return Math.sqrt(x*x + y*y + z*z);
    }

    /**
     * @return LatLng corresponding to the parking location.
     */
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

    /**
     * Determines whether user exited the parking lot on foot or vehicle.
     * @return boolean corresponding to if the user exited on foot.
     */
    public boolean exitsOnFoot() {
        //Get last two activity recognition.
        int walkingThreshold = 50;
        List keyList = new ArrayList<Date>(activityRecogData.keySet());
        //If last two activity recognition don't have on foot > 50,
        for (int i=1; i < 3; i++) {
            if (activityRecogData.get(keyList.toArray()[keyList.size()-i]).walking > 50
                    && activityRecogData.get(keyList.toArray()[keyList.size()-i]).vehicle < 40){
                return true;
            }
        }
        return false;
    }

    /**
     * Determines the timestamp most closely corresponding to the time that the user has parked.
     * @return Date corresponding to timestamp at which parked.
     */
    public Date findParkedTimestamp() {
        Date parkingTimestamp = new Date(Long.MIN_VALUE);
        boolean foundIndex = false;

        List keyList = new ArrayList<Date>(activityRecogData.keySet());

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

                            //Add up averages
                            long average = ((((Date) keyList.toArray()[walkingIndex]).getTime()
                                    - parkingTimestamp.getTime()) / 2 );

                            Calendar cal = Calendar.getInstance();
                            cal.setTime(((Date) keyList.toArray()[walkingIndex-1]));
                            cal.add(Calendar.MILLISECOND, ((int) average));
                            parkingTimestamp = cal.getTime();
                        }
                    }
                }
            }
        }
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

    /**
     * Determines the distance between two LatLng objects.
     */
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

    /**
     * Determines the number of loops that the user has made witin a parking lot.
     * @return integer corresponding to the numer of loops that a user conducted.
     */
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
         }
        return loops;
    }


    /**
    Implements decision tree for determining parking availability.
     */
    public void determineParkingAvailability() {
        double thresholdPOIClose = 4;
        double thresholdPOIFar = 4;

        if (!exitsOnFoot) {
            Constants.PARKING_LOTS_STATUS.put(parkingLot,"Full");
        }
        else {
            //Walking and Number loops == 0
            if (numberOfLoops == 0 ) {

                //Distance Far
                if (distanceFromPOI > thresholdPOIClose) {
                    if (numberOfStops == 0) {
                        //yellow
                        Constants.PARKING_LOTS_STATUS.put(parkingLot,"Semi-Full");
                    }

                    else if (numberOfStops > 1) {
                        Constants.PARKING_LOTS_STATUS.put(parkingLot,"Semi-Full");
                    }
                }

                //Distance Close
                else {
                    if (numberOfStops == 0) {
                        Constants.PARKING_LOTS_STATUS.put(parkingLot,"Empty");
                    }

                    else if (numberOfStops > 1) {
                        Constants.PARKING_LOTS_STATUS.put(parkingLot,"Semi-Empty");
                    }
                }
            }
            //# of Loops is equal to 1.
            else if (numberOfLoops ==1 ) {
                //Distance Far
                if (distanceFromPOI > thresholdPOIClose) {
                    Constants.PARKING_LOTS_STATUS.put(parkingLot,"Semi-Full");
                }

                //Distance Close
                else {
                    Constants.PARKING_LOTS_STATUS.put(parkingLot,"Semi-Full");
                }
            }
            //# of Loops is greater than one.
            else if (numberOfLoops > 1) {
                Constants.PARKING_LOTS_STATUS.put(parkingLot,"Full");
            }
        }
    }
}
