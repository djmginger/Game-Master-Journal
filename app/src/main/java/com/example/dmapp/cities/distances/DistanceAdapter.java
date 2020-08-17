package com.example.dmapp.cities.distances;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dmapp.R;

import java.util.ArrayList;
import java.util.List;

public class DistanceAdapter extends ArrayAdapter<Distance> {

    private final Context mContext;
    private List<Distance> distanceList = new ArrayList<>();
    private final String fromCity;
    private boolean isDisplay;
    private boolean mDarkMode;
    String TAG = "DistanceAdapter";

    public DistanceAdapter(Context context, ArrayList<Distance> list, String passedFromCity, boolean displayed, boolean darkMode) {
        super(context, 0 , list);
        mContext = context;
        distanceList = list;
        fromCity = passedFromCity;
        isDisplay = displayed;
        mDarkMode = darkMode;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItem = convertView;
        if (!mDarkMode) {
            if (listItem == null) {
                listItem = LayoutInflater.from(mContext).inflate(R.layout.distance_item, parent, false);
            }
        } else {
            if (listItem == null) {
                listItem = LayoutInflater.from(mContext).inflate(R.layout.distance_item2, parent, false);
            }
        }

        final Distance currentDistance = distanceList.get(position);
        final TextView toCity = listItem.findViewById(R.id.toCity);
        final TextView distance = listItem.findViewById(R.id.distanceValue);
        final ImageButton delete = listItem.findViewById(R.id.deleteIcon);

        if (isDisplay){
            /*LinearLayout distanceItem = listItem.findViewById(R.id.distanceItem);
            distanceItem.setBackgroundColor(Color.WHITE);
            ImageView distanceColon = listItem.findViewById(R.id.distanceColon);
            distanceColon.setBackgroundColor(Color.WHITE);
            ImageButton distanceArrow = listItem.findViewById(R.id.distanceArrow);
            distanceArrow.setBackgroundColor(Color.WHITE);
            delete.setBackgroundColor(Color.WHITE);*/
            delete.setVisibility(View.GONE);
        }

        toCity.setText(currentDistance.getToCity());
        distance.setText(currentDistance.getDistance());

        delete.setTag(position);
        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                distanceDBHelper mDistanceDBHelper = new distanceDBHelper(mContext);
                mDistanceDBHelper.removeSpecificDistance(fromCity, currentDistance.getToCity());

                distanceList.clear();
                Cursor distances = mDistanceDBHelper.getDistances(fromCity); //Didn't work with getPassedPresetVariable, attempted hardcoded value to test. Still not functional
                try {
                    if (distances.moveToNext()) {
                        do {
                            distanceList.add(new Distance(distances.getString(2), distances.getString(3)));
                        } while (distances.moveToNext());
                    }
                } finally {
                    distances.close();
                }
                notifyDataSetChanged();
            }
        });

        return listItem;
    }
}
