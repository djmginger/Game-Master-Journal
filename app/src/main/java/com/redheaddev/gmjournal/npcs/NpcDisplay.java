package com.redheaddev.gmjournal.npcs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.redheaddev.gmjournal.R;
import com.redheaddev.gmjournal.cities.CityDisplay;
import com.redheaddev.gmjournal.cities.citiesDBHelper;

import java.io.IOException;
import java.util.ArrayList;

public class NpcDisplay extends AppCompatActivity{

    private npcDBHelper mNpcDBHelper;
    private citiesDBHelper mCitiesDBHelper;
    private int addNPCValue = 0;
    private String npcTitle = null;
    private String voice = "";
    private final String TAG = "NPCInfo";
    private boolean isStopped;
    private final ArrayList<String> locations = new ArrayList<>();
    private MediaPlayer mPlayer;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.npc_display);
        context = this;

        mNpcDBHelper = new npcDBHelper(this);
        mCitiesDBHelper = new citiesDBHelper(this);
        ImageView backButton = findViewById(R.id.backButton);
        ImageView editIcon = findViewById(R.id.editIcon);
        final TextView npcName = findViewById(R.id.npcTitle);
        ScrollView npcMainLayout = findViewById(R.id.npcMainLayout);
        LinearLayout npcLayout = findViewById(R.id.npcLayout);
        LinearLayoutCompat imageLayout = findViewById(R.id.imageLayout);
        LinearLayoutCompat npcUnderTitleLayout = findViewById(R.id.npcUnderTitleLayout);
        LinearLayoutCompat npcUnderTitleLayout2 = findViewById(R.id.npcUnderTitleLayout2);
        ImageView stop = findViewById(R.id.stop);
        ImageView play = findViewById(R.id.play);
        ImageView pause = findViewById(R.id.pause);

        mPlayer = new MediaPlayer();

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String theme = sharedPreferences.getString("Theme", "none");
        Boolean darkMode = theme.equals("dark");

        if (theme.equals("dark")){

            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(editIcon.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(stop.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(play.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(pause.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));

            npcLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            npcMainLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            imageLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            npcUnderTitleLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            imageLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            npcUnderTitleLayout2.setBackgroundColor(Color.parseColor("#2C2C2C"));
            backButton.setBackgroundColor(Color.parseColor("#2C2C2C"));
            npcName.setTextColor(Color.WHITE);
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(editIcon.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(stop.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(play.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(pause.getDrawable()), ContextCompat.getColor(context, R.color.black));
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NpcList.class);
                startActivity(intent);
                finish();
            }
        });

        //Set the mediaplayer controls to be invisible until there is an audio file available to play.
        stop.setVisibility(View.INVISIBLE);
        play.setVisibility(View.INVISIBLE);
        pause.setVisibility(View.INVISIBLE);

        //If coming to this activity from clicking on a name, get and fill out all fields with the data for the corresponding npc.
        final Cursor npcInfo = mNpcDBHelper.getSpecificNPC(getIntent().getStringExtra("npcName"));
        npcInfo.moveToFirst();
        npcTitle = npcInfo.getString(1);
        npcName.setText(npcInfo.getString(1));
        npcName.setTextColor(Color.parseColor("#5BCBAE"));

        if(!npcInfo.getString(8).equals("")){
            ImageView npcImage = new ImageView(this);
            Uri imageFileUri = Uri.parse(npcInfo.getString(8));
            npcImage.setImageURI(imageFileUri);
            LinearLayoutCompat.LayoutParams lparams = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lparams.setMargins(0, 20, 0, 0);
            npcImage.setLayoutParams(lparams);
            npcImage.setAdjustViewBounds(true);
            imageLayout.addView(npcImage);
        }

        if((!npcInfo.getString(2).equals("")) && (!npcInfo.getString(2).equals(getString(R.string.nolocation)))) {
            TextView npcLocation = new TextView(this);
            npcLocation.setText(String.format("%s %s", getString(R.string.locationHeader), npcInfo.getString(2)));
            npcLocation.setPaintFlags(npcLocation.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
            npcLocation.setTextSize(18);
            npcLocation.setTypeface(null, Typeface.ITALIC);
            npcLocation.setTextColor(Color.parseColor("#6fd8f9"));
            if (!darkMode) npcLocation.setBackgroundColor(Color.parseColor("#FFFFFF"));
                else npcLocation.setBackgroundColor(Color.parseColor("#2C2C2C"));
            npcLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mCitiesDBHelper.checkNonExistence(npcInfo.getString(2))){
                        if (!isStopped){
                            mPlayer.stop();
                            try{
                                mPlayer.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            isStopped = true;
                        }
                        Intent intent = new Intent(context, CityDisplay.class);
                        intent.putExtra("cityName", npcInfo.getString(2));
                        intent.putExtra("addCityValue", 0);
                        intent.putExtra("npcNav", true); //This is for back button navigation
                        startActivity(intent);
                    } else toast(getString(R.string.nosavedcity));
                }
            });

            npcUnderTitleLayout.addView(npcLocation);
        }

        if((!npcInfo.getString(7).equals("")) && (!npcInfo.getString(7).equals("Race")) && (!npcInfo.getString(7).equals("None"))) {
            TextView npcRace = new TextView(this);
            npcRace.setText(String.format("%s %s", getString(R.string.raceHeader), npcInfo.getString(7)));
            npcRace.setTextSize(18);
            npcRace.setTypeface(null, Typeface.ITALIC);
            if (!darkMode) {
                npcRace.setTextColor(Color.parseColor("#666666"));
                npcRace.setBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                npcRace.setTextColor(Color.parseColor("#FFFFFF"));
                npcRace.setBackgroundColor(Color.parseColor("#2C2C2C"));
            }
            npcUnderTitleLayout2.addView(npcRace);
        }

        if(!npcInfo.getString(3).equals("")) {
            TextView descTitle = new TextView(this);
            descTitle.setText(R.string.descriptionHeader);
            descTitle.setTextSize(20);
            descTitle.setTextColor(Color.parseColor("#FE5F55"));
            if (!darkMode) descTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
                else descTitle.setBackgroundColor(Color.parseColor("#2C2C2C"));
            descTitle.setTypeface(null, Typeface.BOLD);
            npcLayout.addView(descTitle);

            View line2 = new View(this);
            line2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
            if (!darkMode) line2.setBackgroundColor(Color.parseColor("#8C8C8C"));
                else line2.setBackgroundColor(Color.parseColor("#FFFFFF"));
            npcLayout.addView(line2);

            TextView npcDescription = new TextView(this);
            npcDescription.setText(npcInfo.getString(3));
            npcDescription.setTextSize(18);
            if (!darkMode) {
                npcDescription.setBackgroundColor(Color.parseColor("#FFFFFF"));
                npcDescription.setTextColor(Color.parseColor("#000000"));
            } else {
                npcDescription.setBackgroundColor(Color.parseColor("#2C2C2C"));
                npcDescription.setTextColor(Color.parseColor("#FFFFFF"));
            }
            LinearLayoutCompat.LayoutParams lparams2 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lparams2.setMargins(30, 0, 0, 45);
            npcDescription.setLayoutParams(lparams2);
            npcLayout.addView(npcDescription);
        }

        if(!npcInfo.getString(6).equals("")) {
            TextView hookTitle = new TextView(this);
            hookTitle.setText(R.string.plotHooksHeader);
            hookTitle.setTextSize(20);
            hookTitle.setTextColor(Color.parseColor("#FE5F55"));
            if (!darkMode) hookTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
                else hookTitle.setBackgroundColor(Color.parseColor("#2C2C2C"));
            hookTitle.setTypeface(null, Typeface.BOLD);
            npcLayout.addView(hookTitle);

            View line2 = new View(this);
            line2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
            if (!darkMode) line2.setBackgroundColor(Color.parseColor("#8C8C8C"));
                else line2.setBackgroundColor(Color.parseColor("#FFFFFF"));
            npcLayout.addView(line2);

            TextView npcHooks = new TextView(this);
            npcHooks.setText(npcInfo.getString(6));
            npcHooks.setTextSize(18);
            if (!darkMode) {
                npcHooks.setBackgroundColor(Color.parseColor("#FFFFFF"));
                npcHooks.setTextColor(Color.parseColor("#000000"));
            } else {
                npcHooks.setBackgroundColor(Color.parseColor("#2C2C2C"));
                npcHooks.setTextColor(Color.parseColor("#FFFFFF"));
            }
            LinearLayoutCompat.LayoutParams lparams4 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lparams4.setMargins(30, 0, 0, 45);
            npcHooks.setLayoutParams(lparams4);
            npcLayout.addView(npcHooks);
        }

        if(!npcInfo.getString(4).equals("")) {
            TextView notesTitle = new TextView(this);
            notesTitle.setText(R.string.notesHeader);
            notesTitle.setTextSize(20);
            notesTitle.setTextColor(Color.parseColor("#FE5F55"));
            if (!darkMode) notesTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
                else notesTitle.setBackgroundColor(Color.parseColor("#2C2C2C"));
            notesTitle.setTypeface(null, Typeface.BOLD);
            npcLayout.addView(notesTitle);

            View line2 = new View(this);
            line2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
            if (!darkMode) line2.setBackgroundColor(Color.parseColor("#8C8C8C"));
                else line2.setBackgroundColor(Color.parseColor("#FFFFFF"));
            npcLayout.addView(line2);

            TextView npcNotes = new TextView(this);
            npcNotes.setText(npcInfo.getString(4));
            npcNotes.setTextSize(18);
            if (!darkMode) {
                npcNotes.setBackgroundColor(Color.parseColor("#FFFFFF"));
                npcNotes.setTextColor(Color.parseColor("#000000"));
            } else {
                npcNotes.setBackgroundColor(Color.parseColor("#2C2C2C"));
                npcNotes.setTextColor(Color.parseColor("#FFFFFF"));
            }
            LinearLayoutCompat.LayoutParams lparams3 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lparams3.setMargins(30, 0, 0, 45);
            npcNotes.setLayoutParams(lparams3);
            npcLayout.addView(npcNotes);
        }

        voice = npcInfo.getString(5);
        Log.d(TAG, "onCreate: the voice value is:" + voice);

        if (!(voice.equals(""))) {
            TextView voiceTitle = new TextView(this);
            voiceTitle.setText(R.string.voiceHeader);
            voiceTitle.setTextSize(20);
            voiceTitle.setTextColor(Color.parseColor("#FE5F55"));
            if (!darkMode) voiceTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
                else voiceTitle.setBackgroundColor(Color.parseColor("#2C2C2C"));
            voiceTitle.setTypeface(null, Typeface.BOLD);
            npcLayout.addView(voiceTitle);

            View line2 = new View(this);
            line2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
            if (!darkMode) line2.setBackgroundColor(Color.parseColor("#8C8C8C"));
                else line2.setBackgroundColor(Color.parseColor("#FFFFFF"));
            npcLayout.addView(line2);

            //Create a media player and set it to the audio file provided by the user
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

        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.release();
                Intent intent = new Intent(context, NpcInfo.class);
                Log.d(TAG, "onClick: The value being passed to NPCInfo is " + npcTitle);
                intent.putExtra("npcName", npcTitle);
                intent.putExtra("addNPCValue", 0);
                startActivity(intent);
            }
        });
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

    @Override
    public void onBackPressed() {
        Boolean npcNav = getIntent().getBooleanExtra("cityNav", false);
        if(npcNav){
            if (!isStopped){
                mPlayer.stop();
                try{
                    mPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isStopped = true;
            }
            super.onBackPressed();
        } else {
            Intent intent = new Intent(context, NpcList.class);
            startActivity(intent);
            finish();
        }
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}