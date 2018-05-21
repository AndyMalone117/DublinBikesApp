package com.andrewmalone.assignmentapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static com.andrewmalone.assignmentapp.Utilities.readFromFile;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    Marker marker;
    private HashMap<Integer, Marker> mStationMap = new HashMap<>();
    public static String STATION_POSITION = "stationPosition";
    public static String STATION_ID = "stationNumber";
    public static String STATION_LNG = "stationLng";
    public static String STATION_LAT = "statingLat";

    private int stationPosition;
    private int stationNumber;
    private double stationByIdLat;
    private double stationByIdLng;


    ArrayList<Station> stations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        stationPosition = getIntent().getIntExtra(STATION_POSITION, -1);
        stationNumber = getIntent().getIntExtra(STATION_ID, -1);
        stationByIdLng = getIntent().getDoubleExtra(STATION_LNG, -1);
        stationByIdLat = getIntent().getDoubleExtra(STATION_LAT, -1);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void run() {
        String response = readFromFile(getApplicationContext());
        parseJsonResponse(response); // Pass Json string into parseJsonResponse methods
    }

    public void sortStations() {
        Collections.sort(stations, new Comparator<Station>() {
            @Override
            public int compare(Station name, Station name2) {
                String s1 = name.getName();
                String s2 = name2.getName();
                return s1.compareToIgnoreCase(s2);
            }
        });
    }

    public void parseJsonResponse(final String result) {

        JSONObject stationJson;

        try {
            JSONArray jsonArray = new JSONArray(result); //Json array object made from string that
            // was passed into it.
            // Process each result in json array, decode and convert to business object
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    stationJson = jsonArray.getJSONObject(i);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }

                Station station = new Station();
                station.fromJson(stationJson);
                if (station != null) {
                    //This is where we add a particular station into the dataset that backups the
                    // array adapter

                    stations.add(station);
                    LatLng stationPosition = new LatLng(station.getLat(), station.getLng());

                    MarkerOptions markerOption = new MarkerOptions().position(stationPosition)
                            .title(station.getName())
                            .snippet("Available Bikes: " + station.getAvailableBikes()
                                    + " | " + "Parking Spaces: " +
                                    station.getAvailableParking());
//                    markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_directions_bike_24px));


                    marker = mMap.addMarker(markerOption);
                    mStationMap.put(station.getNumber(), marker);
                }
            }
            sortStations();

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        run();

        // Add a marker in Dublin and move the camera
        LatLng point;

        if (stationNumber == -1 || stations.size() <= 0) {
            point = new LatLng(53.3498, -6.2603);
        } else {
            point = new LatLng(stationByIdLat, stationByIdLng);

//            markers.get(stationNumber).showInfoWindow();
            mStationMap.get(stationNumber).showInfoWindow();

        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 14.25f));
    }
}
