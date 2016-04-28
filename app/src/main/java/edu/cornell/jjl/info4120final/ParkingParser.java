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
import java.util.Locale;
import java.util.LinkedHashMap;

public class ParkingParser {

    protected String fileName;
    protected String accelerometerFileName;
    protected String locationFileName;
    protected String activityRecogntionFileName;

    protected LinkedHashMap<Date,LatLng> locationData = new LinkedHashMap<Date,LatLng>();
    protected LinkedHashMap<Date,Accel> accelerometerData = new LinkedHashMap<Date,Accel>();
    protected LinkedHashMap<Date, ActivityRecog> activityRecogData = new LinkedHashMap<Date,ActivityRecog>();

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
            fileReader.readLine();
            //Read the file line by line
            while ((line = fileReader.readLine()) != null)
            {

                //Get all tokens available in line
                String[] tokens = line.split(DELIMITER);

                ActivityRecog activityRecog= new ActivityRecog(Integer.valueOf(tokens[1]),Integer.valueOf(tokens[2])
                ,Integer.valueOf(tokens[3]),Integer.valueOf(tokens[4]),Integer.valueOf(tokens[5]),Integer.valueOf(tokens[6]),
                                Integer.valueOf(tokens[7]),Integer.valueOf(tokens[8]));

                activityRecogData.put(sdf.parse(tokens[0]),activityRecog);

            }
        }
            catch (Exception e) {
            e.printStackTrace();
        }
            finally
            {
                try {
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

                if (notFirst) {
                    LatLng location = new LatLng(Double.valueOf(tokens[1]),Double.valueOf(tokens[2]));
                    locationData.put(sdf.parse(tokens[0]),location);

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
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    }


