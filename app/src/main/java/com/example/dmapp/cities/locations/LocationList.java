package com.example.dmapp.cities.locations;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dmapp.MyRecyclerViewAdapter;
import com.example.dmapp.R;

import java.util.ArrayList;

public class LocationList extends AppCompatActivity implements MyRecyclerViewAdapter.OnNoteListener {

    private MyRecyclerViewAdapter adapter;
    private final ArrayList<String> locationList = new ArrayList<>();
    private final String TAG = "LocationInfo";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_titles);
        String cityName = getIntent().getStringExtra("cityName");

        LinearLayoutCompat layout = findViewById(R.id.locationLayout);
        layout.setBackgroundColor(getResources().getColor(R.color.mainColor2));

        // data to populate the RecyclerView with

        locationsDBHelper mLocationsDBHelper = new locationsDBHelper(this);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int deviceHeight = displayMetrics.heightPixels;

        ImageView backButton = findViewById(R.id.backButton);
        ImageView addEntry = findViewById(R.id.addEntry);

        // set up the RecyclerView with data from the database

        Cursor locations = mLocationsDBHelper.getLocations(cityName);
        try {
            if (locations.moveToNext()) {
                Log.d(TAG, "onCreate: " + locations.getString(2));
                do {
                    locationList.add(locations.getString(2));
                } while (locations.moveToNext());
            }
        } finally {
            locations.close();
        }

        TextView city = findViewById(R.id.city);
        city.setText(String.format("%s %s", getString(R.string.locationTitle), cityName));

        RecyclerView recyclerView = findViewById(R.id.info_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, locationList, this);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);

        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = (int)(deviceHeight * .70);
        recyclerView.setLayoutParams(params);

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
                Intent intent = new Intent(view.getContext(), LocationInfo.class);
                String cityName = getIntent().getStringExtra("cityName");
                intent.putExtra("cityName", cityName);
                startActivity(intent);
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
        Cursor locations = mLocationsDBHelper.getLocations(cityName);
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
    }
}

