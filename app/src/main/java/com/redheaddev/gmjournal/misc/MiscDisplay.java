package com.redheaddev.gmjournal.misc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.KeyListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.redheaddev.gmjournal.MyMiscRecyclerViewAdapter;
import com.redheaddev.gmjournal.MyRecyclerViewAdapter;
import com.redheaddev.gmjournal.R;
import com.redheaddev.gmjournal.cities.CityDisplay;
import com.redheaddev.gmjournal.loot.LootDisplay;
import com.redheaddev.gmjournal.loot.LootInfo;
import com.redheaddev.gmjournal.loot.LootList;
import com.redheaddev.gmjournal.loot.lootDBHelper;
import com.redheaddev.gmjournal.npcs.NpcDisplay;

import java.util.ArrayList;
import java.util.Arrays;

public class MiscDisplay extends AppCompatActivity implements MyMiscRecyclerViewAdapter.OnNoteListener{

    private KeyListener variable;
    private miscDBHelper mMiscDBHelper;
    private lootDBHelper mLootDBHelper;
    private int addMiscValue = 0;
    private String miscTitle = null;
    private final String TAG = "MiscInfo";
    private Context context;
    private Uri imageUri;
    private Boolean darkMode;
    private ArrayList<String> linkedNpcs = new ArrayList<>();
    private ArrayList<String> linkedCities = new ArrayList<>();
    private ArrayList<String> linkedItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.misc_display);
        context = this;

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String theme = sharedPreferences.getString("Theme", "none");
        String infoHeader5 = sharedPreferences.getString("infoHeader5", "none");
        darkMode = theme.equals("dark");

        mMiscDBHelper = new miscDBHelper(this);
        mLootDBHelper = new lootDBHelper(this);
        ImageView backButton = findViewById(R.id.backButton);
        ImageView editIcon = findViewById(R.id.editIcon);
        final TextView miscName = findViewById(R.id.miscTitle);
        ScrollView miscMainLayout = findViewById(R.id.miscMainLayout);
        LinearLayout miscInnerLayout = findViewById(R.id.miscInnerLayout);
        LinearLayout miscOuterLayout = findViewById(R.id.miscOuterLayout);
        LinearLayoutCompat imageLayout = findViewById(R.id.imageLayout);
        LinearLayoutCompat underTitleLayout = findViewById(R.id.underTitleLayout);
        LinearLayoutCompat underTitleLayout2 = findViewById(R.id.underTitleLayout2);

        if (theme.equals("dark")){

            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(editIcon.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));

            miscMainLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            miscInnerLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            miscOuterLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            imageLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            underTitleLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            underTitleLayout2.setBackgroundColor(Color.parseColor("#2C2C2C"));
            backButton.setBackgroundColor(Color.parseColor("#2C2C2C"));
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(editIcon.getDrawable()), ContextCompat.getColor(context, R.color.black));
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MiscList.class);
                startActivity(intent);
                finish();
            }
        });

        //If coming to this activity from clicking on a name, get and fill out all fields with the data for the corresponding npc.
        final Cursor miscInfo = mMiscDBHelper.getSpecificMisc(getIntent().getStringExtra("miscName"));
        miscInfo.moveToFirst();
        miscTitle = miscInfo.getString(1);
        miscName.setText(miscInfo.getString(1));
        miscName.setTextColor(Color.parseColor("#8f6ff8"));

        if(!miscInfo.getString(6).equals("")){
            ImageView itemImage = new ImageView(this);
            String image = miscInfo.getString(6);
            Bitmap bmImg = BitmapFactory.decodeFile(image);
            itemImage.setImageBitmap(bmImg);
            LinearLayoutCompat.LayoutParams lparams = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lparams.setMargins(0, 20, 0, 0);
            itemImage.setLayoutParams(lparams);
            itemImage.setAdjustViewBounds(true);
            imageLayout.addView(itemImage);
        }

        if(!miscInfo.getString(11).equals("") && !miscInfo.getString(11).equals("None") && !miscInfo.getString(11).equals("No Group")) {
            TextView miscGroup = new TextView(this);
            miscGroup.setText(String.format("%s: %s", infoHeader5, miscInfo.getString(11)));
            miscGroup.setPaintFlags(miscGroup.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            miscGroup.setTextSize(16);
            miscGroup.setTypeface(null, Typeface.ITALIC);
            miscGroup.setTextColor(Color.parseColor("#6fd8f9"));
            if (!darkMode) miscGroup.setBackgroundColor(Color.parseColor("#FFFFFF"));
            else miscGroup.setBackgroundColor(Color.parseColor("#2C2C2C"));
            miscGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mMiscDBHelper.checkGroupNonExistence(miscInfo.getString(11))) {
                        Intent intent = new Intent(context, MiscList.class);
                        intent.putExtra("groupClick", true);
                        intent.putExtra("groupName", miscInfo.getString(11));
                        startActivity(intent);
                    } else toast(getString(R.string.nosavedgroup));
                }
            });
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) underTitleLayout.getLayoutParams();
            params.setMargins(0, 15, 0, 0);
            underTitleLayout.setLayoutParams(params);
            underTitleLayout.addView(miscGroup);
        }

        if(!miscInfo.getString(10).equals("") && !miscInfo.getString(10).equals("None") && !miscInfo.getString(10).equals("No Item Group")) {
            TextView miscItemGroup = new TextView(this);
            miscItemGroup.setText(String.format("%s: %s", getString(R.string.item_group), miscInfo.getString(10)));
            miscItemGroup.setPaintFlags(miscItemGroup.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            miscItemGroup.setTextSize(16);
            miscItemGroup.setTypeface(null, Typeface.ITALIC);
            miscItemGroup.setTextColor(Color.parseColor("#6fd8f9"));
            if (!darkMode) miscItemGroup.setBackgroundColor(Color.parseColor("#FFFFFF"));
            else miscItemGroup.setBackgroundColor(Color.parseColor("#2C2C2C"));
            miscItemGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mLootDBHelper.checkGroupNonExistence(miscInfo.getString(10))) {
                        Intent intent = new Intent(context, LootList.class);
                        intent.putExtra("groupClick", true);
                        intent.putExtra("groupName", miscInfo.getString(10));
                        startActivity(intent);
                    } else toast(getString(R.string.nosavedgroup));
                }
            });
            LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) underTitleLayout2.getLayoutParams();
            params2.setMargins(0, 10, 0, 25);
            underTitleLayout2.setLayoutParams(params2);
            underTitleLayout2.addView(miscItemGroup);
        }

        if(!miscInfo.getString(2).equals("")) {
            TextView title1 = new TextView(this);
            String infoHeader1 = sharedPreferences.getString("infoHeader1", "none");
            if (!infoHeader1.equals("none")) title1.setText(infoHeader1);
            else title1.setText(R.string.miscellaneous_info_1);
            title1.setTextSize(20);
            title1.setTextColor(Color.parseColor("#FE5F55"));
            if (!darkMode) title1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                else title1.setBackgroundColor(Color.parseColor("#2C2C2C"));
            title1.setTypeface(null, Typeface.BOLD);
            miscInnerLayout.addView(title1);

            View line2 = new View(this);
            line2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
            if (!darkMode) line2.setBackgroundColor(Color.parseColor("#8C8C8C"));
                else line2.setBackgroundColor(Color.parseColor("#FFFFFF"));
            miscInnerLayout.addView(line2);

            TextView info1 = new TextView(this);
            info1.setText(miscInfo.getString(2));
            info1.setTextSize(18);
            if (!darkMode) {
                info1.setTextColor(Color.parseColor("#666666"));
                info1.setBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                info1.setTextColor(Color.parseColor("#FFFFFF"));
                info1.setBackgroundColor(Color.parseColor("#2C2C2C"));
            }
            LinearLayoutCompat.LayoutParams lparams4 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lparams4.setMargins(30, 0, 0, 45);
            info1.setLayoutParams(lparams4);
            miscInnerLayout.addView(info1);
        }

        if(!miscInfo.getString(3).equals("")) {
            TextView title2 = new TextView(this);
            String infoHeader2 = sharedPreferences.getString("infoHeader2", "none");
            if (!infoHeader2.equals("none")) title2.setText(infoHeader2);
            else title2.setText(R.string.miscellaneous_info_2);
            title2.setTextSize(20);
            title2.setTextColor(Color.parseColor("#FE5F55"));
            if (!darkMode) title2.setBackgroundColor(Color.parseColor("#FFFFFF"));
            else title2.setBackgroundColor(Color.parseColor("#2C2C2C"));
            title2.setTypeface(null, Typeface.BOLD);
            miscInnerLayout.addView(title2);

            View line3 = new View(this);
            line3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
            if (!darkMode) line3.setBackgroundColor(Color.parseColor("#8C8C8C"));
            else line3.setBackgroundColor(Color.parseColor("#FFFFFF"));
            miscInnerLayout.addView(line3);

            TextView info2 = new TextView(this);
            info2.setText(miscInfo.getString(3));
            info2.setTextSize(18);
            if (!darkMode) {
                info2.setTextColor(Color.parseColor("#666666"));
                info2.setBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                info2.setTextColor(Color.parseColor("#FFFFFF"));
                info2.setBackgroundColor(Color.parseColor("#2C2C2C"));
            }
            LinearLayoutCompat.LayoutParams lparams4 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lparams4.setMargins(30, 0, 0, 45);
            info2.setLayoutParams(lparams4);
            miscInnerLayout.addView(info2);
        }

        if(!miscInfo.getString(4).equals("")) {
            TextView title3 = new TextView(this);
            String infoHeader3 = sharedPreferences.getString("infoHeader3", "none");
            if (!infoHeader3.equals("none")) title3.setText(infoHeader3);
            else title3.setText(R.string.miscellaneous_info_3);
            title3.setTextSize(20);
            title3.setTextColor(Color.parseColor("#FE5F55"));
            if (!darkMode) title3.setBackgroundColor(Color.parseColor("#FFFFFF"));
            else title3.setBackgroundColor(Color.parseColor("#2C2C2C"));
            title3.setTypeface(null, Typeface.BOLD);
            miscInnerLayout.addView(title3);

            View line4 = new View(this);
            line4.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
            if (!darkMode) line4.setBackgroundColor(Color.parseColor("#8C8C8C"));
            else line4.setBackgroundColor(Color.parseColor("#FFFFFF"));
            miscInnerLayout.addView(line4);

            TextView info3 = new TextView(this);
            info3.setText(miscInfo.getString(4));
            info3.setTextSize(18);
            if (!darkMode) {
                info3.setTextColor(Color.parseColor("#666666"));
                info3.setBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                info3.setTextColor(Color.parseColor("#FFFFFF"));
                info3.setBackgroundColor(Color.parseColor("#2C2C2C"));
            }
            LinearLayoutCompat.LayoutParams lparams4 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lparams4.setMargins(30, 0, 0, 45);
            info3.setLayoutParams(lparams4);
            miscInnerLayout.addView(info3);
        }

        if(!miscInfo.getString(5).equals("")) {
            TextView title4 = new TextView(this);
            String infoHeader4 = sharedPreferences.getString("infoHeader4", "none");
            if (!infoHeader4.equals("none")) title4.setText(infoHeader4);
            else title4.setText(R.string.miscellaneous_info_4);
            title4.setTextSize(20);
            title4.setTextColor(Color.parseColor("#FE5F55"));
            if (!darkMode) title4.setBackgroundColor(Color.parseColor("#FFFFFF"));
            else title4.setBackgroundColor(Color.parseColor("#2C2C2C"));
            title4.setTypeface(null, Typeface.BOLD);
            miscInnerLayout.addView(title4);

            View line5 = new View(this);
            line5.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
            if (!darkMode) line5.setBackgroundColor(Color.parseColor("#8C8C8C"));
            else line5.setBackgroundColor(Color.parseColor("#FFFFFF"));
            miscInnerLayout.addView(line5);

            TextView info4 = new TextView(this);
            info4.setText(miscInfo.getString(5));
            info4.setTextSize(18);
            if (!darkMode) {
                info4.setTextColor(Color.parseColor("#666666"));
                info4.setBackgroundColor(Color.parseColor("#FFFFFF"));
            } else {
                info4.setTextColor(Color.parseColor("#FFFFFF"));
                info4.setBackgroundColor(Color.parseColor("#2C2C2C"));
            }
            LinearLayoutCompat.LayoutParams lparams5 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lparams5.setMargins(30, 0, 0, 45);
            info4.setLayoutParams(lparams5);
            miscInnerLayout.addView(info4);
        }

        if (!miscInfo.getString(7).equals("")) {
            String npcString = miscInfo.getString(7);
            npcString = npcString.replaceAll(",\\s", ",");
            String[] npcArray = npcString.split(",");
            Log.d(TAG, "onCreate: Array is " + Arrays.toString(npcArray));
            linkedNpcs = new ArrayList<>(Arrays.asList(npcArray));
            Log.d(TAG, "onCreate: Arraylist is " + linkedNpcs);

            LinearLayout npcListTitleLayout = findViewById(R.id.npcListTitleLayout);
            TextView npcTitle = new TextView(this);
            npcTitle.setText(getString(R.string.linked_npc));
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

            RecyclerView npcRecyclerView = findViewById(R.id.npcListLayout);
            npcRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            npcRecyclerView.addItemDecoration(itemDecor);
            MyMiscRecyclerViewAdapter adapter1 = new MyMiscRecyclerViewAdapter(this, linkedNpcs, this, darkMode, "npc"); // change this later
            npcRecyclerView.setAdapter(adapter1);
            npcRecyclerView.addItemDecoration(itemDecor);

            DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
            final int deviceHeight = (displayMetrics.heightPixels);
            final double scale;
            if (deviceHeight < 2600) scale = .07;
            else scale = .06;
            double scaleHeight = linkedNpcs.size() * scale;
            int height = (int) (deviceHeight * scaleHeight);
            //int height = distanceListDisplay.size() * 155;
            ViewGroup.LayoutParams params = npcRecyclerView.getLayoutParams();
            params.height = height;
            npcRecyclerView.setLayoutParams(params);
        }

        if (!miscInfo.getString(8).equals("")) {
            String cityString = miscInfo.getString(8);
            cityString = cityString.replaceAll(",\\s", ",");
            String[] cityArray = cityString.split(",");
            linkedCities = new ArrayList<>(Arrays.asList(cityArray));

            LinearLayout cityListTitleLayout = findViewById(R.id.cityListTitleLayout);
            TextView cityTitle = new TextView(this);
            cityTitle.setText(getString(R.string.linked_city));
            cityTitle.setTextSize(20);
            cityTitle.setTextColor(Color.parseColor("#FE5F55"));
            if (!darkMode) cityTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
            else cityTitle.setBackgroundColor(Color.parseColor("#2C2C2C"));
            cityTitle.setTypeface(null, Typeface.BOLD);
            cityListTitleLayout.addView(cityTitle);
            View line2 = new View(this);
            line2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
            if (!darkMode) line2.setBackgroundColor(Color.parseColor("#8C8C8C"));
            else line2.setBackgroundColor(Color.parseColor("#FFFFFF"));
            cityListTitleLayout.addView(line2);

            RecyclerView cityRecyclerView = findViewById(R.id.cityListLayout);
            cityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            cityRecyclerView.addItemDecoration(itemDecor);
            MyMiscRecyclerViewAdapter adapter2 = new MyMiscRecyclerViewAdapter(this, linkedCities, this, darkMode, "city"); // change this later
            cityRecyclerView.setAdapter(adapter2);
            cityRecyclerView.addItemDecoration(itemDecor);

            DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
            final int deviceHeight = (displayMetrics.heightPixels);
            final double scale;
            if (deviceHeight < 2600) scale = .07;
            else scale = .06;
            double scaleHeight = linkedCities.size() * scale;
            int height = (int) (deviceHeight * scaleHeight);
            //int height = distanceListDisplay.size() * 155;
            ViewGroup.LayoutParams params = cityRecyclerView.getLayoutParams();
            params.height = height;
            cityRecyclerView.setLayoutParams(params);
        }

        if (!miscInfo.getString(9).equals("")) {
            String itemString = miscInfo.getString(9);
            itemString = itemString.replaceAll(",\\s", ",");
            Log.d(TAG, "onCreate: the item string is " + itemString);
            String[] itemArray = itemString.split(",");
            linkedItems = new ArrayList<>(Arrays.asList(itemArray));

            LinearLayout itemListTitleLayout = findViewById(R.id.itemListTitleLayout);
            TextView itemTitle = new TextView(this);
            itemTitle.setText(getString(R.string.linked_item));
            itemTitle.setTextSize(20);
            itemTitle.setTextColor(Color.parseColor("#FE5F55"));
            if (!darkMode) itemTitle.setBackgroundColor(Color.parseColor("#FFFFFF"));
            else itemTitle.setBackgroundColor(Color.parseColor("#2C2C2C"));
            itemTitle.setTypeface(null, Typeface.BOLD);
            itemListTitleLayout.addView(itemTitle);
            View line2 = new View(this);
            line2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3));
            if (!darkMode) line2.setBackgroundColor(Color.parseColor("#8C8C8C"));
            else line2.setBackgroundColor(Color.parseColor("#FFFFFF"));
            itemListTitleLayout.addView(line2);

            RecyclerView itemRecyclerView = findViewById(R.id.itemListLayout);
            itemRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
            itemRecyclerView.addItemDecoration(itemDecor);
            MyMiscRecyclerViewAdapter adapter3 = new MyMiscRecyclerViewAdapter(this, linkedItems, this, darkMode, "item"); // change this later
            itemRecyclerView.setAdapter(adapter3);
            itemRecyclerView.addItemDecoration(itemDecor);

            DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
            final int deviceHeight = (displayMetrics.heightPixels);
            final double scale;
            if (deviceHeight < 2600) scale = .07;
            else scale = .06;
            double scaleHeight = linkedItems.size() * scale;
            int height = (int) (deviceHeight * scaleHeight);
            //int height = distanceListDisplay.size() * 155;
            ViewGroup.LayoutParams params = itemRecyclerView.getLayoutParams();
            params.height = height;
            itemRecyclerView.setLayoutParams(params);
        }

        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MiscInfo.class);
                Log.d(TAG, "onClick: The value being passed to MiscInfo is " + miscTitle);
                intent.putExtra("miscName", miscTitle);
                intent.putExtra("addMiscValue", 0);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, MiscList.class);
        startActivity(intent);
        finish();
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setAddMiscValue(int value){
        addMiscValue = value;
    }
    private void setMiscTitle(String value){
        miscTitle = value;
    }

    @Override
    public void onNoteClick(int position, String mListType) {
        switch (mListType){
            case "npc":
                String npcName = linkedNpcs.get(position);
                Intent intent1 = new Intent(this, NpcDisplay.class);
                Log.d(TAG, "onNoteClick: Passing the name" + npcName + "to npcDisplay");
                intent1.putExtra("npcName", npcName);
                intent1.putExtra("addNPCValue", 0);
                intent1.putExtra("miscNav", true);
                startActivity(intent1);
                break;
            case "city":
                String cityName = linkedCities.get(position);
                Intent intent2 = new Intent(this, CityDisplay.class);
                intent2.putExtra("cityName", cityName);
                intent2.putExtra("addCityValue", 0);
                intent2.putExtra("miscNav", true);
                startActivity(intent2);
                break;
            case "item":
                String itemName = linkedItems.get(position);
                Intent intent3 = new Intent(this, LootDisplay.class);
                intent3.putExtra("lootName", itemName);
                intent3.putExtra("addItemValue", 0);
                intent3.putExtra("miscNav", true);
                startActivity(intent3);
                break;
        }
    }
}