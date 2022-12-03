package com.redheaddev.gmjournal.loot;

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
import com.redheaddev.gmjournal.MyRecyclerViewAdapter;
import com.redheaddev.gmjournal.R;
import com.redheaddev.gmjournal.npcs.npcDBHelper;

import java.util.ArrayList;
import java.util.Locale;

public class LootList extends AppCompatActivity implements MyRecyclerViewAdapter.OnNoteListener {

    private MyRecyclerViewAdapter adapter;
    private final ArrayList<String> lootList = new ArrayList<>();
    private final ArrayList<String> groups = new ArrayList<>();
    private final String TAG = "LootInfo";
    private String filterChoice;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loot_titles);

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String theme = sharedPreferences.getString("Theme", "none");
        Boolean darkMode = theme.equals("dark");

        context = this;
        LinearLayoutCompat layout = findViewById(R.id.loot_layout);
        LinearLayoutCompat headerLayout = findViewById(R.id.headerLayout);
        RelativeLayout innerLayout = findViewById(R.id.innerLayout);
        final Spinner groupFilter = findViewById(R.id.groupFilter);
        ImageView backButton = findViewById(R.id.backButton);
        TextView listTitle = findViewById(R.id.listTitle);
        ImageView addEntry = findViewById(R.id.addEntry);
        RecyclerView recyclerView = findViewById(R.id.info_content);

        lootDBHelper mLootDBHelper = new lootDBHelper(this);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int deviceHeight = (displayMetrics.heightPixels);

        if (theme.equals("dark")){

            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(addEntry.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));

            layout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            innerLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            listTitle.setBackgroundColor(Color.parseColor("#2C2C2C"));
            headerLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            addEntry.setBackgroundColor(Color.parseColor("#2C2C2C"));
            backButton.setBackgroundColor(Color.parseColor("#2C2C2C"));
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(addEntry.getDrawable()), ContextCompat.getColor(context, R.color.black));
        }

        String localeText = sharedPreferences.getString("Locale", "none");
        String loadLocale = String.valueOf(getResources().getConfiguration().locale);
        if(!localeText.equals(loadLocale)){
            Locale locale = new Locale(localeText);
            updateLocale(locale);
        }

        // set up the RecyclerView with data from the database
        Cursor loot = mLootDBHelper.getLoot();

        groups.add(getString(R.string.filtergroup));

        try {
            if (loot.moveToNext()) {
                do {
                    lootList.add(loot.getString(1));
                    if (loot.getString(8) != null && !groups.contains(loot.getString(8)) && !loot.getString(8).equals("")){
                        groups.add(loot.getString(8));
                    }
                } while (loot.moveToNext());
            } else{
                TextView noLoot = new TextView(this);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,100,0,0);
                noLoot.setLayoutParams(params);
                noLoot.setText(R.string.noloottext);
                noLoot.setGravity(Gravity.CENTER_HORIZONTAL);
                noLoot.setTextSize(20);
                noLoot.setTypeface(null, Typeface.ITALIC);
                noLoot.setTextColor(Color.parseColor("#666666"));
                innerLayout.addView(noLoot);
            }
        } finally {
            if (!groups.contains("No Group")) groups.add(getString(R.string.nogroup));
            loot.close();
        }


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, lootList, this, darkMode);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = (int)(deviceHeight * .70);
        recyclerView.setLayoutParams(params);

        String headerText4 = sharedPreferences.getString("headerText4", "none");
        String headerColor4 = sharedPreferences.getString("headerColor4", "none");

        if(!headerText4.equals("none")) listTitle.setText(headerText4);
        else listTitle.setText(R.string.loot);
        if(!headerColor4.equals("none")) listTitle.setTextColor(Color.parseColor(headerColor4));
        else listTitle.setTextColor(Color.parseColor("#6F94F8"));

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
                Intent intent = new Intent(view.getContext(), LootInfo.class);
                startActivity(intent);
                finish();
            }
        });

        final ArrayAdapter<String> groupAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, groups);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupFilter.setAdapter(groupAdapter);

        groupFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterChoice = groups.get(position);
                Log.d(TAG, "onItemSelected: Value of filterChoice is: " + filterChoice);

                if ((!groups.get(position).equals(getString(R.string.filtergroup))) && (!groups.get(position).equals(getString(R.string.clearfilter)))) {
                    lootList.clear();
                    groupAdapter.getDropDownView(position, view, parent);
                    if (!(groups.contains(getString(R.string.clearfilter)))) {
                        groups.add(getString(R.string.clearfilter));
                    }
                    lootDBHelper mLootDBHelper = new lootDBHelper(context);
                    Cursor loot = mLootDBHelper.getLootGroup(filterChoice);
                    try {
                        if (loot.moveToNext()) {
                            do {
                                lootList.add(loot.getString(1));
                            } while (loot.moveToNext());
                        }
                    } finally {
                        loot.close();
                    }

                    adapter.notifyDataSetChanged();

                } else if (groups.get(position).equals(getString(R.string.clearfilter))) {
                    lootList.clear();
                    lootDBHelper mLootDBHelper = new lootDBHelper(context);
                    Cursor loot = mLootDBHelper.getLoot();
                    try {
                        if (loot.moveToNext()) {
                            do {
                                lootList.add(loot.getString(1));
                            } while (loot.moveToNext());
                        }
                        adapter.notifyDataSetChanged();
                    } finally {
                        loot.close();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                Log.i("GTOUTOUT", "Nothing Selected");
            }
        });

        Boolean groupClick = getIntent().getBooleanExtra("groupClick", false);
        if (groupClick){
            String groupName = getIntent().getStringExtra("groupName");
            groupFilter.setSelection(groupAdapter.getPosition(groupName));
        }
    }

    //When resuming the activity (going back from LootInfo, refresh the view with the names
    @Override
    protected void onResume() {
        super.onResume();
        lootList.clear();
        lootDBHelper mLootDBHelper = new lootDBHelper(this);
        Cursor loot = mLootDBHelper.getLoot();
        try {
            if (loot.moveToNext()) {
                do {
                    lootList.add(loot.getString(1));
                } while (loot.moveToNext());
            }
        } finally {
            loot.close();
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        Boolean groupClick = getIntent().getBooleanExtra("groupClick", false);

        if (groupClick) super.onBackPressed();
        else {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onNoteClick(int position) {
        //If I want a reference of the list item I clicked on
        //Pass in the position as an extra to the new activity you'll create
        String lootName = lootList.get(position);
        Log.d(TAG, "onCreate: The value of lootTitle is " + lootName);
        Intent intent = new Intent(this, LootDisplay.class);
        intent.putExtra("lootName", lootName);
        intent.putExtra("addLootValue", 0);
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

