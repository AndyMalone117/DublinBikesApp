package com.andrewmalone.assignmentapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static com.andrewmalone.assignmentapp.Utilities.readFromFile;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    ArrayList<Station> stations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Toast.makeText(MapsActivity.this, "Touch a marker for bike details", Toast.LENGTH_SHORT).show();


    }

    public void run() {

        String response = readFromFile(getApplicationContext());
        parseJsonResponse(response); // Pass Json string into parseJsonResponse methods
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

                    mMap.addMarker(new MarkerOptions().position(stationPosition).
                            title(station.getName()).snippet
                            ("Available Bikes: " + station.getAvailableBikes()
                                    + " | " + "Parking Spaces: " +
                            station.getAvailableParking()));

                }
            }
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

        // Add a marker in Dublin and move the camera
        LatLng Dublin = new LatLng(53.3498, -6.2603);

        mMap.addMarker(new MarkerOptions().position(Dublin).title("Markers in Dublin"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Dublin, 14.25f));

        run();


    }


}
