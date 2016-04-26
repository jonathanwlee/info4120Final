package edu.cornell.jjl.info4120final;


import android.text.format.DateUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.HashMap;
import java.util.Set;

public class ParkingAnalyzer {

    ParkingParser parser;
    protected HashMap<Date,LatLng> locationData = new HashMap<Date,LatLng>();
    protected HashMap<Date,Accel> accelerometerData = new HashMap<Date,Accel>();
    protected HashMap<Date, ActivityRecog> activityRecogData = new HashMap<Date,ActivityRecog>();

    public ParkingAnalyzer(String date) {
        parser = new ParkingParser(date);
        this.locationData = parser.locationData;
        this.accelerometerData = parser.accelerometerData;
        this.activityRecogData = parser.activityRecogData;
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
        for (int i=0;i<keyset.size()-1;i++) {
            if(((Date) (keyset.toArray()[i])).compareTo(min) < 0) {
                min = (Date) keyset.toArray()[i];
            }
        }

        //Find Latest Time
        for (int i=0;i<keyset.size()-1;i++) {
            if(((Date) (keyset.toArray()[i])).compareTo(max) > 0) {
                max = (Date) keyset.toArray()[i];
            }
        }

       // Log.i("DATE:MIN",min.toString());
       // Log.i("DATE:MAX",max.toString());
        return (max.getTime() - min.getTime()) / DateUtils.SECOND_IN_MILLIS;
    }
}
