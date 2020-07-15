package com.example.dmapp.presets;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preset_list);
        setPassedPresetVariable(this.getIntent().getStringExtra("presetVariable"));
        Log.d(TAG, "onCreate: Passed Preset variable is " + getPassedPresetVariable());

        presetsDBHelper mPresetsDBHelper = new presetsDBHelper(this);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int deviceHeight = (displayMetrics.heightPixels);

        Cursor x = mPresetsDBHelper.getPresets();
        Log.d(TAG, "onCreate: the amount of rows in the distance table is " + x.getCount());

        ImageView backButton = findViewById(R.id.backButton);
        final Button addEntry = findViewById(R.id.addEntry);
        final EditText customPreset = findViewById(R.id.customPreset);
        ListView presetListView = findViewById(R.id.presetListView);

        ViewGroup.LayoutParams params = presetListView.getLayoutParams();
        params.height = (int)(deviceHeight * .70);
        presetListView.setLayoutParams(params);

        // set up the RecyclerView with data from the database
        //Log.d(TAG, "onCreate: Preset variable is " + getIntent().getStringExtra("presetVariable"));
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

        presetAdapter = new PresetAdapter(this, presetList, getPassedPresetVariable(), this);
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
                if(mPresetsDBHelper.checkNonExistence(customPresetValue)) {
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

