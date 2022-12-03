package com.redheaddev.gmjournal.loot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.LocaleList;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.redheaddev.gmjournal.R;
import com.redheaddev.gmjournal.presets.PresetList;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Locale;

public class LootInfo extends AppCompatActivity {

    private static final String TAG = "fdsa";
    private lootDBHelper mLootDBHelper;
    private int addLootValue = 0;
    private String lootTitle = null;
    private String image = "";
    private String requirements;
    private final ArrayList<String> rarities = new ArrayList<>();
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 672;
    private Context context;
    private Activity activity;
    private String rarityChoice;
    boolean deleteExists = false;
    private boolean darkMode;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String initName = "";
    private String initRarity = "Loot Rarity:";
    private String initPrice = "";
    private String initRequirement = "no";
    private String initDesc = "";
    private String initDetails = "";
    private String initGroup = "";
    private String initImage = "";
    private boolean hasSaved = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loot_info);
        String TAG = "LootInfo";
        context = this;
        activity = this;

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        String theme = sharedPreferences.getString("Theme", "none");
        darkMode = theme.equals("dark");

        final DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int deviceHeight = (displayMetrics.heightPixels);

        mLootDBHelper = new lootDBHelper(this);
        ImageView backButton = findViewById(R.id.backButton);
        final EditText lootName = findViewById(R.id.lootName);
        final EditText lootPrice = findViewById(R.id.lootPrice);
        final CheckBox lootRequirements = findViewById(R.id.lootRequirements);
        final EditText lootDescription = findViewById(R.id.lootDescription);
        final EditText lootDetails = findViewById(R.id.lootDetails);
        final Spinner lootsRarity = findViewById(R.id.lootsRarity);
        final ScrollView lootMainLayout = findViewById(R.id.lootMainLayout);
        final LinearLayoutCompat buttonLayout = findViewById(R.id.lootButtonLayout);
        final LinearLayoutCompat groupLayout = findViewById(R.id.groupLayout);
        final TextView groupTitle = findViewById(R.id.groupTitle);
        final TextView groupPreset = findViewById(R.id.groupPreset);
        final LinearLayout lootLayout = findViewById(R.id.lootLayout);
        final TextView pageHeader = findViewById(R.id.pageHeader);
        final TextView addImageText = findViewById(R.id.addImageText);
        final TextView nameTitle = findViewById(R.id.nameTitle);
        final TextView lootReqTitle = findViewById(R.id.lootReqTitle);
        final TextView rarityTitle = findViewById(R.id.rarityTitle);
        final TextView priceTitle = findViewById(R.id.priceTitle);
        final TextView descTitle = findViewById(R.id.descTitle);
        final TextView detailsTitle = findViewById(R.id.detailsTitle);
        final ImageView lootImage = findViewById(R.id.lootImage);
        final ImageView addImage = findViewById((R.id.addImage));
        ImageView downArrow = findViewById(R.id.downArrow);
        Button saveLoot =  findViewById(R.id.saveLoot);

        verifyStoragePermissions(this);

        addImageText.setText(R.string.addimage);
        lootImage.setVisibility(View.GONE);
        addImage.setImageResource(R.drawable.addanimage);

        String headerText4 = sharedPreferences.getString("headerText4", "none");
        String headerColor4 = sharedPreferences.getString("headerColor4", "none");
        String infoboxcolor4 = sharedPreferences.getString("infoboxcolor4", "none");
        if(!headerText4.equals("none")) {
            pageHeader.setText(String.format("%s Details", headerText4));
            groupTitle.setText(String.format("%s Group", headerText4));
        }
        if(!headerColor4.equals("none")) pageHeader.setTextColor(Color.parseColor(headerColor4));
        if(!infoboxcolor4.equals("none")) {
            setDrawableColors(infoboxcolor4);
        }

        if (theme.equals("dark")){

            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(downArrow.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));

            addImageText.setTextColor(Color.WHITE);
            nameTitle.setTextColor(Color.WHITE);
            lootReqTitle.setTextColor(Color.WHITE);
            rarityTitle.setTextColor(Color.WHITE);
            priceTitle.setTextColor(Color.WHITE);
            descTitle.setTextColor(Color.WHITE);
            detailsTitle.setTextColor(Color.WHITE);
            groupTitle.setTextColor(Color.WHITE);
            addImage.setBackgroundResource(R.drawable.info_bg8);
            lootName.setBackgroundResource(R.drawable.info_bg7);
            lootName.setTextColor(Color.parseColor("#dadada"));
            lootPrice.setBackgroundResource(R.drawable.info_bg7);
            lootPrice.setTextColor(Color.parseColor("#dadada"));
            lootRequirements.setBackgroundResource(R.drawable.info_bg7);
            lootRequirements.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
            groupLayout.setBackgroundResource(R.drawable.info_bg7);
            groupPreset.setTextColor(Color.parseColor("#dadada"));
            lootDescription.setBackgroundResource(R.drawable.info_bg7);
            lootDescription.setTextColor(Color.parseColor("#dadada"));
            lootDetails.setBackgroundResource(R.drawable.info_bg7);
            lootDetails.setTextColor(Color.parseColor("#dadada"));
            lootMainLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            backButton.setBackgroundColor(Color.parseColor("#2C2C2C"));
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(downArrow.getDrawable()), ContextCompat.getColor(context, R.color.black));
        }

        String localeText = sharedPreferences.getString("Locale", "none");
        String loadLocale = String.valueOf(getResources().getConfiguration().locale);
        if(!localeText.equals(loadLocale)){
            Locale locale = new Locale(localeText);
            updateLocale(locale);
        }

        rarities.add("Loot Rarity:");
        rarities.add("None");
        rarities.add("Common");
        rarities.add("Uncommon");
        rarities.add("Rare");
        rarities.add("Very Rare");
        rarities.add("Legendary");
        rarities.add("Artifact");
        final ArrayAdapter<String> rarityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, rarities);
        rarityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lootsRarity.setAdapter(rarityAdapter);

        lootsRarity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rarityChoice = rarities.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("GTOUTOUT", "Nothing Selected");
            }
        });

        lootImage.setMaxHeight((int)(deviceHeight*.40));

        //if starting the activity from clicking an item, addLootValue will be 0, otherwise, it's -1
        //we use this value to adjust the behavior of the editText values set during onCreate, and whether we need to check the name for an identical value when creating a new item
        setAddLootValue(getIntent().getIntExtra("addLootValue", -1));

        setLootTitle(getIntent().getStringExtra("lootName")); //set the npc Title to what was passed in the intent. We use this to check to see if we need to UPDATE instead of INSERT when adding an npc
        Log.d(TAG, "onCreate: The value of lootTitle is " + lootTitle);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, 1);
                }
                else {
                    requestStoragePermission();
                }
            }
        });

        groupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PresetList.class);
                intent.putExtra("presetVariable", "Group");
                startActivityForResult(intent, 5);
            }
        });

        saveLoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = lootName.getText().toString();
                if (rarityChoice.equals("Loot Rarity:")){
                    rarityChoice = "none";
                }
                String rarity = rarityChoice;
                String price = lootPrice.getText().toString();
                if (lootRequirements.isChecked()){
                    setAttunement("yes");
                } else{
                    setAttunement("no");
                }
                String description = lootDescription.getText().toString();
                String details = lootDetails.getText().toString();
                String group = groupPreset.getText().toString();
                if (group.equals("")) group = "No Group";

                //if the edit text values are non-zero, and there is no identical entry, then add the Loot data.
                if (CheckName(lootName)){

                    if (addLootValue != 0){
                        setLootTitle(name); //if attempting to add a new npc, we set this value to pass to the DBHelper in order to check if we need to update an existing entry
                    }

                    String realAttunement = getAttunement();
                    AddLoot(name, rarity, price, description, details, image, realAttunement, group, lootTitle);
                } else {
                    toast("Please provide a name for you item");
                }
            }
        });

        //if coming to the activity from clicking on an existing piece of loot, pull all relevant info
        if (addLootValue != -1) {
            deleteExists = true;
            Cursor lootInfo = mLootDBHelper.getSpecificLoot(lootTitle);
            lootInfo.moveToFirst();
            initName = lootInfo.getString(1);
            lootName.setText(initName);
            initRarity = lootInfo.getString(2);
            lootsRarity.setSelection(rarityAdapter.getPosition(initRarity));
            initPrice = lootInfo.getString(3);
            lootPrice.setText(initPrice);
            initRequirement = lootInfo.getString(7);
            if(initRequirement.equals("yes")) {
                lootRequirements.setChecked(true);
                setAttunement("yes");
            } else{
                lootRequirements.setChecked(false);
                setAttunement("no");
            }
            initDesc = lootInfo.getString(4);
            lootDescription.setText(initDesc);
            initDetails = lootInfo.getString(5);
            lootDetails.setText(initDetails);
            initGroup = lootInfo.getString(8);
            groupPreset.setText(initGroup);
            ImageView deleteLootButtonImage = new ImageView(this);
            deleteLootButtonImage.setImageResource(R.drawable.delete);
            int deviceWidth = (displayMetrics.widthPixels);
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams((int)(deviceWidth * .11), ViewGroup.LayoutParams.WRAP_CONTENT);
            lparams.setMargins(0,0,0,25);
            lparams.gravity = Gravity.CENTER_HORIZONTAL;
            deleteLootButtonImage.setLayoutParams(lparams);
            deleteLootButtonImage.setAdjustViewBounds(true);
            deleteLootButtonImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = lootName.getText().toString();

                    if (!(mLootDBHelper.checkNonExistence(name))) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setMessage(R.string.areyou);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                mLootDBHelper.removeLoot(lootTitle);
                                Intent intent = new Intent(context, LootList.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }else toast(getString(R.string.noitemtodelete));
                }
            });
            lootLayout.addView(deleteLootButtonImage);

            initImage = lootInfo.getString(6);
            image = initImage;
            if (!image.equals("")) {
                Bitmap bmImg = BitmapFactory.decodeFile(image);
                lootImage.setImageBitmap(bmImg);
                lootImage.setVisibility(View.VISIBLE);
                addImageText.setText(R.string.removeimage);
                addImage.setImageResource(R.drawable.removeimage);
                if (!darkMode) DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.black));
                    else DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));

                addImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        image = "";
                        lootImage.setImageURI(null);
                        lootImage.setVisibility(View.GONE);
                        addImageText.setText(R.string.addimage);
                        addImage.setImageResource(R.drawable.addanimage);
                        if (!darkMode) DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.black));
                            else DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
                        addImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    lootImage.setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                    intent.setType("image/*");
                                    startActivityForResult(intent, 1);
                                } else {
                                    requestStoragePermission();
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        final ImageView lootImage = findViewById(R.id.lootImage);
        final TextView addImageText = findViewById(R.id.addImageText);
        final ImageView addImage = findViewById(R.id.addImage);

        if (resultCode == 5){
            TextView groupPreset = findViewById(R.id.groupPreset);
            if (!data.getStringExtra("presetValue").equals("none")) {
                String presetValue = data.getStringExtra("presetValue");
                groupPreset.setText(presetValue);
            }
            else groupPreset.setText("");

        } else if (resultCode == Activity.RESULT_OK)
            if (requestCode == 1) {
                //data.getData returns the content URI for the selected Image
                Uri selectedImage = data.getData();
                lootImage.setVisibility(View.VISIBLE);
                String savedImagePath = null;
                try {
                    savedImagePath = createFileFromInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "onActivityResult: SavedImagePath is " + savedImagePath);
                image = savedImagePath;
                Bitmap bmImg = BitmapFactory.decodeFile(savedImagePath);
                lootImage.setImageBitmap(bmImg);
                addImageText.setText(R.string.removeimage);
                addImage.setImageResource(R.drawable.removeimage);
                if (!darkMode) DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.black));
                    else DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
                //change the behavior of the addImage button to become a remove button. Then if clicked, change it back.
                addImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        lootImage.setImageURI(null);
                        image = "";
                        lootImage.setVisibility(View.GONE);
                        addImageText.setText(R.string.addimage);
                        addImage.setImageResource(R.drawable.addanimage);
                        if (!darkMode) DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.black));
                            else DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
                        addImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    lootImage.setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(Intent.ACTION_PICK);
                                    intent.setType("image/*");
                                    startActivityForResult(intent, 1);
                                }
                                else {
                                    requestStoragePermission();
                                }
                            }
                        });
                    }
                });
            }
    }

    public void setDrawableColors(String color){
        final EditText lootName = findViewById(R.id.lootName);
        final CheckBox lootRequirements = findViewById(R.id.lootRequirements);
        LinearLayoutCompat groupLayout = findViewById(R.id.groupLayout);
        final EditText lootPrice = findViewById(R.id.lootPrice);
        final EditText lootDescription = findViewById(R.id.lootDescription);
        final EditText lootDetails = findViewById(R.id.lootDetails);
        final ImageView addImage = findViewById(R.id.addImage);

        GradientDrawable drawable1 = (GradientDrawable)lootName.getBackground().getCurrent();
        drawable1.setColor(Color.parseColor(color));
        drawable1.setStroke(4, Color.BLACK);
        GradientDrawable drawable2 = (GradientDrawable)lootRequirements.getBackground().getCurrent();
        drawable2.setColor(Color.parseColor(color));
        drawable2.setStroke(4, Color.BLACK);
        GradientDrawable drawable3 = (GradientDrawable)lootPrice.getBackground().getCurrent();
        drawable3.setColor(Color.parseColor(color));
        drawable3.setStroke(4, Color.BLACK);
        GradientDrawable drawable4 = (GradientDrawable)addImage.getBackground().getCurrent();
        drawable4.setColor(Color.parseColor(color));
        drawable4.setStroke(4, Color.BLACK, 5, 5);
        GradientDrawable drawable5 = (GradientDrawable)lootDescription.getBackground().getCurrent();
        drawable5.setColor(Color.parseColor(color));
        drawable5.setStroke(4, Color.BLACK);
        GradientDrawable drawable6 = (GradientDrawable)lootDetails.getBackground().getCurrent();
        drawable6.setColor(Color.parseColor(color));
        drawable6.setStroke(4, Color.BLACK);
        GradientDrawable drawable7 = (GradientDrawable)groupLayout.getBackground().getCurrent();
        drawable7.setColor(Color.parseColor(color));
        drawable7.setStroke(4, Color.BLACK);
    }


    private String createFileFromInputStream(Uri imageUri) throws FileNotFoundException {

        InputStream in = getContentResolver().openInputStream(imageUri);
        String imageFileName = getFileName(imageUri);
        File exportDir = new File(context.getFilesDir(), "/GMJournalData/");
        if (!exportDir.exists()) {
            if (exportDir.mkdirs()) Log.d(TAG, "doInBackground: File directory was created");
            else Log.d(TAG, "doInBackground: File directory wasn't created");
        }
        String destinationFilename = context.getFilesDir().getPath() + "/GMJournalData/" + imageFileName;

        try {
            File f = new File(destinationFilename);
            f.setWritable(true, false);
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[1024];
            int length = 0;

            while((length=in.read(buffer)) > 0) {
                outputStream.write(buffer,0,length);
            }

            outputStream.close();
            in.close();

            return destinationFilename;
        } catch (IOException e) {
            System.out.println("error in creating a file");
            e.printStackTrace();
        }
        return null;
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    //Make sure the text fields are not left blank (may be edited later to omit the notes section)
    private boolean CheckName(EditText name) {
        return ((name.length() != 0));
    }

    private void AddLoot(final String name, final String rarity, final String price, final String description, final String details, final String image, final String requirements, final String itemGroup, final String lootTitle){
        setLootTitle(name);
        if(addLootValue == 0 || !(mLootDBHelper.checkNonExistence(name))) { //if we're updating an existing entry, then edit current DB values
            boolean insertLoot = mLootDBHelper.addLoot(name, rarity, price, description, details, image, requirements, itemGroup, lootTitle, true);

            if (insertLoot) {
                hasSaved = true;
                toast(getString(R.string.itemsaved));
                onBackPressed();
            } else {
                toast(getString(R.string.error));
            }
        }else { //if the npc with the entered name does not exist, proceed as normal
            boolean insertLoot = mLootDBHelper.addLoot(name, rarity, price, description, details, image, requirements, itemGroup, lootTitle, false);
            if (insertLoot) {
                hasSaved = true;
                toast(getString(R.string.itemsaved));
                onBackPressed();
            } else {
                toast(getString(R.string.error));
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: hasSaved is " + hasSaved);
        if(addLootValue == 0) {
            Intent intent = new Intent(context, LootDisplay.class);
            intent.putExtra("lootName", lootTitle);
            intent.putExtra("addLootValue", 0);
            if (!hasSaved) notSaved(intent);
            else {
                startActivity(intent);
                finish();
            }
        }
        else {
            Intent intent = new Intent(this, LootList.class);
            if (!hasSaved) notSaved(intent);
            else startActivity(intent);
        }
    }

    public void notSaved(Intent intent) {

        EditText lootName = findViewById(R.id.lootName);
        EditText lootPrice = findViewById(R.id.lootPrice);
        CheckBox lootRequirements = findViewById(R.id.lootRequirements);
        EditText lootDescription = findViewById(R.id.lootDescription);
        EditText lootDetails = findViewById(R.id.lootDetails);
        TextView groupPreset = findViewById(R.id.groupPreset);

        String requireValue = "";
        if (lootRequirements.isChecked()) requireValue = "yes";
        else requireValue = "no";

        if ((!(lootName.getText().toString().equals(initName)) || !(lootPrice.getText().toString().equals(initPrice)) || !(requireValue.equals(initRequirement)) || !(lootDescription.getText().toString().equals(initDesc)) || !(lootDetails.getText().toString().equals(initDetails)) || !(rarityChoice.equals(initRarity) || rarityChoice.equals("Loot Rarity:")) || !(image.equals(initImage)) || !(groupPreset.getText().toString().equals(initGroup))) & !hasSaved) {
            Log.d(TAG, "notSaved: City name is:" + lootName.getText().toString() + " and initName is:" + initName);
            Log.d(TAG, "notSaved: City notes is:" + lootPrice.getText().toString() + " and initNote is:" + initPrice);
            Log.d(TAG, "notSaved: City env is:" + requireValue + " and initenv is:" + initRequirement);
            Log.d(TAG, "notSaved: City econ is:" + lootDescription.getText().toString() + " and initecon is:" + initDesc);
            Log.d(TAG, "notSaved: City pop is:" + lootDetails.getText().toString() + " and initpop is:" + initDetails);
            Log.d(TAG, "notSaved: City pop is:" + rarityChoice + " and initpop is:" + initRarity);
            Log.d(TAG, "notSaved: City pop is:" + groupPreset.getText().toString() + " and initpop is:" + initGroup);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.unsaved);
            String finalRequireValue = requireValue;
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    String name = lootName.getText().toString();
                    String price = lootPrice.getText().toString();
                    String description = lootDescription.getText().toString();
                    String details = lootDetails.getText().toString();
                    String group = groupPreset.getText().toString();
                    dialog.dismiss();
                    saveFunctionality(name, rarityChoice, price, description, details, image, finalRequireValue, group);
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    startActivity(intent);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else startActivity(intent);
    }

    public void saveFunctionality(String name, String rarity, String price, String description, String details, String image, String attune, String itemGroup){

        //if the edit text values are non-zero, and there is no identical entry, then add the loot data
        EditText lootName = findViewById(R.id.lootName);
        if (CheckName(lootName)){

            if (addLootValue != 0){
                setLootTitle(name); //if attempting to add a new city, we set this value to pass to the DBHelper in order to check if we need to update an existing entry
            }

            AddLoot(name, rarity, price, description, details, image, attune, itemGroup, lootTitle);
        } else {
            toast("Please provide a name for your loot");
        }
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setAddLootValue(int value){
        addLootValue = value;
    }
    private void setLootTitle(String value){
        lootTitle = value;
    }
    private void setAttunement(String value){
        requirements = value;
    }
    private String getAttunement(){
        return requirements;
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
                    .setMessage(getString(R.string.permissionreasonloot))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(LootInfo.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
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