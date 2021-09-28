package com.example.mycontactlist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;
public class ContactActivity extends AppCompatActivity implements
        com.example.mycontactlist.DatePickerDialog.SaveDateListener{
    private Contact currentContact;

    final int PERMISSION_REQUEST_PHONE = 102;
    final int PERMISSION_REQUEST_CAMERA = 103;
    final int CAMERA_REQUEST = 1888;
    final int PERMISSION_REQUEST_SEND_SMS=101;
    final int PERMISSION_REQUEST_READ_SMS=99;
    final int PERMISSION_REQUEST_RECEIVE_SMS=100;


    private BroadcastReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        initListButton();
        initMapButton();
        initSettingsButton();
        initToggleButton();
        initImageButton();
        setForEditing(false);
        initChangeDateButton();
        initNewButton();
        initTextChangedEvents();
        initSaveButton();

        //P.153
        //initCallFunction();
        initSMSFunction();

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            try {
                initContact(extras.getInt("contactid"));
            }catch(Exception e){

            }
        }
        else {
            currentContact = new Contact();
        }

       smsReceiver=new SMSReceiver(ContactActivity.this);
        registerReceiver(smsReceiver,
                new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
        checkSMSReceivePermission(currentContact.getPhoneNumber());
        checkSMSReadPermission(currentContact.getPhoneNumber());

    }
    protected void onResume() {
        super.onResume();
        registerReceiver(smsReceiver,
                new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
    }
    @Override
    public void onPause(){
        super.onPause();

        try{
            unregisterReceiver(smsReceiver);
        }
        catch(Exception e){

            e.printStackTrace();
        }
    }
private void showMessage(String message ){
    AlertDialog.Builder builder1 = new AlertDialog.Builder(ContactActivity.this);
    //builder1.setMessage("Write your message here.");
    builder1.setMessage(message);
    builder1.setCancelable(true);
    builder1.setPositiveButton(
            "OK",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

    builder1.setNegativeButton(
            "Cancel",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });

    AlertDialog alert11 = builder1.create();
    alert11.show();
}
    private void initListButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonList);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initMapButton() {
        ImageButton ibMap = (ImageButton) findViewById(R.id.imageButtonMap);
//
        ibMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this,
                        ContactMapActivity.class);
                if(currentContact.getContactID()==-1){
                    Toast.makeText(getBaseContext(),
                            "Contact must be saved before it can be mapped",
                            Toast.LENGTH_LONG).show();

                }
                else{
                    intent.putExtra("contactid",currentContact.getContactID());
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initSettingsButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonSettings);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this, ContactSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }


    private void initToggleButton() {
        final ToggleButton editToggle = (ToggleButton) findViewById(R.id.toggleButtonEdit);
        editToggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setForEditing(editToggle.isChecked());
            }
        });
    }
    private void initNewButton() {
        final Button newButton = (Button) findViewById(R.id.buttonNew);
        newButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                setForEditing(true);
                EditText editName = (EditText) findViewById(R.id.editName);
                EditText editAddress = (EditText) findViewById(R.id.editAddress);
                EditText editCity = (EditText) findViewById(R.id.editCity);
                EditText editState = (EditText) findViewById(R.id.editState);
                EditText editZipCode = (EditText) findViewById(R.id.editZipcode);
                EditText editPhone = (EditText) findViewById(R.id.editHome);
                EditText editCell = (EditText) findViewById(R.id.editCell);
                EditText editEmail = (EditText) findViewById(R.id.editEMail);
                CheckBox checkBoxBff=(CheckBox)findViewById(R.id.checkbox_Bff);
                Button buttonChange = (Button) findViewById(R.id.btnBirthday);
                Button buttonSave = (Button) findViewById(R.id.buttonSave);

                editName.setText("");
                editAddress.setText("");
                editCity.setText("");
                editState.setText("");
                editZipCode.setText("");
                editPhone.setText("");
                editCell.setText("");
                editEmail.setText("");
                checkBoxBff.setChecked(false);
//                buttonChange.setEnabled(true);
//                buttonSave.setEnabled(true);

                editName.requestFocus();

                currentContact.setContactID(-1);
            }
        });
    }
    private void initTextChangedEvents(){
        final EditText etContactName = (EditText) findViewById(R.id.editName);
        etContactName.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                currentContact.setContactName(etContactName.getText().toString());
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                //  Auto-generated method stub
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //  Auto-generated method stub
            }
        });

        final EditText etStreetAddress = (EditText) findViewById(R.id.editAddress);
        etStreetAddress.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                currentContact.setStreetAddress(etStreetAddress.getText().toString());
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                //  Auto-generated method stub
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //  Auto-generated method stub
            }
        });

        final EditText etCity = (EditText) findViewById(R.id.editCity);
        etCity.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                currentContact.setCity(etCity.getText().toString());
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                //  Auto-generated method stub
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //  Auto-generated method stub
            }
        });

        final EditText etState = (EditText) findViewById(R.id.editState);
        etState.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                currentContact.setState(etState.getText().toString());
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                //  Auto-generated method stub
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //  Auto-generated method stub
            }
        });

        final EditText etZipCode = (EditText) findViewById(R.id.editZipcode);
        etZipCode.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                currentContact.setZipCode(etZipCode.getText().toString());
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                //  Auto-generated method stub
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //  Auto-generated method stub
            }
        });

        final EditText etHomePhone = (EditText) findViewById(R.id.editHome);
        etHomePhone.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                currentContact.setPhoneNumber(etHomePhone.getText().toString());
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                //  Auto-generated method stub
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //  Auto-generated method stub
            }
        });

        final EditText etCellPhone = (EditText) findViewById(R.id.editCell);
        etCellPhone.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                currentContact.setCellNumber(etCellPhone.getText().toString());
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                //  Auto-generated method stub
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //  Auto-generated method stub
            }
        });

        final EditText etEmail = (EditText) findViewById(R.id.editEMail);
        etEmail.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                currentContact.setEMail(etEmail.getText().toString());
            }
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                //  Auto-generated method stub
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //  Auto-generated method stub
            }
        });

        etHomePhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        etCellPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

    }
    private void initSaveButton() {
        Button saveButton = (Button) findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideKeyboard();
                boolean wasSuccessful = false;
                final CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_Bff);
                currentContact.setBff(checkBox.isChecked()?1:0);

                //The photo is taken in
                // protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                //when testing in a real phone, delete following 5 lines.
