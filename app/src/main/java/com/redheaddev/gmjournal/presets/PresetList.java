package com.redheaddev.gmjournal.presets;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.redheaddev.gmjournal.MyRecyclerViewAdapter;
import com.redheaddev.gmjournal.R;
import com.redheaddev.gmjournal.cities.citiesDBHelper;
import com.redheaddev.gmjournal.loot.lootDBHelper;
import com.redheaddev.gmjournal.npcs.npcDBHelper;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PresetList extends AppCompatActivity {

    MyRecyclerViewAdapter adapter;
    private PresetAdapter presetAdapter;
    private DataAdapter dataAdapter;
    private final ArrayList<Preset> presetList = new ArrayList<>();
    private final ArrayList<String> dataList = new ArrayList<>();
    private final String TAG = "PresetList";
    private String passedPresetVariable;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preset_list);
        setPassedPresetVariable(this.getIntent().getStringExtra("presetVariable"));
        Log.d(TAG, "onCreate: This presetList has a variable of " + getPassedPresetVariable());
        context = this;

        presetsDBHelper mPresetsDBHelper = new presetsDBHelper(this);
        final npcDBHelper mNpcDBHelper = new npcDBHelper(this);
        final citiesDBHelper mCityDBHelper = new citiesDBHelper(this);
        final lootDBHelper mLootDBHelper = new lootDBHelper(this);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int deviceHeight = (displayMetrics.heightPixels);

        LinearLayoutCompat layout = findViewById(R.id.presetLayout);
        ImageView backButton = findViewById(R.id.backButton);
        final Button addEntry = findViewById(R.id.addEntry);
        final Button removeEntry = findViewById(R.id.removeEntry);
        final TextView presetTitle = findViewById(R.id.presetTitle);
        final ListView presetListView = findViewById(R.id.presetListView);

        ViewGroup.LayoutParams params = presetListView.getLayoutParams();
        params.height = (int)(deviceHeight * .70);
        presetListView.setLayoutParams(params);

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String theme = sharedPreferences.getString("Theme", "none");
        final Boolean darkMode = theme.equals("dark");

        if (theme.equals("dark")){
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            presetTitle.setTextColor(Color.WHITE);
            layout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            backButton.setBackgroundColor(Color.parseColor("#2C2C2C"));
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.black));
        }

        //set the title of the preset list
        final String presetVariable = getIntent().getStringExtra("presetVariable");
        switch (presetVariable){
            case "Environment": presetTitle.setText(String.format("%s %s", getString(R.string.environment), getString(R.string.list)));
                break;
            case "Economy": presetTitle.setText(String.format("%s %s", getString(R.string.eco), getString(R.string.list)));
                break;
            case "Population": presetTitle.setText(String.format("%s %s", getString(R.string.pop), getString(R.string.list)));
                break;
            case "Race": presetTitle.setText(String.format("%s %s", getString(R.string.race), getString(R.string.list)));
                break;
            case "Group": presetTitle.setText(String.format("%s %s", getString(R.string.group), getString(R.string.list)));
                break;
            case "MiscGroup":
                String title = sharedPreferences.getString("infoHeader5", "none");
                if (title.equals("none")) title = getString(R.string.miscellaneous_group);
                presetTitle.setText(title);
                break;
            case "NPC": presetTitle.setText(String.format("%s %s", getString(R.string.npc), getString(R.string.list)));
                removeEntry.setVisibility(View.INVISIBLE);
                removeEntry.setClickable(false);
                break;
            case "City": presetTitle.setText(String.format("%s %s", getString(R.string.city), getString(R.string.list)));
                removeEntry.setVisibility(View.INVISIBLE);
                removeEntry.setClickable(false);
                break;
            case "Item": presetTitle.setText(String.format("%s %s", getString(R.string.item), getString(R.string.list)));
                removeEntry.setVisibility(View.INVISIBLE);
                removeEntry.setClickable(false);
                break;
        }

        final boolean isPresetGroup = presetVariable.equals("Environment") || presetVariable.equals("Economy") || presetVariable.equals("Population") || presetVariable.equals("Race") || presetVariable.equals("Group") || presetVariable.equals("MiscGroup");

        String message = presetVariable;
        if (message.equals("MiscGroup")) message = "Group";
        addEntry.setText(String.format("%s %s", getString(R.string.add), message));

        if (isPresetGroup) {
            Cursor presets = mPresetsDBHelper.getSpecificPresets(getIntent().getStringExtra("presetVariable"));
            try {
                if (presets.moveToNext()) {
                    do {
                        presetList.add(new Preset(presets.getString(2)));
                    } while (presets.moveToNext());
                }
            } finally {
                presets.close();
            }

            presetAdapter = new PresetAdapter(this, presetList, getPassedPresetVariable(), this, darkMode);
            presetListView.setAdapter(presetAdapter);
        } else {
            switch (presetVariable){
                case "NPC":
                    Cursor npcs = mNpcDBHelper.getNPCS();
                    try {
                        if (npcs.moveToNext()) {
                            do {
                                dataList.add(npcs.getString(1));
                            } while (npcs.moveToNext());
                        }
                    } finally {
                        npcs.close();
                    }
                    for (int i = 0; i < dataList.size(); i++) {
                        Log.d(TAG, "onCreate: " + (dataList.get(i)) + " is in the arraylist");
                    }
                    dataAdapter = new DataAdapter(this, dataList, getPassedPresetVariable(), this, darkMode);
                    presetListView.setAdapter(dataAdapter);
                    break;

                case "City": presetTitle.setText(String.format("%s %s", getString(R.string.city), getString(R.string.list)));
                    Cursor cities = mCityDBHelper.getCities();
                    try {
                        if (cities.moveToNext()) {
                            do {
                                dataList.add(cities.getString(1));
                            } while (cities.moveToNext());
                        }
                    } finally {
                        cities.close();
                    }
                    dataAdapter = new DataAdapter(this, dataList, getPassedPresetVariable(), this, darkMode);
                    presetListView.setAdapter(dataAdapter);
                    break;

                case "Item": presetTitle.setText(String.format("%s %s", getString(R.string.item), getString(R.string.list)));
                    Cursor items = mLootDBHelper.getLoot();
                    try {
                        if (items.moveToNext()) {
                            do {
                                dataList.add(items.getString(1));
                            } while (items.moveToNext());
                        }
                    } finally {
                        items.close();
                    }
                    dataAdapter = new DataAdapter(this, dataList, getPassedPresetVariable(), this, darkMode);
                    presetListView.setAdapter(dataAdapter);
                    break;
            }
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        removeEntry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int result;

                switch(presetVariable) {
                    case "Environment":
                        result = 1;
                        break;
                    case "Population":
                        result = 2;
                        break;
                    case "Economy":
                        result = 3;
                        break;
                    case "Race":
                        result = 4;
                        break;
                    case "Group":
                        result = 5;
                        break;
                    case "MiscGroup":
                        result = 6;
                        break;
                    case "NPC":
                        result = 7;
                        break;
                    case "City":
                        result = 8;
                        break;
                    case "Item":
                        result = 9;
                        break;
                    default:
                        result = 0;
                }

                Log.d(TAG, "onClick: The result of the click was " + result);

                Intent intent = new Intent();
                intent.putExtra("presetValue", "none");
                setResult(result, intent);
                finish();
            }
        });

        addEntry.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText edittext = new EditText(context);
                String firstMessage = presetVariable.toLowerCase();
                if (firstMessage.equals("MiscGroup")) firstMessage = "Group";
                String secondMessage = presetVariable;
                if (secondMessage.equals("MiscGroup")) secondMessage = "Group";
                alert.setMessage(String.format("%s %s", getString(R.string.newpreset), firstMessage));
                alert.setTitle(String.format("%s %s", getString(R.string.create), secondMessage));
                alert.setView(edittext);

                alert.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String customPresetValue = edittext.getText().toString();

                        if (isPresetGroup) {
                            presetsDBHelper mPresetsDBHelper = new presetsDBHelper(context);
                            if (!customPresetValue.equals("")) {
                                if (mPresetsDBHelper.checkNonExistence(customPresetValue)) {
                                    mPresetsDBHelper.addPreset(getPassedPresetVariable(), customPresetValue);
                                    presetList.clear();
                                    Cursor presets = mPresetsDBHelper.getSpecificPresets(getPassedPresetVariable());
                                    try {
                                        if (presets.moveToNext()) {
                                            do {
                                                presetList.add(new Preset(presets.getString(2)));
                                            } while (presets.moveToNext());
                                        }
                                    } finally {
                                        presets.close();
                                    }
                                    presetAdapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                } else {
                                    toast(getString(R.string.presetexists));
                                }
                            } else {
                                toast(getString(R.string.enteravalue));
                            }
                        } else {
                            switch (presetVariable){
                                case "NPC":
                                    Cursor npcs = mNpcDBHelper.getNPCS();
                                    if (!customPresetValue.equals("")) {
                                        if (mNpcDBHelper.checkNonExistence(customPresetValue)) {
                                            mNpcDBHelper.addNPC(customPresetValue, "", "", "", "", "", "", "", "", false);
                                            dataList.clear();
                                            try {
                                                if (npcs.moveToNext()) {
                                                    do {
                                                        dataList.add(npcs.getString(1));
                                                    } while (npcs.moveToNext());
                                                }
                                            } finally {
                                                npcs.close();
                                            }
                                            dataAdapter.notifyDataSetChanged();
                                            dialog.dismiss();
                                        } else {
                                            toast(getString(R.string.specificnpcexists));
                                        }
                                    } else {
                                        toast(getString(R.string.enteravalue));
                                    }
                                    break;

                                case "City": presetTitle.setText(String.format("%s %s", getString(R.string.city), getString(R.string.list)));
                                    Cursor cities = mCityDBHelper.getCities();
                                    if (!customPresetValue.equals("")) {
                                        if (mCityDBHelper.checkNonExistence(customPresetValue)) {
                                            mCityDBHelper.addCity(customPresetValue, "None", "None", "", "", "", false);
                                            dataList.clear();
                                            try {
                                                if (cities.moveToNext()) {
                                                    do {
                                                        dataList.add(cities.getString(1));
                                                    } while (cities.moveToNext());
                                                }
                                            } finally {
                                                cities.close();
                                            }
                                            dataAdapter.notifyDataSetChanged();
                                            dialog.dismiss();
                                        } else {
                                            toast(getString(R.string.cityalreadyexists));
                                        }
                                    } else {
                                        toast(getString(R.string.enteravalue));
                                    }
                                    break;

                                case "Item": presetTitle.setText(String.format("%s %s", getString(R.string.item), getString(R.string.list)));
                                    Cursor items = mLootDBHelper.getLoot();
                                    if (!customPresetValue.equals("")) {
                                        if (mLootDBHelper.checkNonExistence(customPresetValue)) {
                                            mLootDBHelper.addLoot(customPresetValue, "", "", "", "", "", "", "", "", false);
                                            dataList.clear();
                                            try {
                                                if (items.moveToNext()) {
                                                    do {
                                                        dataList.add(items.getString(1));
                                                    } while (items.moveToNext());
                                                }
                                            } finally {
                                                items.close();
                                            }
                                            dataAdapter.notifyDataSetChanged();
                                            dialog.dismiss();
                                        } else {
                                            toast(getString(R.string.specificitemexists));
                                        }
                                    } else {
                                        toast(getString(R.string.enteravalue));
                                    }
                                    break;
                            }
                        }
                    }
                });

                alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });
    }

    private void setPassedPresetVariable(String passedPresetVariable) {
        this.passedPresetVariable = passedPresetVariable;
    }

    private String getPassedPresetVariable() {
        return passedPresetVariable;
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

