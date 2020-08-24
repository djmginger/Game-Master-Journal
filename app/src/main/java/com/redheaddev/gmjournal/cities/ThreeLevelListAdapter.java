
/*
 * Copyright (c) 2017 Selva.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.redheaddev.gmjournal.cities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.redheaddev.gmjournal.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ThreeLevelListAdapter extends BaseExpandableListAdapter {
    public static final String TAG = "ELVadapter";

    ArrayList<String> parentHeaders;
    List<String[]> secondLevel;
    private Context context;
    List<LinkedHashMap<String, String>> data;
    private Boolean mDarkMode;

    /**
     * Constructor
     * @param context
     * @param parentHeader
     * @param secondLevel
     * @param data
     * @param darkMode
     */
    public ThreeLevelListAdapter(Context context, ArrayList<String> parentHeader, List<String[]> secondLevel, List<LinkedHashMap<String, String>> data, Boolean darkMode) {
        this.context = context;

        this.parentHeaders = parentHeader;

        this.secondLevel = secondLevel;

        this.data = data;

        this.mDarkMode = darkMode;
    }

    @Override
    public int getGroupCount() {
        return parentHeaders.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {


        // no idea why this code is working

        return 1;

    }

    @Override
    public Object getGroup(int groupPosition) {

        return groupPosition;
    }

    @Override
    public Object getChild(int group, int child) {


        return child;


    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (!mDarkMode) convertView = inflater.inflate(R.layout.row_first, null);
            else convertView = inflater.inflate(R.layout.row_first2, null);
        TextView text = (TextView) convertView.findViewById(R.id.rowParentText);
        text.setText(this.parentHeaders.get(groupPosition));

        //if there's ONLY a location title, and no information within, remove the dropdown arrow from the row
        boolean hasSecondLevel = true;
        String childValue = secondLevel.get(groupPosition)[0];
        try {
            secondLevel.get(groupPosition);
        } catch (IndexOutOfBoundsException e){
            hasSecondLevel = false;
            System.out.println("No children");
        };
        if (!hasSecondLevel || (childValue.equals(""))){
            ImageView dropDown = (ImageView) convertView.findViewById(R.id.ivGroupIndicator);
            dropDown.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final SecondLevelExpandableListView secondLevelELV = new SecondLevelExpandableListView(context);
        String[] headers;
        boolean hasSecondLevel = true;
        try {
            headers = secondLevel.get(groupPosition);
        } catch (IndexOutOfBoundsException e){
            hasSecondLevel = false;
            System.out.println("No children");
        };


        List<String> childData = new ArrayList<>();
        HashMap<String, String> secondLevelData;
        try {
            secondLevelData = data.get(groupPosition);
        } catch (IndexOutOfBoundsException e){
            hasSecondLevel = false;
        };

        if (hasSecondLevel) {
            secondLevelData = data.get(groupPosition);
            for (String key : secondLevelData.keySet()) {

                childData.add(secondLevelData.get(key));

            }
        }

        if (hasSecondLevel) {
            headers = secondLevel.get(groupPosition);
            secondLevelELV.setAdapter(new SecondLevelAdapter(context, headers, childData, mDarkMode));

            secondLevelELV.setGroupIndicator(null);

            secondLevelELV.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                int previousGroup = -1;

                @Override
                public void onGroupExpand(int groupPosition) {

                    if (groupPosition != previousGroup)
                        secondLevelELV.collapseGroup(previousGroup);
                    previousGroup = groupPosition;

                }
            });
        }

        return secondLevelELV;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}