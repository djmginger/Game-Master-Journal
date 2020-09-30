package com.redheaddev.gmjournal.loot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.method.KeyListener;
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
import com.redheaddev.gmjournal.cities.CityList;

import java.io.IOException;

public class LootDisplay extends AppCompatActivity{

    private KeyListener variable;
    private lootDBHelper mLootDBHelper;
    private int addLootValue = 0;
    private String lootTitle = null;
    private final String TAG = "LootInfo";
    private Context context;
    private Uri imageUri;
    private Boolean darkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loot_display);
        context = this;

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String theme = sharedPreferences.getString("Theme", "none");
        darkMode = theme.equals("dark");

        mLootDBHelper = new lootDBHelper(this);
        ImageView backButton = findViewById(R.id.backButton);
        ImageView editIcon = findViewById(R.id.editIcon);
        final TextView lootName = findViewById(R.id.lootTitle);
        ScrollView lootMainLayout = findViewById(R.id.lootMainLayout);
        LinearLayout lootLayout = findViewById(R.id.lootLayout);
        LinearLayoutCompat imageLayout = findViewById(R.id.imageLayout);
        LinearLayoutCompat underTitleLayout = findViewById(R.id.underTitleLayout);
        LinearLayoutCompat underTitleLayout2 = findViewById(R.id.underTitleLayout2);
        LinearLayoutCompat underTitleLayout3 = findViewById(R.id.underTitleLayout3);

        if (theme.equals("dark")){

            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(editIcon.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));

            lootMainLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            lootLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            imageLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            underTitleLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            underTitleLayout2.setBackgroundColor(Color.parseColor("#2C2C2C"));
            backButton.setBackgroundColor(Color.parseColor("#2C2C2C"));
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(editIcon.getDrawable()), ContextCompat.getColor(context, R.color.black));
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LootList.class);
                startActivity(intent);
                finish();
            }
        });

        //If coming to this activity from clicking on a name, get and fill out all fields with the data for the corresponding npc.
        final Cursor lootInfo = mLootDBHelper.getSpecificLoot(getIntent().getStringExtra("lootName"));
        lootInfo.moveToFirst();
        lootTitle = lootInfo.getString(1);
        lootName.setText(lootInfo.getString(1));
        lootName.setTextColor(Color.parseColor("#6F94F8"));

        Log.d(TAG, "onCreate: The value of attunement is " + lootInfo.getString(7));

        if(!lootInfo.getString(6).equals("")){
            ImageView itemImage = new ImageView(this);
            String image = lootInfo.getString(6);
            Bitmap bmImg = BitmapFactory.decodeFile(image);
            itemImage.setImageBitmap(bmImg);
            LinearLayoutCompat.LayoutParams lparams = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lparams.setMargins(0, 20, 0, 0);
            itemImage.setLayoutParams(lparams);
            itemImage.setAdjustViewBounds(true);
            imageLayout.addView(itemImage);
        }

        if(!lootInfo.getString(2).equals("none") && !lootInfo.getString(2).equals("None")) {
            TextView lootRarity = new TextView(this);
            lootRarity.setText(lootInfo.getString(2));
            lootRarity.setTextSize(16);
            lootRarity.setTypeface(null, Typeface.ITALIC);
            if (!darkMode) {
                lootRarity.setTextColor(Color.parseColor("#666666"));
                lootRarity.setBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                lootRarity.setTextColor(Color.parseColor("#FFFFFF"));
                lootRarity.setBackgroundColor(Color.parseColor("#2C2C2C"));
            }
            underTitleLayout.addView(lootRarity);
        }

        if((!lootInfo.getString(2).equals("")) && (!lootInfo.getString(3).equals(""))){
            ImageView separator = new ImageView(this);
            separator.setImageDrawable(getResources().getDrawable(R.drawable.dot));
            if (!darkMode) DrawableCompat.setTint(DrawableCompat.wrap(separator.getDrawable()), ContextCompat.getColor(context, R.color.black));
                else DrawableCompat.setTint(DrawableCompat.wrap(separator.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
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
            if (!darkMode) {
                lootPrice.setTextColor(Color.parseColor("#666666"));
                lootPrice.setBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                lootPrice.setTextColor(Color.parseColor("#FFFFFF"));
                lootPrice.setBackgroundColor(Color.parseColor("#2C2C2C"));
            }
            underTitleLayout.addView(lootPrice);
        }

        if((lootInfo.getString(7)).equals("yes")) {
            TextView lootReq = new TextView(this);
            lootReq.setText(R.string.attunement);
            lootReq.setTextSize(16);
            lootReq.setTypeface(null, Typeface.ITALIC);
            if (!darkMode) {
                lootReq.setTextColor(Color.parseColor("#666666"));
                lootReq.setBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                lootReq.setTextColor(Color.parseColor("#FFFFFF"));
                lootReq.setBackgroundColor(Color.parseColor("#2C2C2C"));
            }
            underTitleLayout2.addView(lootReq);
        }

        if(lootInfo.getString(8) != null) {
            if (!lootInfo.getString(8).equals("") && !lootInfo.getString(8).equals("No Group")) {
                TextView itemGroup = new TextView(this);
                itemGroup.setText(String.format("%s %s", getString(R.string.itemgrouplabel), lootInfo.getString(8)));
                itemGroup.setPaintFlags(itemGroup.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                itemGroup.setTextSize(16);
                itemGroup.setTypeface(null, Typeface.ITALIC);
                itemGroup.setTextColor(Color.parseColor("#6fd8f9"));
                if (!darkMode) itemGroup.setBackgroundColor(Color.parseColor("#FFFFFF"));
                else itemGroup.setBackgroundColor(Color.parseColor("#2C2C2C"));
                itemGroup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mLootDBHelper.checkGroupNonExistence(lootInfo.getString(8))) {
                            Intent intent = new Intent(context, LootList.class);
                            intent.putExtra("groupClick", true);
                            intent.putExtra("groupName", lootInfo.getString(8));
                            startActivity(intent);
                        } else toast(getString(R.string.nosaveditemgroup));
                    }
                });
                underTitleLayout3.addView(itemGroup);
            }
        }

        if(!lootInfo.getString(4).equals("")) {
            TextView lootDescTitle = new TextView(this);
            lootDescTitle.setText(R.string.desc);
            lootDescTitle.setTextSize(20);
            lootDescTitle.setTextColor(Color.parseColor("#FE5F55"));
            if (!darkMode) lootDescTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
                else lootDescTitle.setBackgroundColor(Color.parseColor("#2C2C2C"));
            lootDescTitle.setTypeface(null, Typeface.BOLD);
            lootLayout.addView(lootDescTitle);

            View line2 = new View(this);
            line2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
            if (!darkMode) line2.setBackgroundColor(Color.parseColor("#8C8C8C"));
                else line2.setBackgroundColor(Color.parseColor("#FFFFFF"));
            lootLayout.addView(line2);

            TextView lootDesc = new TextView(this);
            lootDesc.setText(lootInfo.getString(4));
            lootDesc.setTextSize(18);
            if (!darkMode) {
                lootDesc.setTextColor(Color.parseColor("#666666"));
                lootDesc.setBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                lootDesc.setTextColor(Color.parseColor("#FFFFFF"));
                lootDesc.setBackgroundColor(Color.parseColor("#2C2C2C"));
            }
            LinearLayoutCompat.LayoutParams lparams4 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lparams4.setMargins(30, 0, 0, 45);
            lootDesc.setLayoutParams(lparams4);
            lootLayout.addView(lootDesc);
        }

        if(!lootInfo.getString(5).equals("")) {
            TextView detailsTitle = new TextView(this);
            detailsTitle.setText(R.string.detailHeader);
            detailsTitle.setTextSize(20);
            detailsTitle.setTextColor(Color.parseColor("#FE5F55"));
            if (!darkMode) detailsTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
                else detailsTitle.setBackgroundColor(Color.parseColor("#2C2C2C"));
            detailsTitle.setTypeface(null, Typeface.BOLD);
            lootLayout.addView(detailsTitle);

            View line2 = new View(this);
            line2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
            if (!darkMode) line2.setBackgroundColor(Color.parseColor("#8C8C8C"));
                else line2.setBackgroundColor(Color.parseColor("#FFFFFF"));
            lootLayout.addView(line2);

            TextView lootDetails = new TextView(this);
            lootDetails.setText(lootInfo.getString(5));
            lootDetails.setTextSize(18);
            if (!darkMode) {
                lootDetails.setTextColor(Color.parseColor("#666666"));
                lootDetails.setBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                lootDetails.setTextColor(Color.parseColor("#FFFFFF"));
                lootDetails.setBackgroundColor(Color.parseColor("#2C2C2C"));
            }
            LinearLayoutCompat.LayoutParams lparams3 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lparams3.setMargins(30, 0, 0, 45);
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
        Boolean miscNav = getIntent().getBooleanExtra("miscNav", false);
        if(miscNav){
            super.onBackPressed();
        } else {
            Intent intent = new Intent(context, LootList.class);
            startActivity(intent);
            finish();
        }
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