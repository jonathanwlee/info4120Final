package edu.cornell.jjl.info4120final;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Android Activity to display parking lots and their availability.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        createParkingParsers();

        populateMarkers();

        //Centers camera view on Ithaca.
        LatLng center = new LatLng(42.447175, -76.483152);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center,14));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return false;
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                Context context = getApplicationContext();

                LinearLayout info = new LinearLayout(context);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(context);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(context);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
    }
    /**
     * Creates a ParkingAnalyzer activity for each file in the phone's file path.
     */
    public void createParkingParsers() {
        //Gets directory.
        File sdCardRoot = Environment.getExternalStorageDirectory();
        File yourDir = new File(sdCardRoot, "info4120data");
        List<String> file_names = new ArrayList<String>();

        //Splits up file names to include just what is needed for a new parking analyzer.
        for (File f : yourDir.listFiles()) {
            String[] file_name_split = f.getName().split("_");
            String file_name = file_name_split[0] + "_" + file_name_split[1] + "_" + file_name_split[2];
            file_names.add(file_name);
        }

        //Get rid of duplicates
        HashSet hs = new HashSet();
        hs.addAll(file_names);
        file_names.clear();
        file_names.addAll(hs);

        for (int i =0; i < file_names.size(); i++) {
            ParkingAnalyzer parkingAnalyzer = new ParkingAnalyzer(file_names.get(i));
        }
    }
    /**
     * Populates markers and colors corresponding to the parking lot status.
     */
    public void populateMarkers() {
        for (Map.Entry<String, LatLng> entry : Constants.PARKING_LOTS.entrySet()) {
            String title = "";
            float marker_color = BitmapDescriptorFactory.HUE_RED;

            //Sets actual titles of parking lots.
            if (entry.getKey() == "Sage") { title = "Sage Hall Parking Lot";}
            else if (entry.getKey() == "WSH") {title = "Willard Straight Hall Parking Lot";}
            else if (entry.getKey() == "Upson") { title = "Upson Hall Parking Lot"; }
            else if (entry.getKey() == "Veterinary") { title = "Veterinary School Parking Lot"; }
            else if (entry.getKey() == "Kite") { title = "Kite Hill Parking Lot"; }
            else if (entry.getKey() == "Law") { title = "Law School Parking Lot"; }
            else if (entry.getKey() == "Bartels") { title = "Bartels Parking Lot"; }

            String status = Constants.PARKING_LOTS_STATUS.get(entry.getKey());

            //Set colors based off status.
            if (status =="Empty") { marker_color = BitmapDescriptorFactory.HUE_GREEN;}
            else if (status =="Semi-Empty") {marker_color = BitmapDescriptorFactory.HUE_YELLOW; }
            else if (status =="Semi-Full") {marker_color = BitmapDescriptorFactory.HUE_ORANGE; }
            else if (status == "Full") {marker_color = BitmapDescriptorFactory.HUE_RED;}

            //Information Label.
            String content = "Total Number of Spaces: " + Constants.PARKING_LOTS_SPACES.get(entry.getKey()) + "\n" +
                    "Status: " + Constants.PARKING_LOTS_STATUS.get(entry.getKey()) + "\n" + "Last Updated: " +
                    Constants.PARKING_LOTS_LAST_UPDATE.get(entry.getKey());
            mMap.addMarker(new MarkerOptions().position(entry.getValue()).title(title).snippet(content)
                    .icon(BitmapDescriptorFactory.defaultMarker(marker_color)));
        }

    }
}