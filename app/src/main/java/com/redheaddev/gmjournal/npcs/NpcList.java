package com.redheaddev.gmjournal.npcs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.LocaleList;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.redheaddev.gmjournal.MainActivity;
import com.redheaddev.gmjournal.cities.citiesDBHelper;
import com.redheaddev.gmjournal.MyRecyclerViewAdapter;
import com.redheaddev.gmjournal.R;

import java.util.ArrayList;
import java.util.Locale;

public class NpcList extends AppCompatActivity implements MyRecyclerViewAdapter.OnNoteListener {

    private MyRecyclerViewAdapter adapter;
    private final ArrayList<String> npcList = new ArrayList<>();
    private final ArrayList<String> locations = new ArrayList<>();
    private final String TAG = "NPCInfo";
    private String filterChoice;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.npc_titles);

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String theme = sharedPreferences.getString("Theme", "none");
        Boolean darkMode = theme.equals("dark");

        Configuration config = getBaseContext().getResources().getConfiguration();
        Log.d(TAG, "setLocale: New config is " + config);

        context = this;
        LinearLayoutCompat layout = findViewById(R.id.npc_layout);
        LinearLayoutCompat headerLayout = findViewById(R.id.headerLayout);
        RelativeLayout innerLayout = findViewById(R.id.innerLayout);
        RecyclerView recyclerView = findViewById(R.id.info_content);
        ImageView backButton = findViewById(R.id.backButton);
        final Spinner filter = findViewById(R.id.filter);
        ImageView addEntry = findViewById(R.id.addEntry);
        TextView npcHeader = findViewById(R.id.npcHeader);

        final npcDBHelper mNpcDBHelper = new npcDBHelper(this);
        final citiesDBHelper mcitiesDBHelper = new citiesDBHelper(this);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int deviceHeight = (displayMetrics.heightPixels);

        String headerText1 = sharedPreferences.getString("headerText1", "none");
        String headerColor1 = sharedPreferences.getString("headerColor1", "none");
        if(!headerText1.equals("none")) npcHeader.setText(headerText1);
        if(!headerColor1.equals("none")) npcHeader.setTextColor(Color.parseColor(headerColor1));

        String localeText = sharedPreferences.getString("Locale", "none");
        String loadLocale = String.valueOf(getResources().getConfiguration().locale);
        if(!localeText.equals(loadLocale)){
            Locale locale = new Locale(localeText);
            updateLocale(locale);
        }

        if (theme.equals("dark")){

            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(addEntry.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));

            layout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            innerLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            npcHeader.setBackgroundColor(Color.parseColor("#2C2C2C"));
            headerLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            addEntry.setBackgroundColor(Color.parseColor("#2C2C2C"));
            backButton.setBackgroundColor(Color.parseColor("#2C2C2C"));
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(addEntry.getDrawable()), ContextCompat.getColor(context, R.color.black));
        }

        // set up the RecyclerView with data from the database
        Cursor npcs = mNpcDBHelper.getNPCS();
        try {
            if (npcs.moveToNext()) {
                do {
                    npcList.add(npcs.getString(1));
                } while (npcs.moveToNext());
            }
            else{
                TextView noNPC = new TextView(this);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,100,0,0);
                noNPC.setLayoutParams(params);
                noNPC.setText(R.string.nonpctext);
                noNPC.setGravity(Gravity.CENTER_HORIZONTAL);
                noNPC.setTextSize(20);
                noNPC.setTypeface(null, Typeface.ITALIC);
                noNPC.setTextColor(Color.parseColor("#666666"));
                innerLayout.addView(noNPC);
            }
        } finally {
            npcs.close();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        //if (theme.equals("dark")) itemDecor.setDrawable(new ColorDrawable(getResources().getColor(R.color.mainBgColor)));
        recyclerView.addItemDecoration(itemDecor);
        adapter = new MyRecyclerViewAdapter(this, npcList, this, darkMode);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(itemDecor);

        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = (int)(deviceHeight * .70);
        recyclerView.setLayoutParams(params);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        addEntry.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), NpcInfo.class);
                startActivity(intent);
                finish();
            }
        });

        locations.add(getString(R.string.filterlocation));

        Cursor cities = mcitiesDBHelper.getCities();
        try {
            if (cities.moveToNext()) {
                do {
                    locations.add(cities.getString(1));
                } while (cities.moveToNext());
            }
        } finally {
            locations.add(getString(R.string.nolocation));
            cities.close();
        }

        final ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locations);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filter.setAdapter(locationAdapter);

        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterChoice = locations.get(position);
                Log.d(TAG, "onItemSelected: Value of filterChoice is: " + filterChoice);

                if ((!locations.get(position).equals(getString(R.string.filterlocation))) && (!locations.get(position).equals(getString(R.string.clearfilter)))) {
                    npcList.clear();
                    locationAdapter.getDropDownView(position, view, parent);
                    if (!(locations.contains(getString(R.string.clearfilter)))) {
                        locations.add(getString(R.string.clearfilter));
                    }
                    npcDBHelper mNpcDBHelper = new npcDBHelper(filter.getContext());
                    Cursor npcs = mNpcDBHelper.getLocationNPCS(filterChoice);
                    try {
                        if (npcs.moveToNext()) {
                            do {
                                npcList.add(npcs.getString(1));
                            } while (npcs.moveToNext());
                        }
                    } finally {
                        npcs.close();
                    }

                    adapter.notifyDataSetChanged();

                } else if (locations.get(position).equals(getString(R.string.clearfilter))) {
                    npcList.clear();
                    npcDBHelper mNpcDBHelper = new npcDBHelper(filter.getContext());
                    Cursor npcs = mNpcDBHelper.getNPCS();
                    try {
                        if (npcs.moveToNext()) {
                            do {
                                npcList.add(npcs.getString(1));
                            } while (npcs.moveToNext());
                        }
                        adapter.notifyDataSetChanged();
                    } finally {
                        npcs.close();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                Log.i("GTOUTOUT", "Nothing Selected");
            }
        });
    }

    //When resuming the activity (going back from NPCInfo, refresh the view with the names
    @Override
    protected void onResume() {
        super.onResume();
        npcList.clear();
        npcDBHelper mNpcDBHelper = new npcDBHelper(this);
        Cursor npcs = mNpcDBHelper.getNPCS();

        if (npcs.moveToNext()) {
            do {
                npcList.add(npcs.getString(1));
            } while (npcs.moveToNext());
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onNoteClick(int position) {
        //If I want a reference of the list item I clicked on
        //Pass in the position as an extra to the new activity you'll create
        String npcName = npcList.get(position);
        Intent intent = new Intent(this, NpcDisplay.class);
        intent.putExtra("npcName", npcName);
        intent.putExtra("addNPCValue", 0);
        startActivity(intent);
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

