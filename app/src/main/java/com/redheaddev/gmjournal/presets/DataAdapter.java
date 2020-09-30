package com.redheaddev.gmjournal.presets;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.redheaddev.gmjournal.R;

import java.util.ArrayList;
import java.util.List;

class DataAdapter extends ArrayAdapter<String> {

    private final Context mContext;
    private List<String> dataList = new ArrayList<>();
    private PresetList presetListClass;
    private final String variable;
    private final Activity dataActivity;
    private final Boolean mDarkMode;
    String TAG = "DataAdapter";

    public DataAdapter(Context context, ArrayList<String> list, String presetVariable, Activity activity, Boolean darkMode) {
        super(context, 0 , list);
        mContext = context;
        dataList = list;
        variable = presetVariable;
        Log.d(TAG, "PresetAdapter: the variable is " + variable);
        dataActivity = activity;
        mDarkMode = darkMode;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(!mDarkMode) {
            if (listItem == null) {
                listItem = LayoutInflater.from(mContext).inflate(R.layout.data_item, parent, false);
            }
        } else {
            if (listItem == null) {
                listItem = LayoutInflater.from(mContext).inflate(R.layout.data_item2, parent, false);
            }
        }

        String currentChoice = dataList.get(position);
        final TextView name = listItem.findViewById(R.id.presetValue);

        name.setText(currentChoice);
        name.setTag(position);
        name.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int position = (Integer) view.getTag();
                // Access the row position here to get the correct data item
                String presetValue = getItem(position);
                // Do what you want here...
                Log.d(TAG, "onClick: The data value selected is " + presetValue);
                Intent intent = new Intent();
                Log.d(TAG, "onClick: ");
                intent.putExtra("presetValue", presetValue);
                int result;

                switch(variable) {
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

                dataActivity.setResult(result, intent);
                dataActivity.finish();
            }
        });

        return listItem;
    }
}
