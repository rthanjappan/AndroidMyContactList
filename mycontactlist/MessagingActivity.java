package com.example.mycontactlist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mycontactlist.R.layout.*;
import com.google.android.material.snackbar.Snackbar;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MessagingActivity extends AppCompatActivity {
    final int PERMISSION_REQUEST_SEND_SMS=101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);


        initSMSFunction();


    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        switch (requestCode) {

            case PERMISSION_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] ==

                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MessagingActivity.this,
                            "You may now send SMS from this app.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MessagingActivity.this,
                            "You will not be able to send SMS from this app",
                            Toast.LENGTH_LONG).show();
                }
            }

        }
    }
    //SMS function
    private void initSMSFunction() {
        EditText editPhone = (EditText) findViewById(R.id.editTextPhone);
        EditText editMessage = (EditText) findViewById(R.id.editTextMessage);

        editPhone.setEnabled(true);
        editMessage.setEnabled(true);


        editPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        editPhone.requestFocus();


        Button sendBtn = (Button) findViewById(R.id.btnSendSMS);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                hideKeyboard();
                checkSMSPermission();

            }
        });


    }
    private void checkSMSPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MessagingActivity.this,
                    android.Manifest.permission.SEND_SMS) !=
                    PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        MessagingActivity.this, android.Manifest.permission.SEND_SMS)) {

                    Snackbar.make(findViewById(R.id.activity_messaging),
                            "MyContactList requires this permission to send " +
                                    "an SMS from the app.", Snackbar.LENGTH_INDEFINITE).
                            setAction("OK", new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {

                                    ActivityCompat.requestPermissions(
                                            MessagingActivity.this,
                                            new String[]{android.Manifest.permission.SEND_SMS},
                                            PERMISSION_REQUEST_SEND_SMS);
                                }
                            }).show();

                } else {
                    ActivityCompat.requestPermissions(MessagingActivity.this,
                            new String[]{android.Manifest.permission.SEND_SMS},
                            PERMISSION_REQUEST_SEND_SMS);
                }
            } else {
                SMSContact();
            }
        } else {
            SMSContact();
        }
    }
    private void SMSContact() {

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getBaseContext(),
                        android.Manifest.permission.SEND_SMS ) !=
                        PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        else {

            sendSMSFunction();



        }
    }
    private void sendSMSFunction() {

                try {


                    EditText txtphoneNo = (EditText) findViewById(R.id.editTextPhone);
                    EditText txtMessage = (EditText) findViewById(R.id.editTextMessage);
                    String phoneNo = txtphoneNo.getText().toString();
                    String message = txtMessage.getText().toString();

                    AlertDialog alertDialog = new AlertDialog.Builder(
                            MessagingActivity.this).create();
                    alertDialog.setTitle("SMS message ");

                    alertDialog.setMessage("MyContactList \nsenderNumber: " + phoneNo +
                            "\n" +
                            "message: " + message);

                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    alertDialog.show();
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "MyContactList : SMS sent.",
                            Toast.LENGTH_LONG).show();
                }
                catch(Exception e){
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }

    }
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        EditText editPhone = (EditText) findViewById(R.id.editTextPhone);
        imm.hideSoftInputFromWindow(editPhone.getWindowToken(), 0);
        EditText editMessage = (EditText) findViewById(R.id.editTextMessage);
        imm.hideSoftInputFromWindow(editMessage.getWindowToken(), 0);

    }



}
