package com.redheaddev.gmjournal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.LocaleList;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;


public class databaseManagement extends AppCompatActivity {
    private static final String TAG = "DatabaseManagement";
    private boolean darkMode;
    private Context context;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 673;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 672;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_management);
        context = this;

        verifyStoragePermissions(this);

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
        Button importNPCS = findViewById(R.id.importNpcs);
        Button importCities = findViewById(R.id.importCities);
        Button importLocations = findViewById(R.id.importLocations);
        Button importLoot = findViewById(R.id.importLoot);
        Button importMisc = findViewById(R.id.importMisc);
        Button deleteMisc = findViewById(R.id.deleteMisc);
        Button deleteCity = findViewById(R.id.deleteCity);
        Button deleteLoot = findViewById(R.id.deleteLoot);
        Button deleteLocations = findViewById(R.id.deleteLocations);
        Button deletePresets = findViewById(R.id.deletePresets);
        Button deleteDistances = findViewById(R.id.deleteDistances);
        TextView title = findViewById(R.id.title);
        TextView dbheader1 = findViewById(R.id.dbheader1);
        TextView dbheader2 = findViewById(R.id.dbheader2);
        TextView dbheader3 = findViewById(R.id.dbheader3);
        TextView dbheader4 = findViewById(R.id.dbheader4);
        TextView dbheader5 = findViewById(R.id.dbheader5);
        TextView dbheader6 = findViewById(R.id.dbheader6);
        TextView dbheader7 = findViewById(R.id.dbheader7);


        if (theme.equals("dark")){
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(this, R.color.mainBgColor));
            title.setTextColor(Color.WHITE);
            dbheader1.setTextColor(Color.WHITE);
            dbheader2.setTextColor(Color.WHITE);
            dbheader3.setTextColor(Color.WHITE);
            dbheader4.setTextColor(Color.WHITE);
            dbheader5.setTextColor(Color.WHITE);
            dbheader6.setTextColor(Color.WHITE);
            dbheader7.setTextColor(Color.WHITE);
            mainLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(this, R.color.black));
        }

        String localeText = sharedPreferences.getString("Locale", "none");
        String loadLocale = String.valueOf(getResources().getConfiguration().locale);
        if(!localeText.equals(loadLocale)){
            Locale locale = new Locale(localeText);
            updateLocale(locale);
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

        importNPCS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("When importing a file, make sure you have exported a test file so you can properly match the formatting. In addition, remove any line breaks or returns from your text fields, as they will cause the import to fail. Text formatting can be done within the app once imported.");
                builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        importy("npc_table");
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
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

        importCities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("When importing a file, make sure you have exported a test file so you can properly match the formatting. In addition, remove any line breaks or returns from your text fields, as they will cause the import to fail. Text formatting can be done within the app once imported.");
                builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        importy("city_table");
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
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

        importLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("When importing a file, make sure you have exported a test file so you can properly match the formatting. In addition, remove any line breaks or returns from your text fields, as they will cause the import to fail. Text formatting can be done within the app once imported.");
                builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        importy("location_table");
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
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

        importLoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("When importing a file, make sure you have exported a test file so you can properly match the formatting. In addition, remove any line breaks or returns from your text fields, as they will cause the import to fail. Text formatting can be done within the app once imported.");
                builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        importy("loot_table");
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
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

        importMisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("When importing a file, make sure you have exported a test file so you can properly match the formatting. In addition, remove any line breaks or returns from your text fields, as they will cause the import to fail. Text formatting can be done within the app once imported.");
                builder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        importy("misc_table");
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
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

    private void importy(final String database){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importing a file will REPLACE all previous info with the new info.");
        builder.setMessage("Importing a file requires you to match the csv format exactly. Export an existing database that has an entry, and use that as a reference.");
        builder.setPositiveButton(getString(R.string.importy), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                selectCSVFile(database);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void selectCSVFile(String database){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        switch (database){
            case "npc_table": startActivityForResult(Intent.createChooser(intent, "Open CSV"), 1); break;
            case "loot_table" : startActivityForResult(Intent.createChooser(intent, "Open CSV"), 2); break;
            case "city_table" : startActivityForResult(Intent.createChooser(intent, "Open CSV"), 3); break;
            case "location_table" : startActivityForResult(Intent.createChooser(intent, "Open CSV"), 4); break;
            case "misc_table" : startActivityForResult(Intent.createChooser(intent, "Open CSV"), 5); break;
        }
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

                csvWrite.writeNext(curCSV.getColumnNames());
                while(curCSV.moveToNext()) {
                    String arrStr[]=null;
                    String[] mySecondStringArray = new String[curCSV.getColumnNames().length];
                    for(int i=0; i<curCSV.getColumnNames().length; i++)
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri = data.getData();
        //String path = uri.getPath();
        String path = FileUtils.getRealPathFromURI_API19(context, uri);
        Log.d(TAG, "onActivityResult: Path is " + path);
        //Assign integer values to each database, and use that corresponding value as the request code, then call proImportCSV with the appropriate db name passed as a string
        switch (requestCode) {
            case 1: importCSV(new File(path), "npc_table"); break;
            case 2: importCSV(new File(path), "loot_table"); break;
            case 3: importCSV(new File(path), "city_table"); break;
            case 4: importCSV(new File(path), "location_table"); break;
            case 5: importCSV(new File(path), "misc_table"); break;
        }
    }

    private void importCSV(File from, String dbName){
        Log.d(TAG, "proImportCSV: File path is " + from);
        try {
            FileReader file = new FileReader(from);
            Log.d(TAG, "proImportCSV: Filereader works");
            BufferedReader buffer = new BufferedReader(file);
            Log.d(TAG, "proImportCSV: BufferedReader works");
            String line = "";
            //Have a switch statement that sets the value of columns depending on the dbName
            String columns = "";
            int columnNumber = 0;

            npcDBHelper mNpcDBHelper = new npcDBHelper(this);
            citiesDBHelper mCitiesDBHelper = new citiesDBHelper(this);
            lootDBHelper mLootDBHelper = new lootDBHelper(this);
            locationsDBHelper mLocationDBHelper = new locationsDBHelper(this);
            miscDBHelper mMiscDBHelper = new miscDBHelper(this);
            SQLiteDatabase db;

            switch (dbName){
                case "npc_table":
                    columns = "ID, name, location, description, notes, voice, plothooks, race, image";
                    db = mNpcDBHelper.getReadableDatabase();
                    db.execSQL("DROP TABLE " + dbName); //Delete all previous entries in the database so the ID can begin at 1 again.
                    mNpcDBHelper.onCreate(db);
                    columnNumber = 9;
                    break;

                case "city_table":
                    columns = "ID, name, environment, population, economy, notes, image";
                    db = mCitiesDBHelper.getReadableDatabase();
                    db.execSQL("DROP TABLE " + dbName); //Delete all previous entries in the database so the ID can begin at 1 again.
                    mCitiesDBHelper.onCreate(db);
                    columnNumber = 7;
                    break;

                case "loot_table":
                    columns = "ID, name, rarity, price, description, details, image, requirements, lootGroup";
                    db = mLootDBHelper.getReadableDatabase();
                    db.execSQL("DROP TABLE " + dbName); //Delete all previous entries in the database so the ID can begin at 1 again.
                    mLootDBHelper.onCreate(db);
                    columnNumber = 9;
                    break;

                case "location_table":
                    columns = "ID, cityname, locname, description, plothooks, notes";
                    db = mLocationDBHelper.getReadableDatabase();
                    db.execSQL("DROP TABLE " + dbName); //Delete all previous entries in the database so the ID can begin at 1 again.
                    mLocationDBHelper.onCreate(db);
                    columnNumber = 6;
                    break;

                case "misc_table":
                    columns = "ID, info1, info2, info3, info4, image, linkednpc, linkedcity, linkedloot, linkeditemgroup, miscgroup";
                    db = mMiscDBHelper.getReadableDatabase();
                    db.execSQL("DROP TABLE " + dbName); //Delete all previous entries in the database so the ID can begin at 1 again.
                    mMiscDBHelper.onCreate(db);
                    columnNumber = 11;
                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + dbName);
            }

            String str1 = "INSERT INTO " + dbName + " (" + columns + ") values(";
            String str2 = ");";

            db.beginTransaction(); //another switch statement that determines which database you're going to open
            buffer.readLine(); //skip first line
            while ((line = buffer.readLine()) != null) {
                StringBuilder sb = new StringBuilder(str1);
                String[] str = line.split(",");
                Log.d(TAG, "importCSV: the imported string is: " + Arrays.toString(str));
                sb.append("'" + str[0].replace("\"","").replace("'", "''") + "','");
                sb.append(str[1].replace("\"","").replace("'", "''") + "','");
                sb.append(str[2].replace("\"","").replace("'", "''") + "','");
                sb.append(str[3].replace("\"","").replace("'", "''") + "','");
                sb.append(str[4].replace("\"","").replace("'", "''") + "','");
                sb.append(str[5].replace("\"","").replace("'", "''") + "'");
                if (columnNumber > 6){
                    sb.append(",'" + str[6].replace("\"","").replace("'", "''") + "'");
                    if (columnNumber > 7){
                        sb.append(",'" + str[7].replace("\"","").replace("'", "''") + "','");
                        sb.append(str[8].replace("\"","").replace("'", "''") + "'");
                        if (columnNumber > 9){
                            sb.append(",'" + str[9].replace("\"","").replace("'", "''") + "',");
                            sb.append("'" + str[10].replace("\"","").replace("'", "''") + "'");
                        }
                    }
                }
                sb.append(str2);
                Log.d(TAG, "proImportCSV: The sql command is: " + sb.toString());
                db.execSQL(sb.toString());
            }
            db.setTransactionSuccessful();
            toast("Import successful!");
            db.endTransaction();
        } catch (FileNotFoundException e){
            toast("Error: File not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }

    private void requestStoragePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(context) // Show an explanation to the user why we need the permission
                    .setTitle(getString(R.string.permissionneeded))
                    .setMessage(getString(R.string.permissionneededdb))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(databaseManagement.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
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
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE){
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        }
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
