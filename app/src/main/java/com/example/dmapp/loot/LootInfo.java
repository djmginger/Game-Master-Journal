package com.example.dmapp.loot;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loot_info);
        String TAG = "LootInfo";
        context = this;
        activity = this;

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int deviceHeight = (displayMetrics.heightPixels);

        mLootDBHelper = new lootDBHelper(this);
        ImageView backButton = findViewById(R.id.backButton);
        final EditText lootName = findViewById(R.id.lootName);
        //final EditText lootRarity = findViewById(R.id.lootRarity);
        final EditText lootPrice = findViewById(R.id.lootPrice);
        final CheckBox lootRequirements = findViewById(R.id.lootRequirements);
        final EditText lootDescription = findViewById(R.id.lootDescription);
        final EditText lootDetails = findViewById(R.id.lootDetails);
        final Spinner lootsRarity = findViewById(R.id.lootsRarity);
        final LinearLayoutCompat buttonLayout = findViewById(R.id.lootButtonLayout);

        ImageView lootImage = findViewById(R.id.lootImage);
        Button addImage = findViewById((R.id.addImage));
        Button saveLoot =  findViewById(R.id.saveLoot);

        rarities.add("Loot Rarity:");
        rarities.add("None");
        rarities.add("Common");
        rarities.add("Uncommon");
        rarities.add("Rare");
        rarities.add("Very Rare");
        rarities.add("Legendary");
        rarities.add("Artifact");
        final ArrayAdapter<String> rarityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, rarities);
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
                    Button deleteLootButton = new Button(context);
                    deleteLootButton.setText("Delete Loot");
                    deleteLootButton.setBackground(ContextCompat.getDrawable(context, R.drawable.buttonbg));
                    LinearLayoutCompat.LayoutParams lparams = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                    lparams.setMargins(5,0, 5, 0);
                    deleteLootButton.setLayoutParams(lparams);
                    deleteLootButton.setPadding(30,10,30,10);
                    deleteLootButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String name = lootName.getText().toString();

                            if (!(mLootDBHelper.checkNonExistence(name))) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                //builder.setTitle(R.string.app_name);
                                builder.setMessage("Are you sure you want to delete this item?");
                                //builder.setIcon(R.drawable.ic_launcher);
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        mLootDBHelper.removeLoot(lootTitle);
                                        finish();
                                    }
                                });
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
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
                        buttonLayout.addView(deleteLootButton);
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
            image = lootInfo.getString(6);
            Uri imageUri = Uri.parse(image);
            lootImage.setImageURI(imageUri);

            Button deleteLootButton = new Button(this);
            deleteLootButton.setText("Delete Loot");
            deleteLootButton.setBackground(ContextCompat.getDrawable(this, R.drawable.buttonbg));
            LinearLayoutCompat.LayoutParams lparams = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
            lparams.setMargins(5,0, 5, 0);
            deleteLootButton.setLayoutParams(lparams);
            deleteLootButton.setPadding(30,10,30,10);
            deleteLootButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = lootName.getText().toString();

                    if (!(mLootDBHelper.checkNonExistence(name))) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        //builder.setTitle(R.string.app_name);
                        builder.setMessage("Are you sure you want to delete this item?");
                        //builder.setIcon(R.drawable.ic_launcher);
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                mLootDBHelper.removeLoot(lootTitle);
                                finish();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }else toast("There is no item to delete");
                }
            });
            buttonLayout.addView(deleteLootButton);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        ImageView lootImage = (ImageView) findViewById(R.id.lootImage);
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == 1) {
                //data.getData returns the content URI for the selected Image
                Uri selectedImage = data.getData();
                image = selectedImage.toString();
                lootImage.setImageURI(selectedImage);
            }
    }

    //Make sure the text fields are not left blank (may be edited later to omit the notes section)
    private boolean CheckName(EditText name) {
        return ((name.length() != 0));
    }

    private void AddLoot(final String name, final String rarity, final String price, final String description, final String details, final String image, final String requirements, final String lootTitle){

        if(!(mLootDBHelper.checkNonExistence(name))) { //if an npc with the entered name already exists, ask the user if they wish to update the entry
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //builder.setTitle(R.string.app_name);
            builder.setMessage("An item with this name already exists. Would you like to update the existing entry with this info?");
            //builder.setIcon(R.drawable.ic_launcher);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    boolean insertLoot = mLootDBHelper.addLoot(name, rarity, price, description, details, image, requirements, lootTitle);

                    if (insertLoot) {
                        toast("Item saved");
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
            boolean insertLoot = mLootDBHelper.addLoot(name, rarity, price, description, details, image, requirements, lootTitle);
            if (insertLoot) {
                toast("Item saved");
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
        else super.onBackPressed();
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