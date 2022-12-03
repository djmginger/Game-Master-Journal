package com.redheaddev.gmjournal.cities.locations;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.method.KeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.redheaddev.gmjournal.R;
import com.redheaddev.gmjournal.cities.citiesDBHelper;

import java.util.ArrayList;

public class LocationInfo extends AppCompatActivity {

    private KeyListener variable;
    private locationsDBHelper mLocationsDBHelper;
    private citiesDBHelper mCitiesDBHelper;
    private int addLocationValue = 0;
    private String locationTitle = null;
    private final String TAG = "LocationInfo";
    private final ArrayList<String> cityList = new ArrayList<>();
    private Context context;
    boolean deleteExists = false;
    private Boolean darkMode;
    private String initName = "";
    private String initDesc = "";
    private String initPlot = "";
    private String initNote = "";
    private boolean hasSaved = false;

    //String cityName = getIntent().getStringExtra("cityName");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_info);
        context = this;

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        String theme = sharedPreferences.getString("Theme", "none");
        darkMode = theme.equals("dark");

        String cityName = getIntent().getStringExtra("cityName");
        Log.d(TAG, "onCreate: cityName is" + cityName);

        mCitiesDBHelper = new citiesDBHelper(this);
        mLocationsDBHelper = new locationsDBHelper(this);
        ImageView backButton = findViewById(R.id.backButton);
        final EditText locationName = findViewById(R.id.locationName);
        final Spinner filter = findViewById(R.id.filter);
        final EditText locationDescription = findViewById(R.id.locationDescription);
        final EditText locationPlotHooks = findViewById(R.id.locationPlotHooks);
        final EditText locationNotes = findViewById(R.id.locationNotes);
        final ScrollView locationMainLayout = findViewById(R.id.locationMainLayout);
        final LinearLayoutCompat buttonLayout = findViewById(R.id.locationButtonLayout);
        final LinearLayout locationLayout = findViewById(R.id.locationLayout);
        final TextView nameTitle = findViewById(R.id.nameTitle);
        final TextView locationHeader = findViewById(R.id.locationHeader);
        final TextView pageHeader = findViewById(R.id.pageHeader);
        final TextView descTitle = findViewById(R.id.descTitle);
        final TextView plotTitle = findViewById(R.id.plotTitle);
        final TextView noteTitle = findViewById(R.id.noteTitle);
        Button saveLocation = findViewById(R.id.saveLocation);
        final DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

