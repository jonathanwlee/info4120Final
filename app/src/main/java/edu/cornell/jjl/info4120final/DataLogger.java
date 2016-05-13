package edu.cornell.jjl.info4120final;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;

import android.os.Environment;
import android.util.Log;

import com.google.android.gms.location.DetectedActivity;

/**
 * Saves and stores sensor data from a parking instance into a csv file for
 * future analysis.
 */
public class DataLogger {
    private String logFileName;
    private String logFilePath;
    private SimpleDateFormat logSDF;

    /**
     * Constructor to create a data logger for a given mode.
     * @param mode specifies file name. Either accelerometer, activity recognition, or location.
     */
    public DataLogger(String mode) {
        logFileName = genFileName(mode);
        logFilePath = genFilePath();
        logSDF = new SimpleDateFormat("HH:mm:ss.SS", Locale.US);
        logSDF.setTimeZone(TimeZone.getTimeZone("UTC-5"));
    }

    /**
     * Returns Log File Path
     */
    public String getLogFilePath() {
        return logFilePath + logFileName;
    }

    /**
     * Creates a csv file for location data. Writes new latitude and longitude values into a
     * new line along with a timestamp of the current time.
     */
    public void logLocation(String latitude, String longitude) {
        File file = new File(logFilePath, logFileName);
        Boolean fileExists = file.exists();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            if (!fileExists) {
                bw.write("Timestamp, latitude, longitude");
                bw.newLine();
            }
            String time = logSDF.format((new Date(System.currentTimeMillis())));

            bw.write(
                    time + "," + latitude + "," + longitude
            );
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

            // Testing: was written file successful?
            //readAndPrintFile(file);
        }
    /**
     * Creates a csv file for accelerometer data. Writes new x, y, and z values into a
     * new line along with a timestamp of the current time.
     */
    public void logAccel(String x, String y, String z) {
        File file = new File(logFilePath, logFileName);
        Boolean fileExists = file.exists();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            if (!fileExists) {
                bw.write("Timestamp, x, y, z");
                bw.newLine();
            }
            String time = logSDF.format((new Date(System.currentTimeMillis())));

            bw.write(
                    time + "," + x + "," + y + "," + z
            );
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Testing: was written file successful?
        //readAndPrintFile(file);
    }
    /**
     * Creates a csv file for activity recognition data. Writes new detected activities data
     * into a new line along with the timestamp of the current time.
     */
    public void logActivity(ArrayList<DetectedActivity> detectedActivities) {
        int still=0;
        int foot=0;
        int walking=0;
        int running=0;
        int bicycle=0;
        int vehicle=0;
        int tilting=0;
        int unknown=0;

        for (DetectedActivity da: detectedActivities) {
            switch(da.getType()) {
                case DetectedActivity.IN_VEHICLE:
                    vehicle = da.getConfidence();
                    break;
                case DetectedActivity.ON_BICYCLE:
                    bicycle = da.getConfidence();
                    break;
                case DetectedActivity.ON_FOOT:
                    foot = da.getConfidence();
                    break;
                case DetectedActivity.RUNNING:
                    running = da.getConfidence();
                    break;
                case DetectedActivity.STILL:
                    still = da.getConfidence();
                    break;
                case DetectedActivity.TILTING:
                    tilting = da.getConfidence();
                    break;
                case DetectedActivity.UNKNOWN:
                    unknown = da.getConfidence();
                    break;
                case DetectedActivity.WALKING:
                    walking = da.getConfidence();
                    break;
                default:
                    break;
            }
        }

        File file = new File(logFilePath, logFileName);
        Boolean fileExists = file.exists();
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
            if (!fileExists) {
                bw.write("Timestamp, Still, Foot, Walking,Running,Bicycle,Vehicle,Tilting,Unknown");
                bw.newLine();
            }
            String time = logSDF.format((new Date(System.currentTimeMillis())));

            bw.write(
                    time + "," + Integer.toString(still) + "," + Integer.toString(foot) + "," + Integer.toString(walking)
                            + "," + Integer.toString(running) + "," + Integer.toString(bicycle) + ","
                            + Integer.toString(vehicle) + "," + Integer.toString(tilting) + "," + Integer.toString(unknown)
            );
            bw.newLine();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Testing: was written file successful?
        //readAndPrintFile(file);
    }
    /**
     * Used for debugging purposes to read the CSV file.
     */
    private void readAndPrintFile(File file) {
        if (file.exists()) {
            StringBuilder text = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Returns a string name which corresponds to the file name for the csv file.
     */

    private String genFileName(String mode) {
        long time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        return "log_" + sdf.format(new Date(time)) + "_" + mode + ".csv";
    }
    /**
     * Returns a string name corresponding to the file path of the csv file.
     */
    private String genFilePath() {
        File storage = Environment.getExternalStorageDirectory();
        String path = storage.getAbsolutePath() + "/info4120data/";
        (new File(path)).mkdirs();
        return path;
    }
}