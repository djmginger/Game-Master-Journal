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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.redheaddev.gmjournal.MyRecyclerViewAdapter;
import com.redheaddev.gmjournal.R;
import com.redheaddev.gmjournal.cities.CityInfo;

import java.util.ArrayList;

public class LocationList extends AppCompatActivity implements MyRecyclerViewAdapter.OnNoteListener {

    private MyRecyclerViewAdapter adapter;
    private final ArrayList<String> locationList = new ArrayList<>();
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
        ImageView backButton = findViewById(R.id.backButton);
        ImageView addEntry = findViewById(R.id.addEntry);
        TextView cityTitle = findViewById(R.id.city);

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

        // set up the RecyclerView with data from the database
        Cursor locations = mLocationsDBHelper.getLocations(cityName);
        try {
            if (locations.moveToNext()) {
                Log.d(TAG, "onCreate: " + locations.getString(2));
                do {
                    locationList.add(locations.getString(2));
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

        cityTitle.setText(String.format("%s %s", getString(R.string.locationTitle), cityName));

        RecyclerView recyclerView = findViewById(R.id.info_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, locationList, this, darkMode);
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
        finish();
    }

    @Override
    public void onBackPressed(){
        String cityName = getIntent().getStringExtra("cityName");
        Log.d(TAG, "onCreate: The value of cityTitle is " + cityName);
        Intent intent = new Intent(this, CityInfo.class);
        intent.putExtra("cityName", cityName);
        intent.putExtra("addCityValue", 0);
        startActivity(intent);
    }
}

