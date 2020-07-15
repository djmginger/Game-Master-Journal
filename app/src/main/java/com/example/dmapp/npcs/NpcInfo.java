package com.example.dmapp.npcs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dmapp.MainActivity;
import com.example.dmapp.cities.citiesDBHelper;
import com.example.dmapp.R;

import java.io.IOException;
import java.util.ArrayList;

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
    private Uri audioFileUri;
    boolean deleteExists = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.npc_info);
        context = this;

        mNpcDBHelper = new npcDBHelper(this);
        citiesDBHelper mCityDBHelper = new citiesDBHelper(this);
        ImageView backButton = findViewById(R.id.backButton);
        final EditText npcName = findViewById(R.id.npcName);
        final EditText npcDescription = findViewById(R.id.npcDescription);
        final EditText npcPlotHooks = findViewById(R.id.npcPlotHooks);
        final EditText npcNotes = findViewById(R.id.npcNotes);
        final LinearLayoutCompat buttonLayout = findViewById(R.id.npcButtonLayout);


        Spinner locationSpinner = findViewById(R.id.locationSpinner);
        Button addVoice = findViewById(R.id.addVoice);
        Button saveNpc = findViewById(R.id.saveNpc);
        Button stop = findViewById(R.id.stop);
        Button play = findViewById(R.id.play);
        Button pause = findViewById(R.id.pause);

        mPlayer = new MediaPlayer();

        //Fill out the locationSpinner with available locations

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                npcSpinnerLocation = locations.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                Log.i("GTOUTOUT", "Nothing Selected");
            }
        });

        //if starting the activity from clicking an item, addNPCValue will be 0, otherwise, it's -1
        //we use this value to adjust the behavior of the editText values set during onCreate, and whether we need to check the name for an identical value when creating a new npc
        setAddNPCValue(getIntent().getIntExtra("addNPCValue", -1));

        setNPCTitle(getIntent().getStringExtra("npcName")); //set the npc Title to what was passed in the intent. We use this to check to see if we need to UPDATE instead of INSERT when adding an npc
        //Log.d(TAG, "onCreate: The value of npcTitle is " + npcTitle);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addNPCValue == 0) {
                    Intent intent = new Intent(context, NpcDisplay.class);
                    intent.putExtra("npcName", npcTitle);
                    intent.putExtra("addNPCValue", 0);
                    startActivity(intent);
                    finish();
                }
                else onBackPressed();
            }
        });

        addVoice.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                mPlayer.stop();
                Intent intent;
                intent = new Intent();
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("audio/*");
                startActivityForResult(intent, 1);
            }
        });

        saveNpc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = npcName.getText().toString();
                String description = npcDescription.getText().toString();
                String plotHooks = npcPlotHooks.getText().toString();
                String notes = npcNotes.getText().toString();

                //if the edit text values are non-zero, and there is no identical entry, then add the NPC data and remove editability again.
                if (CheckName(npcName)){

                    if (addNPCValue != 0){
                        setNPCTitle(name); //if attempting to add a new npc, we set this value to pass to the DBHelper in order to check if we need to update an existing entry
                    }
                    AddNPC(name, npcSpinnerLocation, description, notes, npcTitle, voice, plotHooks);
                    if(addNPCValue == -1) {
                        Button deleteNPCButton = new Button(context);
                        deleteNPCButton.setText(R.string.deletenpc);
                        deleteNPCButton.setBackground(ContextCompat.getDrawable(context, R.drawable.buttonbg));
                        LinearLayoutCompat.LayoutParams lparams = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                        lparams.setMargins(5, 0, 5, 0);
                        deleteNPCButton.setLayoutParams(lparams);
                        deleteNPCButton.setPadding(30, 10, 30, 10);
                        deleteNPCButton.setOnClickListener(new View.OnClickListener() {
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

                                } else toast("There is no item to delete");
                            }
                        });
                        if (!deleteExists) {
                            buttonLayout.addView(deleteNPCButton);
                            deleteExists = true;
                        }
                    }
                } else {
                    toast("Please provide a name for your NPC");
                }
            }
        });

        stop.setVisibility(View.INVISIBLE); //Set the mediaplayer controls to be invisible until there is an audio file available to play.
        play.setVisibility(View.INVISIBLE);
        pause.setVisibility(View.INVISIBLE);

        //If coming to this activity from clicking on a name, get and fill out all fields with the data for the corresponding npc.
        if (addNPCValue != -1) {
            deleteExists = true;
            setNPCTitle(getIntent().getStringExtra("npcName"));
            Log.d(TAG, "onCreate: The npc value passed is " + npcTitle);
            Cursor npcInfo = mNpcDBHelper.getSpecificNPC(npcTitle);
            npcInfo.moveToFirst();
            npcName.setText(npcInfo.getString(1));
            npcDescription.setText(npcInfo.getString(3));
            npcNotes.setText(npcInfo.getString(4));
            Log.d(TAG, "onCreate: the value of plothooks is " + npcInfo.getString(6));
            Button deleteNPCButton = new Button(this);
            deleteNPCButton.setText(R.string.deletenpc);
            deleteNPCButton.setBackground(ContextCompat.getDrawable(this, R.drawable.buttonbg));
            LinearLayoutCompat.LayoutParams lparams = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lparams.setMargins(5,0, 5, 0);
            deleteNPCButton.setLayoutParams(lparams);
            deleteNPCButton.setPadding(30,10,30,10);
            deleteNPCButton.setOnClickListener(new View.OnClickListener() {
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

                    }else toast("There is no item to delete");
                }
            });
            buttonLayout.addView(deleteNPCButton);
            npcPlotHooks.setText(npcInfo.getString(6));

            try (Cursor cities = mCityDBHelper.getCities()) {
                if (cities.moveToNext()) {
                    do {
                        locations.add(cities.getString(1));
                    } while (cities.moveToNext());
                }
            } finally {
                locations.remove(npcInfo.getString(2));
                locations.add(0, npcInfo.getString(2));
                if(!(locations.contains("No Location"))) {
                    locations.add("No Location");
                }
            }

            ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locations);
            locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            locationSpinner.setAdapter(locationAdapter);

            voice = npcInfo.getString(5);
            Log.d(TAG, "onCreate: the voice value is:" + voice);

            if (!(voice.equals(""))) {
                stop.setVisibility(View.VISIBLE);
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.VISIBLE);
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
            }
        } else {
            if(!(locations.contains("No Location"))) {
                locations.add("No Location");
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

            ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locations);
            locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            locationSpinner.setAdapter(locationAdapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d(TAG, "onActivityResult: ResultCode is: " + resultCode);

        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        if(requestCode == 1 && resultCode == -1){
            if ((data != null) && (data.getData() != null)){

                audioFileUri = data.getData();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    this.getContentResolver().takePersistableUriPermission(audioFileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

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
                Button stop = findViewById(R.id.stop);
                Button play = findViewById(R.id.play);
                Button pause = findViewById(R.id.pause);
                addVoice.setText(R.string.changevoice);

                stop.setVisibility(View.VISIBLE);
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.VISIBLE);

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
            }
        }
        else{
            if(audioFileUri != null){
                try{
                    mPlayer.setDataSource(this, audioFileUri);
                    mPlayer.prepare();
                } catch(IOException ie) {
                    ie.printStackTrace();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: voice = " + voice);
        if(!voice.equals("") && mPlayer.isPlaying()) {
            mPlayer.stop();
        }
    }

    //Make sure the text fields are not left blank (may be edited later to omit the notes section)
    private boolean CheckName(EditText name) {
        return ((name.length() != 0));
    }

    private void AddNPC(final String name, final String location, final String description, final String notes, final String npcTitle, final String voice, final String plotHooks){

        if(!(mNpcDBHelper.checkNonExistence(name))) { //if an npc with the entered name already exists, ask the user if they wish to update the entry
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.npcexists);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    boolean insertNPC = mNpcDBHelper.addNPC(name, location, description, notes, npcTitle, voice, plotHooks);
                    if (insertNPC) {
                        toast("NPC Saved");
                    } else {
                        toast("Error with entry");
                    }
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }else { //if the npc with the entered name does not exist, proceed as normal
            boolean insertNPC = mNpcDBHelper.addNPC(name, location, description, notes, npcTitle, voice, plotHooks);
            if (insertNPC) {
                toast("NPC Saved");
            } else {
                toast("Error with entry");
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(addNPCValue == 0) {
            Intent intent = new Intent(context, NpcDisplay.class);
            intent.putExtra("npcName", npcTitle);
            intent.putExtra("addNPCValue", 0);
            startActivity(intent);
            finish();
        }
        else super.onBackPressed();
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