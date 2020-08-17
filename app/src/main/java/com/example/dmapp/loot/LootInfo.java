package com.example.dmapp.loot;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
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

import com.example.dmapp.MainActivity;
import com.example.dmapp.R;
import com.example.dmapp.npcs.npcDBHelper;

import java.util.ArrayList;

public class LootInfo extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loot_info);
        String TAG = "LootInfo";
        context = this;
        activity = this;

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
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
        final LinearLayout lootLayout = findViewById(R.id.lootLayout);
        final TextView addImageText = findViewById(R.id.addImageText);
        final TextView nameTitle = findViewById(R.id.nameTitle);
        final TextView lootReqTitle = findViewById(R.id.lootReqTitle);
        final TextView rarityTitle = findViewById(R.id.rarityTitle);
        final TextView priceTitle = findViewById(R.id.priceTitle);
        final TextView descTitle = findViewById(R.id.descTitle);
        final TextView detailsTitle = findViewById(R.id.detailsTitle);
        final ImageView lootImage = findViewById(R.id.lootImage);
        final ImageView addImage = findViewById((R.id.addImage));
        Button saveLoot =  findViewById(R.id.saveLoot);

        addImageText.setText(R.string.addimage);
        lootImage.setVisibility(View.GONE);
        addImage.setImageResource(R.drawable.addanimage);

        if (theme.equals("dark")){

            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));

            addImageText.setTextColor(Color.WHITE);
            nameTitle.setTextColor(Color.WHITE);
            lootReqTitle.setTextColor(Color.WHITE);
            rarityTitle.setTextColor(Color.WHITE);
            priceTitle.setTextColor(Color.WHITE);
            descTitle.setTextColor(Color.WHITE);
            detailsTitle.setTextColor(Color.WHITE);
            addImage.setBackgroundResource(R.drawable.info_bg8);
            lootName.setBackgroundResource(R.drawable.info_bg7);
            lootName.setTextColor(Color.parseColor("#dadada"));
            lootPrice.setBackgroundResource(R.drawable.info_bg7);
            lootPrice.setTextColor(Color.parseColor("#dadada"));
            lootRequirements.setBackgroundResource(R.drawable.info_bg7);
            lootRequirements.setButtonTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
            lootDescription.setBackgroundResource(R.drawable.info_bg7);
            lootDescription.setTextColor(Color.parseColor("#dadada"));
            lootDetails.setBackgroundResource(R.drawable.info_bg7);
            lootDetails.setTextColor(Color.parseColor("#dadada"));
            lootMainLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            backButton.setBackgroundColor(Color.parseColor("#2C2C2C"));
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.black));
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
                if(addLootValue == 0) {
                    Intent intent = new Intent(context, LootDisplay.class);
                    intent.putExtra("lootName", lootTitle);
                    intent.putExtra("addLootValue", 0);
                    startActivity(intent);
                    finish();
                }
                else onBackPressed();
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

                //if the edit text values are non-zero, and there is no identical entry, then add the Loot data.
                if (CheckName(lootName)){

                    if (addLootValue != 0){
                        setLootTitle(name); //if attempting to add a new npc, we set this value to pass to the DBHelper in order to check if we need to update an existing entry
                    }

                    String realAttunement = getAttunement();
                    AddLoot(name, rarity, price, description, details, image, realAttunement, lootTitle);
                    ImageView deleteLootButtonImage = new ImageView(context);
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

                            }else toast("There is no item to delete");
                        }
                    });
                    if (!deleteExists) {
                        lootLayout.addView(deleteLootButtonImage);
                        deleteExists = true;
                    }
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
            lootName.setText(lootInfo.getString(1));
            lootsRarity.setSelection(rarityAdapter.getPosition(lootInfo.getString(2)));
            lootPrice.setText(lootInfo.getString(3));
            if((lootInfo.getString(7)).equals("yes")) {
                lootRequirements.setChecked(true);
                setAttunement("yes");
            } else{
                lootRequirements.setChecked(false);
                setAttunement("no");
            }
            lootDescription.setText(lootInfo.getString(4));
            lootDetails.setText(lootInfo.getString(5));
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

                    }else toast("There is no item to delete");
                }
            });
            lootLayout.addView(deleteLootButtonImage);

            image = lootInfo.getString(6);
            if (!image.equals("")) {
                Uri imageUri = Uri.parse(image);
                lootImage.setImageURI(imageUri);
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
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == 1) {
                //data.getData returns the content URI for the selected Image
                Uri selectedImage = data.getData();
                image = selectedImage.toString();
                lootImage.setVisibility(View.VISIBLE);
                lootImage.setImageURI(selectedImage);
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

    //Make sure the text fields are not left blank (may be edited later to omit the notes section)
    private boolean CheckName(EditText name) {
        return ((name.length() != 0));
    }

    private void AddLoot(final String name, final String rarity, final String price, final String description, final String details, final String image, final String requirements, final String lootTitle){
        setLootTitle(name);
        if(addLootValue == 0 || !(mLootDBHelper.checkNonExistence(name))) { //if we're updating an existing entry, then edit current DB values
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //builder.setTitle(R.string.app_name);
            builder.setMessage(R.string.updateinfo);
            //builder.setIcon(R.drawable.ic_launcher);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    boolean insertLoot = mLootDBHelper.addLoot(name, rarity, price, description, details, image, requirements, lootTitle, true);

                    if (insertLoot) {
                        toast("Item saved");
                        onBackPressed();
                    } else {
                        toast("Error with entry");
                    }
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }else { //if the npc with the entered name does not exist, proceed as normal
            boolean insertLoot = mLootDBHelper.addLoot(name, rarity, price, description, details, image, requirements, lootTitle, false);
            if (insertLoot) {
                toast("Item saved");
                onBackPressed();
            } else {
                toast("Error with entry");
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(addLootValue == 0) {
            Intent intent = new Intent(context, LootDisplay.class);
            intent.putExtra("lootName", lootTitle);
            intent.putExtra("addLootValue", 0);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent(this, LootList.class);
            startActivity(intent);
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


    private void requestStoragePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            new AlertDialog.Builder(context) // Show an explanation to the user why we need the permission
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed for you to add reference images for your loot")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(LootInfo.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
}