package com.example.dmapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dmapp.cities.CityList;
import com.example.dmapp.loot.LootList;
import com.example.dmapp.npcs.NpcList;
import com.example.dmapp.npcs.npcDBHelper;

public class MainActivity extends AppCompatActivity {
    private npcDBHelper myNPC;
    private static final String TAG = "MainActivity";
    private int backButtonCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myNPC = npcDBHelper.getInstance(getApplicationContext());
        final LinearLayoutCompat mainLayout = findViewById(R.id.mainLayout);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int deviceHeight = (displayMetrics.heightPixels);
        int deviceWidth = (displayMetrics.widthPixels);
        int imageWidth = (int) (deviceWidth * .40);
        int buttonWidth = (int) (deviceWidth * .65);
        int buttonWidth2 = (int) (deviceWidth * .5);
        int margin = (int)(deviceHeight * .08);
        int margin2 = (int)(deviceHeight * .10);
        int margin3 = (int)(deviceHeight * .04);
        int margin4 = (int)(deviceHeight * .13);

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        Log.d(TAG, "onCreate: theme is " + sharedPreferences.getString("Theme", "none"));
        String theme = sharedPreferences.getString("Theme", "none");
        if (theme.equals("none")) {
            editor.putString("Theme", "light");
            editor.apply();
            theme = sharedPreferences.getString("Theme", "none");
        }
        Log.d(TAG, "onCreate: After inital check, theme is " + sharedPreferences.getString("Theme", "none"));

        if (theme.equals("dark")) {
            mainLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
        }

        LinearLayoutCompat.LayoutParams lparams = new LinearLayoutCompat.LayoutParams(buttonWidth, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        lparams.setMargins(0, margin3, 0, 0);
        lparams.gravity = Gravity.CENTER_HORIZONTAL;

        final Button npcs = new Button(this);
        npcs.setText(R.string.npcs);
        npcs.setHeight(250);
        npcs.setTextSize(30);
        npcs.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_titles1));
        npcs.setLayoutParams(lparams);

        LinearLayoutCompat.LayoutParams lparams2 = new LinearLayoutCompat.LayoutParams(buttonWidth, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        lparams2.setMargins(0, margin, 0, 0);
        lparams2.gravity = Gravity.CENTER_HORIZONTAL;

        final Button cities = new Button(this);
        cities.setText(R.string.cities);
        cities.setHeight(250);
        cities.setTextSize(30);
        cities.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_titles2));
        cities.setLayoutParams(lparams2);

        final Button loot = new Button(this);
        loot.setText(R.string.loot);
        loot.setHeight(250);
        loot.setTextSize(30);
        loot.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_titles3));
        loot.setLayoutParams(lparams2);

        Button databaseManagement = new Button(this);
        databaseManagement.setText(R.string.managedb);
        databaseManagement.setHeight(120);
        databaseManagement.setTextSize(15);
        databaseManagement.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_titles4));
        LinearLayoutCompat.LayoutParams lparams3 = new LinearLayoutCompat.LayoutParams(buttonWidth2, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        lparams3.setMargins(0, margin2, 0, 0);
        lparams3.gravity = Gravity.CENTER_HORIZONTAL;
        databaseManagement.setLayoutParams(lparams3);

        mainLayout.addView(npcs);
        mainLayout.addView(cities);
        mainLayout.addView(loot);
        mainLayout.addView(databaseManagement);

        npcs.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NpcList.class);
                startActivity(intent);
            }
        });

        cities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CityList.class);
                startActivity(intent);
            }
        });

        loot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LootList.class);
                startActivity(intent);
            }
        });

        databaseManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, databaseManagement.class);
                startActivity(intent);
            }
        });

        final Button themeSwitch = new Button(this);
        if (theme.equals("light")) {
            themeSwitch.setText(R.string.darkmode);
        } else if (theme.equals("dark")){
            themeSwitch.setText(R.string.lightmode);
        }
        themeSwitch.setTextSize(15);
        themeSwitch.setPadding(20,20,20,20);
        themeSwitch.setBackground(ContextCompat.getDrawable(this, R.drawable.buttonbg));
        LinearLayoutCompat.LayoutParams lparams5 = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        lparams5.setMargins(0, margin4, 0, 0);
        lparams5.gravity = Gravity.CENTER_HORIZONTAL;
        themeSwitch.setLayoutParams(lparams5);
        mainLayout.addView(themeSwitch);

        themeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String theme = sharedPreferences.getString("Theme", "none");
                if (theme.equals("light")){
                    themeSwitch.setText(R.string.lightmode);
                    editor.putString("Theme", "dark");
                    editor.apply();

                    mainLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));

                } else if (theme.equals("dark")) {
                    themeSwitch.setText(R.string.darkmode);
                    editor.putString("Theme", "light");
                    editor.apply();

                    mainLayout.setBackgroundColor(getResources().getColor(R.color.mainBgColor));

                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
