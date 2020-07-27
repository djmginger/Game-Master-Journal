package com.example.dmapp.loot;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dmapp.MainActivity;
import com.example.dmapp.R;
import com.example.dmapp.npcs.NpcInfo;
import com.example.dmapp.npcs.npcDBHelper;

import java.io.IOException;
import java.util.ArrayList;

public class LootDisplay extends AppCompatActivity{

    private KeyListener variable;
    private lootDBHelper mLootDBHelper;
    private int addLootValue = 0;
    private String lootTitle = null;
    private final String TAG = "LootInfo";
    private Context context;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loot_display);
        context = this;

        mLootDBHelper = new lootDBHelper(this);
        ImageView backButton = findViewById(R.id.backButton);
        ImageView editIcon = findViewById(R.id.editIcon);
        final TextView lootName = findViewById(R.id.lootTitle);
        LinearLayout lootLayout = findViewById(R.id.lootLayout);
        LinearLayoutCompat imageLayout = findViewById(R.id.imageLayout);
        LinearLayoutCompat underTitleLayout = findViewById(R.id.underTitleLayout);
        LinearLayoutCompat underTitleLayout2 = findViewById(R.id.underTitleLayout2);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LootList.class);
                startActivity(intent);
                finish();
            }
        });


        //If coming to this activity from clicking on a name, get and fill out all fields with the data for the corresponding npc.
        Cursor lootInfo = mLootDBHelper.getSpecificLoot(getIntent().getStringExtra("lootName"));
        lootInfo.moveToFirst();
        lootTitle = lootInfo.getString(1);
        lootName.setText(lootInfo.getString(1));
        lootName.setTextColor(Color.parseColor("#6F94F8"));
        View line = new View(this);
        line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
        line.setBackgroundColor(Color.parseColor("#F1FDF4"));
        lootLayout.addView(line);

        Log.d(TAG, "onCreate: The value of attunement is " + lootInfo.getString(7));

        if(!lootInfo.getString(6).equals("")){
            ImageView itemImage = new ImageView(this);
            Uri imageFileUri = Uri.parse(lootInfo.getString(6));
            itemImage.setImageURI(imageFileUri);
            LinearLayoutCompat.LayoutParams lparams = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lparams.setMargins(0, 0, 10, 0);
            imageLayout.addView(itemImage);
        }

        if(!lootInfo.getString(2).equals("none") && !lootInfo.getString(2).equals("None")) {
            TextView lootRarity = new TextView(this);
            lootRarity.setText(lootInfo.getString(2));
            lootRarity.setTextSize(16);
            lootRarity.setTypeface(null, Typeface.ITALIC);
            lootRarity.setTextColor(Color.parseColor("#666666"));
            lootRarity.setBackgroundColor(Color.parseColor("#FFFFFF"));
            underTitleLayout.addView(lootRarity);
        }

        if((!lootInfo.getString(2).equals("")) && (!lootInfo.getString(3).equals(""))){
            ImageView separator = new ImageView(this);
            separator.setImageDrawable(getResources().getDrawable(R.drawable.dot));
            LinearLayoutCompat.LayoutParams lparams = new LinearLayoutCompat.LayoutParams(30, 30);
            lparams.setMargins(30, 0, 30, 0);
            separator.setLayoutParams(lparams);
            underTitleLayout.addView(separator);
        }

        if(!lootInfo.getString(3).equals("")) {
            TextView lootPrice = new TextView(this);
            lootPrice.setText(lootInfo.getString(3));
            lootPrice.setTextSize(16);
            lootPrice.setTypeface(null, Typeface.ITALIC);
            lootPrice.setTextColor(Color.parseColor("#666666"));
            lootPrice.setBackgroundColor(Color.parseColor("#FFFFFF"));
            underTitleLayout.addView(lootPrice);
        }

        if((lootInfo.getString(7)).equals("yes")) {
            TextView lootReq = new TextView(this);
            lootReq.setText("Requires Attunement");
            lootReq.setTextSize(16);
            lootReq.setTypeface(null, Typeface.ITALIC);
            lootReq.setTextColor(Color.parseColor("#666666"));
            lootReq.setBackgroundColor(Color.parseColor("#FFFFFF"));
            underTitleLayout2.addView(lootReq);
        }

        if(!lootInfo.getString(4).equals("")) {
            TextView lootDescTitle = new TextView(this);
            lootDescTitle.setText("Description");
            lootDescTitle.setTextSize(20);
            lootDescTitle.setTextColor(Color.parseColor("#FE5F55"));
            lootDescTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
            lootDescTitle.setTypeface(null, Typeface.BOLD);
            lootLayout.addView(lootDescTitle);

            View line2 = new View(this);
            line2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
            line2.setBackgroundColor(Color.parseColor("#000000"));
            lootLayout.addView(line2);

            TextView lootDesc = new TextView(this);
            lootDesc.setText(lootInfo.getString(4));
            lootDesc.setTextSize(18);
            lootDesc.setBackgroundColor(Color.parseColor("#FFFFFF"));
            LinearLayoutCompat.LayoutParams lparams4 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lparams4.setMargins(65, 0, 0, 45);
            lootDesc.setLayoutParams(lparams4);
            lootLayout.addView(lootDesc);
        }

        if(!lootInfo.getString(5).equals("")) {
            TextView detailsTitle = new TextView(this);
            detailsTitle.setText("Details");
            detailsTitle.setTextSize(20);
            detailsTitle.setTextColor(Color.parseColor("#FE5F55"));
            detailsTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
            detailsTitle.setTypeface(null, Typeface.BOLD);
            lootLayout.addView(detailsTitle);

            View line2 = new View(this);
            line2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
            line2.setBackgroundColor(Color.parseColor("#000000"));
            lootLayout.addView(line2);

            TextView lootDetails = new TextView(this);
            lootDetails.setText(lootInfo.getString(5));
            lootDetails.setTextSize(18);
            lootDetails.setBackgroundColor(Color.parseColor("#FFFFFF"));
            LinearLayoutCompat.LayoutParams lparams3 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lparams3.setMargins(65, 0, 0, 45);
            lootDetails.setLayoutParams(lparams3);
            lootLayout.addView(lootDetails);
        }

        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LootInfo.class);
                Log.d(TAG, "onClick: The value being passed to NPCInfo is " + lootTitle);
                intent.putExtra("lootName", lootTitle);
                intent.putExtra("addLootValue", 0);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, LootList.class);
        startActivity(intent);
        finish();
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setAddLootValue(int value){
        addLootValue = value;
    }
    private void setLootTitle(String value){
        lootTitle = value;
    }
}