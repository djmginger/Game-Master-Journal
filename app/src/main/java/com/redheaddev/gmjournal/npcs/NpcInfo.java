package com.redheaddev.gmjournal.npcs;

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
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.provider.OpenableColumns;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.redheaddev.gmjournal.cities.citiesDBHelper;
import com.redheaddev.gmjournal.R;
import com.redheaddev.gmjournal.presets.PresetList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class NpcInfo extends AppCompatActivity{

    private KeyListener variable;
    private npcDBHelper mNpcDBHelper;
    private int addNPCValue = 0;
    private String npcTitle = null;
    private String voice = "";
    private final String TAG = "NPCInfo";
    private boolean isStopped;
    private final ArrayList<String> locations = new ArrayList<>();
    private String npcSpinnerLocation;
    private MediaPlayer mPlayer;
    private Context context;
    private Uri audioFileUri = Uri.EMPTY;
    boolean deleteExists = false;
    private String image = "";
    private ArrayAdapter<String> locationAdapter;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 672;
    private Boolean darkMode;
    private Boolean hasSaved = false;
    private String initName = "";
    private String initDesc = "";
    private String initPlotHooks = "";
    private String initNotes = "";
    private String initRace = "";
    private String initImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.npc_info);
        context = this;

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        String theme = sharedPreferences.getString("Theme", "none");
        darkMode = theme.equals("dark");

        mNpcDBHelper = new npcDBHelper(this);
        final citiesDBHelper mCityDBHelper = new citiesDBHelper(this);
        ImageView backButton = findViewById(R.id.backButton);
        final EditText npcName = findViewById(R.id.npcName);
        final LinearLayoutCompat raceLayout = findViewById(R.id.raceLayout);
        final TextView npcRace = findViewById(R.id.racePreset);
        final EditText npcDescription = findViewById(R.id.npcDescription);
        final EditText npcPlotHooks = findViewById(R.id.npcPlotHooks);
        final EditText npcNotes = findViewById(R.id.npcNotes);
        final ScrollView npcMainLayout = findViewById(R.id.npcMainLayout);
        final LinearLayout npcLayout = findViewById(R.id.npcLayout);
        final TextView addImageText = findViewById(R.id.addImageText);
        TextView pageHeader = findViewById(R.id.pageHeader);
        final TextView npcDescTitle = findViewById(R.id.npcDescTitle);
        final TextView npcNameTitle = findViewById(R.id.npcNameTitle);
        final TextView npcPlotTitle = findViewById(R.id.npcPlotTitle);
        final TextView npcNoteTitle = findViewById(R.id.npcNoteTitle);
        final TextView raceTitle = findViewById(R.id.raceTitle);
        final TextView locationTitle = findViewById(R.id.locationTitle);
        final ImageView npcImage = findViewById(R.id.npcImage);
        final ImageView addImage = findViewById(R.id.addImage);
        ImageView downArrow = findViewById(R.id.downArrow);
        final Spinner locationSpinner = findViewById(R.id.locationSpinner);
        final Button addVoice = findViewById(R.id.addVoice);
        Button saveNpc = findViewById(R.id.saveNpc);
        final ImageView stop = findViewById(R.id.stop);
        final ImageView play = findViewById(R.id.play);
        final ImageView pause = findViewById(R.id.pause);
        final ImageView deleteVoice = findViewById(R.id.deleteVoice);

        addImageText.setText(R.string.addimage);
        npcImage.setVisibility(View.GONE);
        addImage.setImageResource(R.drawable.addanimage);
        npcMainLayout.setBackgroundColor(Color.WHITE);

        String headerText1 = sharedPreferences.getString("headerText1", "none");
        String headerColor1 = sharedPreferences.getString("headerColor1", "none");
        String infoboxcolor1 = sharedPreferences.getString("infoboxcolor1", "none");
        if(!headerText1.equals("none")) {
            pageHeader.setText(headerText1 + " details");
            npcNameTitle.setText(headerText1 + " Name");
        }
        if(!headerColor1.equals("none")) pageHeader.setTextColor(Color.parseColor(headerColor1));
        if(!infoboxcolor1.equals("none")){
            setDrawableColors(infoboxcolor1);
        }

        if (theme.equals("dark")){

            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(deleteVoice.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(stop.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(play.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(pause.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(downArrow.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));

            npcLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            addImageText.setTextColor(Color.WHITE);
            npcDescTitle.setTextColor(Color.WHITE);
            npcNameTitle.setTextColor(Color.WHITE);
            npcPlotTitle.setTextColor(Color.WHITE);
            npcNoteTitle.setTextColor(Color.WHITE);
            raceTitle.setTextColor(Color.WHITE);
            locationTitle.setTextColor(Color.WHITE);
            addImage.setBackgroundResource(R.drawable.info_bg8);
            npcName.setBackgroundResource(R.drawable.info_bg7);
            npcName.setTextColor(Color.parseColor("#dadada"));
            raceLayout.setBackgroundResource(R.drawable.info_bg7);
            npcRace.setTextColor(Color.parseColor("#dadada"));
            npcDescription.setBackgroundResource(R.drawable.info_bg7);
            npcDescription.setTextColor(Color.parseColor("#dadada"));
            npcPlotHooks.setBackgroundResource(R.drawable.info_bg7);
            npcPlotHooks.setTextColor(Color.parseColor("#dadada"));
            npcNotes.setBackgroundResource(R.drawable.info_bg7);
            npcNotes.setTextColor(Color.parseColor("#dadada"));
            npcMainLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            backButton.setBackgroundColor(Color.parseColor("#2C2C2C"));
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(deleteVoice.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(stop.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(play.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(pause.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.black));
        }

        String localeText = sharedPreferences.getString("Locale", "none");
        String loadLocale = String.valueOf(getResources().getConfiguration().locale);
        if(!localeText.equals(loadLocale)){
            Locale locale = new Locale(localeText);
            updateLocale(locale);
        }

        mPlayer = new MediaPlayer();

        //if starting the activity from clicking an item, addNPCValue will be 0, otherwise, it's -1
        //we use this value to adjust the behavior of the editText values set during onCreate, and whether we need to check the name for an identical value when creating a new npc
        setAddNPCValue(getIntent().getIntExtra("addNPCValue", -1));
        setNPCTitle(getIntent().getStringExtra("npcName")); //set the npc Title to what was passed in the intent. We use this to check to see if we need to UPDATE instead of INSERT when adding an npc

        raceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PresetList.class);
                intent.putExtra("presetVariable", "Race");
                startActivityForResult(intent, 1);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 3);
                }
                else {
                    requestStoragePermission();
                }
            }
        });

        addVoice.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: isStopped is " + isStopped);
                //Log.d(TAG, "onClick: Empty Uri? : " + Uri.EMPTY.equals(audioFileUri));
                if (!isStopped && !voice.equals("")) {
                    mPlayer.stop();
                    try{
                        mPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    isStopped = true;
                }
                Intent intent;
                intent = new Intent();
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("audio/*");
                startActivityForResult(intent, 2);
            }
        });

        saveNpc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = npcName.getText().toString();
                String race = npcRace.getText().toString();
                String description = npcDescription.getText().toString();
                String plotHooks = npcPlotHooks.getText().toString();
                String notes = npcNotes.getText().toString();

                //if the edit text values are non-zero, and there is no identical entry, then add the NPC data and remove editability again.
                if (CheckName(npcName)){

                    if (addNPCValue != 0){
                        setNPCTitle(name); //if attempting to add a new npc, we set this value to pass to the DBHelper in order to check if we need to update an existing entry
                    }
                    AddNPC(name, npcSpinnerLocation, description, notes, npcTitle, voice, plotHooks, race, image);
                    if(addNPCValue == -1) {
                        ImageView deleteNPCButtonImage = new ImageView(context);
                        deleteNPCButtonImage.setImageResource(R.drawable.delete);
                        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                        int deviceWidth = (displayMetrics.widthPixels);
                        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams((int)(deviceWidth * .11), ViewGroup.LayoutParams.WRAP_CONTENT);
                        lparams.setMargins(0,0,0,25);
                        lparams.gravity = Gravity.CENTER_HORIZONTAL;
                        deleteNPCButtonImage.setLayoutParams(lparams);
                        if (darkMode) DrawableCompat.setTint(DrawableCompat.wrap(deleteNPCButtonImage.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
                            else DrawableCompat.setTint(DrawableCompat.wrap(deleteNPCButtonImage.getDrawable()), ContextCompat.getColor(context, R.color.delete));
                        deleteNPCButtonImage.setAdjustViewBounds(true);
                        deleteNPCButtonImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String name = npcName.getText().toString();

                                if (!(mNpcDBHelper.checkNonExistence(name))) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                    builder.setMessage(R.string.areyou);
                                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                            mNpcDBHelper.removeNPC(npcTitle);
                                            Intent intent = new Intent(context, NpcList.class);
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
                        if (!deleteExists) {
                            npcLayout.addView(deleteNPCButtonImage);
                            deleteExists = true;
                        }
                    }
                } else {
                    toast(getString(R.string.namefornpc));
                }
            }
        });

        //Set the mediaplayer controls to be invisible until there is an audio file available to play.
        stop.setVisibility(View.INVISIBLE);
        play.setVisibility(View.INVISIBLE);
        pause.setVisibility(View.INVISIBLE);
        deleteVoice.setVisibility(View.INVISIBLE);

        //If coming to this activity from clicking on a name, get and fill out all fields with the data for the corresponding npc.
        if (addNPCValue != -1) {
            deleteExists = true;
            setNPCTitle(getIntent().getStringExtra("npcName"));
            Log.d(TAG, "onCreate: The npc value passed is " + npcTitle);
            Cursor npcInfo = mNpcDBHelper.getSpecificNPC(npcTitle);
            npcInfo.moveToFirst();
            initName = npcInfo.getString(1);
            npcName.setText(initName);
            initRace = npcInfo.getString(7);
            npcRace.setText(initRace);
            initDesc = npcInfo.getString(3);
            npcDescription.setText(initDesc);
            initNotes = npcInfo.getString(4);
            npcNotes.setText(initNotes);
            initPlotHooks = npcInfo.getString(6);
            npcPlotHooks.setText(initPlotHooks);
            initImage = npcInfo.getString(8);
            image = initImage;
            if (!image.equals("")) {
                Uri imageUri = Uri.parse(image);
                npcImage.setImageURI(imageUri);
                npcImage.setVisibility(View.VISIBLE);
                addImageText.setText(R.string.removeimage);
                addImage.setImageResource(R.drawable.removeimage);
                if (!darkMode) DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.black));
                    else DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));

                //change the behavior of the addImage button to become a remove button. Then if clicked, change it back.
                addImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        image = "";
                        npcImage.setImageURI(null);
                        npcImage.setVisibility(View.GONE);
                        addImageText.setText(R.string.addimage);
                        addImage.setImageResource(R.drawable.addanimage);
                        if (!darkMode) DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.black));
                            else DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
                        addImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    npcImage.setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                    intent.setType("image/*");
                                    startActivityForResult(intent, 3);
                                } else {
                                    requestStoragePermission();
                                }
                            }
                        });
                    }
                });
            }

            try (Cursor cities = mCityDBHelper.getCities()) {
                if (cities.moveToNext()) {
                    do {
                        locations.add(cities.getString(1));
                    } while (cities.moveToNext());
                }
            } finally {
                locations.remove(npcInfo.getString(2));
                locations.add(0, npcInfo.getString(2));
                if(!(locations.contains(getString(R.string.nolocation)))) {
                    locations.add(getString(R.string.nolocation));
                }
                if(!(locations.contains(getString(R.string.newlocation)))) {
                    locations.add(getString(R.string.newlocation));
                }
            }

            locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locations);
            locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            locationSpinner.setAdapter(locationAdapter);

            voice = npcInfo.getString(5);
            Log.d(TAG, "onCreate: the voice value is:" + voice);

            if (!(voice.equals(""))) {
                stop.setVisibility(View.VISIBLE);
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.VISIBLE);
                deleteVoice.setVisibility(View.VISIBLE);
                Uri audioFileUri = Uri.parse(voice);
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                // Now you can use that Uri to get the file path, or upload it, ...
                Log.d("media", "onActivityResult: " + audioFileUri);

                try {
                    mPlayer.setDataSource(this, audioFileUri);
                    mPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                addVoice.setText(R.string.changevoice);

                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mPlayer.stop();
                        try{
                            mPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        isStopped = true;
                    }
                });

                stop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPlayer.stop();
                        try{
                            mPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        isStopped = true;
                    }
                });

                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPlayer.start();
                        isStopped = false;
                    }
                });

                pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!isStopped) {
                            mPlayer.pause();
                        }
                    }
                });

                deleteVoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPlayer.stop();
                        try{
                            mPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        isStopped = true;

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(R.string.areyou);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                mPlayer.release();
                                addVoice.setText(R.string.addvoice);
                                stop.setVisibility(View.INVISIBLE);
                                play.setVisibility(View.INVISIBLE);
                                pause.setVisibility(View.INVISIBLE);
                                deleteVoice.setVisibility(View.INVISIBLE);
                                voice = "";
                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });
            }
        } else {
            if(!(locations.contains(getString(R.string.nolocation)))) {
                locations.add(getString(R.string.nolocation));
            }
            if(!(locations.contains(getString(R.string.newlocation)))) {
                locations.add(getString(R.string.newlocation));
            }

            Cursor cities = mCityDBHelper.getCities();
            try {
                if (cities.moveToNext()) {
                    do {
                        locations.add(cities.getString(1));
                    } while (cities.moveToNext());
                }
            } finally {
                cities.close();
            }

            locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locations);
            locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            locationSpinner.setAdapter(locationAdapter);
        }

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                npcSpinnerLocation = locations.get(position);
                if (npcSpinnerLocation.equals(getString(R.string.newlocation))){

                    //Create a dialog box that asks the user to enter in a name for the new city. If they provide a new name, add it to the cityDB and update the spinner
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    final EditText edittext = new EditText(context);
                    alert.setMessage(R.string.namenewcity);
                    alert.setTitle(R.string.createcity);
                    alert.setView(edittext);

                    alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String newCityName = edittext.getText().toString();
                            if(!mCityDBHelper.checkNonExistence(newCityName)) {
                                toast(getString(R.string.cityalreadyexists));
                            } else{
                                dialog.dismiss();

                                mCityDBHelper.addCity(newCityName, "Environment", "Population", "Economy", "", "", "", false);

                                try (Cursor cities = mCityDBHelper.getCities()) {
                                    locations.clear();
                                    if (cities.moveToNext()) {
                                        do {
                                            locations.add(cities.getString(1));
                                        } while (cities.moveToNext());
                                    }
                                } finally {
                                    if(!(locations.contains(getString(R.string.nolocation)))) {
                                        locations.add(getString(R.string.nolocation));
                                    }
                                    if(!(locations.contains(getString(R.string.nolocation)))) {
                                        locations.add(getString(R.string.nolocation));
                                    }
                                    locationAdapter.notifyDataSetChanged();
                                    int spinnerPosition = locationAdapter.getPosition(newCityName);
                                    locationSpinner.setSelection(spinnerPosition);

                                }

                            }
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    });

                    alert.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                Log.i("GTOUTOUT", "Nothing Selected");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "onActivityResult: ResultCode is: " + resultCode);
        //Log.d(TAG, "onActivityResult: preset value is: " + data.getStringExtra("presetValue"));

        if (resultCode == 4){
            TextView npcRace = findViewById(R.id.racePreset);
            if (!data.getStringExtra("presetValue").equals("none")) {
                String presetValue4 = data.getStringExtra("presetValue");
                npcRace.setText(presetValue4);
            }
            else npcRace.setText("");
        }

        else if(requestCode == 2 && resultCode == -1){
            if ((data != null) && (data.getData() != null)){

                mPlayer = new MediaPlayer();
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                audioFileUri = data.getData();

                this.getContentResolver().takePersistableUriPermission(audioFileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                voice = audioFileUri.toString();
                Log.d(TAG, "onActivityResult: The initial value of voice is: " + voice);
                // Now you can use that Uri to get the file path, or upload it, ...

                try{
                    mPlayer.setDataSource(this, audioFileUri);
                    mPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Button addVoice = findViewById(R.id.addVoice);
                ImageView stop = findViewById(R.id.stop);
                ImageView play = findViewById(R.id.play);
                ImageView pause = findViewById(R.id.pause);
                ImageView deleteVoice = findViewById(R.id.deleteVoice);
                addVoice.setText(R.string.changevoice);

                stop.setVisibility(View.VISIBLE);
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.VISIBLE);
                deleteVoice.setVisibility(View.VISIBLE);

                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mPlayer.stop();
                        try{
                            mPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (IllegalStateException x) {
                            toast(getString(R.string.noaudiofile));
                        }
                        isStopped = true;
                    }
                });

                stop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPlayer.stop();
                        try{
                            mPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        isStopped = true;
                    }
                });

                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPlayer.start();
                        isStopped = false;
                    }
                });

                pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!isStopped) {
                            mPlayer.pause();
                        }
                    }
                });
            } else if(audioFileUri != null){
                try{
                    mPlayer.setDataSource(this, audioFileUri);
                    mPlayer.prepare();
                } catch(IOException ie) {
                    ie.printStackTrace();
                }
            }
        } else if (resultCode == Activity.RESULT_OK)
            if (requestCode == 3) {
                final ImageView npcImage = findViewById(R.id.npcImage);
                final TextView addImageText = findViewById(R.id.addImageText);
                final ImageView addImage = findViewById(R.id.addImage);
                //data.getData returns the content URI for the selected Image
                Uri selectedImage = data.getData();
                npcImage.setVisibility(View.VISIBLE);
                String savedImagePath = null;
                try {
                    savedImagePath = createFileFromInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "onActivityResult: SavedImagePath is " + savedImagePath);
                image = savedImagePath;
                Bitmap bmImg = BitmapFactory.decodeFile(savedImagePath);
                npcImage.setImageBitmap(bmImg);
                addImageText.setText(R.string.removeimage);
                addImage.setImageResource(R.drawable.removeimage);
                if (!darkMode) DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.black));
                    else DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));

                //change the behavior of the addImage button to become a remove button. Then if clicked, change it back.
                addImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        npcImage.setImageURI(null);
                        image = "";
                        npcImage.setVisibility(View.GONE);
                        addImageText.setText(R.string.addimage);
                        addImage.setImageResource(R.drawable.addanimage);
                        if (!darkMode) DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.black));
                            else DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
                        addImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    npcImage.setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                    intent.setType("image/*");
                                    startActivityForResult(intent, 3);
                                }
                                else {
                                    requestStoragePermission();
                                }
                            }
                        });
                    }
                });
            }


        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setDrawableColors(String color){
        final EditText npcName = findViewById(R.id.npcName);
        final LinearLayoutCompat raceLayout = findViewById(R.id.raceLayout);
        final EditText npcDescription = findViewById(R.id.npcDescription);
        final EditText npcPlotHooks = findViewById(R.id.npcPlotHooks);
        final EditText npcNotes = findViewById(R.id.npcNotes);
        final ImageView addImage = findViewById(R.id.addImage);

        GradientDrawable drawable1 = (GradientDrawable)npcName.getBackground().getCurrent();
        drawable1.setColor(Color.parseColor(color));
        drawable1.setStroke(4, Color.BLACK);
        GradientDrawable drawable2 = (GradientDrawable)raceLayout.getBackground().getCurrent();
        drawable2.setColor(Color.parseColor(color));
        drawable2.setStroke(4, Color.BLACK);
        GradientDrawable drawable3 = (GradientDrawable)addImage.getBackground().getCurrent();
        drawable3.setColor(Color.parseColor(color));
        drawable3.setStroke(4, Color.BLACK, 5, 5);
        GradientDrawable drawable4 = (GradientDrawable)npcDescription.getBackground().getCurrent();
        drawable4.setColor(Color.parseColor(color));
        drawable4.setStroke(4, Color.BLACK);
        GradientDrawable drawable5 = (GradientDrawable)npcPlotHooks.getBackground().getCurrent();
        drawable5.setColor(Color.parseColor(color));
        drawable5.setStroke(4, Color.BLACK);
        GradientDrawable drawable6 = (GradientDrawable)npcNotes.getBackground().getCurrent();
        drawable6.setColor(Color.parseColor(color));
        drawable6.setStroke(4, Color.BLACK);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: voice = " + voice);
        if(!voice.equals("") && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer.release();
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

    //Make sure the text fields are not left blank (may be edited later to omit the notes section)
    private boolean CheckName(EditText name) {
        return ((name.length() != 0));
    }

    private void AddNPC(final String name, final String location, final String description, final String notes, final String npcTitle, final String voice, final String plotHooks, final String race, final String image){
        setNPCTitle(name);
        if(addNPCValue == 0 || !(mNpcDBHelper.checkNonExistence(name))) { //if an npc with the entered name already exists, ask the user if they wish to update the entry
            boolean insertNPC = mNpcDBHelper.addNPC(name, location, description, notes, npcTitle, voice, plotHooks, race, image, true);
            if (insertNPC) {
                hasSaved = true;
                toast(getString(R.string.npcsaved));
                onBackPressed();
            } else {
                toast(getString(R.string.error));
            }
        }else { //if the npc with the entered name does not exist, proceed as normal
            boolean insertNPC = mNpcDBHelper.addNPC(name, location, description, notes, npcTitle, voice, plotHooks, race, image, false);
            if (insertNPC) {
                hasSaved = true;
                toast(getString(R.string.npcsaved));
                onBackPressed();
            } else {
                toast(getString(R.string.error));
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(addNPCValue == 0) {
            Intent intent = new Intent(context, NpcDisplay.class);
            intent.putExtra("npcName", npcTitle);
            intent.putExtra("addNPCValue", 0);
            if (!hasSaved) notSaved(intent);
            else {
                startActivity(intent);
                finish();
            }
        }
        else {
            Intent intent = new Intent(this, NpcList.class);
            if (!hasSaved) notSaved(intent);
            else startActivity(intent);
        }
    }

    public void notSaved(Intent intent) {

        EditText npcName = findViewById(R.id.npcName);
        EditText npcDescription = findViewById(R.id.npcDescription);
        EditText npcPlotHooks = findViewById(R.id.npcPlotHooks);
        EditText npcNotes = findViewById(R.id.npcNotes);
        TextView racePreset = findViewById(R.id.racePreset);

        if (!(npcName.getText().toString().equals(initName)) || !(npcDescription.getText().toString().equals(initDesc)) || !(npcPlotHooks.getText().toString().equals(initPlotHooks)) || !(npcNotes.getText().toString().equals(initNotes)) || !(racePreset.getText().toString().equals(initRace)) || !(image.equals(initImage)) & !hasSaved) {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.unsaved);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    String name = npcName.getText().toString();
                    String race = racePreset.getText().toString();
                    String description = npcDescription.getText().toString();
                    String plotHooks = npcPlotHooks.getText().toString();
                    String notes = npcNotes.getText().toString();
                    dialog.dismiss();
                    saveFunctionality(name, npcSpinnerLocation, description, notes, voice, plotHooks, race, image);
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

    public void saveFunctionality(String name, String location, String description, String notes, String voice, String plotHooks, String race, String image){

        //if the edit text values are non-zero, and there is no identical entry, then add the loot NPC
        EditText npcName = findViewById(R.id.npcName);
        if (CheckName(npcName)){

            if (addNPCValue != 0){
                setNPCTitle(name); //if attempting to add a new NPC, we set this value to pass to the DBHelper in order to check if we need to update an existing entry
            }

            AddNPC(name, location, description, notes, npcTitle, voice, plotHooks, race, image);
        } else {
            toast("Please provide a name for your NPC");
        }
    }

    private void requestStoragePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(context) // Show an explanation to the user why we need the permission
                    .setTitle(getString(R.string.permissionneeded))
                    .setMessage(getString(R.string.permissionreason))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(NpcInfo.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setAddNPCValue(int value){
        addNPCValue = value;
    }
    private void setNPCTitle(String value){
        npcTitle = value;
    }
}