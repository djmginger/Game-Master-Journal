package com.redheaddev.gmjournal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class databaseManagement extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private boolean darkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_management);

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String theme = sharedPreferences.getString("Theme", "none");

        LinearLayoutCompat mainLayout = findViewById(R.id.mainLayout);
        ImageView backButton = findViewById(R.id.backButton);
        Button deleteNPC = findViewById(R.id.deleteNPC);
        Button deleteCity = findViewById(R.id.deleteCity);
        Button deleteLoot = findViewById(R.id.deleteLoot);
        Button deleteLocations = findViewById(R.id.deleteLocations);
        Button deletePresets = findViewById(R.id.deletePresets);
        Button deleteDistances = findViewById(R.id.deleteDistances);
        TextView title = findViewById(R.id.title);

        if (theme.equals("dark")){

            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(this, R.color.mainBgColor));
            title.setTextColor(Color.WHITE);
            mainLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(this, R.color.black));
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        deleteNPC.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onClick(View view) {
                delete("npc_table", getString(R.string.deletenpcs));
            }
        });

        deleteCity.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                //builder.setTitle(R.string.app_name);
                builder.setMessage(R.string.yousurecity);
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getApplicationContext().deleteDatabase("city_table");
                        getApplicationContext().deleteDatabase("location_table");
                        getApplicationContext().deleteDatabase("distance_table");
                        toast(getString(R.string.deletecities));
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        deleteLoot.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onClick(View view) {
                delete("loot_table", getString(R.string.deleteloot));
            }
        });

        deleteLocations.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onClick(View view) {
                delete("location_table", getString(R.string.deletelocation));
            }
        });

        deletePresets.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onClick(View view) {
                delete("presets_table", getString(R.string.resetpreset));
            }
        });

        deleteDistances.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onClick(View view) {
                delete("distance_table", getString(R.string.deletedistance));
            }
        });
    }

    private void delete(final String database, final String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.yousure);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getApplicationContext().deleteDatabase(database);
                toast(text);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
