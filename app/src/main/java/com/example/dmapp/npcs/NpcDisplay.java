package com.example.dmapp.npcs;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dmapp.MainActivity;
import com.example.dmapp.R;

import java.io.IOException;
import java.util.ArrayList;

public class NpcDisplay extends AppCompatActivity{

    private npcDBHelper mNpcDBHelper;
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
        ImageView backButton = findViewById(R.id.backButton);
        ImageView editIcon = findViewById(R.id.editIcon);
        final TextView npcName = findViewById(R.id.npcTitle);
        LinearLayout npcLayout = findViewById(R.id.npcLayout);
        LinearLayoutCompat npcUnderTitleLayout = findViewById(R.id.npcUnderTitleLayout);
        ImageView stop = findViewById(R.id.stop);
        ImageView play = findViewById(R.id.play);
        ImageView pause = findViewById(R.id.pause);

        mPlayer = new MediaPlayer();

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
        Cursor npcInfo = mNpcDBHelper.getSpecificNPC(getIntent().getStringExtra("npcName"));
        npcInfo.moveToFirst();
        npcTitle = npcInfo.getString(1);
        npcName.setText(npcInfo.getString(1));
        npcName.setTextColor(Color.parseColor("#5BCBAE"));
        View line = new View(this);
        line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
        line.setBackgroundColor(Color.parseColor("#F1FDF4"));
        npcLayout.addView(line);

        if((!npcInfo.getString(2).equals("")) && (!npcInfo.getString(2).equals("No Location"))) {
            TextView npcLocation = new TextView(this);
            npcLocation.setText(String.format("%s %s", getString(R.string.locationHeader), npcInfo.getString(2)));
            npcLocation.setTextSize(18);
            npcLocation.setTypeface(null, Typeface.ITALIC);
            npcLocation.setTextColor(Color.parseColor("#666666"));
            npcLocation.setBackgroundColor(Color.parseColor("#FFFFFF"));
            npcUnderTitleLayout.addView(npcLocation);
        }

        if(!npcInfo.getString(3).equals("")) {
            TextView descTitle = new TextView(this);
            descTitle.setText(R.string.descriptionHeader);
            descTitle.setTextSize(20);
            descTitle.setTextColor(Color.parseColor("#FE5F55"));
            descTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
            descTitle.setTypeface(null, Typeface.BOLD);
            npcLayout.addView(descTitle);

            View line2 = new View(this);
            line2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
            line2.setBackgroundColor(Color.parseColor("#000000"));
            npcLayout.addView(line2);

            TextView npcDescription = new TextView(this);
            npcDescription.setText(npcInfo.getString(3));
            npcDescription.setTextSize(18);
            npcDescription.setBackgroundColor(Color.parseColor("#FFFFFF"));
            LinearLayoutCompat.LayoutParams lparams2 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lparams2.setMargins(65, 0, 0, 45);
            npcDescription.setLayoutParams(lparams2);
            npcLayout.addView(npcDescription);
        }

        if(!npcInfo.getString(6).equals("")) {
            TextView hookTitle = new TextView(this);
            hookTitle.setText(R.string.plotHooksHeader);
            hookTitle.setTextSize(20);
            hookTitle.setTextColor(Color.parseColor("#FE5F55"));
            hookTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
            hookTitle.setTypeface(null, Typeface.BOLD);
            npcLayout.addView(hookTitle);

            View line2 = new View(this);
            line2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
            line2.setBackgroundColor(Color.parseColor("#000000"));
            npcLayout.addView(line2);

            TextView npcHooks = new TextView(this);
            npcHooks.setText(npcInfo.getString(6));
            npcHooks.setTextSize(18);
            npcHooks.setBackgroundColor(Color.parseColor("#FFFFFF"));
            LinearLayoutCompat.LayoutParams lparams4 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lparams4.setMargins(65, 0, 0, 45);
            npcHooks.setLayoutParams(lparams4);
            npcLayout.addView(npcHooks);
        }

        if(!npcInfo.getString(4).equals("")) {
            TextView notesTitle = new TextView(this);
            notesTitle.setText(R.string.notesHeader);
            notesTitle.setTextSize(20);
            notesTitle.setTextColor(Color.parseColor("#FE5F55"));
            notesTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
            notesTitle.setTypeface(null, Typeface.BOLD);
            npcLayout.addView(notesTitle);

            View line2 = new View(this);
            line2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
            line2.setBackgroundColor(Color.parseColor("#000000"));
            npcLayout.addView(line2);

            TextView npcNotes = new TextView(this);
            npcNotes.setText(npcInfo.getString(4));
            npcNotes.setTextSize(18);
            npcNotes.setBackgroundColor(Color.parseColor("#FFFFFF"));
            LinearLayoutCompat.LayoutParams lparams3 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lparams3.setMargins(65, 0, 0, 45);
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
            voiceTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
            voiceTitle.setTypeface(null, Typeface.BOLD);
            npcLayout.addView(voiceTitle);

            View line2 = new View(this);
            line2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
            line2.setBackgroundColor(Color.parseColor("#000000"));
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
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, NpcList.class);
        startActivity(intent);
        finish();
    }
}