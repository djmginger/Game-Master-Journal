package com.redheaddev.gmjournal.cities.locations;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.redheaddev.gmjournal.MainActivity;
import com.redheaddev.gmjournal.MyRecyclerViewAdapter;
import com.redheaddev.gmjournal.R;
import com.redheaddev.gmjournal.cities.CityInfo;
import com.redheaddev.gmjournal.cities.citiesDBHelper;

import java.util.ArrayList;

public class LocationList extends AppCompatActivity implements MyRecyclerViewAdapter.OnNoteListener {

    private MyRecyclerViewAdapter adapter;
    private final ArrayList<String> locationList = new ArrayList<>();
    private final ArrayList<String> cityList = new ArrayList<>();
    private String filterChoice;
    private final String TAG = "LocationInfo";
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_titles);
        String cityName = getIntent().getStringExtra("cityName");
        context = this;

        LinearLayoutCompat layout = findViewById(R.id.locationLayout);
        LinearLayoutCompat headerLayout = findViewById(R.id.headerLayout);
        RelativeLayout innerLayout = findViewById(R.id.innerLayout);
        final Spinner filter = findViewById(R.id.filter);
        ImageView backButton = findViewById(R.id.backButton);
        ImageView addEntry = findViewById(R.id.addEntry);
        TextView cityTitle = findViewById(R.id.city);
        RecyclerView recyclerView = findViewById(R.id.info_content);

        final citiesDBHelper mCitiesDBHelper = new citiesDBHelper(this);
        locationsDBHelper mLocationsDBHelper = new locationsDBHelper(this);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int deviceHeight = displayMetrics.heightPixels;

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String theme = sharedPreferences.getString("Theme", "none");
        Boolean darkMode = theme.equals("dark");

        if (theme.equals("dark")){
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(addEntry.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));

            layout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            innerLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            cityTitle.setBackgroundColor(Color.parseColor("#2C2C2C"));
            headerLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            addEntry.setBackgroundColor(Color.parseColor("#2C2C2C"));
            backButton.setBackgroundColor(Color.parseColor("#2C2C2C"));
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(addEntry.getDrawable()), ContextCompat.getColor(context, R.color.black));
        }

        //If coming here from main page, get all locations, otherwise get the city-specific locations
        Cursor locations;
        if (cityName.equals("None")) { locations = mLocationsDBHelper.getAllLocations(); }
        else { locations = mLocationsDBHelper.getLocations(cityName); }

        try {
            if (locations.moveToNext()) {
                Log.d(TAG, "onCreate: " + locations.getString(2));
                do {
                    locationList.add(locations.getString(2));
                    Log.d(TAG, "onCreate: The location added is " + locations.getString(2));
                } while (locations.moveToNext());
            } else{
                TextView noLocations = new TextView(this);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,100,0,0);
                noLocations.setLayoutParams(params);
                noLocations.setText(R.string.nolocationtext);
                noLocations.setGravity(Gravity.CENTER_HORIZONTAL);
                noLocations.setTextSize(20);
                noLocations.setTypeface(null, Typeface.ITALIC);
                noLocations.setTextColor(Color.parseColor("#666666"));
                innerLayout.addView(noLocations);
            }
        } finally {
            locations.close();
        }

        String headerText3 = sharedPreferences.getString("headerText3", "none");
        String headerColor3 = sharedPreferences.getString("headerColor3", "none");
        if (cityName.equals("None")) { //Check if we arrived here from the main page, or from a city
            if(!headerText3.equals("none")) cityTitle.setText(headerText3);
            else cityTitle.setText(getString(R.string.locations));
        }
        else if(!headerText3.equals("none")) cityTitle.setText(String.format("%s %s", headerText3 + " within", cityName));
        else cityTitle.setText(String.format("%s %s", getString(R.string.locationTitle), cityName));
        if(!headerColor3.equals("none")) cityTitle.setTextColor(Color.parseColor(headerColor3));

        //Add all the locations
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        adapter = new MyRecyclerViewAdapter(this, locationList, this, darkMode);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(itemDecor);

        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = (int)(deviceHeight * .70);
        /*if (cityName.equals("None")) { params.height = (int)(deviceHeight * .60); }
        else { params.height = (int)(deviceHeight * .70); }*/

        recyclerView.setLayoutParams(params);

        //Set functionality for the spinner here. If coming here from a city, set the visibility of the spinner to GONE

        if (!cityName.equals("None")) { filter.setVisibility(View.GONE); }
        else {
            cityList.add(getString(R.string.filtercity));

            Cursor cities = mCitiesDBHelper.getCities();
            try {
                if (cities.moveToNext()) {
                    do {
                        cityList.add(cities.getString(1));
                    } while (cities.moveToNext());
                }
            } finally {
                cityList.add(getString(R.string.nocity));
                cities.close();
            }

            final ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityList);
            locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            filter.setAdapter(locationAdapter);

            filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    filterChoice = cityList.get(position);
                    Log.d(TAG, "onItemSelected: Value of filterChoice is: " + filterChoice);

                    if ((!cityList.get(position).equals(getString(R.string.filtercity))) && (!cityList.get(position).equals(getString(R.string.clearfilter))) && (!cityList.get(position).equals("No city"))) {
                        locationList.clear();
                        locationAdapter.getDropDownView(position, view, parent);
                        if (!(cityList.contains(getString(R.string.clearfilter)))) {
                            cityList.add(getString(R.string.clearfilter));
                        }

                        Cursor locations = mLocationsDBHelper.getLocations(filterChoice);
                        try {
                            if (locations.moveToNext()) {
                                do {
                                    locationList.add(locations.getString(2));
                                    Log.d(TAG, "onItemSelected: Filter choice matches Cityname of " + locations.getString(2));
                                    Log.d(TAG, "onItemSelected: Filter choice matches Location name of " + locations.getString(3));
                                } while (locations.moveToNext());
                            }
                        } finally {
                            locations.close();
                        }
                        adapter.notifyDataSetChanged();

                    } else if (cityList.get(position).equals(getString(R.string.clearfilter))) {
                        locationList.clear();
                        Cursor locations = mLocationsDBHelper.getAllLocations();
                        try {
                            if (locations.moveToNext()) {
                                do {
                                    locationList.add(locations.getString(2));
                                } while (locations.moveToNext());
                            }
                            adapter.notifyDataSetChanged();
                        } finally {
                            locations.close();
                        }
                    } else if (cityList.get(position).equals("No city")) {
                        filterChoice = cityList.get(position);
                        Log.d(TAG, "onItemSelected: Value of filterChoice is: " + filterChoice);

                        if ((!cityList.get(position).equals(getString(R.string.filtercity))) && (!cityList.get(position).equals(getString(R.string.clearfilter)))) {
                            locationList.clear();
                            locationAdapter.getDropDownView(position, view, parent);
                            if (!(cityList.contains(getString(R.string.clearfilter)))) {
                                cityList.add(getString(R.string.clearfilter));
                            }

                            Cursor locations = mLocationsDBHelper.getLocations(filterChoice);
                            Cursor locations2 = mLocationsDBHelper.getLocations("None");
                            try {
                                if (locations.moveToNext()) {
                                    do {
                                        locationList.add(locations.getString(2));
                                    } while (locations.moveToNext());
                                }
                                if (locations2.moveToNext()) {
                                    do {
                                        locationList.add(locations2.getString(2));
                                    } while (locations2.moveToNext());
                                }
                            } finally {
                                locations.close();
                                locations2.close();
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                    Log.i("GTOUTOUT", "Nothing Selected");
                }
            });
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        addEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), LocationInfo.class);
                String cityName = getIntent().getStringExtra("cityName");
                intent.putExtra("cityName", cityName);
                startActivity(intent);
                finish();
            }
        });
    }

    //When resuming the activity (going back from CityInfo, refresh the view with the names
    @Override
    protected void onResume() {
        String cityName = getIntent().getStringExtra("cityName");
        super.onResume();
        locationList.clear();
        locationsDBHelper mLocationsDBHelper = new locationsDBHelper(this);
        Cursor locations;
        if (cityName.equals("None")) { locations = mLocationsDBHelper.getAllLocations(); }
        else { locations = mLocationsDBHelper.getLocations(cityName); }
        try {
            if (locations.moveToNext()) {
                do {
                    locationList.add(locations.getString(2));
                } while (locations.moveToNext());
            }
        } finally {
            locations.close();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNoteClick(int position) {
        //If I want a reference of the list item I clicked on
        //Pass in the position as an extra to the new activity you'll create
        String cityName = getIntent().getStringExtra("cityName");
        String locationName = locationList.get(position);
        Log.d(TAG, "onCreate: The value of locationTitle is " + locationName);
        Intent intent = new Intent(this, LocationInfo.class);
        intent.putExtra("cityName", cityName);
        intent.putExtra("locationName", locationName);
        intent.putExtra("addLocationValue", 0);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed(){
        String cityName = getIntent().getStringExtra("cityName");
        if (cityName.equals("None")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, CityInfo.class);
            intent.putExtra("cityName", cityName);
            intent.putExtra("addCityValue", 0);
            startActivity(intent);
        }
    }
}

