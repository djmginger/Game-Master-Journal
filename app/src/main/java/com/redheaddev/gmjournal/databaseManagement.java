package com.redheaddev.gmjournal;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.redheaddev.gmjournal.cities.citiesDBHelper;
import com.redheaddev.gmjournal.cities.distances.distanceDBHelper;
import com.redheaddev.gmjournal.cities.locations.locationsDBHelper;
import com.redheaddev.gmjournal.loot.LootInfo;
import com.redheaddev.gmjournal.loot.lootDBHelper;
import com.redheaddev.gmjournal.misc.miscDBHelper;
import com.redheaddev.gmjournal.npcs.npcDBHelper;
import com.redheaddev.gmjournal.presets.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class databaseManagement extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private boolean darkMode;
    private Context context;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 673;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_management);
        context = this;

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String theme = sharedPreferences.getString("Theme", "none");

        LinearLayout mainLayout = findViewById(R.id.mainLayout);
        ImageView backButton = findViewById(R.id.backButton);
        Button deleteNPC = findViewById(R.id.deleteNPC);
        Button exportNPCS = findViewById(R.id.exportNPCs);
        Button exportCities = findViewById(R.id.exportCities);
        Button exportLocations = findViewById(R.id.exportLocations);
        Button exportLoot = findViewById(R.id.exportLoot);
        Button exportMisc = findViewById(R.id.exportMisc);
        Button deleteMisc = findViewById(R.id.deleteMisc);
        Button deleteCity = findViewById(R.id.deleteCity);
        Button deleteLoot = findViewById(R.id.deleteLoot);
        Button deleteLocations = findViewById(R.id.deleteLocations);
        Button deletePresets = findViewById(R.id.deletePresets);
        Button deleteDistances = findViewById(R.id.deleteDistances);
        TextView title = findViewById(R.id.title);

        if (theme.equals("dark")){

            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(this, R.color.mainBgColor));
            title.setTextColor(Color.WHITE);
            mainLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(this, R.color.black));
        }

        String miscName = sharedPreferences.getString("miscName", "none");
        String deleteToast = "Successfully deleted all Misc";
        if (!miscName.equals("none")) {
            deleteMisc.setText(String.format("%s %s", getString(R.string.deleteall), miscName));
            exportMisc.setText(String.format("%s %s", getString(R.string.export), miscName));
            deleteToast = String.format("Successfully deleted all %s", miscName);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        deleteNPC.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onClick(View view) {
                delete("npc_table", getString(R.string.deletenpcs));
            }
        });

        deleteCity.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                //builder.setTitle(R.string.app_name);
                builder.setMessage(R.string.yousurecity);
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getApplicationContext().deleteDatabase("city_table");
                        getApplicationContext().deleteDatabase("location_table");
                        getApplicationContext().deleteDatabase("distance_table");
                        toast(getString(R.string.deletecities));
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        deleteLoot.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onClick(View view) {
                delete("loot_table", getString(R.string.deleteloot));
            }
        });

        deleteLocations.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onClick(View view) {
                delete("location_table", getString(R.string.deletelocation));
            }
        });

        deletePresets.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onClick(View view) {
                delete("presets_table", getString(R.string.resetpreset));
            }
        });

        deleteDistances.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onClick(View view) {
                delete("distance_table", getString(R.string.deletedistance));
            }
        });

        final String finalDeleteToast = deleteToast;
        deleteMisc.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.
            @Override
            public void onClick(View view) {
                delete("misc_table", finalDeleteToast);
            }
        });

        exportNPCS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    new ExportDatabaseCSVTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"npc_table");
                }
                else {
                    requestWritePermission();
                }
            }
        });

        exportCities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    new ExportDatabaseCSVTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"city_table");
                }
                else {
                    requestWritePermission();
                }
            }
        });

        exportLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    new ExportDatabaseCSVTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"location_table");
                }
                else {
                    requestWritePermission();
                }
            }
        });

        exportLoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    new ExportDatabaseCSVTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"loot_table");
                }
                else {
                    requestWritePermission();
                }
            }
        });

        exportMisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    new ExportDatabaseCSVTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"misc_table");
                }
                else {
                    requestWritePermission();
                }
            }
        });
    }

    private void delete(final String database, final String text){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.yousure);
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getApplicationContext().deleteDatabase(database);
                toast(text);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public class ExportDatabaseCSVTask extends AsyncTask<String, Void, Boolean> {
        private String tableName;
        private npcDBHelper mNpcDBHelper;
        private citiesDBHelper mCitiesDBHelper;
        private lootDBHelper mLootDBHelper;
        private locationsDBHelper mLocationsDBHelper;
        private miscDBHelper mMiscDBHelper;
        private distanceDBHelper mDistanceDBHelper;
        private String fileName;
        Context context = databaseManagement.this;

        /*public ExportDatabaseCSVTask(String table) {
            super();
            tableName = table;
        }*/

        private final ProgressDialog dialog = new ProgressDialog(context);
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting database...");
            this.dialog.show();
        }

        protected Boolean doInBackground(final String... args) {
            String tableName = args[0];
            Log.d(TAG, "doInBackground: String passed to doInBackground is " + args[0]);
            Log.d(TAG, "doInBackground: initial environment is " + Environment.getDataDirectory());
            File exportDir = new File(context.getFilesDir() + "/GMJournalData/");
            if (!exportDir.exists()) {
                if (exportDir.mkdirs()) Log.d(TAG, "doInBackground: File directory was created");
                else Log.d(TAG, "doInBackground: File directory wasn't created");
            }
            Log.d(TAG, "doInBackground: The filepath is " + exportDir);
            Log.d(TAG, "doInBackground: does the filepath exist: " + exportDir.exists());
            fileName = tableName + ".csv";
            Log.d(TAG, "doInBackground: filename is " + fileName);
            File file = new File(exportDir, fileName);
            try {
                file.createNewFile();
                CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
                Cursor curCSV;
                switch (tableName){
                    case "npc_table":
                        mNpcDBHelper = new npcDBHelper(context);
                        curCSV = mNpcDBHelper.getNPCS();
                        break;
                    case "city_table":
                        mCitiesDBHelper = new citiesDBHelper(context);
                        curCSV = mCitiesDBHelper.getCities();
                        break;
                    case "location_table":
                        mLocationsDBHelper = new locationsDBHelper(context);
                        curCSV = mLocationsDBHelper.getAllLocations();
                        break;
                    case "loot_table":
                        mLootDBHelper = new lootDBHelper(context);
                        curCSV = mLootDBHelper.getLoot();
                        break;
                    case "misc_table":
                        mMiscDBHelper = new miscDBHelper(context);
                        curCSV = mMiscDBHelper.getMisc();
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + tableName);
                }
                 // this is where you'll have to do a lot of changing. Either huge case statements, or rename all database select all methods
                csvWrite.writeNext(curCSV.getColumnNames());
                while(curCSV.moveToNext()) {
                    String arrStr[]=null;
                    String[] mySecondStringArray = new String[curCSV.getColumnNames().length];
                    for(int i=0;i<curCSV.getColumnNames().length;i++)
                    {
                        mySecondStringArray[i] =curCSV.getString(i);
                    }
                    csvWrite.writeNext(mySecondStringArray);
                }
                csvWrite.close();
                curCSV.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        protected void onPostExecute(final Boolean success) {
            if (this.dialog.isShowing()) { this.dialog.dismiss(); }
            if (success) {
                Toast.makeText(context, "Export successful!", Toast.LENGTH_SHORT).show();
                ShareFile();
            } else {
                Toast.makeText(context, "Export failed", Toast.LENGTH_SHORT).show();
            }
        }

        private void ShareFile() {
            File exportDir = new File(context.getFilesDir(), "/GMJournalData/");
            Log.d(TAG, "ShareFile: The filename when shared is " + fileName);
            File sharingFile = new File(exportDir, fileName);
            Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
            shareIntent.setType("application/csv");
            //Uri uri = Uri.fromFile(sharingFile);
            Uri uri = FileProvider.getUriForFile(databaseManagement.this, "ccom.redheaddev.gmjournal.provider", sharingFile);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(shareIntent, "Share CSV"));
        }
    }

    private void requestWritePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(context) // Show an explanation to the user why we need the permission
                    .setTitle(getString(R.string.permissionneeded))
                    .setMessage(getString(R.string.permissiondb))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(databaseManagement.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE){
            // If request is cancelled, the result arrays are empty.
            new ExportDatabaseCSVTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"npc_table");
        }
    }
}
