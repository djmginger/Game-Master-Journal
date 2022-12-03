package com.redheaddev.gmjournal.cities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.LocaleList;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.redheaddev.gmjournal.MainActivity;
import com.redheaddev.gmjournal.MyRecyclerViewAdapter;
import com.redheaddev.gmjournal.R;

import java.util.ArrayList;
import java.util.Locale;

public class CityList extends AppCompatActivity implements MyRecyclerViewAdapter.OnNoteListener {

    private MyRecyclerViewAdapter adapter;
    private final ArrayList<String> cityList = new ArrayList<>();
    private final String TAG = "CityInfo";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_titles);

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String theme = sharedPreferences.getString("Theme", "none");
        Boolean darkMode = theme.equals("dark");

        context = this;
        LinearLayoutCompat layout = findViewById(R.id.listLayout);
        LinearLayoutCompat headerLayout = findViewById(R.id.headerLayout);
        RelativeLayout innerLayout = findViewById(R.id.innerLayout);
        ImageView addEntry = findViewById(R.id.addEntry);
        citiesDBHelper mCitiesDBHelper = new citiesDBHelper(this);
        ImageView backButton = findViewById(R.id.backButton);
        TextView listTitle = findViewById(R.id.listTitle);
        RecyclerView recyclerView = findViewById(R.id.info_content);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int deviceHeight = (displayMetrics.heightPixels);

        if (theme.equals("dark")){
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(addEntry.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));

            layout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            innerLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            listTitle.setBackgroundColor(Color.parseColor("#2C2C2C"));
            headerLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            addEntry.setBackgroundColor(Color.parseColor("#2C2C2C"));
            backButton.setBackgroundColor(Color.parseColor("#2C2C2C"));
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(addEntry.getDrawable()), ContextCompat.getColor(context, R.color.black));
        }

        String localeText = sharedPreferences.getString("Locale", "none");
        String loadLocale = String.valueOf(getResources().getConfiguration().locale);
        if(!localeText.equals(loadLocale)){
            Locale locale = new Locale(localeText);
            updateLocale(locale);
        }

        // set up the RecyclerView with data from the database
        Cursor cities = mCitiesDBHelper.getCities();
        try {
            if (cities.moveToNext()) {
                do {
                    cityList.add(cities.getString(1));
                } while (cities.moveToNext());
            } else{
                TextView noCity = new TextView(this);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,100,0,0);
                noCity.setLayoutParams(params);
                noCity.setText(R.string.nocitytext);
                noCity.setGravity(Gravity.CENTER_HORIZONTAL);
                noCity.setTextSize(20);
                noCity.setTypeface(null, Typeface.ITALIC);
                noCity.setTextColor(Color.parseColor("#666666"));
                innerLayout.addView(noCity);
            }
        } finally {
            cities.close();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        adapter = new MyRecyclerViewAdapter(this, cityList, this, darkMode);
        recyclerView.setAdapter(adapter);

        ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
        params.height = (int)(deviceHeight * .75);
        recyclerView.setLayoutParams(params);
        String headerText2 = sharedPreferences.getString("headerText2", "none");
        String headerColor2 = sharedPreferences.getString("headerColor2", "none");
        if (!headerText2.equals("none")) listTitle.setText(headerText2);
        else listTitle.setText(R.string.cities);
        if(!headerColor2.equals("none")) listTitle.setTextColor(Color.parseColor(headerColor2));
        else listTitle.setTextColor(Color.parseColor("#6FD8F8"));

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        addEntry.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CityInfo.class);
                startActivity(intent);
            }
        });
    }

    //When resuming the activity (going back from CityInfo, refresh the view with the names
    @Override
    protected void onResume() {
        super.onResume();
        cityList.clear();
        citiesDBHelper mCitiesDBHelper = new citiesDBHelper(this);
        Cursor cities = mCitiesDBHelper.getCities();
        try {
            if (cities.moveToNext()) {
                do {
                    cityList.add(cities.getString(1));
                } while (cities.moveToNext());
            }
        } finally {
            cities.close();
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onNoteClick(int position) {
        //If I want a reference of the list item I clicked on
        //Pass in the position as an extra to the new activity you'll create
        String cityName = cityList.get(position);
        Log.d(TAG, "onCreate: The value of cityTitle is " + cityName);
        Intent intent = new Intent(this, CityDisplay.class);
        intent.putExtra("cityName", cityName);
        intent.putExtra("addCityValue", 0);
        startActivity(intent);
    }

    @SuppressLint("NewApi")
    public void updateLocale(Locale locale) {
        Resources res = getResources();
        Locale.setDefault(locale);

        Configuration configuration = res.getConfiguration();

        if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 24) {
            LocaleList localeList = new LocaleList(locale);

            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);
            configuration.setLocale(locale);

        } else if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 17){
            configuration.setLocale(locale);

        } else {
            configuration.locale = locale;
        }

        res.updateConfiguration(configuration, res.getDisplayMetrics());
        recreate();
    }
}