//                Bitmap photo = BitmapFactory.decodeResource(getResources(),
//                        R.drawable.sample_drawable_image);
//                Bitmap scaledPhoto = Bitmap.createScaledBitmap(photo, 144, 144, true);
//                ImageButton imageContact = (ImageButton) findViewById(R.id.imageContact);
//                imageContact.setImageBitmap(scaledPhoto);
//                currentContact.setPicture(scaledPhoto);
    //-----------------------------------------------------------
                ContactDataSource ds = new ContactDataSource(ContactActivity.this);
                try {
                    ds.open();

                    if (currentContact.getContactID() == -1) {
                        wasSuccessful = ds.insertContact(currentContact);

                        if(wasSuccessful){
                            int newId = ds.getLastContactId();
                            currentContact.setContactID(newId);
                        }

                    } else {
                        wasSuccessful = ds.updateContact(currentContact);
                    }
                    ds.close();
                }
                catch (Exception e) {
                    wasSuccessful = false;
                }

                if (wasSuccessful) {
                    ToggleButton editToggle = (ToggleButton) findViewById(R.id.toggleButtonEdit);
                    editToggle.toggle();
                    setForEditing(false);
//                    currentContact.setContactID(-1);
                }

            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        EditText editName = (EditText) findViewById(R.id.editName);
        imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
        EditText editAddress = (EditText) findViewById(R.id.editAddress);
        imm.hideSoftInputFromWindow(editAddress.getWindowToken(), 0);
        EditText editCity = (EditText) findViewById(R.id.editCity);
        imm.hideSoftInputFromWindow(editCity.getWindowToken(), 0);
        EditText editState= (EditText) findViewById(R.id.editState);
        imm.hideSoftInputFromWindow(editState.getWindowToken(), 0);
        EditText editZip = (EditText) findViewById(R.id.editZipcode);
        imm.hideSoftInputFromWindow(editZip.getWindowToken(), 0);
        EditText editHome = (EditText) findViewById(R.id.editHome);
        imm.hideSoftInputFromWindow(editHome.getWindowToken(), 0);
        EditText editCell = (EditText) findViewById(R.id.editCell);
        imm.hideSoftInputFromWindow(editCell.getWindowToken(), 0);
        EditText editEMail = (EditText) findViewById(R.id.editEMail);
        imm.hideSoftInputFromWindow(editEMail.getWindowToken(), 0);
    }

    private void setForEditing(boolean enabled) {
        EditText editName = (EditText) findViewById(R.id.editName);
        EditText editAddress = (EditText) findViewById(R.id.editAddress);
        EditText editCity = (EditText) findViewById(R.id.editCity);
        EditText editState = (EditText) findViewById(R.id.editState);
        EditText editZipCode = (EditText) findViewById(R.id.editZipcode);
        EditText editPhone = (EditText) findViewById(R.id.editHome);
        EditText editCell = (EditText) findViewById(R.id.editCell);
        EditText editEmail = (EditText) findViewById(R.id.editEMail);
        Button buttonChange = (Button) findViewById(R.id.btnBirthday);
        CheckBox checkBoxBff=(CheckBox)findViewById(R.id.checkbox_Bff);
        Button buttonSave = (Button) findViewById(R.id.buttonSave);
        Button buttonNew = (Button) findViewById(R.id.buttonNew);
        ImageButton picture = (ImageButton) findViewById(R.id.imageContact);

        picture.setEnabled(enabled);
        editName.setEnabled(enabled);
        editAddress.setEnabled(enabled);
        editCity.setEnabled(enabled);
        editState.setEnabled(enabled);
        editZipCode.setEnabled(enabled);
        //editPhone.setEnabled(enabled);
        //editCell.setEnabled(enabled);
        editEmail.setEnabled(enabled);
        buttonChange.setEnabled(enabled);
        checkBoxBff.setEnabled(enabled);
        buttonSave.setEnabled(enabled);
        buttonNew.setEnabled(enabled);

        if (enabled) {
            editName.requestFocus();
            editPhone.setEnabled(true);
            editCell.setEnabled(true);
            editPhone.setInputType(InputType.TYPE_CLASS_PHONE);
            editCell.setInputType(InputType.TYPE_CLASS_PHONE);
        }
        else {
            editPhone.setInputType(InputType.TYPE_NULL);
            editCell.setInputType(InputType.TYPE_NULL);
            editPhone.setEnabled(false);
            editCell.setEnabled(false);
            ScrollView s = (ScrollView) findViewById(R.id.scrollView);
            s.fullScroll(ScrollView.FOCUS_UP);
            s.clearFocus();
        }

    }

    private void initChangeDateButton() {
        Button changeDate = (Button) findViewById(R.id.btnBirthday);
        changeDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();                  //1
                com.example.mycontactlist.DatePickerDialog datePickerDialog =
                        new com.example.mycontactlist.DatePickerDialog();        //2
                datePickerDialog.show(fm, "DatePick");                             //3
            }
        });
    }

    @Override
    public void didFinishDatePickerDialog(Calendar selectedTime) {
        TextView birthDay = (TextView) findViewById(R.id.textBirthday);
        birthDay.setText(DateFormat.format(
                "MM/dd/yyyy", selectedTime.getTimeInMillis()).toString());
        currentContact.setBirthday(selectedTime);

    }
    private void initContact(int id) {

        ContactDataSource ds = new ContactDataSource(ContactActivity.this);
        try {
            ds.open();
            currentContact = ds.getSpecificContact(id);
            ds.close();
        }
        catch (Exception e) {
            Toast.makeText(this, e.getMessage()+" Load Contact Failed ,Current ID : "+id, Toast.LENGTH_LONG).show();
        }

        EditText editName = (EditText) findViewById(R.id.editName);
        EditText editAddress = (EditText) findViewById(R.id.editAddress);
        EditText editCity = (EditText) findViewById(R.id.editCity);
        EditText editState = (EditText) findViewById(R.id.editState);
        EditText editZipCode = (EditText) findViewById(R.id.editZipcode);
        EditText editPhone = (EditText) findViewById(R.id.editHome);
        EditText editCell = (EditText) findViewById(R.id.editCell);
        EditText editEmail = (EditText) findViewById(R.id.editEMail);
        TextView birthDay = (TextView) findViewById(R.id.textBirthday);
        CheckBox checkBoxBff=(CheckBox)findViewById(R.id.checkbox_Bff);

        editName.setText(currentContact.getContactName());
        editAddress.setText(currentContact.getStreetAddress());
        editCity.setText(currentContact.getCity());
        editState.setText(currentContact.getState());
        editZipCode.setText(currentContact.getZipCode());
        editPhone.setText(currentContact.getPhoneNumber());
        editCell.setText(currentContact.getCellNumber());
        editEmail.setText(currentContact.getEMail());
        birthDay.setText(DateFormat.format("MM/dd/yyyy",
                currentContact.getBirthday().getTimeInMillis ()).toString());
        checkBoxBff.setChecked(currentContact.getBff()==0?false:true);
        ImageButton picture = (ImageButton) findViewById(R.id.imageContact);


        if (currentContact.getPicture() != null) {
            picture.setImageBitmap(currentContact.getPicture());
        }
        else {
            picture.setImageResource(R.drawable.photoicon);
        }
    }
    private void initCallFunction() {
        EditText editPhone = (EditText) findViewById(R.id.editHome);
        editPhone.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View arg0) {
                checkPhonePermission(currentContact.getPhoneNumber());
                return false;
            }
        });

        EditText editCell = (EditText) findViewById(R.id.editCell);
        editCell.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View arg0) {
                checkPhonePermission(currentContact.getCellNumber());


                return false;
            }
        });
    }
    private void checkPhonePermission(String phoneNumber) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(ContactActivity.this,
                    android.Manifest.permission.CALL_PHONE) !=
                    PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        ContactActivity.this, android.Manifest.permission.CALL_PHONE)) {

                    Snackbar.make(findViewById(R.id.activity_contact),
                            "MyContactList requires this permission to place " +
                                    "a call from the app.", Snackbar.LENGTH_INDEFINITE).
                            setAction("OK", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            ActivityCompat.requestPermissions(
                                    ContactActivity.this,
                                    new String[]{android.Manifest.permission.CALL_PHONE},
                                    PERMISSION_REQUEST_PHONE);
                        }
                    }).show();

                } else {
                    ActivityCompat.requestPermissions(ContactActivity.this,
                            new String[]{android.Manifest.permission.CALL_PHONE},
                            PERMISSION_REQUEST_PHONE);
                }
            } else {
                callContact(phoneNumber);
            }
        } else {
            callContact(phoneNumber);
        }
    }
    private void callContact(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.CALL_PHONE ) !=
                        PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        else {
            startActivity(intent);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_PHONE: {
                if (grantResults.length > 0 && grantResults[0] ==

                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ContactActivity.this,
                            "You may now call from this app.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ContactActivity.this,
                            "You will not be able to make calls from this app",
                            Toast.LENGTH_LONG).show();
                }
            }
            case PERMISSION_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    Toast.makeText(ContactActivity.this,
                            "You will not be able to save " +
                                    "contact pictures from this app",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
            case PERMISSION_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] ==

                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ContactActivity.this,
                            "You may now send SMS from this app.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ContactActivity.this,
                            "You will not be able to send SMS from this app",
                            Toast.LENGTH_LONG).show();
                }
            }
