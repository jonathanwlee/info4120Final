package edu.cornell.jjl.info4120final;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ParkingParser {

    protected String fileName;
    protected String accelerometerFileName;
    protected String locationFileName;
    protected String activityRecogntionFileName;

    protected HashMap<Date,LatLng> locationData = new HashMap<Date,LatLng>();
    protected HashMap<Date,Accel> accelerometerData = new HashMap<Date,Accel>();
    protected HashMap<Date, ActivityRecog> activityRecogData = new HashMap<Date,ActivityRecog>();

    protected SimpleDateFormat sdf;

    public ParkingParser(String date) {
        this.fileName = date;

        File storage = Environment.getExternalStorageDirectory();
        String path = storage.getAbsolutePath() + "/info4120data/";
        sdf = new SimpleDateFormat("HH:mm:ss.SS", Locale.US);

        accelerometerFileName = path + fileName + "_accel.csv";
        locationFileName = path + fileName + "_location.csv";
        activityRecogntionFileName = path + fileName + "_activity_recognition.csv";
        init();
    }

    public void init() {
        populateLocationData();
        populateAccelerometerData();
        populateActivityData();
    }

    public void populateAccelerometerData() {
        BufferedReader fileReader = null;
        //Delimiter used in CSV file
        final String DELIMITER = ",";
        try
        {
            String line = "";
            //Create the file reader
            fileReader = new BufferedReader(new FileReader(accelerometerFileName));
            boolean notFirst = false;
            //Read the file line by line
            while ((line = fileReader.readLine()) != null)
            {
                //Get all tokens available in line
                String[] tokens = line.split(DELIMITER);

                if (notFirst) {
                    Accel accel = new Accel(Float.valueOf(tokens[1]),Float.valueOf(tokens[2]),Float.valueOf(tokens[3]));
                    accelerometerData.put(sdf.parse(tokens[0]),accel);
                }
                notFirst = true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            try {
                Log.i("Tokens:Accel", Integer.toString(accelerometerData.size()));
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void populateActivityData() {
        BufferedReader fileReader = null;
        //Delimiter used in CSV file
        final String DELIMITER = ",";
        try
        {
            String line = "";
            //Create the file reader
            fileReader = new BufferedReader(new FileReader(activityRecogntionFileName));
            boolean notFirst = false;
            //Read the file line by line
            while ((line = fileReader.readLine()) != null)
            {
                //Get all tokens available in line
                String[] tokens = line.split(DELIMITER);
                Log.i("token","ran");

                if (notFirst) {
                    ActivityRecog activityRecog= new ActivityRecog(Integer.valueOf(tokens[1]),Integer.valueOf(tokens[2])
                    ,Integer.valueOf(tokens[3]),Integer.valueOf(tokens[4]),Integer.valueOf(tokens[5]),Integer.valueOf(tokens[6]),
                                    Integer.valueOf(tokens[7]),Integer.valueOf(tokens[8]));

                    activityRecogData.put(sdf.parse(tokens[0]),activityRecog);
                    Log.i("tokendate:location", sdf.parse(tokens[0]).toString());

                }
                notFirst = true;
            }
        }
            catch (Exception e) {
            e.printStackTrace();
        }
            finally
            {
                try {
                    Log.i("Tokens:ActivityRecog", Integer.toString(activityRecogData.size()));
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    public void populateLocationData() {
        BufferedReader fileReader = null;
        //Delimiter used in CSV file
        final String DELIMITER = ",";
        try
        {
            String line = "";
            //Create the file reader
            fileReader = new BufferedReader(new FileReader(locationFileName));
            boolean notFirst = false;
            //Read the file line by line
            while ((line = fileReader.readLine()) != null)
            {
                //Get all tokens available in line
                String[] tokens = line.split(DELIMITER);
                Log.i("token","ran");

                if (notFirst) {
                    LatLng location = new LatLng(Double.valueOf(tokens[1]),Double.valueOf(tokens[2]));
                    locationData.put(sdf.parse(tokens[0]),location);
                    Log.i("token","running inside");

                }
                notFirst = true;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally
        {
            try {
                Log.i("Tokens:Location", Integer.toString(locationData.size()));
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    }


