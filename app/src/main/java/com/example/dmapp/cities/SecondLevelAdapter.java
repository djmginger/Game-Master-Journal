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

package com.example.dmapp.cities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dmapp.R;
import java.util.List;


public class SecondLevelAdapter extends BaseExpandableListAdapter {
    private Context context;


    List<String> data;

    String[] headers;

    ImageView ivGroupIndicator;

    boolean mDarkMode;


    public SecondLevelAdapter(Context context, String[] headers, List<String> data, boolean darkMode) {
        this.context = context;
        this.data = data;
        this.headers = headers;
        this.mDarkMode = darkMode;
    }

    @Override
    public Object getGroup(int groupPosition) {

        return headers[groupPosition];
    }

    @Override
    public int getGroupCount() {

        return headers.length;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (!mDarkMode) convertView = inflater.inflate(R.layout.row_second, null);
            else convertView = inflater.inflate(R.layout.row_second2, null);
        TextView text = (TextView) convertView.findViewById(R.id.rowSecondText);
        String groupText = getGroup(groupPosition).toString();
        text.setText(groupText);

        if (isExpanded){

        }
        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        String childData;

        childData = data.get(childPosition);

        return childData;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (!mDarkMode) convertView = inflater.inflate(R.layout.row_third, null);
            else convertView = inflater.inflate(R.layout.row_third2, null);

        TextView textView = convertView.findViewById(R.id.rowThirdText);

        String childArray = data.get(groupPosition);

        String text = childArray;

        textView.setText(text);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String children = data.get(groupPosition);


        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