        if (theme.equals("dark")){

            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));

            locationName.setBackgroundResource(R.drawable.info_bg7);
            locationName.setTextColor(Color.parseColor("#dadada"));
            locationDescription.setBackgroundResource(R.drawable.info_bg7);
            locationDescription.setTextColor(Color.parseColor("#dadada"));
            locationPlotHooks.setBackgroundResource(R.drawable.info_bg7);
            locationPlotHooks.setTextColor(Color.parseColor("#dadada"));
            locationNotes.setBackgroundResource(R.drawable.info_bg7);
            locationNotes.setTextColor(Color.parseColor("#dadada"));
            nameTitle.setTextColor(Color.WHITE);
            locationHeader.setTextColor(Color.WHITE);
            descTitle.setTextColor(Color.WHITE);
            plotTitle.setTextColor(Color.WHITE);
            noteTitle.setTextColor(Color.WHITE);
            locationMainLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            backButton.setBackgroundColor(Color.parseColor("#2C2C2C"));
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.black));
        }

        String headerText3 = sharedPreferences.getString("headerText3", "none");
        String headerColor3 = sharedPreferences.getString("headerColor3", "none");
        String infoboxcolor3 = sharedPreferences.getString("infoboxcolor3", "none");
        if(!headerText3.equals("none")) {
            pageHeader.setText(headerText3 + " details");
            nameTitle.setText(headerText3 + " Name");
        }
        if(!headerColor3.equals("none")) pageHeader.setTextColor(Color.parseColor(headerColor3));
        if(!infoboxcolor3.equals("none")) {
            setDrawableColors(infoboxcolor3);
        }

        //if starting the activity from clicking an item, addLocationValue will be 0, otherwise, it's -1
        //we use this value to adjust the behavior of the editText values set during onCreate, and whether we need to check the name for an identical value when creating a new npc
        setAddLocationValue(getIntent().getIntExtra("addLocationValue", -1));
        setLocationTitle(getIntent().getStringExtra("locationName")); //set the location Title to what was passed in the intent. We use this to check to see if we need to UPDATE instead of INSERT when adding an npc

        Log.d(TAG, "onCreate: The value of locationTitle is " + locationTitle);

        cityList.add(getString(R.string.nocity));

        Cursor cities = mCitiesDBHelper.getCities();
        try {
            if (cities.moveToNext()) {
                do {
                    cityList.add(cities.getString(1));
                } while (cities.moveToNext());
            }
        } finally {
            cities.close();
        }

        final ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityList);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filter.setAdapter(locationAdapter);

        Log.d(TAG, "onCreate: The city name is: " + cityName);

        if (!cityName.equals("None") && !cityName.equals("No city")) {
            int spinnerPosition = locationAdapter.getPosition(cityName);
            filter.setSelection(spinnerPosition);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        saveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = locationName.getText().toString();
                String description = locationDescription.getText().toString();
                String plotHooks = locationPlotHooks.getText().toString();
                String notes = locationNotes.getText().toString();

                saveFunctionality(name, description, plotHooks, notes);
            }
        });

        //If coming to this activity from clicking on a name, get and fill out all fields with the data for the corresponding location.
        if (addLocationValue != -1) {
            deleteExists = true;
            Cursor locationInfo = mLocationsDBHelper.getSpecificLocation(locationTitle);
            Log.d(TAG, "onCreate: the cursor value is " + locationInfo.moveToFirst());
            locationInfo.moveToFirst();
            initName = locationInfo.getString(2);
            locationName.setText(initName);
            initDesc = locationInfo.getString(3);
            locationDescription.setText(initDesc);
            initPlot = locationInfo.getString(4);
            locationPlotHooks.setText(initPlot);
            initNote = locationInfo.getString(5);
            locationNotes.setText(initNote);

            ImageView deleteLocationButtonImage = new ImageView(this);
            deleteLocationButtonImage.setImageResource(R.drawable.delete);
            int deviceWidth = (displayMetrics.widthPixels);
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams((int)(deviceWidth * .11), ViewGroup.LayoutParams.WRAP_CONTENT);
            lparams.setMargins(0,0,0,25);
            lparams.gravity = Gravity.CENTER_HORIZONTAL;
            deleteLocationButtonImage.setLayoutParams(lparams);
            deleteLocationButtonImage.setAdjustViewBounds(true);
            deleteLocationButtonImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = locationName.getText().toString();

                    if (!(mLocationsDBHelper.checkNonExistence(name))) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setMessage(R.string.areyou);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                mLocationsDBHelper.removeLocation(locationTitle);
                                Intent intent = new Intent(context, LocationList.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }else toast("There is no item to delete");
                }
            });
            locationLayout.addView(deleteLocationButtonImage);

            cityList.clear();
            try (Cursor cities2 = mCitiesDBHelper.getCities()) {
                if (cities2.moveToNext()) {
                    do {
                        cityList.add(cities2.getString(1));
                    } while (cities2.moveToNext());
                }
            } finally {
                cityList.remove(locationInfo.getString(1));
                cityList.add(0, locationInfo.getString(1));
                if(!(cityList.contains(getString(R.string.nocity)))) {
                    cityList.add(getString(R.string.nocity));
                }
            }
        }
    }

    public void saveFunctionality(String name, String description, String plotHooks, String notes){

        //if the edit text values are non-zero, and there is no identical entry, then add the NPC data
        EditText locationName = findViewById(R.id.locationName);
        if (CheckData(locationName)){

            if (addLocationValue != 0){
                setLocationTitle(name); //if attempting to add a new location, we set this value to pass to the DBHelper in order to check if we need to update an existing entry
            }

            AddLocation(name, description, plotHooks, notes, locationTitle);
        } else {
            toast("Please provide a name for your location");
        }
    }

    public void setDrawableColors(String color){
        final EditText locationName = findViewById(R.id.locationName);
        final EditText locationDescription = findViewById(R.id.locationDescription);
        final EditText locationPlotHooks = findViewById(R.id.locationPlotHooks);
        final EditText locationNotes = findViewById(R.id.locationNotes);

        GradientDrawable drawable1 = (GradientDrawable)locationName.getBackground().getCurrent();
        drawable1.setColor(Color.parseColor(color));
        drawable1.setStroke(4, Color.BLACK);
        GradientDrawable drawable2 = (GradientDrawable)locationDescription.getBackground().getCurrent();
        drawable2.setColor(Color.parseColor(color));
        drawable2.setStroke(4, Color.BLACK);
        GradientDrawable drawable3 = (GradientDrawable)locationPlotHooks.getBackground().getCurrent();
        drawable3.setColor(Color.parseColor(color));
        drawable3.setStroke(4, Color.BLACK);
        GradientDrawable drawable4 = (GradientDrawable)locationNotes.getBackground().getCurrent();
        drawable4.setColor(Color.parseColor(color));
        drawable4.setStroke(4, Color.BLACK);
    }

    private boolean CheckData(EditText name) {
        return ((name.length() != 0));
    }

    private void AddLocation(final String name, final String description, final String plotHooks, final String notes, final String locationTitle){
        final Spinner filter = findViewById(R.id.filter);
        final String cityName = filter.getSelectedItem().toString();
        Log.d(TAG, "addLocation: cityname = " + cityName);
        if(addLocationValue == 0 || !(mLocationsDBHelper.checkNonExistence(name))) { //if a location with the entered name already exists, make sure to tell the database to update the existing entry
            boolean insertLocation = mLocationsDBHelper.addLocation(cityName, name, description, plotHooks, notes, locationTitle, true);
            if (insertLocation) {
                toast("Location saved");
                hasSaved = true;
                onBackPressed();
            } else {
                toast("Error with entry");
            }
            onBackPressed();
        }else { //if the location with the entered name does not exist, proceed as normal
            boolean insertLocation = mLocationsDBHelper.addLocation(cityName, name, description, plotHooks, notes, locationTitle, false);
            if (insertLocation) {
                toast("Location saved");
                hasSaved = true;
                onBackPressed();
            } else {
                toast("Error with entry");
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LocationList.class);
        String cityName = getIntent().getStringExtra("cityName");
        intent.putExtra("cityName", cityName);

        final EditText locationName = findViewById(R.id.locationName);
        final EditText locationDescription = findViewById(R.id.locationDescription);
        final EditText locationPlotHooks = findViewById(R.id.locationPlotHooks);
        final EditText locationNotes = findViewById(R.id.locationNotes);

        if ((!(locationName.getText().toString().equals(initName)) || !(locationDescription.getText().toString().equals(initDesc)) || !(locationPlotHooks.getText().toString().equals(initPlot)) || !(locationDescription.getText().toString().equals(initDesc))) & !hasSaved) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.unsaved);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    String name = locationName.getText().toString();
                    String description = locationDescription.getText().toString();
                    String plotHooks = locationPlotHooks.getText().toString();
                    String notes = locationNotes.getText().toString();
                    dialog.dismiss();
                    saveFunctionality(name, description, plotHooks, notes);
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    startActivity(intent);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            startActivity(intent);
        }
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setAddLocationValue(int value){
        addLocationValue = value;
    }
    private void setLocationTitle(String value){
        locationTitle = value;
    }
}