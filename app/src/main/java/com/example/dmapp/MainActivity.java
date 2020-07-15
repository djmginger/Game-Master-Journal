package com.example.dmapp;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
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
        LinearLayoutCompat mainLayout = findViewById(R.id.mainLayout);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int deviceHeight = (displayMetrics.heightPixels);
        int margin = (int)(deviceHeight * .08);
        int margin2 = (int)(deviceHeight * .10);
        int margin3 = (int)(deviceHeight * .04);

        LinearLayoutCompat.LayoutParams lparams = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        lparams.setMargins(0, margin3, 0, 0);

        Button npcs = new Button(this);
        npcs.setText("NPCs");
        npcs.setHeight(250);
        npcs.setTextSize(30);
        npcs.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_titles1));
        npcs.setLayoutParams(lparams);

        LinearLayoutCompat.LayoutParams lparams2 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        lparams2.setMargins(0, margin, 0, 0);

        Button cities = new Button(this);
        cities.setText("Cities");
        cities.setHeight(250);
        cities.setTextSize(30);
        cities.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_titles2));
        cities.setLayoutParams(lparams2);

        Button loot = new Button(this);
        loot.setText("Loot");
        loot.setHeight(250);
        loot.setTextSize(30);
        loot.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_titles3));
        loot.setLayoutParams(lparams2);

        Button databaseManagement = new Button(this);
        databaseManagement.setText("Manage Databases");
        databaseManagement.setHeight(120);
        databaseManagement.setTextSize(15);
        databaseManagement.setBackground(ContextCompat.getDrawable(this, R.drawable.rounded_titles4));
        LinearLayoutCompat.LayoutParams lparams3 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        lparams3.setMargins(0, margin2, 0, 0);
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
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CityList.class);
                startActivity(intent);
            }
        });

        loot.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LootList.class);
                startActivity(intent);
            }
        });

        databaseManagement.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, databaseManagement.class);
                startActivity(intent);
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
            Toast.makeText(this, "Press the back button again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}