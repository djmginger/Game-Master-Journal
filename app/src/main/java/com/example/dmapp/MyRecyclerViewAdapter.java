package com.example.dmapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private final List<String> mData;
    private final LayoutInflater mInflater;
    //For clickable rows
    private final OnNoteListener mOnNoteListener;
    private final Boolean mDarkMode;


    // data is passed into the constructor
    public MyRecyclerViewAdapter(Context context, List<String> data, OnNoteListener onNoteListener) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mOnNoteListener = onNoteListener;
        this.mDarkMode = false;
    }

    public MyRecyclerViewAdapter(Context context, List<String> data, OnNoteListener onNoteListener,Boolean darkMode) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mOnNoteListener = onNoteListener;
        this.mDarkMode = darkMode;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(!mDarkMode) {
            View view = mInflater.inflate(R.layout.recyclerview_row, parent, false);
            return new ViewHolder(view, mOnNoteListener);
        } else{
            View view = mInflater.inflate(R.layout.recyclerview_row3, parent, false);
            return new ViewHolder(view, mOnNoteListener);
        }
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String listItem = mData.get(position);
        holder.myTextView.setText(listItem);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView myTextView;
        final OnNoteListener onNoteListener;

        ViewHolder(View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.rowTitle);

            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }

    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught

    public interface OnNoteListener {
        void onNoteClick(int position);
    }

}



