package com.example.dmapp.cities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dmapp.cities.distances.DistanceList;
import com.example.dmapp.cities.distances.distanceDBHelper;
import com.example.dmapp.cities.locations.LocationList;
import com.example.dmapp.cities.locations.locationsDBHelper;
import com.example.dmapp.presets.PresetList;
import com.example.dmapp.R;

public class CityInfo extends AppCompatActivity {

    private citiesDBHelper mCityDBHelper;
    private locationsDBHelper mLocationDBHelper;
    private distanceDBHelper mDistanceDBHelper;
    private int addCityValue = 0;
    private String cityTitle = null;
    private final String TAG = "CityInfo";
    private Context context;
    boolean deleteExists = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_info);
        LinearLayout layout = findViewById(R.id.cityLayout);
        context = this;

        mLocationDBHelper = new locationsDBHelper(this);
        mDistanceDBHelper = new distanceDBHelper(this);
        mCityDBHelper = new citiesDBHelper(this);
        ImageView backButton = findViewById(R.id.backButton);
        final EditText cityName = findViewById(R.id.cityName);
        final TextView cityEnvironment = findViewById(R.id.environmentPreset);
        final LinearLayoutCompat environmentLayout = findViewById(R.id.environmentLayout);
        final LinearLayoutCompat populationLayout = findViewById(R.id.populationLayout);
        final LinearLayoutCompat economyLayout = findViewById(R.id.economyLayout);
        final TextView cityEconomy = findViewById(R.id.economyPreset);
        final TextView cityPopulation = findViewById(R.id.populationPreset);
        final EditText cityNotes = findViewById(R.id.cityNotes);
        final LinearLayoutCompat buttonLayout = findViewById(R.id.cityButtonLayout);

        final Button gotoLocations = findViewById(R.id.gotoLocations);
        final Button gotoDistance = findViewById(R.id.gotoDistance);
        Button saveCity = findViewById(R.id.saveCity);

        //if starting the activity from clicking an item, addCityValue will be 0, otherwise, it's -1
        //we use this value to adjust the behavior of the editText values set during onCreate, and whether we need to check the name for an identical value when creating a new City
        setAddCityValue(getIntent().getIntExtra("addCityValue", -1));

        setCityTitle(getIntent().getStringExtra("cityName")); //set the City Title to what was passed in the intent. We use this to check to see if we need to UPDATE instead of INSERT when adding a City

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addCityValue == 0) {
                    Intent intent = new Intent(context, CityDisplay.class);
                    intent.putExtra("cityName", cityTitle);
                    intent.putExtra("addCityValue", 0);
                    startActivity(intent);
                    finish();
                }
                else onBackPressed();
            }
        });

        environmentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PresetList.class);
                intent.putExtra("presetVariable", "Environment");
                startActivityForResult(intent, 1);
            }
        });

        populationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PresetList.class);
                intent.putExtra("presetVariable", "Population");
                startActivityForResult(intent, 1);
            }
        });

        economyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PresetList.class);
                intent.putExtra("presetVariable", "Economy");
                startActivityForResult(intent, 1);
            }
        });

        gotoLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameOfCity = cityName.getText().toString();
                if(!(mCityDBHelper.checkNonExistence(nameOfCity))){
                    Intent intent = new Intent(context, LocationList.class);
                    intent.putExtra("cityName", nameOfCity);
                    startActivity(intent);
                } else{
                    toast("This city has not yet been saved. Please save all changes, and try again.");
                }
            }
        });

        gotoDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameOfCity = cityName.getText().toString();
                if(!(mCityDBHelper.checkNonExistence(nameOfCity))){
                    Intent intent = new Intent(context, DistanceList.class);
                    intent.putExtra("cityFrom", nameOfCity);
                    startActivity(intent);
                } else{
                    toast("This city has not yet been saved. Please save all changes, and try again.");
                }
            }
        });

        saveCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = cityName.getText().toString();
                String environment = cityEnvironment.getText().toString();
                String population = cityPopulation.getText().toString();
                String economy = cityEconomy.getText().toString();
                String notes = cityNotes.getText().toString();

                //if the edit text values are non-zero, and there is no identical entry, then add the City data.
                if (CheckData(cityName)){

                    if (addCityValue != 0){
                        setCityTitle(name); //if attempting to add a new City, we set this value to pass to the DBHelper in order to check if we need to update an existing entry
                    }

                    AddCity(name, environment, population, economy, notes, cityTitle);
                    Button deleteCityButton = new Button(context);
                    deleteCityButton.setText(R.string.deletecity);
                    deleteCityButton.setBackground(ContextCompat.getDrawable(context, R.drawable.buttonbg));
                    LinearLayoutCompat.LayoutParams lparams2 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                    lparams2.setMargins(5,0, 5, 0);
                    deleteCityButton.setLayoutParams(lparams2);
                    deleteCityButton.setPadding(30,10,30,10);
                    deleteCityButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String name = cityName.getText().toString();

                            if (!(mCityDBHelper.checkNonExistence(name))) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                builder.setMessage("Are you sure you want to delete this item?");
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        mCityDBHelper.removeCity(cityTitle);
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
                        buttonLayout.addView(deleteCityButton);
                        deleteExists = true;
                    }
                } else {
                    toast("Please provide a name for your city");
                }
            }
        });

        //If coming to this activity from clicking on a name, get and fill out all fields with the data for the corresponding City.
        if (addCityValue != -1) {
            deleteExists = true;
            Cursor cityInfo = mCityDBHelper.getSpecificCity(cityTitle);
            cityInfo.moveToFirst();
            cityName.setText(cityInfo.getString(1));
            cityEnvironment.setText(cityInfo.getString(2));
            cityPopulation.setText(cityInfo.getString(3));
            cityEconomy.setText(cityInfo.getString(4));
            cityNotes.setText((cityInfo.getString(5)));

            Button deleteCityButton = new Button(this);
            deleteCityButton.setText(R.string.deletecity);
            deleteCityButton.setBackground(ContextCompat.getDrawable(this, R.drawable.buttonbg));
            LinearLayoutCompat.LayoutParams lparams2 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lparams2.setMargins(5,0, 5, 0);
            deleteCityButton.setLayoutParams(lparams2);
            deleteCityButton.setPadding(30,10,30,10);
            deleteCityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = cityName.getText().toString();

                    if (!(mCityDBHelper.checkNonExistence(name))) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setMessage("Are you sure you want to delete this item?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                mCityDBHelper.removeCity(cityTitle);
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
            buttonLayout.addView(deleteCityButton);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        switch(resultCode) {
            case 1:
                String presetValue1 = data.getStringExtra("presetValue");
                TextView cityEnvironment = findViewById(R.id.environmentPreset);
                cityEnvironment.setText(presetValue1);
                break;
            case 2:
                String presetValue2 = data.getStringExtra("presetValue");
                TextView cityPopulation = findViewById(R.id.populationPreset);
                cityPopulation.setText(presetValue2);
                break;
            case 3:
                String presetValue3 = data.getStringExtra("presetValue");
                TextView cityEconomy = findViewById(R.id.economyPreset);
                cityEconomy.setText(presetValue3);
                break;
        }
    }

    //Make sure the text fields are not left blank (may be edited later to omit the notes section)
    private boolean CheckData(EditText name) {
        return ((name.length() != 0));
    }

    private void AddCity(final String name, final String environment, final String population, final String economy, final String notes, final String theCityTitle){
        setCityTitle(name);
        if(addCityValue == 0  || !(mCityDBHelper.checkNonExistence(name))) { //if we're updating an existing entry, then edit current DB values
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //builder.setTitle(R.string.app_name);
            builder.setMessage(R.string.updateinfo);
            //builder.setIcon(R.drawable.ic_launcher);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    boolean insertCity = mCityDBHelper.addCity(name, environment, population, economy, notes, theCityTitle, true);

                    if(!name.equals(theCityTitle)) {
                        mLocationDBHelper.updateCityName(name, theCityTitle);
                        mDistanceDBHelper.updateCitiesName(name, theCityTitle);
                    }
                    if (insertCity) {
                        toast("City saved");
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
        }else { //if the City with the entered name does not exist, proceed as normal
            boolean insertCity = mCityDBHelper.addCity(name, environment, population, economy, notes, cityTitle, false);
            if (insertCity) {
                toast("City saved");
            } else {
                toast("Error with entry");
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(addCityValue == 0) {
            Intent intent = new Intent(context, CityDisplay.class);
            Log.d(TAG, "onBackPressed: City title = " + cityTitle);
            intent.putExtra("cityName", cityTitle);
            intent.putExtra("addCityValue", 0);
            startActivity(intent);
            finish();
        }
        else super.onBackPressed();
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setAddCityValue(int value){
        addCityValue = value;
    }
    private void setCityTitle(String value){
        cityTitle = value;
    }
}