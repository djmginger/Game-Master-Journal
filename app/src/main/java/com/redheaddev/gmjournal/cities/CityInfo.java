package com.redheaddev.gmjournal.cities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.LocaleList;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
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

import com.redheaddev.gmjournal.cities.distances.DistanceList;
import com.redheaddev.gmjournal.cities.distances.distanceDBHelper;
import com.redheaddev.gmjournal.cities.locations.LocationList;
import com.redheaddev.gmjournal.cities.locations.locationsDBHelper;
import com.redheaddev.gmjournal.loot.LootInfo;
import com.redheaddev.gmjournal.presets.PresetList;
import com.redheaddev.gmjournal.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

public class CityInfo extends AppCompatActivity {

    private citiesDBHelper mCityDBHelper;
    private locationsDBHelper mLocationDBHelper;
    private distanceDBHelper mDistanceDBHelper;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 672;
    private String image = "";
    private int addCityValue = 0;
    private String cityTitle = null;
    private final String TAG = "CityInfo";
    private Context context;
    boolean deleteExists = false;
    private boolean darkMode;
    private String initName = "";
    private String initNote = "";
    private String initEnv = "Environment";
    private String initPop = "Population";
    private String initEcon = "Economy";
    private String initImage = "";
    private boolean hasSaved = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_info);
        context = this;

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        String theme = sharedPreferences.getString("Theme", "none");
        darkMode = theme.equals("dark");

        mLocationDBHelper = new locationsDBHelper(this);
        mDistanceDBHelper = new distanceDBHelper(this);
        mCityDBHelper = new citiesDBHelper(this);
        ImageView backButton = findViewById(R.id.backButton);
        final EditText cityName = findViewById(R.id.cityName);
        final TextView cityEnvironment = findViewById(R.id.environmentPreset);
        final LinearLayoutCompat environmentLayout = findViewById(R.id.environmentLayout);
        final LinearLayoutCompat populationLayout = findViewById(R.id.populationLayout);
        final LinearLayoutCompat economyLayout = findViewById(R.id.economyLayout);
        TextView pageHeader = findViewById(R.id.pageHeader);
        final TextView cityEconomy = findViewById(R.id.economyPreset);
        final TextView cityPopulation = findViewById(R.id.populationPreset);
        final TextView cityOverviewTitle = findViewById(R.id.cityOverviewTitle);
        final TextView cityNameTitle = findViewById(R.id.cityNameTitle);
        final TextView cityNotesTitle = findViewById(R.id.cityNotesTitle);
        final EditText cityNotes = findViewById(R.id.cityNotes);
        ImageView downArrow = findViewById(R.id.downArrow);
        ImageView downArrow2 = findViewById(R.id.downArrow2);
        ImageView downArrow3 = findViewById(R.id.downArrow3);
        final LinearLayoutCompat buttonLayout = findViewById(R.id.cityButtonLayout);
        final LinearLayout cityLayout = findViewById(R.id.cityLayout);
        final ScrollView cityMainLayout = findViewById(R.id.cityMainLayout);
        final Button gotoLocations = findViewById(R.id.gotoLocations);
        final Button gotoDistance = findViewById(R.id.gotoDistance);
        Button saveCity = findViewById(R.id.saveCity);
        final ImageView cityImage = findViewById(R.id.cityImage);
        final ImageView addImage = findViewById((R.id.addImage));
        final TextView addImageText = findViewById(R.id.addImageText);

        final DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int deviceHeight = (displayMetrics.heightPixels);

        addImageText.setText(R.string.addimage);
        cityImage.setVisibility(View.GONE);
        cityImage.setMaxHeight((int)(deviceHeight*.40));
        addImage.setImageResource(R.drawable.addanimage);

        String headerText2 = sharedPreferences.getString("headerText2", "none");
        String headerColor2 = sharedPreferences.getString("headerColor2", "none");
        String infoboxcolor2 = sharedPreferences.getString("infoboxcolor2", "none");
        if(!headerText2.equals("none")) {
            pageHeader.setText(headerText2 + " details");
            cityNameTitle.setText(headerText2 + " Name");
        }
        if(!headerColor2.equals("none")) pageHeader.setTextColor(Color.parseColor(headerColor2));
        if(!infoboxcolor2.equals("none")){
            setDrawableColors(infoboxcolor2);
        }

        if (theme.equals("dark")){

            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(downArrow.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            downArrow2.setImageResource(R.drawable.downarrow);
            downArrow3.setImageResource(R.drawable.downarrow);

            cityName.setBackgroundResource(R.drawable.info_bg7);
            cityName.setTextColor(Color.parseColor("#dadada"));
            environmentLayout.setBackgroundResource(R.drawable.info_bg7);
            cityEnvironment.setTextColor(Color.parseColor("#dadada"));
            populationLayout.setBackgroundResource(R.drawable.info_bg7);
            cityPopulation.setTextColor(Color.parseColor("#dadada"));
            economyLayout.setBackgroundResource(R.drawable.info_bg7);
            cityEconomy.setTextColor(Color.parseColor("#dadada"));
            cityNotes.setBackgroundResource(R.drawable.info_bg7);
            cityNotes.setTextColor(Color.parseColor("#dadada"));
            addImageText.setTextColor(Color.WHITE);
            addImage.setBackgroundResource(R.drawable.info_bg8);
            cityOverviewTitle.setTextColor(Color.WHITE);
            cityNameTitle.setTextColor(Color.WHITE);
            cityNotesTitle.setTextColor(Color.WHITE);
            cityMainLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            backButton.setBackgroundColor(Color.parseColor("#2C2C2C"));
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(downArrow.getDrawable()), ContextCompat.getColor(context, R.color.black));
            downArrow2.setImageResource(R.drawable.downarrow);
            downArrow3.setImageResource(R.drawable.downarrow);
        }

        String localeText = sharedPreferences.getString("Locale", "none");
        String loadLocale = String.valueOf(getResources().getConfiguration().locale);
        if(!localeText.equals(loadLocale)){
            Locale locale = new Locale(localeText);
            updateLocale(locale);
        }

        //if starting the activity from clicking an item, addCityValue will be 0, otherwise, it's -1
        //we use this value to adjust the behavior of the editText values set during onCreate, and whether we need to check the name for an identical value when creating a new City
        setAddCityValue(getIntent().getIntExtra("addCityValue", -1));

        setCityTitle(getIntent().getStringExtra("cityName")); //set the City Title to what was passed in the intent. We use this to check to see if we need to UPDATE instead of INSERT when adding a City

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);
                }
                else {
                    requestStoragePermission();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        environmentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PresetList.class);
                intent.putExtra("presetVariable", "Environment");
                startActivityForResult(intent, 2);
            }
        });

        populationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PresetList.class);
                intent.putExtra("presetVariable", "Population");
                startActivityForResult(intent, 3);
            }
        });

        economyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PresetList.class);
                intent.putExtra("presetVariable", "Economy");
                startActivityForResult(intent, 4);
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
                    toast(getString(R.string.citynotsaved));
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
                    toast(getString(R.string.citynotsaved));
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

                    AddCity(name, environment, population, economy, notes, image, cityTitle);
                    //Once you've saved the city, return to the city selection screen
                } else {
                    toast(getString(R.string.nameforcity));
                }
            }
        });

        //If coming to this activity from clicking on a name, get and fill out all fields with the data for the corresponding City.
        if (addCityValue != -1) {
            deleteExists = true;
            Cursor cityInfo = mCityDBHelper.getSpecificCity(cityTitle);
            cityInfo.moveToFirst();
            initName = cityInfo.getString(1);
            cityName.setText(initName);
            initEnv = cityInfo.getString(2);
            cityEnvironment.setText(initEnv);
            initPop = cityInfo.getString(3);
            cityPopulation.setText(initPop);
            initEcon = cityInfo.getString(4);
            cityEconomy.setText(initEcon);
            initNote = cityInfo.getString(5);
            cityNotes.setText(initNote);

            ImageView deleteCityButtonImage = new ImageView(this);
            deleteCityButtonImage.setImageResource(R.drawable.delete);
            int deviceWidth = (displayMetrics.widthPixels);
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams((int)(deviceWidth * .11), ViewGroup.LayoutParams.WRAP_CONTENT);
            lparams.setMargins(0,0,0,25);
            lparams.gravity = Gravity.CENTER_HORIZONTAL;
            deleteCityButtonImage.setLayoutParams(lparams);
            deleteCityButtonImage.setAdjustViewBounds(true);
            deleteCityButtonImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = cityName.getText().toString();

                    if (!(mCityDBHelper.checkNonExistence(name))) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setMessage(R.string.areyou);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                mCityDBHelper.removeCity(cityTitle);
                                Intent intent = new Intent(context, CityList.class);
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

                    }else toast(getString(R.string.noitemtodelete));
                }
            });
            cityLayout.addView(deleteCityButtonImage);

            initImage = cityInfo.getString(6);
            image = initImage;
            if (!image.equals("")) {
                Bitmap bmImg = BitmapFactory.decodeFile(image);
                cityImage.setImageBitmap(bmImg);
                cityImage.setVisibility(View.VISIBLE);
                addImageText.setText(R.string.removeimage);
                addImage.setImageResource(R.drawable.removeimage);
                if (!darkMode) DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.black));
                else DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));

                addImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        image = "";
                        cityImage.setImageURI(null);
                        cityImage.setVisibility(View.GONE);
                        addImageText.setText(R.string.addimage);
                        addImage.setImageResource(R.drawable.addanimage);
                        if (!darkMode) DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.black));
                        else DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
                        addImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    cityImage.setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                    intent.setType("image/*");
                                    startActivityForResult(intent, 1);
                                } else {
                                    requestStoragePermission();
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        final ImageView cityImage = findViewById(R.id.cityImage);
        final TextView addImageText = findViewById(R.id.addImageText);
        final ImageView addImage = findViewById(R.id.addImage);
        Log.d(TAG, "onActivityResult: The request code is " + requestCode);
        Log.d(TAG, "onActivityResult: The result code is " + resultCode);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                //data.getData returns the content URI for the selected Image
                Uri selectedImage = data.getData();
                cityImage.setVisibility(View.VISIBLE);
                String savedImagePath = null;
                try {
                    savedImagePath = createFileFromInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "onActivityResult: SavedImagePath is " + savedImagePath);
                image = savedImagePath;
                Bitmap bmImg = BitmapFactory.decodeFile(savedImagePath);
                cityImage.setImageBitmap(bmImg);
                addImageText.setText(R.string.removeimage);
                addImage.setImageResource(R.drawable.removeimage);
                if (!darkMode)
                    DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.black));
                else
                    DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
                //change the behavior of the addImage button to become a remove button. Then if clicked, change it back.
                addImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cityImage.setImageURI(null);
                        image = "";
                        cityImage.setVisibility(View.GONE);
                        addImageText.setText(R.string.addimage);
                        addImage.setImageResource(R.drawable.addanimage);
                        if (!darkMode)
                            DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.black));
                        else
                            DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
                        addImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    cityImage.setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                    intent.setType("image/*");
                                    startActivityForResult(intent, 1);
                                } else {
                                    requestStoragePermission();
                                }
                            }
                        });
                    }
                });
            }
        } else switch (resultCode) {
            case 1:
                Log.d(TAG, "onActivityResult: We got to the result");
                Log.d(TAG, "onActivityResult: The result is " + data.getStringExtra("presetValue"));
                TextView cityEnvironment = findViewById(R.id.environmentPreset);
                if (!data.getStringExtra("presetValue").equals("none")) {
                    String presetValue1 = data.getStringExtra("presetValue");
                    cityEnvironment.setText(presetValue1);
                } else cityEnvironment.setText(getString(R.string.environment));
                break;

            case 2:
                TextView cityPopulation = findViewById(R.id.populationPreset);
                if (!data.getStringExtra("presetValue").equals("none")) {
                    String presetValue2 = data.getStringExtra("presetValue");
                    cityPopulation.setText(presetValue2);
                } else cityPopulation.setText(getString(R.string.pop));
                break;

            case 3:
                TextView cityEconomy = findViewById(R.id.economyPreset);
                if (!data.getStringExtra("presetValue").equals("none")) {
                    String presetValue3 = data.getStringExtra("presetValue");
                    cityEconomy.setText(presetValue3);
                } else cityEconomy.setText(getString(R.string.eco));
                break;

            default:
                break;
        }
    }

    public void setDrawableColors(String color){
        final EditText cityName = findViewById(R.id.cityName);
        final LinearLayoutCompat environmentLayout = findViewById(R.id.environmentLayout);
        final LinearLayoutCompat populationLayout = findViewById(R.id.populationLayout);
        final LinearLayoutCompat economyLayout = findViewById(R.id.economyLayout);
        final EditText cityNotes = findViewById(R.id.cityNotes);
        final ImageView addImage = findViewById(R.id.addImage);

        GradientDrawable drawable1 = (GradientDrawable)cityName.getBackground().getCurrent();
        drawable1.setColor(Color.parseColor(color));
        drawable1.setStroke(4, Color.BLACK);
        GradientDrawable drawable2 = (GradientDrawable)environmentLayout.getBackground().getCurrent();
        drawable2.setColor(Color.parseColor(color));
        drawable2.setStroke(4, Color.BLACK);
        GradientDrawable drawable3 = (GradientDrawable)populationLayout.getBackground().getCurrent();
        drawable3.setColor(Color.parseColor(color));
        drawable3.setStroke(4, Color.BLACK);
        GradientDrawable drawable4 = (GradientDrawable)economyLayout.getBackground().getCurrent();
        drawable4.setColor(Color.parseColor(color));
        drawable4.setStroke(4, Color.BLACK);
        GradientDrawable drawable5 = (GradientDrawable)addImage.getBackground().getCurrent();
        drawable5.setColor(Color.parseColor(color));
        drawable5.setStroke(4, Color.BLACK, 5, 5);
        GradientDrawable drawable6 = (GradientDrawable)cityNotes.getBackground().getCurrent();
        drawable6.setColor(Color.parseColor(color));
        drawable6.setStroke(4, Color.BLACK);
    }

    private boolean CheckData(EditText name) {
        return ((name.length() != 0));
    }

    private void AddCity(final String name, final String environment, final String population, final String economy, final String notes, final String image, final String theCityTitle){
        setCityTitle(name);
        if(addCityValue == 0  || !(mCityDBHelper.checkNonExistence(name))) { //if we're updating an existing entry, then edit current DB values
            boolean insertCity = mCityDBHelper.addCity(name, environment, population, economy, notes, image, theCityTitle, true);

            if(!name.equals(theCityTitle)) {
                mLocationDBHelper.updateCityName(name, theCityTitle);
                mDistanceDBHelper.updateCitiesName(name, theCityTitle);
            }
            if (insertCity) {
                hasSaved = true;
                toast(getString(R.string.citysaved));
            } else {
                toast(getString(R.string.error));
            }
            onBackPressed();
        } else { //if the City with the entered name does not exist, proceed as normal
            boolean insertCity = mCityDBHelper.addCity(name, environment, population, economy, notes, image, cityTitle, false);
            if (insertCity) {
                hasSaved = true;
                toast(getString(R.string.citysaved));
                onBackPressed();
            } else {
                toast(getString(R.string.error));
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(addCityValue == 0) {
            Intent intent = new Intent(context, CityDisplay.class);
            intent.putExtra("cityName", cityTitle);
            intent.putExtra("addCityValue", 0);
            if (!hasSaved) notSaved(intent);
            else {
                startActivity(intent);
                finish();
            }
        }
        else{
            Intent intent = new Intent(this, CityList.class);
            if (!hasSaved) notSaved(intent);
            else startActivity(intent);
        }
    }

    private String createFileFromInputStream(Uri imageUri) throws FileNotFoundException {

        InputStream in = getContentResolver().openInputStream(imageUri);
        String imageFileName = getFileName(imageUri);
        File exportDir = new File(context.getFilesDir(), "/GMJournalData/");
        if (!exportDir.exists()) {
            if (exportDir.mkdirs()) Log.d(TAG, "doInBackground: File directory was created");
            else Log.d(TAG, "doInBackground: File directory wasn't created");
        }
        String destinationFilename = context.getFilesDir().getPath() + "/GMJournalData/" + imageFileName;

        try {
            File f = new File(destinationFilename);
            f.setWritable(true, false);
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length = 0;

            while((length=in.read(buffer)) > 0) {
                outputStream.write(buffer,0,length);
            }

            outputStream.close();
            in.close();

            return destinationFilename;
        } catch (IOException e) {
            System.out.println("error in creating a file");
            e.printStackTrace();
        }
        return null;
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void requestStoragePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(context) // Show an explanation to the user why we need the permission
                    .setTitle(getString(R.string.permissionneeded))
                    .setMessage(getString(R.string.permissionreasonloot))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(CityInfo.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        }
    }

    public void saveFunctionality(String name, String environment, String population, String economy, String notes, String image){

        //if the edit text values are non-zero, and there is no identical entry, then add the city data
        EditText cityName = findViewById(R.id.cityName);
        if (CheckData(cityName)){

            if (addCityValue != 0){
                setCityTitle(name); //if attempting to add a new city, we set this value to pass to the DBHelper in order to check if we need to update an existing entry
            }

            AddCity(name, environment, population, economy, notes, image, cityTitle);
        } else {
            toast("Please provide a name for your city");
        }
    }

    public void notSaved(Intent intent) {

        EditText cityName = findViewById(R.id.cityName);
        TextView cityEnvironment = findViewById(R.id.environmentPreset);
        TextView cityEconomy = findViewById(R.id.economyPreset);
        TextView cityPopulation = findViewById(R.id.populationPreset);
        EditText cityNotes = findViewById(R.id.cityNotes);

        if ((!(cityName.getText().toString().equals(initName)) || !(cityNotes.getText().toString().equals(initNote)) || !(cityEnvironment.getText().toString().equals(initEnv)) || !(cityEconomy.getText().toString().equals(initEcon)) || !(cityPopulation.getText().toString().equals(initPop)) || !(image.equals(initImage))) & !hasSaved) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.unsaved);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    String name = cityName.getText().toString();
                    String environment = cityEnvironment.getText().toString();
                    String population = cityPopulation.getText().toString();
                    String economy = cityEconomy.getText().toString();
                    String notes = cityNotes.getText().toString();
                    dialog.dismiss();
                    saveFunctionality(name, environment, population, economy, notes, image);
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
        } else startActivity(intent);
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

    @SuppressLint("NewApi")
    public void updateLocale(Locale locale) {
        Resources res = getResources();
        Locale.setDefault(locale);

        Configuration configuration = res.getConfiguration();

        if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 24) {
            LocaleList localeList = new LocaleList(locale);

            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);
            configuration.setLocale(locale);

        } else if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 17){
            configuration.setLocale(locale);

        } else {
            configuration.locale = locale;
        }

        res.updateConfiguration(configuration, res.getDisplayMetrics());
        recreate();
    }
}