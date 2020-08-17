package com.example.dmapp.presets;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
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

import com.example.dmapp.MyRecyclerViewAdapter;
import com.example.dmapp.R;

import java.util.ArrayList;

public class PresetList extends AppCompatActivity {

    MyRecyclerViewAdapter adapter;
    private PresetAdapter presetAdapter;
    private final ArrayList<Preset> presetList = new ArrayList<>();
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
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int deviceHeight = (displayMetrics.heightPixels);

        LinearLayoutCompat layout = findViewById(R.id.presetLayout);
        ImageView backButton = findViewById(R.id.backButton);
        final Button addEntry = findViewById(R.id.addEntry);
        final EditText customPreset = findViewById(R.id.customPreset);
        TextView presetTitle = findViewById(R.id.presetTitle);
        ListView presetListView = findViewById(R.id.presetListView);

        ViewGroup.LayoutParams params = presetListView.getLayoutParams();
        params.height = (int)(deviceHeight * .70);
        presetListView.setLayoutParams(params);

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String theme = sharedPreferences.getString("Theme", "none");
        Boolean darkMode = theme.equals("dark");

        if (theme.equals("dark")){
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            customPreset.setTextColor(Color.WHITE);
            customPreset.setHintTextColor(Color.WHITE);
            presetTitle.setTextColor(Color.WHITE);
            layout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            backButton.setBackgroundColor(Color.parseColor("#2C2C2C"));
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.black));
        }

        //set the title of the preset list
        String presetVariable = getIntent().getStringExtra("presetVariable");
        switch (presetVariable){
            case "Environment": presetTitle.setText(String.format("%s %s", getString(R.string.environment), getString(R.string.list)));
                break;
            case "Economy": presetTitle.setText(String.format("%s %s", getString(R.string.eco), getString(R.string.list)));
                break;
            case "Population": presetTitle.setText(String.format("%s %s", getString(R.string.pop), getString(R.string.list)));
                break;
            case "Race": presetTitle.setText(String.format("%s %s", getString(R.string.race), getString(R.string.list)));
                break;
        }

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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        addEntry.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onClick(View view) {
                String customPresetValue = customPreset.getText().toString();
                presetsDBHelper mPresetsDBHelper = new presetsDBHelper(addEntry.getContext());
                if(!customPresetValue.equals("")) {
                    if (mPresetsDBHelper.checkNonExistence(customPresetValue)) {
                        mPresetsDBHelper.addPreset(getPassedPresetVariable(), customPresetValue);
                        presetList.clear();
                        Cursor presets = mPresetsDBHelper.getSpecificPresets(getPassedPresetVariable()); //Didn't work with getPassedPresetVariable, attempted hardcoded value to test. Still not functional
                        try {
                            if (presets.moveToNext()) {
                                do {
                                    presetList.add(new Preset(presets.getString(2)));
                                } while (presets.moveToNext());
                            }
                        } finally {
                            presets.close();
                        }
                        customPreset.setText("");
                        presetAdapter.notifyDataSetChanged();
                    } else {
                        toast("This preset already exists!");
                    }
                }
                else {
                    toast("Please enter a value!");
                }
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

