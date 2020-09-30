package com.redheaddev.gmjournal.misc;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.redheaddev.gmjournal.R;
import com.redheaddev.gmjournal.MyRecyclerViewAdapter;
import com.redheaddev.gmjournal.loot.LootDisplay;
import com.redheaddev.gmjournal.loot.LootList;
import com.redheaddev.gmjournal.loot.lootDBHelper;
import com.redheaddev.gmjournal.presets.PresetList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class MiscInfo extends AppCompatActivity{

    private static String TAG = "";

    MiscAdapter npcAdapter;
    MiscAdapter cityAdapter;
    MiscAdapter itemAdapter;
    private miscDBHelper mMiscDBHelper;
    private Timer timer = new Timer();
    private int addMiscValue = 0;
    private String miscTitle = null;
    private String image = "";
    private String requirements;
    private ArrayList<String> linkedNpcs = new ArrayList<>();
    private ArrayList<String> linkedCities = new ArrayList<>();
    private ArrayList<String> linkedItems = new ArrayList<>();
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 672;
    private Context context;
    private Activity activity;
    private String rarityChoice;
    boolean deleteExists = false;
    private boolean darkMode;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.misc_info);
        TAG = "MiscInfo";
        context = this;
        activity = this;

        final SharedPreferences sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        String theme = sharedPreferences.getString("Theme", "none");
        final String infoHeader1 = sharedPreferences.getString("infoHeader1", "none");
        String infoHeader2 = sharedPreferences.getString("infoHeader2", "none");
        String infoHeader3 = sharedPreferences.getString("infoHeader3", "none");
        String infoHeader4 = sharedPreferences.getString("infoHeader4", "none");
        String infoHeader5 = sharedPreferences.getString("infoHeader5", "none");
        String miscInfoTitle = sharedPreferences.getString("miscInfoTitle", "none");
        darkMode = theme.equals("dark");

        final DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int deviceHeight = (displayMetrics.heightPixels);

        mMiscDBHelper = new miscDBHelper(this);
        ImageView backButton = findViewById(R.id.backButton);
        final EditText miscName = findViewById(R.id.miscName);
        final EditText miscInfo1 = findViewById(R.id.miscInfo1);
        final EditText miscInfo2 = findViewById(R.id.miscInfo2);
        final EditText miscInfo3 = findViewById(R.id.miscInfo3);
        final EditText miscInfo4 = findViewById(R.id.miscInfo4);
        final NestedScrollView miscMainLayout = findViewById(R.id.miscMainLayout);
        final LinearLayoutCompat buttonLayout = findViewById(R.id.lootButtonLayout);
        final LinearLayoutCompat groupLayout = findViewById(R.id.groupLayout);
        final TextView groupPreset = findViewById(R.id.groupPreset);
        final TextView linkedNpc = findViewById(R.id.linkedNpc);
        final TextView linkedNpcTitle = findViewById(R.id.linkedNpcTitle);
        final TextView linkedCity = findViewById(R.id.linkedCity);
        final TextView linkedCityTitle = findViewById(R.id.linkedCityTitle);
        final TextView linkedItem = findViewById(R.id.linkedItem);
        final TextView linkedItemTitle = findViewById(R.id.linkedItemTitle);
        final TextView linkedItemGroup = findViewById(R.id.linkedItemGroup);
        final TextView linkedItemGroupTitle = findViewById(R.id.linkedItemGroupTitle);
        final LinearLayoutCompat linkedItemGroupLayout = findViewById(R.id.linkedItemGroupLayout);
        final LinearLayout miscLayout = findViewById(R.id.miscLayout);
        final TextView addImageText = findViewById(R.id.addImageText);
        final TextView miscMainTitle = findViewById(R.id.miscMainTitle);
        final TextView nameTitle = findViewById(R.id.miscTitle);
        final TextView miscHeader1 = findViewById(R.id.miscHeader1);
        final TextView miscHeader2 = findViewById(R.id.miscHeader2);
        final TextView miscHeader3 = findViewById(R.id.miscHeader3);
        final TextView miscHeader4 = findViewById(R.id.miscHeader4);
        final TextView miscGroupHeader = findViewById(R.id.miscGroupTitle);
        final ImageView editIcon1 = findViewById(R.id.editIcon1);
        final ImageView editIcon2 = findViewById(R.id.editIcon2);
        final ImageView editIcon3 = findViewById(R.id.editIcon3);
        final ImageView editIcon4 = findViewById(R.id.editIcon4);
        final ImageView editIcon5 = findViewById(R.id.editIcon5);
        final ImageView editIcon6 = findViewById(R.id.editIcon6);
        final ImageView miscImage = findViewById(R.id.miscImage);
        final ImageView addImage = findViewById((R.id.addImage));
        final ListView npcListLayout = findViewById(R.id.npcListLayout);
        final ListView cityListLayout = findViewById(R.id.cityListLayout);
        final ListView itemListLayout = findViewById(R.id.itemListLayout);
        View line1 = findViewById(R.id.line1);
        View line2 = findViewById(R.id.line2);
        View line3 = findViewById(R.id.line3);
        Button saveMisc =  findViewById(R.id.saveMisc);

        verifyStoragePermissions(this);

        if (!infoHeader1.equals("none")) miscHeader1.setText(infoHeader1);
        if (!infoHeader2.equals("none")) miscHeader2.setText(infoHeader2);
        if (!infoHeader3.equals("none")) miscHeader3.setText(infoHeader3);
        if (!infoHeader4.equals("none")) miscHeader4.setText(infoHeader4);
        if (!infoHeader5.equals("none")) miscGroupHeader.setText(infoHeader5);
        if(!miscInfoTitle.equals("none")) miscMainTitle.setText(miscInfoTitle);

        addImageText.setText(R.string.addimage);
        miscImage.setVisibility(View.GONE);
        addImage.setImageResource(R.drawable.addanimage);


        // basically, constantly refresh the sizes of these lists based on the children. This is done to handle when things are removed.
        /*timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                    setListViewHeightBasedOnChildren(npcListLayout);
                    setListViewHeightBasedOnChildren(cityListLayout);
                    setListViewHeightBasedOnChildren(itemListLayout);
                }
            },
                0, 5);   // 1000 Millisecond  = 1 second*/

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                MiscInfo.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setListViewHeightBasedOnChildren(npcListLayout);
                        setListViewHeightBasedOnChildren(cityListLayout);
                        setListViewHeightBasedOnChildren(itemListLayout);
                    }

                    private void setListViewHeightBasedOnChildren(ListView listView) {
                        Log.e("Listview Size ", "" + listView.getCount());
                        ListAdapter listAdapter = listView.getAdapter();
                        if (listAdapter == null) {
                            return;
                        }

                        int totalHeight = 0;
                        for (int i = 0; i < listAdapter.getCount(); i++) {
                            View listItem = listAdapter.getView(i, null, listView);
                            listItem.measure(0, 0);
                            totalHeight += listItem.getMeasuredHeight();
                        }

                        ViewGroup.LayoutParams params = listView.getLayoutParams();
                        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
                        listView.setLayoutParams(params);
                        listView.requestLayout();
                    }
                });
            }}, 0, 100);

        if (theme.equals("dark")){

            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(editIcon1.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(editIcon2.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(editIcon3.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(editIcon4.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
            DrawableCompat.setTint(DrawableCompat.wrap(editIcon5.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));

            addImageText.setTextColor(Color.WHITE);
            miscHeader1.setTextColor(Color.WHITE);
            miscHeader2.setTextColor(Color.WHITE);
            miscHeader3.setTextColor(Color.WHITE);
            miscHeader4.setTextColor(Color.WHITE);
            miscMainTitle.setTextColor(Color.WHITE);
            linkedNpcTitle.setTextColor(Color.WHITE);
            linkedCityTitle.setTextColor(Color.WHITE);
            linkedItemTitle.setTextColor(Color.WHITE);
            linkedItemGroupTitle.setTextColor(Color.WHITE);
            nameTitle.setTextColor(Color.WHITE);
            miscGroupHeader.setTextColor(Color.WHITE);
            line1.setBackgroundColor(Color.WHITE);
            line2.setBackgroundColor(Color.WHITE);
            line3.setBackgroundColor(Color.WHITE);
            addImage.setBackgroundResource(R.drawable.info_bg8);
            linkedNpc.setBackgroundResource(R.drawable.info_bg7);
            linkedNpc.setTextColor(Color.parseColor("#dadada"));
            linkedCity.setBackgroundResource(R.drawable.info_bg7);
            linkedCity.setTextColor(Color.parseColor("#dadada"));
            linkedItem.setBackgroundResource(R.drawable.info_bg7);
            linkedItem.setTextColor(Color.parseColor("#dadada"));
            miscName.setBackgroundResource(R.drawable.info_bg7);
            miscName.setTextColor(Color.parseColor("#dadada"));
            miscInfo1.setBackgroundResource(R.drawable.info_bg7);
            miscInfo1.setTextColor(Color.parseColor("#dadada"));
            groupLayout.setBackgroundResource(R.drawable.info_bg7);
            groupPreset.setHintTextColor(Color.parseColor("#dadada"));
            groupPreset.setTextColor(Color.parseColor("#dadada"));
            linkedItemGroupLayout.setBackgroundResource(R.drawable.info_bg7);
            linkedItemGroup.setHintTextColor(Color.parseColor("#dadada"));
            linkedItemGroup.setTextColor(Color.parseColor("#dadada"));
            miscInfo2.setBackgroundResource(R.drawable.info_bg7);
            miscInfo2.setTextColor(Color.parseColor("#dadada"));
            miscInfo3.setBackgroundResource(R.drawable.info_bg7);
            miscInfo3.setTextColor(Color.parseColor("#dadada"));
            miscInfo4.setBackgroundResource(R.drawable.info_bg7);
            miscInfo4.setTextColor(Color.parseColor("#dadada"));
            miscMainLayout.setBackgroundColor(Color.parseColor("#2C2C2C"));
            backButton.setBackgroundColor(Color.parseColor("#2C2C2C"));
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(backButton.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(editIcon1.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(editIcon2.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(editIcon3.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(editIcon4.getDrawable()), ContextCompat.getColor(context, R.color.black));
            DrawableCompat.setTint(DrawableCompat.wrap(editIcon5.getDrawable()), ContextCompat.getColor(context, R.color.black));
        }

        miscImage.setMaxHeight((int)(deviceHeight*.40));

        //if starting the activity from clicking an item, addMiscValue will be 0, otherwise, it's -1
        //we use this value to adjust the behavior of the editText values set during onCreate, and whether we need to check the name for an identical value when creating a new item
        setAddLootValue(getIntent().getIntExtra("addMiscValue", -1));

        //set the misc Title to what was passed in the intent. We use this to check to see if we need to UPDATE instead of INSERT when adding an npc
        setMiscTitle(getIntent().getStringExtra("miscName"));
        Log.d(TAG, "onCreate: The value of miscTitle is " + miscTitle);

        //Set all the recyclerViewAdapters for the linked data. We refresh these adapters when we add new data via the associated buttons
        npcAdapter = new MiscAdapter(this, linkedNpcs, this, darkMode);
        npcListLayout.setAdapter(npcAdapter);

        cityAdapter = new MiscAdapter(this, linkedCities, this, darkMode);
        cityListLayout.setAdapter(cityAdapter);

        itemAdapter = new MiscAdapter(this, linkedItems, this, darkMode);
        itemListLayout.setAdapter(itemAdapter);

        linkedNpc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PresetList.class);
                intent.putExtra("presetVariable", "NPC");
                startActivityForResult(intent, 7);
            }
        });

        linkedCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PresetList.class);
                intent.putExtra("presetVariable", "City");
                startActivityForResult(intent, 8);
            }
        });

        linkedItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PresetList.class);
                intent.putExtra("presetVariable", "Item");
                startActivityForResult(intent, 9);
            }
        });

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
                intent.putExtra("presetVariable", "MiscGroup");
                startActivityForResult(intent, 6);
            }
        });

        editIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText edittext = new EditText(context);
                alert.setMessage("Renaming this header applies it to all other items in this section");
                alert.setTitle("Edit Header Title");
                alert.setView(edittext);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        editor.putString("infoHeader1", edittext.getText().toString());
                        editor.apply();
                        miscHeader1.setText(sharedPreferences.getString("infoHeader1", "none"));
                    }
                });
                alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });

        editIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText edittext = new EditText(context);
                alert.setMessage("Renaming this header applies it to all other items in this section");
                alert.setTitle("Edit Header Title");
                alert.setView(edittext);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        editor.putString("infoHeader2", edittext.getText().toString());
                        editor.apply();
                        miscHeader2.setText(sharedPreferences.getString("infoHeader2", "none"));
                    }
                });
                alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });

        editIcon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText edittext = new EditText(context);
                alert.setMessage("Renaming this header applies it to all other items in this section");
                alert.setTitle("Edit Header Title");
                alert.setView(edittext);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        editor.putString("infoHeader3", edittext.getText().toString());
                        editor.apply();
                        miscHeader3.setText(sharedPreferences.getString("infoHeader3", "none"));
                    }
                });
                alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });

        editIcon4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText edittext = new EditText(context);
                alert.setMessage("Renaming this header applies it to all other items in this section");
                alert.setTitle("Edit Header Title");
                alert.setView(edittext);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        editor.putString("infoHeader4", edittext.getText().toString());
                        editor.apply();
                        miscHeader4.setText(sharedPreferences.getString("infoHeader4", "none"));
                    }
                });
                alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });

        editIcon5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText edittext = new EditText(context);
                alert.setMessage("Renaming this header applies it to all other items in this section");
                alert.setTitle("Edit Header Title");
                alert.setView(edittext);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        editor.putString("infoHeader5", edittext.getText().toString());
                        editor.apply();
                        miscGroupHeader.setText(sharedPreferences.getString("infoHeader5", "none"));
                    }
                });
                alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });

        editIcon6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                final EditText edittext = new EditText(context);
                alert.setMessage("Renaming this header applies it to all other items");
                alert.setTitle("Edit Page Title");
                alert.setView(edittext);

                alert.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        editor.putString("miscInfoTitle", edittext.getText().toString());
                        editor.apply();
                        miscMainTitle.setText(sharedPreferences.getString("miscInfoTitle", "none"));
                    }
                });
                alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });

        linkedItemGroupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PresetList.class);
                intent.putExtra("presetVariable", "Group");
                startActivityForResult(intent, 5);
            }
        });

        saveMisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = miscName.getText().toString();
                String info1 = miscInfo1.getText().toString();
                String info2 = miscInfo2.getText().toString();
                String info3 = miscInfo3.getText().toString();
                String info4 = miscInfo4.getText().toString();
                String group = groupPreset.getText().toString();
                if (group.equals("")) group = "No Group";
                String itemGroup = linkedItemGroup.getText().toString();
                if (itemGroup.equals("")) itemGroup = "No Item Group";

                ArrayList<String> savedNpcList = new ArrayList<>();
                for (int i = 0; i < npcListLayout.getCount(); i++) {
                    View v = npcListLayout.getChildAt(i);
                    TextView text = v.findViewById(R.id.dataValue);
                    savedNpcList.add(text.getText().toString());
                }
                String savedNpcListString = savedNpcList.toString().replace("[", "").replace("]", "").trim();

                ArrayList<String> savedCityList = new ArrayList<>();
                for (int i = 0; i < cityListLayout.getCount(); i++) {
                    View v = cityListLayout.getChildAt(i);
                    TextView text = v.findViewById(R.id.dataValue);
                    savedCityList.add(text.getText().toString());
                }
                String savedCityListString = savedCityList.toString().replace("[", "").replace("]", "").trim();

                ArrayList<String> savedItemList = new ArrayList<>();
                for (int i = 0; i < itemListLayout.getCount(); i++) {
                    View v = itemListLayout.getChildAt(i);
                    TextView text = v.findViewById(R.id.dataValue);
                    savedItemList.add(text.getText().toString());
                }
                String savedItemListString = savedItemList.toString().replace("[", "").replace("]", "").trim();

                //if the edit text values are non-zero, and there is no identical entry, then add the Loot data.
                if (CheckName(miscName)){

                    if (addMiscValue != 0){
                        setMiscTitle(name); //if attempting to add a new npc, we set this value to pass to the DBHelper in order to check if we need to update an existing entry
                    }

                    Log.d(TAG, "onClick: Attempting to save " + name + " to the MiscList");
                    AddMisc(name, info1, info2, info3, info4, image, savedNpcListString, savedCityListString, savedItemListString, itemGroup, group, miscTitle);
                    ImageView deleteMiscButtonImage = new ImageView(context);
                    deleteMiscButtonImage.setImageResource(R.drawable.delete);
                    int deviceWidth = (displayMetrics.widthPixels);
                    LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams((int)(deviceWidth * .11), ViewGroup.LayoutParams.WRAP_CONTENT);
                    lparams.setMargins(0,0,0,25);
                    lparams.gravity = Gravity.CENTER_HORIZONTAL;
                    deleteMiscButtonImage.setLayoutParams(lparams);
                    deleteMiscButtonImage.setAdjustViewBounds(true);
                    deleteMiscButtonImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String name = miscName.getText().toString();

                            if (!(mMiscDBHelper.checkNonExistence(name))) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                builder.setMessage(R.string.areyou);
                                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        mMiscDBHelper.removeMisc(miscTitle);
                                        Intent intent = new Intent(context, MiscList.class);
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
                        miscLayout.addView(deleteMiscButtonImage);
                        deleteExists = true;
                    }
                } else {
                    toast("Please provide a name for you item");
                }
            }
        });

        //if coming to the activity from clicking on an existing piece of loot, pull all relevant info
        if (addMiscValue != -1) {
            deleteExists = true;
            Cursor miscInfo = mMiscDBHelper.getSpecificMisc(miscTitle);
            miscInfo.moveToFirst();
            miscName.setText(miscInfo.getString(1));
            miscInfo1.setText(miscInfo.getString(2));
            miscInfo2.setText(miscInfo.getString(3));
            miscInfo3.setText(miscInfo.getString(4));
            miscInfo4.setText(miscInfo.getString(5));
            groupPreset.setText(miscInfo.getString(11));

            String npcString = miscInfo.getString(7);
            if (!npcString.equals("")) {
                npcString = npcString.replaceAll(",\\s", ",");
                String[] npcArray = npcString.split(",");
                linkedNpcs = new ArrayList<>(Arrays.asList(npcArray));
                npcAdapter = new MiscAdapter(this, linkedNpcs, this, darkMode);
                npcListLayout.setAdapter(npcAdapter);
                setListViewHeightBasedOnChildren(npcListLayout);
            }

            String cityString = miscInfo.getString(8);
            if (!cityString.equals("")) {
                cityString = cityString.replaceAll(",\\s", ",");
                String[] cityArray = cityString.split(",");
                linkedCities = new ArrayList<>(Arrays.asList(cityArray));
                cityAdapter = new MiscAdapter(this, linkedCities, this, darkMode);
                cityListLayout.setAdapter(cityAdapter);
                setListViewHeightBasedOnChildren(cityListLayout);
            }

            String itemString = miscInfo.getString(9);
            if (!itemString.equals("")) {
                itemString = itemString.replaceAll(",\\s", ",");
                String[] itemArray = itemString.split(",");
                linkedItems = new ArrayList<>(Arrays.asList(itemArray));
                itemAdapter = new MiscAdapter(this, linkedItems, this, darkMode);
                itemListLayout.setAdapter(itemAdapter);
                setListViewHeightBasedOnChildren(itemListLayout);
            }

            linkedItemGroup.setText(miscInfo.getString(10));
            ImageView deleteMiscButtonImage = new ImageView(this);
            deleteMiscButtonImage.setImageResource(R.drawable.delete);
            int deviceWidth = (displayMetrics.widthPixels);
            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams((int)(deviceWidth * .11), ViewGroup.LayoutParams.WRAP_CONTENT);
            lparams.setMargins(0,0,0,25);
            lparams.gravity = Gravity.CENTER_HORIZONTAL;
            deleteMiscButtonImage.setLayoutParams(lparams);
            deleteMiscButtonImage.setAdjustViewBounds(true);
            deleteMiscButtonImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = miscName.getText().toString();

                    if (!(mMiscDBHelper.checkNonExistence(name))) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setMessage(R.string.areyou);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                mMiscDBHelper.removeMisc(miscTitle);
                                Intent intent = new Intent(context, MiscList.class);
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
            miscLayout.addView(deleteMiscButtonImage);

            image = miscInfo.getString(6);
            if (!image.equals("")) {
                Bitmap bmImg = BitmapFactory.decodeFile(image);
                miscImage.setImageBitmap(bmImg);
                miscImage.setVisibility(View.VISIBLE);
                addImageText.setText(R.string.removeimage);
                addImage.setImageResource(R.drawable.removeimage);
                if (!darkMode) DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.black));
                    else DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));

                addImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        image = "";
                        miscImage.setImageURI(null);
                        miscImage.setVisibility(View.GONE);
                        addImageText.setText(R.string.addimage);
                        addImage.setImageResource(R.drawable.addanimage);
                        if (!darkMode) DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.black));
                            else DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
                        addImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    miscImage.setVisibility(View.VISIBLE);
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
        final ImageView miscImage = findViewById(R.id.miscImage);
        final TextView addImageText = findViewById(R.id.addImageText);
        final ImageView addImage = findViewById(R.id.addImage);

        if (resultCode == 6){
            TextView miscGroup = findViewById(R.id.groupPreset);
            if (!data.getStringExtra("presetValue").equals("none")) {
                String presetValue = data.getStringExtra("presetValue");
                miscGroup.setText(presetValue);
            }
            else miscGroup.setText("");

        } else if (resultCode == 7){
            if (!data.getStringExtra("presetValue").equals("none")) {
                String dataValue = data.getStringExtra("presetValue");
                linkedNpcs.add(dataValue);
                npcAdapter.notifyDataSetChanged();
                ListView npcListLayout = findViewById(R.id.npcListLayout);
                setListViewHeightBasedOnChildren(npcListLayout);
            }

        } else if (resultCode == 8){
            if (!data.getStringExtra("presetValue").equals("none")) {
                String dataValue = data.getStringExtra("presetValue");
                linkedCities.add(dataValue);
                cityAdapter.notifyDataSetChanged();
                ListView cityListLayout = findViewById(R.id.cityListLayout);
                setListViewHeightBasedOnChildren(cityListLayout);
            }

        } else if (resultCode == 9){
            if (!data.getStringExtra("presetValue").equals("none")) {
                String dataValue = data.getStringExtra("presetValue");
                linkedItems.add(dataValue);
                itemAdapter.notifyDataSetChanged();
                ListView itemListLayout = findViewById(R.id.itemListLayout);
                setListViewHeightBasedOnChildren(itemListLayout);
            }

        } else if (resultCode == 5){
            TextView linkedItemGroup = findViewById(R.id.linkedItemGroup);
            if (!data.getStringExtra("presetValue").equals("none")) {
                String presetValue = data.getStringExtra("presetValue");
                linkedItemGroup.setText(presetValue);
            }
            else linkedItemGroup.setText("");

        } else if (resultCode == Activity.RESULT_OK)
            if (requestCode == 1) {
                //data.getData returns the content URI for the selected Image
                Uri selectedImage = data.getData();
                miscImage.setVisibility(View.VISIBLE);
                String savedImagePath = null;
                try {
                    savedImagePath = createFileFromInputStream(selectedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "onActivityResult: SavedImagePath is " + savedImagePath);
                image = savedImagePath;
                Bitmap bmImg = BitmapFactory.decodeFile(savedImagePath);
                miscImage.setImageBitmap(bmImg);
                addImageText.setText(R.string.removeimage);
                addImage.setImageResource(R.drawable.removeimage);
                if (!darkMode) DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.black));
                    else DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
                //change the behavior of the addImage button to become a remove button. Then if clicked, change it back.
                addImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        miscImage.setImageURI(null);
                        image = "";
                        miscImage.setVisibility(View.GONE);
                        addImageText.setText(R.string.addimage);
                        addImage.setImageResource(R.drawable.addanimage);
                        if (!darkMode) DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.black));
                            else DrawableCompat.setTint(DrawableCompat.wrap(addImage.getDrawable()), ContextCompat.getColor(context, R.color.mainBgColor));
                        addImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    miscImage.setVisibility(View.VISIBLE);
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

    private void AddMisc(final String name, final String info1, final String info2, final String info3, final String info4, final String image, final String linkedNpc, final String linkedCity, final String linkedLoot, final String linkedItemGroup, final String miscGroup, final String miscTitle){
        setMiscTitle(name);
        if(addMiscValue == 0 || !(mMiscDBHelper.checkNonExistence(name))) { //if we're updating an existing entry, then edit current DB values
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.updateinfo);
            builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    boolean insertMisc = mMiscDBHelper.addMisc(name, info1, info2, info3, info4, image, linkedNpc, linkedCity, linkedLoot, linkedItemGroup, miscGroup, miscTitle, true);

                    if (insertMisc) {
                        toast(getString(R.string.itemsaved));
                        onBackPressed();
                    } else {
                        toast(getString(R.string.error));
                    }
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
        }else { //if the npc with the entered name does not exist, proceed as normal
            boolean insertMisc = mMiscDBHelper.addMisc(name, info1, info2, info3, info4, image, linkedNpc, linkedCity, linkedLoot, linkedItemGroup, miscGroup, miscTitle, false);
            if (insertMisc) {
                Log.d(TAG, "onClick: Saving " + name + ", " + info1 + ", " + info2 + ", " + info3 + ", " + info4 + ", " + image + ", " + linkedNpc + ", " + linkedCity + ", " + linkedLoot + ", " + linkedItemGroup + ", " + miscGroup + ", " + miscTitle + " " + " to the MiscList");
                toast(getString(R.string.itemsaved));
                onBackPressed();
            } else {
                toast(getString(R.string.error));
            }
        }
    }

    @Override
    public void onBackPressed() {
        timer.cancel();
        if(addMiscValue == 0) {
            Intent intent = new Intent(context, MiscDisplay.class);
            intent.putExtra("miscName", miscTitle);
            intent.putExtra("addMiscValue", 0);
            startActivity(intent);
            finish();
        }
        else {
            Intent intent = new Intent(this, MiscList.class);
            startActivity(intent);
            finish();
        }
    }

    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setAddLootValue(int value){
        addMiscValue = value;
    }
    private void setMiscTitle(String value){
        miscTitle = value;
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
                            ActivityCompat.requestPermissions(MiscInfo.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
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

    private void setListViewHeightBasedOnChildren(ListView listView) {
        Log.e("Listview Size ", "" + listView.getCount());
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    /*@Override
    public void onUserInteraction() {
        final ListView npcListLayout = findViewById(R.id.npcListLayout);
        final ListView cityListLayout = findViewById(R.id.cityListLayout);
        final ListView itemListLayout = findViewById(R.id.itemListLayout);
        setListViewHeightBasedOnChildren(npcListLayout);
        setListViewHeightBasedOnChildren(cityListLayout);
        setListViewHeightBasedOnChildren(itemListLayout);
        super.onUserInteraction();
    }*/

}