package com.redheaddev.gmjournal.misc;
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
import com.redheaddev.gmjournal.presets.PresetList;
import com.redheaddev.gmjournal.presets.presetsDBHelper;

import java.util.ArrayList;
import java.util.List;

class MiscAdapter extends ArrayAdapter<String> {

    private final Context mContext;
    private List<String> dataList = new ArrayList<>();
    private PresetList presetListClass;
    private final Activity currentActivity;
    private final Boolean mDarkMode;
    String TAG = "PresetAdapter";

    public MiscAdapter(Context context, ArrayList<String> list, Activity activity, Boolean darkMode) {
        super(context, 0 , list);
        mContext = context;
        dataList = list;
        currentActivity = activity;
        mDarkMode = darkMode;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if(!mDarkMode) {
            if (listItem == null) {
                listItem = LayoutInflater.from(mContext).inflate(R.layout.data_list_item, parent, false);
            }
        } else {
            if (listItem == null) {
                listItem = LayoutInflater.from(mContext).inflate(R.layout.data_list_item2, parent, false);
            }
        }

        String currentPreset = dataList.get(position);
        final TextView name = listItem.findViewById(R.id.dataValue);
        ImageButton delete = listItem.findViewById(R.id.deleteIcon);
        Log.d(TAG, "getView: Current preset is " + currentPreset);
        name.setText(currentPreset);
        name.setTag(position);

        delete.setTag(position);
        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View view) {
                int position = (Integer) view.getTag();
                dataList.remove(position);
                notifyDataSetChanged();
            }
        });

        return listItem;
    }
}