//            final int PERMISSION_REQUEST_READ_SMS=99;
//            final int PERMISSION_REQUEST_RECEIVE_SMS=100;
            case PERMISSION_REQUEST_READ_SMS:{
                if (grantResults.length > 0 && grantResults[0] ==

                        PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(ContactActivity.this,
//                            "MCL : You may now read SMS from this app.",
//                            Toast.LENGTH_LONG).show();
                    AlertDialog alertDialog1 = new AlertDialog.Builder(
                            ContactActivity.this).create();
                    alertDialog1.setTitle("SMS message ");

                    alertDialog1.setMessage("MyContactList : You may now read SMS from this app.");

                    alertDialog1.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                } });
                    alertDialog1.show();
                } else {
                    Toast.makeText(ContactActivity.this,
                            "You will not be able to read SMS from this app",
                            Toast.LENGTH_LONG).show();
                }
            }
            case PERMISSION_REQUEST_RECEIVE_SMS: {
                if (grantResults.length > 0 && grantResults[0] ==

                        PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(ContactActivity.this,
//                            "MCL: You may now receive SMS from this app.",
//                            Toast.LENGTH_LONG).show();
                    AlertDialog alertDialog1 = new AlertDialog.Builder(
                            ContactActivity.this).create();
                    alertDialog1.setTitle("SMS message ");

                    alertDialog1.setMessage("MyContactList : " +
                            " You may now receive SMS from this app.");

                    alertDialog1.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                } });
                    alertDialog1.show();
                } else {
                    Toast.makeText(ContactActivity.this,
                            "You will not be able to receive SMS from this app",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    public void takePhoto(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }


    @SuppressLint("MissingSuperCall")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Bitmap scaledPhoto = Bitmap.createScaledBitmap(photo,
                        144, 144, true);
                ImageButton imageContact = (ImageButton) findViewById(R.id.imageContact);
                imageContact.setImageBitmap(scaledPhoto);
                currentContact.setPicture(scaledPhoto);
            }
        }
    }
    private void initImageButton() {
        ImageButton ib = (ImageButton) findViewById(R.id.imageContact);
        ib.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(ContactActivity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(ContactActivity.this, android.Manifest.permission.CAMERA)) {
                            Snackbar.make(findViewById(R.id.activity_contact),
                                    "The app needs permission to take pictures.",
                                    Snackbar.LENGTH_INDEFINITE).setAction("Ok",
                                    new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    ActivityCompat.requestPermissions(
                                            ContactActivity.this,
                                            new String[]{ android.Manifest.permission.CAMERA},
                                            PERMISSION_REQUEST_CAMERA);
                                }
                            }).show();
                        } else {
                            ActivityCompat.requestPermissions(
                                    ContactActivity.this,
                                    new String[]{android.Manifest.permission.CAMERA},
                                    PERMISSION_REQUEST_CAMERA);
                        }
                    }
                    else {
                        takePhoto();
                    }
                } else {
                    takePhoto();
                }
            }
        });
    }

