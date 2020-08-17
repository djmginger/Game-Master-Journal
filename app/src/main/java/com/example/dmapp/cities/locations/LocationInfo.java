package com.example.dmapp.cities.locations;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dmapp.R;
import com.example.dmapp.npcs.NpcDisplay;
import com.example.dmapp.npcs.NpcList;

public class LocationInfo extends AppCompatActivity {

    private KeyListener variable;
    private locationsDBHelper mLocationsDBHelper;
    private int addLocationValue = 0;
    private String locationTitle = null;
    private final String TAG = "LocationInfo";
    private Context context;
    boolean deleteExists = false;
    private Boolean darkMode;
    //String cityName = getIntent().getStringExtra("cityName");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_info);
        context = this;

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String theme = sharedPreferences.getString("Theme", "none");
        darkMode = theme.equals("dark");

        String cityName = getIntent().getStringExtra("cityName");
        Log.d(TAG, "onCreate: cityName is" + cityName);

        mLocationsDBHelper = new locationsDBHelper(this);
        ImageView backButton = findViewById(R.id.backButton);
        final EditText locationName = findViewById(R.id.locationName);
        final EditText locationDescription = findViewById(R.id.locationDescription);
        final EditText locationPlotHooks = findViewById(R.id.locationPlotHooks);
        final EditText locationNotes = findViewById(R.id.locationNotes);
        final ScrollView locationMainLayout = findViewById(R.id.locationMainLayout);
        final LinearLayoutCompat buttonLayout = findViewById(R.id.locationButtonLayout);
        final LinearLayout locationLayout = findViewById(R.id.locationLayout);
        final TextView nameTitle = findViewById(R.id.nameTitle);
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
            descTitle.setTextColor(Color.WHITE);
            plotTitle.setTextColor(Color.WHITE);
            noteTitle.setTextColor(Color.WHITE);
            locationMainLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            backButton.setBackgroundColor(Color.parseColor("#2C2C2C"));
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.black));
        }

        //if starting the activity from clicking an item, addLocationValue will be 0, otherwise, it's -1
        //we use this value to adjust the behavior of the editText values set during onCreate, and whether we need to check the name for an identical value when creating a new npc
        setAddLocationValue(getIntent().getIntExtra("addLocationValue", -1));
        setLocationTitle(getIntent().getStringExtra("locationName")); //set the location Title to what was passed in the intent. We use this to check to see if we need to UPDATE instead of INSERT when adding an npc

        Log.d(TAG, "onCreate: The value of locationTitle is " + locationTitle);

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

                //if the edit text values are non-zero, and there is no identical entry, then add the NPC data and remove editability again.
                if (CheckData(locationName)){

                    if (addLocationValue != 0){
                        setLocationTitle(name); //if attempting to add a new location, we set this value to pass to the DBHelper in order to check if we need to update an existing entry
                    }

                    AddLocation(name, description, plotHooks, notes, locationTitle);

                    ImageView deleteLocationButtonImage = new ImageView(context);
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
                                        String cityName = getIntent().getStringExtra("cityName");
                                        intent.putExtra("cityName", cityName);
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

                    if (!deleteExists) {
                        locationLayout.addView(deleteLocationButtonImage);
                        deleteExists = true;
                    }
                } else {
                    toast("Please provide a name for your city");
                }
            }
        });

        //If coming to this activity from clicking on a name, get and fill out all fields with the data for the corresponding location.
        if (addLocationValue != -1) {
            deleteExists = true;
            Cursor locationInfo = mLocationsDBHelper.getSpecificLocation(locationTitle);
            Log.d(TAG, "onCreate: the cursor value is " + locationInfo.moveToFirst());
            locationInfo.moveToFirst();
            locationName.setText(locationInfo.getString(2));
            locationDescription.setText(locationInfo.getString(3));
            locationPlotHooks.setText(locationInfo.getString(4));
            locationNotes.setText(locationInfo.getString(5));

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
        }
    }

    private boolean CheckData(EditText name) {
        return ((name.length() != 0));
    }

    private void AddLocation(final String name, final String description, final String plotHooks, final String notes, final String locationTitle){
        final String cityName = getIntent().getStringExtra("cityName");
        Log.d(TAG, "addLocation: cityname = " + cityName);
        if(addLocationValue == 0 || !(mLocationsDBHelper.checkNonExistence(name))) { //if a location with the entered name already exists, ask the user if they wish to update the entry
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //builder.setTitle(R.string.app_name);
            builder.setMessage(R.string.updateinfo);
            //builder.setIcon(R.drawable.ic_launcher);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    boolean insertLocation = mLocationsDBHelper.addLocation(cityName, name, description, plotHooks, notes, locationTitle, true);
                    if (insertLocation) {
                        toast("Location saved");
                        onBackPressed();
                    } else {
                        toast("Error with entry");
                    }
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }else { //if the location with the entered name does not exist, proceed as normal
            boolean insertLocation = mLocationsDBHelper.addLocation(cityName, name, description, plotHooks, notes, locationTitle, false);
            if (insertLocation) {
                toast("Location saved");
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
        startActivity(intent);
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