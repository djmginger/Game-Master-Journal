package com.redheaddev.gmjournal.cities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.redheaddev.gmjournal.MyRecyclerViewAdapter;
import com.redheaddev.gmjournal.R;
import com.redheaddev.gmjournal.cities.distances.Distance;
import com.redheaddev.gmjournal.cities.distances.DistanceAdapter;
import com.redheaddev.gmjournal.cities.distances.distanceDBHelper;
import com.redheaddev.gmjournal.cities.locations.locationsDBHelper;
import com.redheaddev.gmjournal.npcs.NpcDisplay;
import com.redheaddev.gmjournal.npcs.npcDBHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CityDisplay extends AppCompatActivity implements MyRecyclerViewAdapter.OnNoteListener{

    private final ArrayList<Distance> distanceListDisplay = new ArrayList<>();
    private DistanceAdapter distanceAdapter;
    private citiesDBHelper mCitiesDBHelper;
    private npcDBHelper mNpcDBHelper;
    private distanceDBHelper mDistanceDBHelper;
    private int addLootValue = 0;
    private String cityTitle = null;
    private final String TAG = "LootInfo";
    private Context context;
    private ExpandableListView expandableListView;
    private final ArrayList<String> npcList = new ArrayList<>();
    private Boolean darkMode;

    locationsDBHelper mLocationsDBHelper = new locationsDBHelper(this);
    ArrayList<String> locationNames = new ArrayList<>(); // parent of the exapandable list
    ArrayList<String> locationDesc = new ArrayList<>();
    ArrayList<String> locationHooks = new ArrayList<>();
    ArrayList<String> locationNotes = new ArrayList<>();

    List<String[]> secondLevel = new ArrayList<>();
    List<LinkedHashMap<String, String>> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_display);
        context = this;

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String theme = sharedPreferences.getString("Theme", "none");
        darkMode = theme.equals("dark");

        mCitiesDBHelper = new citiesDBHelper(this);
        mNpcDBHelper = new npcDBHelper(this);
        mDistanceDBHelper = new distanceDBHelper(this);
        ImageView backButton = findViewById(R.id.backButton);
        ImageView editIcon = findViewById(R.id.editIcon);
        final TextView cityName = findViewById(R.id.cityTitle);
        NestedScrollView cityMainLayout = findViewById(R.id.cityDisplayMainLayout);
        LinearLayout cityLayout = findViewById(R.id.cityLayout);
        LinearLayoutCompat underTitleLayout = findViewById(R.id.underTitleLayout);
        LinearLayoutCompat underTitleLayout2 = findViewById(R.id.underTitleLayout2);
        LinearLayoutCompat underTitleLayout3 = findViewById(R.id.underTitleLayout3);
        LinearLayout distanceListTitle = findViewById(R.id.distanceListTitle);
        RelativeLayout distanceListLayout = findViewById(R.id.distanceListLayout);
        ListView distanceListViewDisplay = findViewById(R.id.distanceListViewDisplay);

        if (theme.equals("dark")){

            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(editIcon.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));

            cityLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            cityMainLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            underTitleLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            underTitleLayout2.setBackgroundColor(Color.parseColor("#2C2C2C"));
            underTitleLayout3.setBackgroundColor(Color.parseColor("#2C2C2C"));
            distanceListTitle.setBackgroundColor(Color.parseColor("#2C2C2C"));
            distanceListLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            distanceListViewDisplay.setBackgroundColor(Color.parseColor("#2C2C2C"));
            backButton.setBackgroundColor(Color.parseColor("#2C2C2C"));
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(editIcon.getDrawable()), ContextCompat.getColor(context, R.color.black));
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CityList.class);
                startActivity(intent);
                finish();
            }
        });

        //If coming to this activity from clicking on a name, get and fill out all fields with the data for the corresponding npc.
        Log.d(TAG, "onCreate: City name is " +getIntent().getStringExtra("cityName"));
        Cursor cityInfo = mCitiesDBHelper.getSpecificCity(getIntent().getStringExtra("cityName"));
        cityInfo.moveToFirst();
        cityTitle = cityInfo.getString(1);
        cityName.setText(cityInfo.getString(1));
        cityName.setTextColor(Color.parseColor("#6FD8F8"));

        if(!cityInfo.getString(3).equals("Population") && (!cityInfo.getString(3).equals("None"))) {
            TextView popLabel = new TextView(this);
            popLabel.setText(String.format("%s %s", getString(R.string.poplabel), cityInfo.getString(3)));
            popLabel.setTextSize(16);
            popLabel.setTypeface(null, Typeface.ITALIC);
            if (!darkMode) {
                popLabel.setTextColor(Color.parseColor("#666666"));
                popLabel.setBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                popLabel.setTextColor(Color.parseColor("#FFFFFF"));
                popLabel.setBackgroundColor(Color.parseColor("#2C2C2C"));
            }
            underTitleLayout.addView(popLabel);
        }

        if(!cityInfo.getString(2).equals("Environment") && (!cityInfo.getString(2).equals("None"))) {
            TextView envLabel = new TextView(this);
            envLabel.setText(String.format("%s %s", getString(R.string.envlabel), cityInfo.getString(2)));
            envLabel.setTextSize(16);
            envLabel.setTypeface(null, Typeface.ITALIC);
            if (!darkMode) {
                envLabel.setTextColor(Color.parseColor("#666666"));
                envLabel.setBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                envLabel.setTextColor(Color.parseColor("#FFFFFF"));
                envLabel.setBackgroundColor(Color.parseColor("#2C2C2C"));
            }
            underTitleLayout2.addView(envLabel);
        }


        if(!cityInfo.getString(4).equals("") && !cityInfo.getString(4).equals("Economy") && (!cityInfo.getString(4).equals("None"))) {
            TextView econLabel = new TextView(this);
            econLabel.setText(String.format("%s %s", getString(R.string.economyHeader), cityInfo.getString(4)));
            econLabel.setTextSize(16);
            econLabel.setTypeface(null, Typeface.ITALIC);
            if (!darkMode) {
                econLabel.setTextColor(Color.parseColor("#666666"));
                econLabel.setBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                econLabel.setTextColor(Color.parseColor("#FFFFFF"));
                econLabel.setBackgroundColor(Color.parseColor("#2C2C2C"));
            }
            underTitleLayout3.addView(econLabel);
        }

        if(!cityInfo.getString(5).equals("")) {
            TextView detailsTitle = new TextView(this);
            detailsTitle.setText(R.string.detailHeader);
            detailsTitle.setTextSize(20);
            detailsTitle.setTextColor(Color.parseColor("#FE5F55"));
            if (!darkMode) detailsTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
                else detailsTitle.setBackgroundColor(Color.parseColor("#2C2C2C"));
            detailsTitle.setTypeface(null, Typeface.BOLD);
            cityLayout.addView(detailsTitle);

            View line2 = new View(this);
            line2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
            if (!darkMode) line2.setBackgroundColor(Color.parseColor("#8C8C8C"));
                else line2.setBackgroundColor(Color.parseColor("#FFFFFF"));
            cityLayout.addView(line2);

            TextView cityDetails = new TextView(this);
            cityDetails.setText(cityInfo.getString(5));
            cityDetails.setTextSize(18);
            if (!darkMode) {
                cityDetails.setTextColor(Color.parseColor("#666666"));
                cityDetails.setBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                cityDetails.setTextColor(Color.parseColor("#FFFFFF"));
                cityDetails.setBackgroundColor(Color.parseColor("#2C2C2C"));
            }
            LinearLayoutCompat.LayoutParams lparams3 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lparams3.setMargins(30, 0, 0, 45);
            cityDetails.setLayoutParams(lparams3);
            cityLayout.addView(cityDetails);
        }

        Cursor locationCursor = mLocationsDBHelper.getLocations(cityTitle);

        //if there is at least 1 location listed for the current city, create arraylists for each variable of each city, and then call the adapter that creates the expandable list view
        try {
            if (locationCursor.moveToNext()) {

                TextView locationsTitle = new TextView(this);
                locationsTitle.setText(R.string.noteableLocations);
                locationsTitle.setTextSize(20);
                locationsTitle.setTextColor(Color.parseColor("#FE5F55"));
                if (!darkMode) locationsTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    else locationsTitle.setBackgroundColor(Color.parseColor("#2C2C2C"));
                locationsTitle.setTypeface(null, Typeface.BOLD);
                cityLayout.addView(locationsTitle);
                View line2 = new View(this);
                line2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
                if (!darkMode) line2.setBackgroundColor(Color.parseColor("#8C8C8C"));
                    else line2.setBackgroundColor(Color.parseColor("#FFFFFF"));
                cityLayout.addView(line2);

                do {
                    locationNames.add(locationCursor.getString(2));
                    locationDesc.add(locationCursor.getString(3));
                    locationHooks.add(locationCursor.getString(4));
                    locationNotes.add(locationCursor.getString(5));
                } while (locationCursor.moveToNext());

                setUpAdapter();
                LinearLayout expandableListViewContainer = findViewById(R.id.expandable_listview_container);
                if (!darkMode) expandableListViewContainer.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    else expandableListViewContainer.setBackgroundColor(Color.parseColor("#2C2C2C"));
                View line3 = new View(this);
                line3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
                if (!darkMode) line3.setBackgroundColor(Color.parseColor("#8C8C8C"));
                    else line3.setBackgroundColor(Color.parseColor("#FFFFFF"));
                expandableListViewContainer.addView(line3);
            }
        } finally {
            locationCursor.close();
        }

        Cursor distanceCursor = mDistanceDBHelper.getDistances(getIntent().getStringExtra("cityName"));

        try {
            if (distanceCursor.moveToNext()) {

                TextView detailsTitle = new TextView(this);
                detailsTitle.setText(R.string.travelTimeHeader);
                detailsTitle.setTextSize(20);
                detailsTitle.setTextColor(Color.parseColor("#FE5F55"));
                if (!darkMode) detailsTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    else detailsTitle.setBackgroundColor(Color.parseColor("#2C2C2C"));
                detailsTitle.setTypeface(null, Typeface.BOLD);
                distanceListTitle.addView(detailsTitle);
                View line2 = new View(this);
                line2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
                if (!darkMode) line2.setBackgroundColor(Color.parseColor("#8C8C8C"));
                    else line2.setBackgroundColor(Color.parseColor("#FFFFFF"));
                distanceListTitle.addView(line2);

                do {
                    distanceListDisplay.add(new Distance(distanceCursor.getString(2), distanceCursor.getString(3)));
                } while (distanceCursor.moveToNext());

                distanceAdapter = new DistanceAdapter(this, distanceListDisplay, cityTitle, true, darkMode);
                distanceListViewDisplay.setAdapter(distanceAdapter);
            }
        } finally {
            distanceCursor.close();
        }

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        final int deviceHeight = (displayMetrics.heightPixels);
        final double scale;
        if (deviceHeight < 2600) scale = .07;
        else scale = .06;
        double scaleHeight = distanceListDisplay.size() * scale;
        int height = (int) (deviceHeight * scaleHeight);
        //int height = distanceListDisplay.size() * 155;
        ViewGroup.LayoutParams params = distanceListLayout.getLayoutParams();
        params.height = height;
        distanceListLayout.setLayoutParams(params);

        //create a list of NPC's associated with the city

        Cursor npcs = mNpcDBHelper.getLocationNPCS(cityTitle);
        try {
            if (npcs.moveToNext()) {
                LinearLayout npcListTitleLayout = findViewById(R.id.npcListTitleLayout);
                TextView npcTitle = new TextView(this);
                npcTitle.setText(String.format("%s%s", getString(R.string.npclisttitle), cityTitle));
                npcTitle.setTextSize(20);
                npcTitle.setTextColor(Color.parseColor("#FE5F55"));
                if (!darkMode) npcTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    else npcTitle.setBackgroundColor(Color.parseColor("#2C2C2C"));
                npcTitle.setTypeface(null, Typeface.BOLD);
                npcListTitleLayout.addView(npcTitle);
                View line2 = new View(this);
                line2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
                if (!darkMode) line2.setBackgroundColor(Color.parseColor("#8C8C8C"));
                    else line2.setBackgroundColor(Color.parseColor("#FFFFFF"));
                npcListTitleLayout.addView(line2);

                do {
                    npcList.add(npcs.getString(1));
                } while (npcs.moveToNext());
            }
        } finally {
            npcs.close();
        }

        RecyclerView recyclerView = findViewById(R.id.npcListLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(this, npcList, this, darkMode); // change this later
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(itemDecor);

        ViewGroup.LayoutParams params2 = recyclerView.getLayoutParams();
        params2.height = 165 * npcList.size();
        recyclerView.setLayoutParams(params2);

        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CityInfo.class);
                Log.d(TAG, "onClick: The value being passed to CityInfo is " + cityTitle);
                intent.putExtra("cityName", cityTitle);
                intent.putExtra("addCityValue", 0);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setUpAdapter() {

        //Retrieve all relevant information stored for each location, and set appropriate headers depending on what fields were filled out
        for(int i = 0; i < locationNames.size(); i++) {
            if(!locationDesc.get(i).equals("") && !locationHooks.get(i).equals("") && !locationNotes.get(i).equals("")) {
                String[] level2 = new String[]{getString(R.string.desc), getString(R.string.plothooks), getString(R.string.addnotes)};
                secondLevel.add(level2);
                LinkedHashMap<String, String> level3 = new LinkedHashMap<>();
                level3.put(level2[0], locationDesc.get(i));
                level3.put(level2[1], locationHooks.get(i));
                level3.put(level2[2], locationNotes.get(i));
                data.add(level3);
            }
            else if(!locationDesc.get(i).equals("") && !locationHooks.get(i).equals("") && locationNotes.get(i).equals("")) {
                String[] level2 = new String[]{getString(R.string.desc), getString(R.string.plothooks)};
                secondLevel.add(level2);
                LinkedHashMap<String, String> level3 = new LinkedHashMap<>();
                level3.put(level2[0], locationDesc.get(i));
                level3.put(level2[1], locationHooks.get(i));
                data.add(level3);
            }
            else if(!locationDesc.get(i).equals("") && locationHooks.get(i).equals("") && !locationNotes.get(i).equals("")) {
                String[] level2 = new String[]{getString(R.string.desc), getString(R.string.addnotes)};
                secondLevel.add(level2);
                LinkedHashMap<String, String> level3 = new LinkedHashMap<>();
                level3.put(level2[0], locationDesc.get(i));
                level3.put(level2[1], locationNotes.get(i));
                data.add(level3);
            }
            else if(locationDesc.get(i).equals("") && !locationHooks.get(i).equals("") && !locationNotes.get(i).equals("")) {
                String[] level2 = new String[]{getString(R.string.plothooks), getString(R.string.addnotes)};
                secondLevel.add(level2);
                LinkedHashMap<String, String> level3 = new LinkedHashMap<>();
                level3.put(level2[0], locationHooks.get(i));
                level3.put(level2[1], locationNotes.get(i));
                data.add(level3);
            }
            else if(!locationDesc.get(i).equals("") && locationHooks.get(i).equals("") && locationNotes.get(i).equals("")) {
                String[] level2 = new String[]{getString(R.string.desc)};
                secondLevel.add(level2);
                LinkedHashMap<String, String> level3 = new LinkedHashMap<>();
                level3.put(level2[0], locationDesc.get(i));
                data.add(level3);
            }
            else if(locationDesc.get(i).equals("") && !locationHooks.get(i).equals("") && locationNotes.get(i).equals("")) {
                String[] level2 = new String[]{getString(R.string.plothooks)};
                secondLevel.add(level2);
                LinkedHashMap<String, String> level3 = new LinkedHashMap<>();
                level3.put(level2[0], locationHooks.get(i));
                data.add(level3);
            }
            else if(locationDesc.get(i).equals("") && locationHooks.get(i).equals("") && !locationNotes.get(i).equals("")) {
                String[] level2 = new String[]{getString(R.string.addnotes)};
                secondLevel.add(level2);
                LinkedHashMap<String, String> level3 = new LinkedHashMap<>();
                level3.put(level2[0], locationNotes.get(i));
                data.add(level3);
            }
            else {
                String[] level2 = new String[]{""};
                secondLevel.add(level2);
                LinkedHashMap<String, String> level3 = new LinkedHashMap<>();
                level3.put(level2[0], "");
                data.add(level3);
            }
        }

        // Set the height of the list of locations to be 35% of the screen
        expandableListView = findViewById(R.id.expandable_listview);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        final int deviceHeight = (displayMetrics.heightPixels);
        final double scale;
        if (deviceHeight < 2600) scale = .07;
        else scale = .06;
        //int listHeight = (locationNames.size() * scale);
        Log.d(TAG, "setUpAdapter: Device Height is " + deviceHeight);
        double listScale = locationNames.size() * scale;
        final int listHeight = (int) (deviceHeight * listScale);
        ViewGroup.LayoutParams params = expandableListView.getLayoutParams();
        params.height = listHeight;
        expandableListView.setLayoutParams(params);

        //passing three levels of information to constructor
        ThreeLevelListAdapter threeLevelListAdapterAdapter = new ThreeLevelListAdapter(this, locationNames, secondLevel, data, darkMode);
        expandableListView.setAdapter(threeLevelListAdapterAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    expandableListView.collapseGroup(previousGroup);
                previousGroup = groupPosition;

                boolean hasSecondLevel = true;
                String[] headers;
                try {
                    headers = secondLevel.get(groupPosition);
                } catch (IndexOutOfBoundsException e){
                    hasSecondLevel = false;
                    System.out.println("No children");
                };
                if (hasSecondLevel) {

                    String[] level2 = secondLevel.get(groupPosition);
                    Log.d(TAG, "onGroupExpand: secondLevel size is " + level2.length);
                    //int listHeight = (locationNames.size() * 150) + ((level2.length) * 150) + 150;
                    double listScale = (locationNames.size() * scale) + ((level2.length) * scale + (scale * .5));
                    int listHeight = (int) (deviceHeight * listScale);
                    ExpandableListView expandableListView = ((CityDisplay) context).findViewById(R.id.expandable_listview);
                    ViewGroup.LayoutParams params = expandableListView.getLayoutParams();
                    params.height = listHeight;
                    expandableListView.setLayoutParams(params);
                }
            }
        });

        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                if ((secondLevel.get(i)[0]).equals("")){
                    return true;
                }
                return false;
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                //int listHeight = (locationNames.size() * 150) + 150;
                double listScale = locationNames.size() * scale;
                final int listHeight = (int) (deviceHeight * listScale);
                ViewGroup.LayoutParams params = expandableListView.getLayoutParams();
                params.height = listHeight;
                expandableListView.setLayoutParams(params);
            }
        });
    }

    @Override
    public void onNoteClick(int position) {
        //If I want a reference of the list item I clicked on
        //Pass in the position as an extra to the new activity you'll create
        String npcName = npcList.get(position);
        Intent intent = new Intent(this, NpcDisplay.class);
        intent.putExtra("npcName", npcName);
        intent.putExtra("addNPCValue", 0);
        intent.putExtra("cityNav", true);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Boolean npcNav = getIntent().getBooleanExtra("npcNav", false);
        Boolean miscNav = getIntent().getBooleanExtra("miscNav", false);
        if(npcNav || miscNav){
            super.onBackPressed();
        } else {
            Intent intent = new Intent(context, CityList.class);
            startActivity(intent);
            finish();
        }
    }
}