package com.redheaddev.gmjournal.misc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
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
import com.redheaddev.gmjournal.loot.LootDisplay;
import com.redheaddev.gmjournal.loot.LootInfo;
import com.redheaddev.gmjournal.loot.lootDBHelper;

import java.util.ArrayList;

public class MiscList extends AppCompatActivity implements MyRecyclerViewAdapter.OnNoteListener {

    private MyRecyclerViewAdapter adapter;
    private final ArrayList<String> miscList = new ArrayList<>();
    private final ArrayList<String> groups = new ArrayList<>();
    private final String TAG = "MiscInfo";
    private String filterChoice;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.misc_titles);

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String theme = sharedPreferences.getString("Theme", "none");
        Boolean darkMode = theme.equals("dark");

        context = this;
        LinearLayoutCompat layout = findViewById(R.id.misc_layout);
        LinearLayoutCompat headerLayout = findViewById(R.id.headerLayout);
        RelativeLayout innerLayout = findViewById(R.id.innerLayout);
        final Spinner groupFilter = findViewById(R.id.groupFilter);
        ImageView backButton = findViewById(R.id.backButton);
        TextView listTitle = findViewById(R.id.listTitle);
        ImageView addEntry = findViewById(R.id.addEntry);
        RecyclerView recyclerView = findViewById(R.id.info_content);

        miscDBHelper mMiscDBHelper = new miscDBHelper(this);
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

        // set up the RecyclerView with data from the database
        Cursor misc = mMiscDBHelper.getMisc();
        groups.add(getString(R.string.filtergroup2));

        try {
            if (misc.moveToNext()) {
                do {
                    Log.d(TAG, "onCreate: " + misc.getString(1) + " in is the Misc DB");
                    miscList.add(misc.getString(1));
                    if (misc.getString(11) != null && !groups.contains(misc.getString(11)) && !misc.getString(11).equals("")){
                        groups.add(misc.getString(11));
                    }
                } while (misc.moveToNext());
            } else{
                TextView noMisc = new TextView(this);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,100,0,0);
                noMisc.setLayoutParams(params);
                noMisc.setText(R.string.noentrytext);
                noMisc.setGravity(Gravity.CENTER_HORIZONTAL);
                noMisc.setTextSize(20);
                noMisc.setTypeface(null, Typeface.ITALIC);
                noMisc.setTextColor(Color.parseColor("#666666"));
                innerLayout.addView(noMisc);
            }
        } finally {
            if (!groups.contains("No Group")) groups.add(getString(R.string.nogroup));
            misc.close();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, miscList, this, darkMode);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = (int)(deviceHeight * .70);
        recyclerView.setLayoutParams(params);

        String miscName = sharedPreferences.getString("miscName", "none");
        if (!miscName.equals("none")) listTitle.setText(miscName);
        else listTitle.setText(R.string.misc);
        listTitle.setTextColor(Color.parseColor("#8f6ff8"));

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
                Intent intent = new Intent(view.getContext(), MiscInfo.class);
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

                if ((!groups.get(position).equals(getString(R.string.filtergroup2))) && (!groups.get(position).equals(getString(R.string.clearfilter)))) {
                    miscList.clear();
                    groupAdapter.getDropDownView(position, view, parent);
                    if (!(groups.contains(getString(R.string.clearfilter)))) {
                        groups.add(getString(R.string.clearfilter));
                    }
                    miscDBHelper mMiscDBHelper = new miscDBHelper(context);
                    Cursor miscGroup = mMiscDBHelper.getMiscGroup(filterChoice);
                    try {
                        if (miscGroup.moveToNext()) {
                            do {
                                miscList.add(miscGroup.getString(1));
                            } while (miscGroup.moveToNext());
                        }
                    } finally {
                        miscGroup.close();
                    }

                    adapter.notifyDataSetChanged();

                } else if (groups.get(position).equals(getString(R.string.clearfilter))) {
                    miscList.clear();
                    miscDBHelper mMiscDBHelper = new miscDBHelper(context);
                    Cursor miscGroup = mMiscDBHelper.getMisc();
                    try {
                        if (miscGroup.moveToNext()) {
                            do {
                                miscList.add(miscGroup.getString(1));
                            } while (miscGroup.moveToNext());
                        }
                        adapter.notifyDataSetChanged();
                    } finally {
                        miscGroup.close();
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
        miscList.clear();
        miscDBHelper mMiscDBHelper = new miscDBHelper(this);
        Cursor misc = mMiscDBHelper.getMisc();
        try {
            if (misc.moveToNext()) {
                do {
                    miscList.add(misc.getString(1));
                } while (misc.moveToNext());
            }
        } finally {
            misc.close();
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        boolean groupClick = getIntent().getBooleanExtra("groupClick", false);
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
        String miscName = miscList.get(position);
        Log.d(TAG, "onCreate: The value of miscTitle is " + miscName);
        Intent intent = new Intent(this, MiscDisplay.class);
        intent.putExtra("miscName", miscName);
        intent.putExtra("addMiscValue", 0);
        startActivity(intent);
    }
}

