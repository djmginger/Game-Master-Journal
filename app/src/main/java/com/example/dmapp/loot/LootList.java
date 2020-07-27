package com.example.dmapp.loot;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

        context = this;
        LinearLayoutCompat layout = findViewById(R.id.listLayout);
        //layout.setBackgroundColor(getResources().getColor(R.color.mainColor3));

        lootDBHelper mLootDBHelper = new lootDBHelper(this);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int deviceHeight = (displayMetrics.heightPixels);

        ImageView backButton = findViewById(R.id.backButton);
        TextView listTitle = findViewById(R.id.listTitle);
        ImageView addEntry = findViewById(R.id.addEntry);

        // set up the RecyclerView with data from the database
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

        RecyclerView recyclerView = findViewById(R.id.info_content);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, lootList, this);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = (int)(deviceHeight * .75);
        recyclerView.setLayoutParams(params);

        listTitle.setText("Loot");
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

