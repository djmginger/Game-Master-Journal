package com.redheaddev.gmjournal;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.redheaddev.gmjournal.cities.CityList;
import com.redheaddev.gmjournal.loot.LootList;
import com.redheaddev.gmjournal.npcs.NpcList;
import com.redheaddev.gmjournal.npcs.npcDBHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {
    private static final String TAG = "MainActivity";
    private int backButtonCount;
    BillingProcessor bp;
    Context context;
    Activity activity;
    Boolean darkModePurchased;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

                    } else if (theme.equals("dark")) {
                        themeSwitch.setText(R.string.darkmode);
                        editor.putString("Theme", "light");
                        editor.apply();

                        mainLayout.setBackgroundColor(getResources().getColor(R.color.mainBgColor));
                        title1.setTextColor(Color.BLACK);
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

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
