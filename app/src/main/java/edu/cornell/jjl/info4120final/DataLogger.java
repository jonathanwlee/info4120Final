package edu.cornell.jjl.info4120final;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import android.os.Environment;

public class DataLogger {
    private String logFileName;
    private String logFilePath;
    private SimpleDateFormat logSDF;

    public DataLogger(String mode) {
        logFileName = genFileName(mode);
        logFilePath = genFilePath();
        logSDF = new SimpleDateFormat("HH:mm:ss.SS", Locale.US);
        logSDF.setTimeZone(TimeZone.getTimeZone("UTC-5"));
    }

    public String getLogFilePath() {
        return logFilePath + logFileName;
    }

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

    public void logAccel(String x, String y, String z) {
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

    private String genFileName(String mode) {
        long time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC-5"));
        return "log_" + sdf.format(new Date(time)) + "_" + mode + ".csv";
    }

    private String genFilePath() {
        File storage = Environment.getExternalStorageDirectory();
        String path = storage.getAbsolutePath() + "/data/";
        (new File(path)).mkdirs();
        return path;
    }
}