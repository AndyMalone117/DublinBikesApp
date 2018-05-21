package com.andrewmalone.assignmentapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.andrewmalone.assignmentapp.R.id.bikeListView;
import static com.andrewmalone.assignmentapp.R.id.btnFavourites;
import static com.andrewmalone.assignmentapp.R.id.refresh;
import static com.andrewmalone.assignmentapp.Utilities.readFromFile;

public class ListActivity extends AppCompatActivity {

    private StationAdapter mAdapter;
    private List<Station> backupStations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("City Bikes");
        //button takes you to the map view
        Button btnGoToMap = findViewById(R.id.btnMapView);
        //button takes you to the FavouritesActivity view
        Button btnFavourites = findViewById(R.id.btnFavourites);


        RecyclerView mRecyclerView = findViewById(bikeListView);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new StationAdapter();
        //ListView is empty while awaiting stationArrayList info from run()

        mRecyclerView.setAdapter(mAdapter);

        btnGoToMap.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent myIntent = new Intent(view.getContext(), MapsActivity.class);
                        startActivity(myIntent);
                    }
                }
        );

        btnFavourites.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent myIntent = new Intent(view.getContext(), FavouritesActivity.class);
                        startActivity(myIntent);
                    }
                }
        );

        run();
    }

    public static Intent createIntent(Context context) {
        Intent myIntent = new Intent(context, ListActivity.class);
        return myIntent;
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
                    mAdapter.addStation(station);
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

    public void run() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url("https://api.jcdecaux.com/vls/v1/stations?contract=Dublin&apiKey=bd691853cab2e508f00c0fea04bd3599d1ba42e5")
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("MAPS ACTIVITY", "Error Present");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String result = Objects.requireNonNull(response.body()).string(); //Json string
                try {
                    parseJsonResponse(result); // Pass Json string into parseJsonResponse methods
                    writeToFile(result, getApplicationContext());
                    updateAdapter();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        MenuItem refreshItem = menu.findItem(R.id.refresh);


        refreshItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mAdapter.notifyDataSetChanged();
                return true;
            }
        });

        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && !newText.isEmpty()) {
                    mAdapter.stations = new ArrayList<>(backupStations);

                    ArrayList<Station> stations = new ArrayList<>(backupStations);
                    for (Station item : stations) {
                        if (!item.getName().contains(newText.toUpperCase())) {
                            mAdapter.stations.remove(item);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                } else {
                    mAdapter.stations = new ArrayList<>(backupStations);
                    mAdapter.notifyDataSetChanged();
                }

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    void updateAdapter() {
        runOnUiThread(new Runnable() {
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    //Writing the json data to file
    public void writeToFile(String data, Context context) {
        String filename = "stationArrayList.json";
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
