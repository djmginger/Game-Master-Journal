package com.example.dmapp.loot;

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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dmapp.MainActivity;
import com.example.dmapp.MyRecyclerViewAdapter;
import com.example.dmapp.R;

import java.util.ArrayList;

public class LootList extends AppCompatActivity implements MyRecyclerViewAdapter.OnNoteListener {

    private MyRecyclerViewAdapter adapter;
    private final ArrayList<String> lootList = new ArrayList<>();
    private final String TAG = "LootInfo";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_titles);

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String theme = sharedPreferences.getString("Theme", "none");
        Boolean darkMode = theme.equals("dark");

        context = this;
        LinearLayoutCompat layout = findViewById(R.id.listLayout);
        LinearLayoutCompat headerLayout = findViewById(R.id.headerLayout);
        RelativeLayout innerLayout = findViewById(R.id.innerLayout);
        ImageView backButton = findViewById(R.id.backButton);
        TextView listTitle = findViewById(R.id.listTitle);
        ImageView addEntry = findViewById(R.id.addEntry);

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

        // set up the RecyclerView with data from the database
        Cursor loot = mLootDBHelper.getLoot();
        try {
            if (loot.moveToNext()) {
                do {
                    lootList.add(loot.getString(1));
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
            loot.close();
        }

        RecyclerView recyclerView = findViewById(R.id.info_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, lootList, this, darkMode);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = (int)(deviceHeight * .75);
        recyclerView.setLayoutParams(params);

        listTitle.setText(R.string.loot);
        listTitle.setTextColor(Color.parseColor("#6F94F8"));

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
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        finish();
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
}

