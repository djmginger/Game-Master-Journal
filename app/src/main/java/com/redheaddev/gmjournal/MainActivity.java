package com.redheaddev.gmjournal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.LocaleList;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.redheaddev.gmjournal.cities.CityList;
import com.redheaddev.gmjournal.cities.distances.Distance;
import com.redheaddev.gmjournal.cities.distances.distanceDBHelper;
import com.redheaddev.gmjournal.cities.locations.LocationList;
import com.redheaddev.gmjournal.loot.LootList;
import com.redheaddev.gmjournal.misc.MiscList;
import com.redheaddev.gmjournal.npcs.NpcList;
import com.redheaddev.gmjournal.npcs.npcDBHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    private static final String TAG = "MainActivity";
    private int backButtonCount;
    BillingProcessor bp;
    Context context;
    Activity activity;
    Resources resources;
    Boolean darkModePurchased;
    private boolean hasUpdatedLocale = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String localeText = sharedPreferences.getString("Locale", "none");
        String loadLocale = String.valueOf(getResources().getConfiguration().locale);
        Log.d(TAG, "onCreate: The stored Locale is " + localeText);
        if(!localeText.equals(loadLocale)){
            Locale locale = new Locale(localeText);
            updateLocale(locale);
        }

        setContentView(R.layout.activity_main);
        context = this;
        activity = MainActivity.this;

        bp = new BillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqsg06Z8AmS00KH/fv+ucKHPQoxiIhjE5iNbMa92BQuip+928+vLib1jKiG+QjMUq2mHm2Igl1JpqawuFT0Fjp//vSH1j13W89ASBMZ62Plj9OxbWwKrQhxnQxDGirx7NZT6UcyxKTcgv4dGM4aW+UN5c6Pesfh9PLLLEPRXn+JEQp6rKdvttc/aB7dbv6ZTCMs51lVl5jKZ9lua/Ro9sGuwy9fY03baA2jnyIVYawNdG0YxMed7XRA8jEiPTWCc/kWQfmhPEc3s9VGu7kc6C8NcfV0m8RCY3mSkPEIHs4c7tmkXBcfPG7BOMorye28IVMxV7G831JUou0rmpqIUk7wIDAQAB", this);
        bp.initialize();

        npcDBHelper myNPC = npcDBHelper.getInstance(getApplicationContext());
        final LinearLayoutCompat mainLayout = findViewById(R.id.mainLayout);
        final TextView title1 = findViewById(R.id.title1);
        final Button npcs = findViewById(R.id.npcs);
        final Button cities = findViewById(R.id.cities);
        final Button locations = findViewById(R.id.locations);
        final Button loot = findViewById(R.id.loot);
        final Button misc = findViewById(R.id.misc);
        final ImageView edit = findViewById(R.id.editIcon);
        final Button databaseManagement = findViewById(R.id.databaseManagement);
        final Button themeSwitch = findViewById(R.id.themeSwitch);
        ImageView settings = findViewById(R.id.settings);
        ImageView language = findViewById(R.id.language);
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int deviceHeight = (displayMetrics.heightPixels);
        int deviceWidth = (displayMetrics.widthPixels);
        int imageWidth = (int) (deviceWidth * .40);
        int buttonWidth = (int) (deviceWidth * .65);
        int buttonWidth2 = (int) (deviceWidth * .5);
        int margin = (int)(deviceHeight * .04);
        int margin2 = (int)(deviceHeight * .06);
        int margin3 = (int)(deviceHeight * .04);
        int margin4 = (int)(deviceHeight * .05);
        Log.d(TAG, "onCreate: the locale on load is: " + getResources().getConfiguration().locale);

        final SharedPreferences.Editor editor = sharedPreferences.edit();

        String headerText1 = sharedPreferences.getString("headerText1", "none");
        String headerText2 = sharedPreferences.getString("headerText2", "none");
        String headerText3 = sharedPreferences.getString("headerText3", "none");
        String headerText4 = sharedPreferences.getString("headerText4", "none");
        String miscName = sharedPreferences.getString("miscName", "none");
        String headerColor1 = sharedPreferences.getString("headerColor1", "none");
        String headerColor2 = sharedPreferences.getString("headerColor2", "none");
        String headerColor3 = sharedPreferences.getString("headerColor3", "none");
        String headerColor4 = sharedPreferences.getString("headerColor4", "none");
        String headerColor5 = sharedPreferences.getString("headerColor5", "none");
        /*String localeText = sharedPreferences.getString("Locale", "none");
        String loadLocale = String.valueOf(getResources().getConfiguration().locale);
        Log.d(TAG, "onCreate: The stored Locale is " + localeText);
        if(!localeText.equals(loadLocale)){
            Locale locale = new Locale(localeText);
            updateLocale(locale);
            recreate();
        }*/

        if(!headerColor1.equals("none")) DrawableCompat.setTint(DrawableCompat.wrap(npcs.getBackground()), Color.parseColor(headerColor1));
        if(!headerColor2.equals("none")) DrawableCompat.setTint(DrawableCompat.wrap(cities.getBackground()), Color.parseColor(headerColor2));
        if(!headerColor3.equals("none")) DrawableCompat.setTint(DrawableCompat.wrap(locations.getBackground()), Color.parseColor(headerColor3));
        if(!headerColor4.equals("none")) DrawableCompat.setTint(DrawableCompat.wrap(loot.getBackground()), Color.parseColor(headerColor4));
        if(!headerColor5.equals("none")) DrawableCompat.setTint(DrawableCompat.wrap(misc.getBackground()), Color.parseColor(headerColor5));
        if(!headerText1.equals("none")) npcs.setText(headerText1);
        if(!headerText2.equals("none")) cities.setText(headerText2);
        if(!headerText3.equals("none")) locations.setText(headerText3);
        if(!headerText4.equals("none")) loot.setText(headerText4);
        if(!miscName.equals("none")) misc.setText(miscName);

        darkModePurchased = sharedPreferences.getBoolean("Dark Mode Purchased", false);
        String theme = sharedPreferences.getString("Theme", "none");
        if (theme.equals("none")) {
            editor.putString("Theme", "light");
            editor.apply();
            theme = sharedPreferences.getString("Theme", "none");
        }

        if (theme.equals("dark")) {
            mainLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            title1.setTextColor(Color.WHITE);
            DrawableCompat.setTint(DrawableCompat.wrap(settings.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(language.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(settings.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(language.getDrawable()), ContextCompat.getColor(context, R.color.black));
        }

        LinearLayoutCompat.LayoutParams lparams = new LinearLayoutCompat.LayoutParams(buttonWidth, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        lparams.setMargins(0, margin3, 0, 0);
        lparams.gravity = Gravity.CENTER_HORIZONTAL;

        npcs.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NpcList.class);
                startActivity(intent);
            }
        });

        cities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CityList.class);
                startActivity(intent);
            }
        });

        locations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LocationList.class);
                intent.putExtra("cityName", "None");
                startActivity(intent);
            }
        });

        loot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LootList.class);
                startActivity(intent);
            }
        });

        misc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putBoolean("hasBeenUpdated", false);
                Intent intent = new Intent(MainActivity.this, MiscList.class);
                startActivity(intent);
            }
        });

        databaseManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, databaseManagement.class);
                startActivity(intent);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OptionPage.class);
                startActivity(intent);
            }
        });

        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.language);

                String[] languages = {"English", "Español", "Deutsch", "Français", "日本語", "Русский"};
                builder.setItems(languages, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Locale usLocale = new Locale("en");
                                editor.putString("Locale", "en");
                                editor.apply();
                                updateLocale(usLocale);
                                dialog.dismiss();
                                break;// english
                            case 1:
                                Locale esLocale = new Locale("es");
                                editor.putString("Locale", "es");
                                editor.apply();
                                updateLocale(esLocale);
                                dialog.dismiss();
                                break;// spanish
                            case 2:
                                Locale deLocale = new Locale("de");
                                editor.putString("Locale", "de");
                                editor.apply();
                                updateLocale(deLocale);
                                dialog.dismiss();
                                break;// german
                            case 3:
                                Locale frLocale = new Locale("fr");
                                editor.putString("Locale", "fr");
                                editor.apply();
                                updateLocale(frLocale);
                                dialog.dismiss();
                                break;// french
                            case 4:
                                Locale jaLocale = new Locale("ja");
                                editor.putString("Locale", "ja");
                                editor.apply();
                                updateLocale(jaLocale);
                                dialog.dismiss();
                                break;// japanese
                            case 5:
                                Locale ruLocale = new Locale("ru");
                                editor.putString("Locale", "ru");
                                editor.apply();
                                updateLocale(ruLocale);
                                dialog.dismiss();
                                break;// russian
                        }
                        recreate();
                    }
                });

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                // create and show the alert dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        //Attempt to load purchases and check if the user has already bought dark mode. I can't tell but I'm not sure the first one works.
        bp.loadOwnedPurchasesFromGoogle();
        List<String> products = bp.listOwnedProducts();
        if (products.contains("gmjournal.darkmode")){
            editor.putBoolean("Dark Mode Purchased", true);
            editor.apply();
            darkModePurchased = sharedPreferences.getBoolean("Dark Mode Purchased", false);
        }

        TransactionDetails transactionDetails = bp.getPurchaseTransactionDetails("gmjournal.darkmode");
        if (transactionDetails != null) {
            editor.putBoolean("Dark Mode Purchased", true);
            editor.apply();
            darkModePurchased = sharedPreferences.getBoolean("Dark Mode Purchased", false);
        }

        //final Button themeSwitch = new Button(this);
        if (theme.equals("light")) {
            themeSwitch.setText(R.string.darkmode);
        } else if (theme.equals("dark")){
            themeSwitch.setText(R.string.lightmode);
        }

        themeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(darkModePurchased) {
                    String theme = sharedPreferences.getString("Theme", "none");
                    if (theme.equals("light")) {
                        themeSwitch.setText(R.string.lightmode);
                        editor.putString("Theme", "dark");
                        editor.apply();

                        mainLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
                        title1.setTextColor(Color.WHITE);
                        DrawableCompat.setTint(DrawableCompat.wrap(settings.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
                        DrawableCompat.setTint(DrawableCompat.wrap(language.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));

                    } else if (theme.equals("dark")) {
                        themeSwitch.setText(R.string.darkmode);
                        editor.putString("Theme", "light");
                        editor.apply();

                        mainLayout.setBackgroundColor(getResources().getColor(R.color.mainBgColor));
                        title1.setTextColor(Color.BLACK);
                        DrawableCompat.setTint(DrawableCompat.wrap(settings.getDrawable()), ContextCompat.getColor(context, R.color.black));
                        DrawableCompat.setTint(DrawableCompat.wrap(language.getDrawable()), ContextCompat.getColor(context, R.color.black));
                    }
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(R.string.purchasedark);
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                            bp.purchase(activity, "gmjournal.darkmode");
                        }
                    });
                    builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        //This code is to fix the distance display. The only reason it's here is to make sure people who've already made distances see the new changes. The distanceList activity has been updated with new logic
        distanceDBHelper mDistanceDBHelper = new distanceDBHelper(context);
        Cursor distanceCursor = mDistanceDBHelper.getAllDistances();
        if (distanceCursor.moveToNext()) {
            do {
                //If the distance value isn't in the DB as city1 > city2 AND city2 > city1, then add it
                boolean isNotBackwards = mDistanceDBHelper.checkNonExistence(distanceCursor.getString(2), distanceCursor.getString(1));
                if(isNotBackwards){
                    mDistanceDBHelper.addDistance(distanceCursor.getString(2), distanceCursor.getString(1), distanceCursor.getString(3));
                }
            } while (distanceCursor.moveToNext());
        }
    }

    @Override
    public void onBackPressed()
    {
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, getString(R.string.presstoexit), Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String headerText1 = sharedPreferences.getString("headerText1", "none");
        String headerText2 = sharedPreferences.getString("headerText2", "none");
        String headerText3 = sharedPreferences.getString("headerText3", "none");
        String headerText4 = sharedPreferences.getString("headerText4", "none");
        String miscName = sharedPreferences.getString("miscName", "none");
        String headerColor1 = sharedPreferences.getString("headerColor1", "none");
        String headerColor2 = sharedPreferences.getString("headerColor2", "none");
        String headerColor3 = sharedPreferences.getString("headerColor3", "none");
        String headerColor4 = sharedPreferences.getString("headerColor4", "none");
        String headerColor5 = sharedPreferences.getString("headerColor5", "none");

        final Button npcs = findViewById(R.id.npcs);
        final Button cities = findViewById(R.id.cities);
        final Button locations = findViewById(R.id.locations);
        final Button loot = findViewById(R.id.loot);
        final Button misc = findViewById(R.id.misc);
        if(!headerColor1.equals("none")) DrawableCompat.setTint(DrawableCompat.wrap(npcs.getBackground()), Color.parseColor(headerColor1));
        else DrawableCompat.setTint(DrawableCompat.wrap(npcs.getBackground()), ContextCompat.getColor(context, R.color.mainColor1));

        if(!headerColor2.equals("none")) DrawableCompat.setTint(DrawableCompat.wrap(cities.getBackground()), Color.parseColor(headerColor2));
        else DrawableCompat.setTint(DrawableCompat.wrap(cities.getBackground()), ContextCompat.getColor(context, R.color.mainColor2));

        if(!headerColor3.equals("none")) DrawableCompat.setTint(DrawableCompat.wrap(locations.getBackground()), Color.parseColor(headerColor3));
        else DrawableCompat.setTint(DrawableCompat.wrap(locations.getBackground()), ContextCompat.getColor(context, R.color.mainColor2));

        if(!headerColor4.equals("none")) DrawableCompat.setTint(DrawableCompat.wrap(loot.getBackground()), Color.parseColor(headerColor4));
        else DrawableCompat.setTint(DrawableCompat.wrap(loot.getBackground()), ContextCompat.getColor(context, R.color.mainColor3));

        if(!headerColor5.equals("none")) DrawableCompat.setTint(DrawableCompat.wrap(misc.getBackground()), Color.parseColor(headerColor5));
        else DrawableCompat.setTint(DrawableCompat.wrap(misc.getBackground()), ContextCompat.getColor(context, R.color.mainColor5));

        if(!headerText1.equals("none")) npcs.setText(headerText1);
        else npcs.setText(getString(R.string.npcs));

        if(!headerText2.equals("none")) cities.setText(headerText2);
        else cities.setText(getString(R.string.cities));

        if(!headerText3.equals("none")) locations.setText(headerText3);
        else locations.setText(getString(R.string.locations));

        if(!headerText4.equals("none")) loot.setText(headerText4);
        else loot.setText(getString(R.string.loot));

        if(!miscName.equals("none")) misc.setText(miscName);
        else misc.setText(getString(R.string.misc));
    }

    @Override
    public void onBillingInitialized() {
        /*
         * Called when BillingProcessor was initialized and it's ready to purchase
         */
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        /*
         * Called when requested PRODUCT ID was successfully purchased
         */
        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("Dark Mode Purchased", true);
        editor.apply();
        darkModePurchased = sharedPreferences.getBoolean("Dark Mode Purchased", false);
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        toast("Something went wrong!");
        /*
         * Called when some error occurred. See Constants class for more details
         *
         * Note - this includes handling the case where the user canceled the buy dialog:
         * errorCode = Constants.BILLING_RESPONSE_RESULT_USER_CANCELED
         */
    }

    @Override
    public void onPurchaseHistoryRestored() {
        /*
         * Called when purchase history was restored and the list of all owned PRODUCT ID's
         * was loaded from Google Play
         */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @SuppressLint("NewApi")
    public void updateLocale(Locale locale) {
        Resources res = getResources();
        Locale.setDefault(locale);

        Configuration configuration = res.getConfiguration();

        if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 24) {
            Log.e(TAG, "updateLocale: the os version is " + Integer.parseInt(android.os.Build.VERSION.SDK));
            LocaleList localeList = new LocaleList(locale);

            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);
            configuration.setLocale(locale);

        } else if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 17){
            Log.e(TAG, "updateLocale: the os version is " + Integer.parseInt(android.os.Build.VERSION.SDK));
            configuration.setLocale(locale);

        } else {
            configuration.locale = locale;
        }

        res.updateConfiguration(configuration, res.getDisplayMetrics());
        recreate();
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
