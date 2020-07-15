package com.example.dmapp.cities.distances;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dmapp.cities.citiesDBHelper;
import com.example.dmapp.R;

import java.util.ArrayList;

public class DistanceList extends AppCompatActivity {

    private DistanceAdapter distanceAdapter;
    private final ArrayList<Distance> distanceList = new ArrayList<>();
    private final ArrayList<String> cityList = new ArrayList<>();
    private final String TAG = "PresetList";
    private String passedCityFrom;
    private String cityTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.distance_list);
        setPassedCityFrom(this.getIntent().getStringExtra("cityFrom"));
        Log.d(TAG, "onCreate: Passed Preset variable is " + getPassedCityFrom());


        // data to populate the RecyclerView with

        distanceDBHelper mDistanceDBHelper = new distanceDBHelper(this);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int deviceHeight = (displayMetrics.heightPixels);

        Cursor x = mDistanceDBHelper.getAllDistances();
        Log.d(TAG, "onCreate: the amount of rows in the distance table is " + x.getCount());

        ImageView backButton = findViewById(R.id.backButton);
        TextView distanceTitle = findViewById(R.id.city);
        final Button addEntry = findViewById(R.id.addEntry);
        final EditText customDistance = findViewById(R.id.customDistanceValue);
        ListView distanceListView = findViewById(R.id.distanceListView);
        final Spinner customToCity = findViewById(R.id.customToCity);

        ViewGroup.LayoutParams params = distanceListView.getLayoutParams();
        params.height = (int)(deviceHeight * .65);
        distanceListView.setLayoutParams(params);

        distanceTitle.setText("Travel time from " + getPassedCityFrom());

        // set up the RecyclerView with data from the database
        //Log.d(TAG, "onCreate: Preset variable is " + getIntent().getStringExtra("cityFrom"));
        Cursor distances = mDistanceDBHelper.getDistances(getIntent().getStringExtra("cityFrom"));
        try {
            if (distances.moveToNext()) {
                do {
                    distanceList.add(new Distance(distances.getString(2), distances.getString(3)));
                } while (distances.moveToNext());
            }
        } finally {
            distances.close();
        }

        distanceAdapter = new DistanceAdapter(this, distanceList, getPassedCityFrom(), false);
        distanceListView.setAdapter(distanceAdapter);

        //setup the spinner for the cities:

        cityList.add("Destination:");
        final citiesDBHelper mCitiesDBHelper = new citiesDBHelper(this);
        final Cursor cities = mCitiesDBHelper.getCities();
        try {
            if (cities.moveToNext()) {
                do {
                    if(!(passedCityFrom.equals(cities.getString(1)))) {
                        Log.d(TAG, "onCreate: the passedcity = " + passedCityFrom + ", and the cities value = " + cities.getString(1));
                        cityList.add(cities.getString(1));
                    }
                } while (cities.moveToNext());
            }
        } finally {
            cities.close();
        }

        final ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cityList);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        customToCity.setAdapter(locationAdapter);

        customToCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cityChoice = cityList.get(position);
                setCityTo(cityChoice);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("Spinner selection", "Nothing Selected");
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        addEntry.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onClick(View view) {
                String customDistanceValue = customDistance.getText().toString();
                distanceDBHelper mDistanceDBHelper = new distanceDBHelper(addEntry.getContext());
                if(getCityTo().equals("Destination:")) {
                    toast("Please select your destination");
                } else if(customDistanceValue.equals("")) {
                    toast("Please provide a distance");
                } else if(mDistanceDBHelper.checkNonExistence(getPassedCityFrom(), getCityTo())) { //add the selected spinner value here
                    mDistanceDBHelper.addDistance(getPassedCityFrom(), getCityTo(), customDistanceValue);
                    distanceList.clear();
                    try (Cursor distances = mDistanceDBHelper.getDistances(getPassedCityFrom())) {
                        Log.d(TAG, "onClick: ayyyyyy we're here");
                        if (distances.moveToNext()) {
                            Log.d(TAG, "onClick: Distances are: " + distances.getString(2) + " to " + distances.getString(3));
                            do {
                                distanceList.add(new Distance(distances.getString(2), distances.getString(3)));
                            } while (distances.moveToNext());
                        }
                    }
                    customDistance.setText("");
                    customToCity.setSelection(0);
                    distanceAdapter.notifyDataSetChanged();
                } else {
                    toast("This set already exists!");
                }
            }
        });
    }

    private void setPassedCityFrom(String passedCityFrom) {
        this.passedCityFrom = passedCityFrom;
    }

    private String getPassedCityFrom() {
        return passedCityFrom;
    }

    private String getCityTo() {
        return cityTo;
    }

    private void setCityTo(String cityTo) {
        this.cityTo = cityTo;
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}


