package com.example.dmapp.presets;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.dmapp.R;

import java.util.ArrayList;
import java.util.List;

class PresetAdapter extends ArrayAdapter<Preset> {

    private final Context mContext;
    private List<Preset> presetList = new ArrayList<>();
    private PresetList presetListClass;
    private final String variable;
    private final Activity presetActivity;
    String TAG = "PresetAdapter";

    public PresetAdapter(Context context, ArrayList<Preset> list, String presetVariable, Activity activity) {
        super(context, 0 , list);
        mContext = context;
        presetList = list;
        variable = presetVariable;
        Log.d(TAG, "PresetAdapter: the variable is " + variable);
        presetActivity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = convertView;
        if(listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.preset_item, parent, false);
        }

        Preset currentPreset = presetList.get(position);
        final TextView name = listItem.findViewById(R.id.presetValue);
        ImageButton delete = listItem.findViewById(R.id.deleteIcon);

        name.setText(currentPreset.getValue());
        name.setTag(position);
        name.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                // Access the row position here to get the correct data item
                Preset preset = getItem(position);
                // Do what you want here...
                String presetValue = preset.getValue();
                Log.d(TAG, "onClick: The preset value selected is " + presetValue);
                Intent intent = new Intent();
                intent.putExtra("presetValue", presetValue);
                int result;

                switch(variable) {
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
                    default:
                        result = 0;
                }

                Log.d(TAG, "onClick: The result of the click was " + result);

                presetActivity.setResult(result, intent);
                presetActivity.finish();
            }
        });

        delete.setTag(position);
        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Are you sure you want to delete this item?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        int position = (Integer) view.getTag();
                        // Access the row position here to get the correct data item
                        Preset preset = getItem(position);
                        // Do what you want here...
                        String presetValue = preset.getValue();
                        presetsDBHelper mPresetsDBHelper = new presetsDBHelper(mContext);
                        mPresetsDBHelper.removeSpecificPreset(presetValue);

                        presetList.clear();
                        Cursor presets = mPresetsDBHelper.getSpecificPresets(variable); //Didn't work with getPassedPresetVariable, attempted hardcoded value to test. Still not functional
                        try {
                            if (presets.moveToNext()) {
                                do {
                                    presetList.add(new Preset(presets.getString(2)));
                                } while (presets.moveToNext());
                            }
                        } finally {
                            presets.close();
                        }
                        notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        return listItem;
    }
}
