package com.example.mycontactlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import java.util.ArrayList;

public class ContactSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_settings);
        initListButton();
        initSettingsButton();
        initMapButton();

        initSettings();

        initSortByClick();
        initSortOrderClick();


        initSelectBgColorClick();

//        ScrollView scrollViewObject=findViewById(R.id.scrollView);
//        scrollViewObject.setBackgroundResource(R.color.color_lightblue);



    }

    private void initListButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonList);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactSettingsActivity.this, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initMapButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonMap);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactSettingsActivity.this, ContactMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initSettingsButton() {
        ImageButton ibSettings = (ImageButton) findViewById(R.id.imageButtonSettings);
        ibSettings.setEnabled(false);
    }
    private void initSettings() {
        String sortBy = getSharedPreferences("MyContactListPreferences",
                Context.MODE_PRIVATE).getString("sortfield","contactname");
        String sortOrder = getSharedPreferences("MyContactListPreferences",
                Context.MODE_PRIVATE).getString("sortorder","ASC");
        String bgColor = getSharedPreferences("MyContactListPreferences",
                Context.MODE_PRIVATE).getString("bgColor","Light Blue");

        RadioButton rbName = (RadioButton) findViewById(R.id.radioName);
        RadioButton rbCity = (RadioButton) findViewById(R.id.radioCity);
        RadioButton rbBirthDay = (RadioButton) findViewById(R.id.radioBirthday);
        if (sortBy.equalsIgnoreCase("contactname")) {
            rbName.setChecked(true);
        }
        else if (sortBy.equalsIgnoreCase("city")) {
            rbCity.setChecked(true);
        }
        else {
            rbBirthDay.setChecked(true);
        }

        RadioButton rbAscending = (RadioButton) findViewById(R.id.radioAscending);
        RadioButton rbDescending = (RadioButton) findViewById(R.id.radioDescending);
        if (sortOrder.equalsIgnoreCase("ASC")) {
            rbAscending.setChecked(true);
        }
        else {
            rbDescending.setChecked(true);
        }

        RadioGroup radioGroup=findViewById(R.id.radioBgColor);
        int count=radioGroup.getChildCount();
        int bGroundColor ;//R.color.color_lightblue;//variable to hold the color value
        //ArrayList<RadioButton> listOfRadioButtons = new ArrayList<RadioButton>();
        for (int i=0;i<count;i++) {
            View o = radioGroup.getChildAt(i);
            if (o instanceof RadioButton) {
                RadioButton r = (RadioButton) o;
                //listOfRadioButtons.add(r);
                if (bgColor.equalsIgnoreCase(r.getText().toString())) {
                    r.setChecked(true);
                    switch (i) {
                        case 0:
                            bGroundColor = R.color.color_lightblue;
                            break;
                        case 1:
                            bGroundColor = R.color.color_chocolateBrown;
                            break;

                        case 2:
                            bGroundColor = R.color.color_pink;
                            break;

                        case 3:
                            bGroundColor = R.color.color_aqua;
                            break;
                        default:
                            bGroundColor = R.color.color_purple;

                    }


                    ScrollView scrollViewObject = findViewById(R.id.scrollView);
                    scrollViewObject.setBackgroundResource(bGroundColor);
                }
            }
        }


    }

    private void initSortByClick() {
        RadioGroup rgSortBy = (RadioGroup) findViewById(R.id.radioGroupSortBy);
        rgSortBy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                RadioButton rbName = (RadioButton) findViewById(R.id.radioName);
                RadioButton rbCity = (RadioButton) findViewById(R.id.radioCity);
                if (rbName.isChecked()) {
                    getSharedPreferences("MyContactListPreferences",
                            Context.MODE_PRIVATE).edit() .
                            putString("sortfield", "contactname").commit();
                }
                else if (rbCity.isChecked()) {
                    getSharedPreferences("MyContactListPreferences",
                            Context.MODE_PRIVATE).edit().
                            putString("sortfield", "city").commit();
                }
                else {
                    getSharedPreferences("MyContactListPreferences",
                            Context.MODE_PRIVATE).edit().
                            putString("sortfield", "birthday").commit();
                }
            }
        });
    }

    private void initSortOrderClick() {
        RadioGroup rgSortOrder = (RadioGroup) findViewById(R.id.radioGroupSortOrder);
        rgSortOrder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                RadioButton rbAscending = (RadioButton) findViewById(R.id.radioAscending);
                if (rbAscending.isChecked()) {
                    getSharedPreferences("MyContactListPreferences",
                            Context.MODE_PRIVATE).edit().
                            putString("sortorder", "ASC").commit();
                }
                else {
                    getSharedPreferences("MyContactListPreferences",
                            Context.MODE_PRIVATE).edit().
                            putString("sortorder", "DESC").commit();
                }
            }
        });
    }
    private void initSelectBgColorClick() {
        RadioGroup rgBgColor = (RadioGroup) findViewById(R.id.radioBgColor);
        rgBgColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {

                int selectedId = arg0.getCheckedRadioButtonId();//gets the selected radio button id
                RadioButton bgColorButton =
                        (RadioButton) findViewById(selectedId);//gets the selected radio button
                String textValue = (String) bgColorButton.getText();//get the text value of
                // the selected radio button


                //saving the background color selected
                getSharedPreferences("MyContactListPreferences",
                        Context.MODE_PRIVATE).edit().
                        putString("bgColor", textValue).commit();

                bgColorButton.setChecked(true);//checking the selected radio button
                int bGroundColor = R.color.color_lightblue;//variable to hold the color value

                //get the index of the radio button
                //in the radio group
                int indexOfChild = arg0.indexOfChild(bgColorButton);
                switch (indexOfChild) {
                    case 0:
                        bGroundColor = R.color.color_lightblue;
                        break;
                    case 1:
                        bGroundColor = R.color.color_chocolateBrown;
                        break;

                    case 2:
                        bGroundColor = R.color.color_pink;
                        break;

                    case 3:
                        bGroundColor = R.color.color_aqua;
                        break;
                    default:
                        bGroundColor = R.color.color_purple;

                }


                ScrollView scrollViewObject = findViewById(R.id.scrollView);
                scrollViewObject.setBackgroundResource(bGroundColor);
            }


        });
    }
}