//SMS function
    private void initSMSFunction() {
        EditText editPhone = (EditText) findViewById(R.id.editHome);
        editPhone.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View arg0) {
                checkSMSPermission(currentContact.getPhoneNumber());
                return false;
            }
        });

        EditText editCell = (EditText) findViewById(R.id.editCell);
        editCell.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View arg0) {
                checkSMSPermission(currentContact.getCellNumber());
                return false;
            }
        });
    }
    private void checkSMSPermission(String phoneNumber) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(ContactActivity.this,
                    android.Manifest.permission.SEND_SMS) !=
                    PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        ContactActivity.this, android.Manifest.permission.SEND_SMS)) {

                    Snackbar.make(findViewById(R.id.activity_contact),
                            "MyContactList requires this permission to send " +
                                    "an SMS from the app.", Snackbar.LENGTH_INDEFINITE).
                            setAction("OK", new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {

                                    ActivityCompat.requestPermissions(
                                            ContactActivity.this,
                                            new String[]{android.Manifest.permission.SEND_SMS},
                                            PERMISSION_REQUEST_SEND_SMS);
                                }
                            }).show();

                } else {
                    ActivityCompat.requestPermissions(ContactActivity.this,
                            new String[]{android.Manifest.permission.SEND_SMS},
                            PERMISSION_REQUEST_SEND_SMS);
                }
            } else {
                SMSContact(phoneNumber);

            }
        } else {
            SMSContact(phoneNumber);
        }
    }
    private void SMSContact(String phoneNumber) {
//        Uri uri = Uri.parse("smsto:"+ phoneNumber);
//        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
//        intent.putExtra("sms_body", "The SMS text");
//
        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.SEND_SMS ) !=
                        PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        else {
            //startActivity(intent);//opens default SMS Messenger

            //Opens a dialog for SMS
            Intent intent = new Intent(ContactActivity.this,
                    MessagingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);



        }
    }



    private static class SMSReceiver extends BroadcastReceiver {
        private Bundle bundle;
        private SmsMessage currentSMS;
        private String message;
        private AppCompatActivity activity;

        public SMSReceiver(){}
        public SMSReceiver(AppCompatActivity activity){

            this.activity= activity;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // Retrieves a map of extended data from the intent.
            final Bundle bundle = intent.getExtras();

            try {

                if (bundle != null) {

                    final Object[] pdusObj = (Object[]) bundle.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {

                        SmsMessage currentMessage = SmsMessage.createFromPdu(
                                (byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                        String message = currentMessage.getDisplayMessageBody();

                        //
                        AlertDialog alertDialog = new AlertDialog.Builder(
                                activity).create();
                        alertDialog.setTitle("SMS message ");

                        alertDialog.setMessage("MyContactList \nsenderNumber: " + phoneNumber +
                                "\n" +
                                "message: " +message);

                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    } });
                        alertDialog.show();

                    }
                }

            } catch (Exception e) {
                AlertDialog alertDialog = new AlertDialog.Builder(
                        activity).create();
                alertDialog.setTitle("SMS message ");

                alertDialog.setMessage("MyContactList : an exception occured \n"+
                        e.getMessage());

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            } });
                alertDialog.show();
            }
        }

    }


    private void checkSMSReceivePermission(String phoneNumber) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(ContactActivity.this,
                    android.Manifest.permission.RECEIVE_SMS) !=
                    PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        ContactActivity.this,
                        android.Manifest.permission.RECEIVE_SMS)) {

                    Snackbar.make(findViewById(R.id.activity_contact),
                            "MyContactList requires this permission to receive " +
                                    "an SMS.", Snackbar.LENGTH_INDEFINITE).
                            setAction("OK", new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {

                                    ActivityCompat.requestPermissions(
                                            ContactActivity.this,
                                            new String[]{
                                                    android.Manifest.permission.RECEIVE_SMS},
                                            PERMISSION_REQUEST_RECEIVE_SMS);
                                }
                            }).show();

                } else {
                    ActivityCompat.requestPermissions(ContactActivity.this,
                            new String[]{android.Manifest.permission.RECEIVE_SMS},
                            PERMISSION_REQUEST_RECEIVE_SMS);
                }
            } else {
                //SMSContact(phoneNumber);

            }
        } else {
            //SMSContact(phoneNumber);


        }
    }
    private void checkSMSReadPermission(String phoneNumber) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(ContactActivity.this,
                    android.Manifest.permission.READ_SMS) !=
                    PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        ContactActivity.this, android.Manifest.permission.READ_SMS)) {

                    Snackbar.make(findViewById(R.id.activity_contact),
                            "MyContactList requires this permission to read " +
                                    "an SMS .", Snackbar.LENGTH_INDEFINITE).
                            setAction("OK", new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {

                                    ActivityCompat.requestPermissions(
                                            ContactActivity.this,
                                            new String[]{android.Manifest.permission.READ_SMS},
                                            PERMISSION_REQUEST_READ_SMS);
                                }
                            }).show();

                } else {
                    ActivityCompat.requestPermissions(ContactActivity.this,
                            new String[]{android.Manifest.permission.READ_SMS},
                            PERMISSION_REQUEST_READ_SMS);
                }
            } else {
                //SMSContact(phoneNumber);



            }
        } else {
            //SMSContact(phoneNumber);


        }
    }


}
