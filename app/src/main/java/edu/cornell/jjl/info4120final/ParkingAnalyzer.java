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


    public long timeInLot() {
        Date first = (Date) accelerometerData.keySet().toArray()[0];
        Date last = (Date) accelerometerData.keySet().toArray()[accelerometerData.size()-1];

        Set<Date> keyset = accelerometerData.keySet();
        Date min = new Date(Long.MIN_VALUE);
        for (int i=0;i<keyset.size()-1;i++) {
            if(((Date) (keyset.toArray()[i])).compareTo(min) < 0) {
                min = (Date) keyset.toArray()[i];
            }
        }

        Log.i("DATE:1",min.toString());
        Log.i("DATE:2",last.toString());

        return (last.getTime() - first.getTime()) / DateUtils.SECOND_IN_MILLIS;
    }
}
