package com.example.dmapp.cities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dmapp.R;
import com.example.dmapp.cities.distances.Distance;
import com.example.dmapp.cities.distances.DistanceAdapter;
import com.example.dmapp.cities.distances.distanceDBHelper;
import com.example.dmapp.cities.locations.locationsDBHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class CityDisplay extends AppCompatActivity{

    private final ArrayList<Distance> distanceListDisplay = new ArrayList<>();
    private DistanceAdapter distanceAdapter;
    private citiesDBHelper mCitiesDBHelper;
    private distanceDBHelper mDistanceDBHelper;
    private int addLootValue = 0;
    private String cityTitle = null;
    private final String TAG = "LootInfo";
    private Context context;
    private ExpandableListView expandableListView;

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

        mCitiesDBHelper = new citiesDBHelper(this);
        mDistanceDBHelper = new distanceDBHelper(this);
        ImageView backButton = findViewById(R.id.backButton);
        ImageView editIcon = findViewById(R.id.editIcon);
        final TextView cityName = findViewById(R.id.cityTitle);
        LinearLayout cityLayout = findViewById(R.id.cityLayout);
        LinearLayoutCompat underTitleLayout = findViewById(R.id.underTitleLayout);
        LinearLayoutCompat underTitleLayout2 = findViewById(R.id.underTitleLayout2);
        LinearLayout distanceListTitle = findViewById(R.id.distanceListTitle);
        RelativeLayout distanceListLayout = findViewById(R.id.distanceListLayout);
        ListView distanceListViewDisplay = findViewById(R.id.distanceListViewDisplay);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CityList.class);
                startActivity(intent);
                finish();
            }
        });


        //If coming to this activity from clicking on a name, get and fill out all fields with the data for the corresponding npc.
        Cursor cityInfo = mCitiesDBHelper.getSpecificCity(getIntent().getStringExtra("cityName"));
        cityInfo.moveToFirst();
        cityTitle = cityInfo.getString(1);
        cityName.setText(cityInfo.getString(1));
        cityName.setTextColor(Color.parseColor("#5BCBAE"));
        View line = new View(this);
        line.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
        line.setBackgroundColor(Color.parseColor("#F1FDF4"));
        cityLayout.addView(line);

        if(!cityInfo.getString(3).equals("Population")) {
            TextView citySize = new TextView(this);
            citySize.setText(cityInfo.getString(3));
            citySize.setTextSize(16);
            citySize.setTypeface(null, Typeface.ITALIC);
            citySize.setTextColor(Color.parseColor("#666666"));
            citySize.setBackgroundColor(Color.parseColor("#FFFFFF"));
            underTitleLayout.addView(citySize);
        }

        if((!cityInfo.getString(2).equals("Environment")) && (!cityInfo.getString(3).equals("Population"))){
            ImageView separator = new ImageView(this);
            separator.setImageDrawable(getResources().getDrawable(R.drawable.dot));
            LinearLayoutCompat.LayoutParams lparams = new LinearLayoutCompat.LayoutParams(30, 30);
            lparams.setMargins(30, 0, 30, 0);
            separator.setLayoutParams(lparams);
            underTitleLayout.addView(separator);
        }

        if(!cityInfo.getString(2).equals("Environment")) {
            TextView cityEnviron = new TextView(this);
            cityEnviron.setText(cityInfo.getString(2));
            cityEnviron.setTextSize(16);
            cityEnviron.setTypeface(null, Typeface.ITALIC);
            cityEnviron.setTextColor(Color.parseColor("#666666"));
            cityEnviron.setBackgroundColor(Color.parseColor("#FFFFFF"));
            underTitleLayout.addView(cityEnviron);
        }

        if(!cityInfo.getString(4).equals("")) {
            TextView econLabel = new TextView(this);
            econLabel.setText(R.string.economyHeader);
            econLabel.setTextSize(16);
            econLabel.setTypeface(null, Typeface.ITALIC);
            econLabel.setTextColor(Color.parseColor("#666666"));
            econLabel.setBackgroundColor(Color.parseColor("#FFFFFF"));
            underTitleLayout2.addView(econLabel);

            TextView cityEcon = new TextView(this);
            cityEcon.setText(cityInfo.getString(4));
            cityEcon.setTextSize(16);
            cityEcon.setTypeface(null, Typeface.ITALIC);
            cityEcon.setTextColor(Color.parseColor("#666666"));
            cityEcon.setBackgroundColor(Color.parseColor("#FFFFFF"));
            LinearLayoutCompat.LayoutParams lparams = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lparams.setMargins(25, 0, 0, 0);
            cityEcon.setLayoutParams(lparams);
            underTitleLayout2.addView(cityEcon);
        }

        if(!cityInfo.getString(5).equals("")) {
            TextView detailsTitle = new TextView(this);
            detailsTitle.setText(R.string.detailHeader);
            detailsTitle.setTextSize(20);
            detailsTitle.setTextColor(Color.parseColor("#FE5F55"));
            detailsTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
            detailsTitle.setTypeface(null, Typeface.BOLD);
            cityLayout.addView(detailsTitle);

            View line2 = new View(this);
            line2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
            line2.setBackgroundColor(Color.parseColor("#000000"));
            cityLayout.addView(line2);

            TextView cityDetails = new TextView(this);
            cityDetails.setText(cityInfo.getString(5));
            cityDetails.setTextSize(18);
            cityDetails.setBackgroundColor(Color.parseColor("#FFFFFF"));
            LinearLayoutCompat.LayoutParams lparams3 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lparams3.setMargins(65, 0, 0, 45);
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
                locationsTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
                locationsTitle.setTypeface(null, Typeface.BOLD);
                cityLayout.addView(locationsTitle);
                View line2 = new View(this);
                line2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
                line2.setBackgroundColor(Color.parseColor("#000000"));
                cityLayout.addView(line2);

                do {
                    locationNames.add(locationCursor.getString(2));
                    locationDesc.add(locationCursor.getString(3));
                    locationHooks.add(locationCursor.getString(4));
                    locationNotes.add(locationCursor.getString(5));
                } while (locationCursor.moveToNext());
            }
        } finally {
            locationCursor.close();
        }
        setUpAdapter();

        Cursor distanceCursor = mDistanceDBHelper.getDistances(getIntent().getStringExtra("cityName"));

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mv = inflater.inflate(R.layout.distance_item, null);
        LinearLayout distanceItem = mv.findViewById(R.id.distanceItem);
        distanceItem.setBackgroundColor(Color.WHITE);
        ImageView distanceColon = mv.findViewById(R.id.distanceColon);
        distanceColon.setBackgroundColor(Color.WHITE);
        ImageButton distanceArrow = mv.findViewById(R.id.distanceArrow);
        distanceArrow.setBackgroundColor(Color.WHITE);
        ImageButton deleteIcon = mv.findViewById(R.id.deleteIcon);
        deleteIcon.setBackgroundColor(Color.WHITE);

        try {
            if (distanceCursor.moveToNext()) {

                TextView detailsTitle = new TextView(this);
                detailsTitle.setText(R.string.travelTimeHeader);
                detailsTitle.setTextSize(20);
                detailsTitle.setTextColor(Color.parseColor("#FE5F55"));
                detailsTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
                detailsTitle.setTypeface(null, Typeface.BOLD);
                distanceListTitle.addView(detailsTitle);
                View line2 = new View(this);
                line2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
                line2.setBackgroundColor(Color.parseColor("#000000"));
                distanceListTitle.addView(line2);

                do {
                    distanceListDisplay.add(new Distance(distanceCursor.getString(2), distanceCursor.getString(3)));
                } while (distanceCursor.moveToNext());

                distanceAdapter = new DistanceAdapter(this, distanceListDisplay, cityTitle, true);
                distanceListViewDisplay.setAdapter(distanceAdapter);
            }
        } finally {
            distanceCursor.close();
        }

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int deviceHeight = (displayMetrics.heightPixels);
        int height = (int)(deviceHeight * .25);
        ViewGroup.LayoutParams params = distanceListLayout.getLayoutParams();
        params.height = height;
        distanceListLayout.setLayoutParams(params);


        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CityInfo.class);
                Log.d(TAG, "onClick: The value being passed to CityInfo is " + cityTitle);
                intent.putExtra("cityName", cityTitle);
                intent.putExtra("addCityValue", 0);
                startActivity(intent);
            }
        });
    }

    private void setUpAdapter() {

        //Retrieve all relevant information stored for each location, and set appropriate headers depending on what fields were filled out
        for(int i = 0; i < locationNames.size(); i++) {
            if(!locationDesc.get(i).equals("") && !locationHooks.get(i).equals("") && !locationNotes.get(i).equals("")) {
                String[] level2 = new String[]{"Description", "Plot Hooks", "Additional Notes"};
                secondLevel.add(level2);
                LinkedHashMap<String, String> level3 = new LinkedHashMap<>();
                level3.put(level2[0], locationDesc.get(i));
                level3.put(level2[1], locationHooks.get(i));
                level3.put(level2[2], locationNotes.get(i));
                data.add(level3);
            }
            else if(!locationDesc.get(i).equals("") && !locationHooks.get(i).equals("") && locationNotes.get(i).equals("")) {
                String[] level2 = new String[]{"Description", "Plot Hooks"};
                secondLevel.add(level2);
                LinkedHashMap<String, String> level3 = new LinkedHashMap<>();
                level3.put(level2[0], locationDesc.get(i));
                level3.put(level2[1], locationHooks.get(i));
                data.add(level3);
            }
            else if(!locationDesc.get(i).equals("") && locationHooks.get(i).equals("") && !locationNotes.get(i).equals("")) {
                String[] level2 = new String[]{"Description", "Additional Notes"};
                secondLevel.add(level2);
                LinkedHashMap<String, String> level3 = new LinkedHashMap<>();
                level3.put(level2[0], locationDesc.get(i));
                level3.put(level2[1], locationNotes.get(i));
                data.add(level3);
            }
            else if(locationDesc.get(i).equals("") && !locationHooks.get(i).equals("") && !locationNotes.get(i).equals("")) {
                String[] level2 = new String[]{"Plot Hooks", "Additional Notes"};
                secondLevel.add(level2);
                LinkedHashMap<String, String> level3 = new LinkedHashMap<>();
                level3.put(level2[0], locationHooks.get(i));
                level3.put(level2[1], locationNotes.get(i));
                data.add(level3);
            }
            else if(!locationDesc.get(i).equals("") && locationHooks.get(i).equals("") && locationNotes.get(i).equals("")) {
                String[] level2 = new String[]{"Description"};
                secondLevel.add(level2);
                LinkedHashMap<String, String> level3 = new LinkedHashMap<>();
                level3.put(level2[0], locationDesc.get(i));
                data.add(level3);
            }
            else if(locationDesc.get(i).equals("") && !locationHooks.get(i).equals("") && locationNotes.get(i).equals("")) {
                String[] level2 = new String[]{"Plot Hooks"};
                secondLevel.add(level2);
                LinkedHashMap<String, String> level3 = new LinkedHashMap<>();
                level3.put(level2[0], locationHooks.get(i));
                data.add(level3);
            }
            else if(locationDesc.get(i).equals("") && locationHooks.get(i).equals("") && !locationNotes.get(i).equals("")) {
                String[] level2 = new String[]{"Additional Notes"};
                secondLevel.add(level2);
                LinkedHashMap<String, String> level3 = new LinkedHashMap<>();
                level3.put(level2[0], locationNotes.get(i));
                data.add(level3);
            }
        }

        // Set the height of the list of locations to be 35% of the screen
        expandableListView = findViewById(R.id.expandable_listview);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int deviceHeight = (displayMetrics.heightPixels);
        int listHeight = (locationNames.size() * 150) + 250;
        ViewGroup.LayoutParams params = expandableListView.getLayoutParams();
        params.height = listHeight;
        expandableListView.setLayoutParams(params);

        //passing three levels of information to constructor
        ThreeLevelListAdapter threeLevelListAdapterAdapter = new ThreeLevelListAdapter(this, locationNames, secondLevel, data);
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
                    int listHeight = (locationNames.size() * 150) + ((level2.length) * 150) + 250;
                    ExpandableListView expandableListView = ((CityDisplay) context).findViewById(R.id.expandable_listview);
                    ViewGroup.LayoutParams params = expandableListView.getLayoutParams();
                    params.height = listHeight;
                    expandableListView.setLayoutParams(params);
                }
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                int listHeight = (locationNames.size() * 150) + 250;
                ViewGroup.LayoutParams params = expandableListView.getLayoutParams();
                params.height = listHeight;
                expandableListView.setLayoutParams(params);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, CityList.class);
        startActivity(intent);
        finish();
    }
}