package com.example.dmapp.cities.locations;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dmapp.R;

public class LocationInfo extends AppCompatActivity {

    private KeyListener variable;
    private locationsDBHelper mLocationsDBHelper;
    private int addLocationValue = 0;
    private String locationTitle = null;
    private final String TAG = "LocationInfo";
    private Context context;
    boolean deleteExists = false;
    //String cityName = getIntent().getStringExtra("cityName");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_info);
        context = this;

        String cityName = getIntent().getStringExtra("cityName");
        Log.d(TAG, "onCreate: cityName is" + cityName);

        mLocationsDBHelper = new locationsDBHelper(this);
        ImageView backButton = findViewById(R.id.backButton);
        final EditText locationName = findViewById(R.id.locationName);
        final EditText locationDescription = findViewById(R.id.locationDescription);
        final EditText locationPlotHooks = findViewById(R.id.locationPlotHooks);
        final EditText locationNotes = findViewById(R.id.locationNotes);
        final LinearLayoutCompat buttonLayout = findViewById(R.id.locationButtonLayout);

        Button saveLocation = findViewById(R.id.saveLocation);

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

                    Button deleteLocationButton = new Button(context);
                    deleteLocationButton.setText("Delete Location");
                    deleteLocationButton.setBackground(ContextCompat.getDrawable(context, R.drawable.buttonbg));
                    LinearLayoutCompat.LayoutParams lparams = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                    lparams.setMargins(5,0, 5, 0);
                    deleteLocationButton.setLayoutParams(lparams);
                    deleteLocationButton.setPadding(30,10,30,10);
                    deleteLocationButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String name = locationName.getText().toString();

                            if (!(mLocationsDBHelper.checkNonExistence(name))) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                //builder.setTitle(R.string.app_name);
                                builder.setMessage("Are you sure you want to delete this item?");
                                //builder.setIcon(R.drawable.ic_launcher);
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        mLocationsDBHelper.removeLocation(locationTitle);
                                        finish();
                                    }
                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
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
                        buttonLayout.addView(deleteLocationButton);
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

            Button deleteLocationButton = new Button(context);
            deleteLocationButton.setText("Delete Location");
            deleteLocationButton.setBackground(ContextCompat.getDrawable(context, R.drawable.buttonbg));
            LinearLayoutCompat.LayoutParams lparams = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lparams.setMargins(5,0, 5, 0);
            deleteLocationButton.setLayoutParams(lparams);
            deleteLocationButton.setPadding(30,10,30,10);
            deleteLocationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = locationName.getText().toString();

                    if (!(mLocationsDBHelper.checkNonExistence(name))) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        //builder.setTitle(R.string.app_name);
                        builder.setMessage("Are you sure you want to delete this item?");
                        //builder.setIcon(R.drawable.ic_launcher);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                mLocationsDBHelper.removeLocation(locationTitle);
                                finish();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }else toast("There is no item to delete");
                }
            });
            buttonLayout.addView(deleteLocationButton);
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
            } else {
                toast("Error with entry");
            }
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