package com.redheaddev.gmjournal.cities.distances;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.redheaddev.gmjournal.cities.citiesDBHelper;
import com.redheaddev.gmjournal.R;

import java.util.ArrayList;

public class DistanceList extends AppCompatActivity {

    private DistanceAdapter distanceAdapter;
    private final ArrayList<Distance> distanceList = new ArrayList<>();
    private final ArrayList<String> cityList = new ArrayList<>();
    private final String TAG = "PresetList";
    private String passedCityFrom;
    private String cityTo;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.distance_list);
        setPassedCityFrom(this.getIntent().getStringExtra("cityFrom"));
        context = this;

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String theme = sharedPreferences.getString("Theme", "none");
        Boolean darkMode = theme.equals("dark");

        distanceDBHelper mDistanceDBHelper = new distanceDBHelper(this);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int deviceHeight = (displayMetrics.heightPixels);

        Cursor x = mDistanceDBHelper.getAllDistances();

        LinearLayoutCompat layout = findViewById(R.id.distanceLayout);
        LinearLayoutCompat headerLayout = findViewById(R.id.distanceHeaderLayout);
        LinearLayout addLayout = findViewById(R.id.distanceItem);
        ImageView backButton = findViewById(R.id.backButton);
        ImageView toArrow = findViewById(R.id.toArrow);
        ImageView distanceColon = findViewById(R.id.distanceColon);
        TextView distanceTitle = findViewById(R.id.city);
        final Button addEntry = findViewById(R.id.addEntry);
        final EditText customDistance = findViewById(R.id.customDistanceValue);
        ListView distanceListView = findViewById(R.id.distanceListView);
        final Spinner customToCity = findViewById(R.id.customToCity);

        ViewGroup.LayoutParams params = distanceListView.getLayoutParams();
        params.height = (int)(deviceHeight * .73);
        distanceListView.setLayoutParams(params);

        distanceTitle.setText(String.format("%s %s", getString(R.string.traveltimefrom), getPassedCityFrom()));

        if (theme.equals("dark")){
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(toArrow.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(distanceColon.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            distanceTitle.setTextColor(Color.WHITE);
            layout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            headerLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            addLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            toArrow.setBackgroundColor(Color.parseColor("#2C2C2C"));
            distanceColon.setBackgroundColor(Color.parseColor("#2C2C2C"));
            backButton.setBackgroundColor(Color.parseColor("#2C2C2C"));
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(toArrow.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(distanceColon.getDrawable()), ContextCompat.getColor(context, R.color.black));
        }

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

        distanceAdapter = new DistanceAdapter(this, distanceList, getPassedCityFrom(), false, darkMode);
        distanceListView.setAdapter(distanceAdapter);

        //setup the spinner for the cities:

        cityList.add(getString(R.string.destination));
        final citiesDBHelper mCitiesDBHelper = new citiesDBHelper(this);
        final Cursor cities = mCitiesDBHelper.getCities();
        try {
            if (cities.moveToNext()) {
                do {
                    if(!(passedCityFrom.equals(cities.getString(1)))) {
                        cityList.add(cities.getString(1));
                    }
                } while (cities.moveToNext());
            }
        } finally {
            cities.close();
        }

        final ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityList);
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
                distanceDBHelper mDistanceDBHelper = new distanceDBHelper(context);
                if(getCityTo().equals(getString(R.string.destination))) {
                    toast(getString(R.string.selectdestination));
                } else if(customDistanceValue.equals("")) {
                    toast(getString(R.string.providedistance));
                } else if(mDistanceDBHelper.checkNonExistence(getPassedCityFrom(), getCityTo())) { //add the selected spinner value here
                    mDistanceDBHelper.addDistance(getPassedCityFrom(), getCityTo(), customDistanceValue);
                    mDistanceDBHelper.addDistance(getCityTo(), getPassedCityFrom(), customDistanceValue);
                    distanceList.clear();
                    try (Cursor distances = mDistanceDBHelper.getDistances(getPassedCityFrom())) {
                        if (distances.moveToNext()) {
                            do {
                                distanceList.add(new Distance(distances.getString(2), distances.getString(3)));
                            } while (distances.moveToNext());
                        }
                    }
                    customDistance.setText("");
                    customToCity.setSelection(0);
                    distanceAdapter.notifyDataSetChanged();
                } else {
                    toast(getString(R.string.alreadyexists));
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


