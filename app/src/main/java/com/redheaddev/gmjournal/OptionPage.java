package com.redheaddev.gmjournal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.LocaleList;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.redheaddev.gmjournal.cities.citiesDBHelper;
import com.redheaddev.gmjournal.cities.distances.distanceDBHelper;
import com.redheaddev.gmjournal.cities.locations.locationsDBHelper;
import com.redheaddev.gmjournal.loot.lootDBHelper;
import com.redheaddev.gmjournal.misc.miscDBHelper;
import com.redheaddev.gmjournal.npcs.npcDBHelper;
import com.redheaddev.gmjournal.presets.CSVWriter;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;


public class OptionPage extends AppCompatActivity {
    private static final String TAG = "OptionPage";
    private boolean darkMode;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_page);
        context = this;

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        String theme = sharedPreferences.getString("Theme", "none");

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

        LinearLayout mainLayout = findViewById(R.id.mainLayout);
        ImageView backButton = findViewById(R.id.backButton);
        TextView header1 = findViewById(R.id.header1);
        TextView header2 = findViewById(R.id.header2);
        TextView header3 = findViewById(R.id.header3);
        TextView header4 = findViewById(R.id.header4);
        TextView header5 = findViewById(R.id.header5);
        Button editHeader1 = findViewById(R.id.editHeader1);
        Button editColor1 = findViewById(R.id.editColor1);
        Button editInfoColor1 = findViewById(R.id.editInfoColor1);
        Button editHeader2 = findViewById(R.id.editHeader2);
        Button editColor2 = findViewById(R.id.editColor2);
        Button editInfoColor2 = findViewById(R.id.editInfoColor2);
        Button editHeader3 = findViewById(R.id.editHeader3);
        Button editColor3 = findViewById(R.id.editColor3);
        Button editInfoColor3 = findViewById(R.id.editInfoColor3);
        Button editHeader4 = findViewById(R.id.editHeader4);
        Button editColor4 = findViewById(R.id.editColor4);
        Button editInfoColor4 = findViewById(R.id.editInfoColor4);
        Button editHeader5 = findViewById(R.id.editHeader5);
        Button editColor5 = findViewById(R.id.editColor5);
        Button editInfoColor5 = findViewById(R.id.editInfoColor5);
        Button reset1 = findViewById(R.id.reset1);
        Button reset2 = findViewById(R.id.reset2);
        Button reset3 = findViewById(R.id.reset3);
        Button reset4 = findViewById(R.id.reset4);
        Button reset5 = findViewById(R.id.reset5);
        TextView title = findViewById(R.id.title);

        if (theme.equals("dark")){
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(this, R.color.mainBgColor));
            title.setTextColor(Color.WHITE);
            mainLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(this, R.color.black));
        }

        if(!headerColor1.equals("none")) DrawableCompat.setTint(DrawableCompat.wrap(header1.getBackground()), Color.parseColor(headerColor1));
        if(!headerColor2.equals("none")) DrawableCompat.setTint(DrawableCompat.wrap(header2.getBackground()), Color.parseColor(headerColor2));
        if(!headerColor3.equals("none")) DrawableCompat.setTint(DrawableCompat.wrap(header3.getBackground()), Color.parseColor(headerColor3));
        if(!headerColor4.equals("none")) DrawableCompat.setTint(DrawableCompat.wrap(header4.getBackground()), Color.parseColor(headerColor4));
        if(!headerColor5.equals("none")) DrawableCompat.setTint(DrawableCompat.wrap(header5.getBackground()), Color.parseColor(headerColor5));
        if(!headerText1.equals("none")) header1.setText(headerText1);
        if(!headerText2.equals("none")) header2.setText(headerText2);
        if(!headerText3.equals("none")) header3.setText(headerText3);
        if(!headerText4.equals("none")) header4.setText(headerText4);
        if(!miscName.equals("none")) header5.setText(miscName);

        String localeText = sharedPreferences.getString("Locale", "none");
        String loadLocale = String.valueOf(getResources().getConfiguration().locale);
        if(!localeText.equals(loadLocale)){
            Locale locale = new Locale(localeText);
            updateLocale(locale);
        }

        editColor1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText edittext = new EditText(context);
                alert.setMessage(R.string.entercolor);
                alert.setTitle(R.string.editcolor);
                alert.setView(edittext);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String hexcolor = "#" + edittext.getText().toString();
                        try{
                            DrawableCompat.setTint(DrawableCompat.wrap(header1.getBackground()), Color.parseColor(hexcolor));
                            dialog.dismiss();
                            editor.putString("headerColor1", hexcolor);
                            editor.apply();
                        } catch (IllegalArgumentException i){
                            toast("That is not a valid hex color code. Please try again.");
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        editColor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText edittext = new EditText(context);
                alert.setMessage(R.string.entercolor);
                alert.setTitle(R.string.editcolor);
                alert.setView(edittext);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String hexcolor = "#" + edittext.getText().toString();
                        try{
                            DrawableCompat.setTint(DrawableCompat.wrap(header2.getBackground()), Color.parseColor(hexcolor));
                            dialog.dismiss();
                            editor.putString("headerColor2", hexcolor);
                            editor.apply();
                        } catch (IllegalArgumentException i){
                            toast("That is not a valid hex color code. Please try again.");
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        editColor3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText edittext = new EditText(context);
                alert.setMessage(R.string.entercolor);
                alert.setTitle(R.string.editcolor);
                alert.setView(edittext);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String hexcolor = "#" + edittext.getText().toString();
                        try{
                            DrawableCompat.setTint(DrawableCompat.wrap(header3.getBackground()), Color.parseColor(hexcolor));
                            dialog.dismiss();
                            editor.putString("headerColor3", hexcolor);
                            editor.apply();
                        } catch (IllegalArgumentException i){
                            toast("That is not a valid hex color code. Please try again.");
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        editColor4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText edittext = new EditText(context);
                alert.setMessage(R.string.entercolor);
                alert.setTitle(R.string.editcolor);
                alert.setView(edittext);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String hexcolor = "#" + edittext.getText().toString();
                        try{
                            DrawableCompat.setTint(DrawableCompat.wrap(header4.getBackground()), Color.parseColor(hexcolor));
                            dialog.dismiss();
                            editor.putString("headerColor4", hexcolor);
                            editor.apply();
                        } catch (IllegalArgumentException i){
                            toast("That is not a valid hex color code. Please try again.");
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        editColor5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText edittext = new EditText(context);
                alert.setMessage(R.string.entercolor);
                alert.setTitle(R.string.editcolor);
                alert.setView(edittext);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String hexcolor = "#" + edittext.getText().toString();
                        try{
                            DrawableCompat.setTint(DrawableCompat.wrap(header5.getBackground()), Color.parseColor(hexcolor));
                            dialog.dismiss();
                            editor.putString("headerColor5", hexcolor);
                            editor.apply();
                        } catch (IllegalArgumentException i){
                            toast("That is not a valid hex color code. Please try again.");
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        editHeader1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText edittext = new EditText(context);
                alert.setMessage(R.string.enterheader);
                alert.setTitle(R.string.editheader);
                alert.setView(edittext);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newheader = edittext.getText().toString();
                        header1.setText(newheader);
                        dialog.dismiss();
                        editor.putString("headerText1", newheader);
                        editor.apply();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        editHeader2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText edittext = new EditText(context);
                alert.setMessage(R.string.enterheader);
                alert.setTitle(R.string.editheader);
                alert.setView(edittext);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newheader = edittext.getText().toString();
                        header2.setText(newheader);
                        dialog.dismiss();
                        editor.putString("headerText2", newheader);
                        editor.apply();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        editHeader3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText edittext = new EditText(context);
                alert.setMessage(R.string.enterheader);
                alert.setTitle(R.string.editheader);
                alert.setView(edittext);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newheader = edittext.getText().toString();
                        header3.setText(newheader);
                        dialog.dismiss();
                        editor.putString("headerText3", newheader);
                        editor.apply();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        editHeader4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText edittext = new EditText(context);
                alert.setMessage(R.string.enterheader);
                alert.setTitle(R.string.editheader);
                alert.setView(edittext);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newheader = edittext.getText().toString();
                        header4.setText(newheader);
                        dialog.dismiss();
                        editor.putString("headerText4", newheader);
                        editor.apply();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        editHeader5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText edittext = new EditText(context);
                alert.setMessage(R.string.enterheader);
                alert.setTitle(R.string.editheader);
                alert.setView(edittext);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newheader = edittext.getText().toString();
                        header5.setText(newheader);
                        dialog.dismiss();
                        editor.putString("headerText5", newheader);
                        editor.apply();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        editInfoColor1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText edittext = new EditText(context);
                alert.setMessage(R.string.entercolor);
                alert.setTitle(R.string.editcolor);
                alert.setView(edittext);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String hexcolor = "#" + edittext.getText().toString();
                        try{
                            dialog.dismiss();
                            editor.putString("infoboxcolor1", hexcolor);
                            editor.apply();
                        } catch (IllegalArgumentException i){
                            toast("That is not a valid hex color code. Please try again.");
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        editInfoColor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText edittext = new EditText(context);
                alert.setMessage(R.string.entercolor);
                alert.setTitle(R.string.editcolor);
                alert.setView(edittext);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String hexcolor = "#" + edittext.getText().toString();
                        try{
                            dialog.dismiss();
                            editor.putString("infoboxcolor2", hexcolor);
                            editor.apply();
                        } catch (IllegalArgumentException i){
                            toast("That is not a valid hex color code. Please try again.");
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        editInfoColor3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText edittext = new EditText(context);
                alert.setMessage(R.string.entercolor);
                alert.setTitle(R.string.editcolor);
                alert.setView(edittext);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String hexcolor = "#" + edittext.getText().toString();
                        try{
                            dialog.dismiss();
                            editor.putString("infoboxcolor3", hexcolor);
                            editor.apply();
                        } catch (IllegalArgumentException i){
                            toast("That is not a valid hex color code. Please try again.");
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        editInfoColor4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText edittext = new EditText(context);
                alert.setMessage(R.string.entercolor);
                alert.setTitle(R.string.editcolor);
                alert.setView(edittext);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String hexcolor = "#" + edittext.getText().toString();
                        try{
                            dialog.dismiss();
                            editor.putString("infoboxcolor4", hexcolor);
                            editor.apply();
                        } catch (IllegalArgumentException i){
                            toast("That is not a valid hex color code. Please try again.");
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        editInfoColor5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText edittext = new EditText(context);
                alert.setMessage(R.string.entercolor);
                alert.setTitle(R.string.editcolor);
                alert.setView(edittext);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String hexcolor = "#" + edittext.getText().toString();
                        try{
                            dialog.dismiss();
                            editor.putString("infoboxcolor5", hexcolor);
                            editor.apply();
                        } catch (IllegalArgumentException i){
                            toast("That is not a valid hex color code. Please try again.");
                        }
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        reset1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle(R.string.resetHeader);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        header1.setText(getString(R.string.npcs));
                        DrawableCompat.setTint(DrawableCompat.wrap(header1.getBackground()), ContextCompat.getColor(context, R.color.mainColor1));
                        editor.putString("headerColor1", "none");
                        editor.putString("infoboxcolor1", "none");
                        editor.putString("headerText1", "none");
                        editor.apply();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        reset2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle(R.string.resetHeader);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DrawableCompat.setTint(DrawableCompat.wrap(header2.getBackground()), ContextCompat.getColor(context, R.color.mainColor2));
                        header2.setText(getString(R.string.cities));
                        editor.putString("headerColor2", "none");
                        editor.putString("infoboxcolor2", "none");
                        editor.putString("headerText2", "none");
                        editor.apply();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        reset3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle(R.string.resetHeader);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DrawableCompat.setTint(DrawableCompat.wrap(header3.getBackground()), ContextCompat.getColor(context, R.color.mainColor2));
                        header3.setText(getString(R.string.locations));
                        editor.putString("headerColor3", "none");
                        editor.putString("infoboxcolor3", "none");
                        editor.putString("headerText3", "none");
                        editor.apply();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        reset4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle(R.string.resetHeader);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DrawableCompat.setTint(DrawableCompat.wrap(header4.getBackground()), ContextCompat.getColor(context, R.color.mainColor3));
                        header4.setText(getString(R.string.loot));
                        editor.putString("headerColor4", "none");
                        editor.putString("infoboxcolor4", "none");
                        editor.putString("headerText4", "none");
                        editor.apply();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        reset5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle(R.string.resetHeader);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DrawableCompat.setTint(DrawableCompat.wrap(header5.getBackground()), ContextCompat.getColor(context, R.color.mainColor5));
                        header5.setText(getString(R.string.misc));
                        editor.putString("headerColor5", "none");
                        editor.putString("infoboxcolor5", "none");
                        editor.putString("miscName", "none");
                        editor.apply();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
