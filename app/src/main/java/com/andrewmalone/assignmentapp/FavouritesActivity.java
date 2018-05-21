package com.andrewmalone.assignmentapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import static com.andrewmalone.assignmentapp.R.id.btnFavourites;
import static com.andrewmalone.assignmentapp.R.id.btnGoToList;

public class FavouritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("City Bikes");
        //button takes you to the map view
        Button btnGoToMap = (Button) findViewById(R.id.btnMapView);
        //button takes you to the FavouritesActivity view
        Button btnGoToList = (Button) findViewById(R.id.btnGoToList);

        btnGoToMap.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent myIntent = new Intent(view.getContext(), MapsActivity.class);
                        startActivity(myIntent);
                    }
                }
        );


        btnGoToList.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent myIntent = new Intent(view.getContext(), ListActivity.class);
                        startActivity(myIntent);
                    }
                }
        );

    }

}
