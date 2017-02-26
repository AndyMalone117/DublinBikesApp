package com.andrewmalone.assignmentapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

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
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Station station = stations.get(position);

        viewHolder.mTitleView.setText(station.getName());
//        viewHolder.mSubTitleView.setText(station.getAddress());
        viewHolder.mNumberView.setText(station.getAvailableBikes() + "");
        viewHolder.mSubNumberView.setText(station.getAvailableParking() + "");


    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTitleView;
//        public TextView mSubTitleView;
        public TextView mNumberView;
        public TextView mSubNumberView;

        public ViewHolder(View view) {
            super(view);
//            mTextView = v;
            mTitleView = (TextView) view.findViewById(R.id.station_item_title);
//            mSubTitleView = (TextView) view.findViewById(R.id.station_item_subtitle);
            mNumberView = (TextView) view.findViewById(R.id.station_item_bikes);
            mSubNumberView = (TextView) view.findViewById(R.id.station_item_parking);


        }
    }


}
