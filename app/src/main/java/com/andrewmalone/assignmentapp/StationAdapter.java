package com.andrewmalone.assignmentapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.andrewmalone.assignmentapp.MapsActivity.STATION_POSITION;
import static com.andrewmalone.assignmentapp.MapsActivity.STATION_ID;
import static com.andrewmalone.assignmentapp.MapsActivity.STATION_LAT;
import static com.andrewmalone.assignmentapp.MapsActivity.STATION_LNG;


import static com.andrewmalone.assignmentapp.R.id.map;

/**
 * Created by Andy on 18/02/2017.
 */

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.ViewHolder> {
    //List of stations
    public ArrayList<Station> stations = new ArrayList<>();

    public StationAdapter() {
    }

    void addStation(Station station) {
        stations.add(station);
    }

    void removeStation(int position) {
        stations.remove(position);
    }

    Station getStation(int position) {
        return stations.get(position);
    }

    private List<Station> backupStations = new ArrayList<>();

    private StationAdapter mAdapter;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


//        Here we setup an empty station_item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.station_item, parent, false);

        // set the view's size, margins, paddings and layout parameters
//        ...
//        here we create a VieWHolder, and pass it the above view
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final Station station = stations.get(position);

        viewHolder.mTitleView.setText(station.getName());
//        viewHolder.mSubTitleView.setText(station.getAddress());
        viewHolder.mNumberView.setText(station.getAvailableBikes() + "");
        viewHolder.mSubNumberView.setText(station.getAvailableParking() + "");

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), MapsActivity.class);
                myIntent.putExtra(STATION_ID, station.getNumber());
                myIntent.putExtra(STATION_LNG, station.getLng());
                myIntent.putExtra(STATION_LAT, station.getLat());
                view.getContext().startActivity(myIntent);

            }
        });

    }


    @Override
    public int getItemCount() {
        return stations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView mTitleView;
        TextView mNumberView;
        TextView mSubNumberView;
        final View mView;


        ViewHolder(View view) {
            super(view);
//            mTextView = v;
            mTitleView = (TextView) view.findViewById(R.id.station_item_title);
            mNumberView = (TextView) view.findViewById(R.id.station_item_bikes);
            mSubNumberView = (TextView) view.findViewById(R.id.station_item_parking);
            mView = view;

        }
    }

    public void parseJsonResponse(final String result) {

        JSONObject stationJson;

        try {
            JSONArray jsonArray = new JSONArray(result); //Json array object made from string that was passed into it.
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
                    //This is where we add a particular station into the dataset that backups the array adapter
                    try {
                        mAdapter.addStation(station);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return;
                    }
                }
            }
            Collections.sort(backupStations = mAdapter.stations, new Comparator<Station>() {
                @Override
                public int compare(Station name, Station name2) {
                    String s1 = name.getName();
                    String s2 = name2.getName();
                    return s1.compareToIgnoreCase(s2);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
