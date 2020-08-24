package com.redheaddev.gmjournal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.redheaddev.gmjournal.cities.CityList;
import com.redheaddev.gmjournal.loot.LootList;
import com.redheaddev.gmjournal.npcs.NpcList;
import com.redheaddev.gmjournal.npcs.npcDBHelper;

public class MainActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    private static final String TAG = "MainActivity";
    private int backButtonCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        npcDBHelper myNPC = npcDBHelper.getInstance(getApplicationContext());
        final LinearLayoutCompat mainLayout = findViewById(R.id.mainLayout);
        final TextView title1 = findViewById(R.id.title1);
        final TextView title2 = findViewById(R.id.title2);
        final Button npcs = findViewById(R.id.npcs);
        final Button cities = findViewById(R.id.cities);
        final Button loot = findViewById(R.id.loot);
        final Button databaseManagement = findViewById(R.id.databaseManagement);
        final Button themeSwitch = findViewById(R.id.themeSwitch);

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

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        String theme = sharedPreferences.getString("Theme", "none");
        if (theme.equals("none")) {
            editor.putString("Theme", "light");
            editor.apply();
            theme = sharedPreferences.getString("Theme", "none");
        }


        if (theme.equals("dark")) {
            mainLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            title1.setTextColor(Color.WHITE);
            title2.setTextColor(Color.WHITE);
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

        loot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LootList.class);
                startActivity(intent);
            }
        });

        databaseManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, databaseManagement.class);
                startActivity(intent);
            }
        });

        //final Button themeSwitch = new Button(this);
        if (theme.equals("light")) {
            themeSwitch.setText(R.string.darkmode);
        } else if (theme.equals("dark")){
            themeSwitch.setText(R.string.lightmode);
        }

        themeSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String theme = sharedPreferences.getString("Theme", "none");
                if (theme.equals("light")){
                    themeSwitch.setText(R.string.lightmode);
                    editor.putString("Theme", "dark");
                    editor.apply();

                    mainLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
                    title1.setTextColor(Color.WHITE);
                    title2.setTextColor(Color.WHITE);

                } else if (theme.equals("dark")) {
                    themeSwitch.setText(R.string.darkmode);
                    editor.putString("Theme", "light");
                    editor.apply();

                    mainLayout.setBackgroundColor(getResources().getColor(R.color.mainBgColor));
                    title1.setTextColor(Color.BLACK);
                    title2.setTextColor(Color.BLACK);
                }
            }
        });
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
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
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

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
