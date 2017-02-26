package com.andrewmalone.assignmentapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ListActivity extends AppCompatActivity {

//    public ListView stationsListView;
//    public ArrayAdapter arrayAdapter;

    private RecyclerView mRecyclerView;
    private StationAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        //calling the run() method
        run();

        //button takes you to the map view
        Button btnGoToMap = (Button) findViewById(R.id.btnMapView);
        //button takes you to the profile view
        Button btnViewProfile = (Button) findViewById(R.id.btnViewProfile);

        mRecyclerView = (RecyclerView) findViewById(R.id.bikeListView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

//        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stations);

        mAdapter = new StationAdapter();
        //ListView is empty while awaiting stationArrayList info from run()

//        stationsListView.setAdapter(arrayAdapter);
        mRecyclerView.setAdapter(mAdapter);

//        stationsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(), "hello" + stations.get(position), Toast.LENGTH_LONG).show();
//            }
//        });


        btnGoToMap.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent myIntent = new Intent(view.getContext(), MapsActivity.class);
                        startActivity(myIntent);
                    }
                }
        );


        btnViewProfile.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent myIntent = new Intent(view.getContext(), ProfileActivity.class);
                        startActivity(myIntent);
                    }
                }
        );

        /*
        AsyncTask task = new AsyncTask<String, String, String>() {

            @Override
            protected String doInBackground(String... strings) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url("https://api.jcdecaux.com/vls/v1/stations/{station_number}?contract={contract_name}")
                        .get()
                        .build();

                try {
                    Response execute = client.newCall(request).execute();
//                    Do JSON Stuff
                    return execute.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return "Failed";
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                    Do JSON Stuff

                //doSomething();
            }
        };

        task.execute("","","");
        */
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
            public void onFailure(Call call, IOException e) {
                Log.d("MAPS ACTIVITY", "Error Present");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string(); //Json string
                parseJsonResponse(result); // Pass Json string into parseJsonResponse methods
                writeToFile(result, getApplicationContext());
                updateAdapter();


                //               try {
                //                   JSONArray jsonObject = new JSONArray(result);
//                    Stations stationArrayList = new Stations();
//                    stationArrayList.fromJson(jsonObject);
//                    stationArrayList.stationArrayList.get(0);
//                    try(BufferedWriter writer= new BufferedWriter(new FileWriter("bikes.txt"))){
//                    writer.write(stationArrayList.toString());
//                    writer.flush();
//                    }
//                    catch (IOException e){
//                        e.printStackTrace();
//                    }

                //            } catch (JSONException e) {
//                    e.printStackTrace();
                //            }

                //              Log.d("",result);

//                ListView bikeListView = (ListView) findViewById(R.id.bikeListView);
//
//                final ArrayList<String> myBikes = new ArrayList<String>(asList(result));
//
//
//
//                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myBikes);
//
//                bikeListView.setAdapter(arrayAdapter);

            }
        });
    }

    void updateAdapter() {
        runOnUiThread(new Runnable() {
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    //Writing the json data to file
    private void writeToFile(String data, Context context) {
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
