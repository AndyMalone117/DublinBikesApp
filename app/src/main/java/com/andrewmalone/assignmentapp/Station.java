package com.andrewmalone.assignmentapp;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Andy on 08/02/2017.
 */

public class Station {

    private int number;
    private String name;
    private String address;
    private double lat;
    private double lng;
    private int availableBikes;
    private int availableParking;

    public int getNumber() {
        return this.number;
    }

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
    }

    public int getAvailableBikes() {
        return this.availableBikes;
    }

    public int getAvailableParking() {
        return this.availableParking;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public void fromJson(JSONObject jsonObject) {

        // Deserialize json into object fields
        try {

            this.number = jsonObject.getInt("number");
            this.name = jsonObject.getString("name");
            this.address = jsonObject.getString("address");
            this.availableBikes = jsonObject.getInt("available_bikes");
            this.availableParking = jsonObject.getInt("available_bike_stands");
            this.lat = jsonObject.getJSONObject("position").getDouble("lat");
            this.lng = jsonObject.getJSONObject("position").getDouble("lng");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}

