package com.example.mycontactlist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity {
    ArrayList<Contact> contacts;
    boolean isDeleting =false;
    ContactAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);
        initListButton();
        initSettingsButton();
        initMapButton();

//        //P.98 :Listing 6.3 Simple list activation code
//        ContactDataSource ds = new ContactDataSource(this);
//        ArrayList<String> names;
//        try {
//            ds.open();
//            names = ds.getContactName();
//            ds.close();
//            ListView listView=(ListView)findViewById(R.id.lvContacts);
//            listView.setAdapter(new ArrayAdapter<String>(this,
//                    android.R.layout.simple_list_item_1,names));
//
//        }
//        catch (Exception e) {
//            Toast.makeText(this,
//                    "Error retrieving contacts", Toast.LENGTH_LONG).show();
//        }
        //P.104 :Listing 6.8 selected item click


        initItemClick();
        initAddContactButton();
        initDeleteButton();

        BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                double batteryLevel= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
                double levelScale= intent.getIntExtra(BatteryManager.EXTRA_SCALE,0);
                int batteryPercent = (int) Math.floor(batteryLevel/levelScale*100);
                TextView textBatteryState=(TextView)findViewById(R.id.textBatteryLevel);
                textBatteryState.setText(batteryPercent+"%");
            }
        };

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, filter);

    }
    @Override
    public void onResume(){
        super.onResume();
        String sortBy = getSharedPreferences("MyContactListPreferences",
                Context.MODE_PRIVATE).getString("sortfield", "contactname");
        String sortOrder = getSharedPreferences("MyContactListPreferences",
                Context.MODE_PRIVATE).getString("sortorder", "ASC");

        ContactDataSource ds = new ContactDataSource(this);

        try {
            ds.open();
            contacts = ds.getContacts(sortBy,sortOrder);
            ds.close();
            if(contacts.size()>0){
                ListView listView=(ListView)findViewById(R.id.lvContacts);
                //listView.setAdapter(new ContactAdapter(this,contacts));
                //P.113
                adapter = new ContactAdapter(this,contacts);
                listView.setAdapter(adapter);
            }else{
                Intent intent = new Intent(ContactListActivity.this,
                        ContactActivity.class);
                startActivity(intent);
            }


        }
        catch (Exception e) {
            Toast.makeText(this,
                    "Error retrieving contacts", Toast.LENGTH_LONG).show();
        }

    }
    private void initListButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonList);
        ibList.setEnabled(false);
    }

    private void initMapButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonMap);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactListActivity.this,
                        ContactMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initSettingsButton() {
        ImageButton ibList = (ImageButton) findViewById(R.id.imageButtonSettings);
        ibList.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactListActivity.this, ContactSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


    }
//    private void initItemClick() {
//        ListView listView=(ListView)findViewById(R.id.lvContacts);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View itemClicked,
//                                    int position, long id) {
//                Contact selectedContact=contacts.get(position);
//                Intent intent = new Intent(ContactListActivity.this,
//                        ContactActivity.class);
//                intent.putExtra("contactid",selectedContact.getContactID());
//                startActivity(intent);
//            }
//
//
//
//        });
//    }
        private void initItemClick() {
        ListView listView=(ListView)findViewById(R.id.lvContacts);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked,
                                    int position, long id) {
                Contact selectedContact=contacts.get(position);
                if(isDeleting){
                    adapter.showDelete(position,itemClicked,
                            ContactListActivity.this,selectedContact);

                }
                else{
                    Intent intent = new Intent(ContactListActivity.this,
                            ContactActivity.class);
                    intent.putExtra("contactid",selectedContact.getContactID());
                    startActivity(intent);
                }

            }



        });
    }
    private void initAddContactButton() {
        Button newContact = (Button) findViewById(R.id.buttonAdd);
        newContact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(ContactListActivity.this,
                        ContactActivity.class);
                startActivity(intent);
            }
        });
    }
//Listing 6.16 initDeleteButton() code
    private void initDeleteButton() {
        final Button deleteButton = (Button) findViewById(R.id.buttonDelete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (isDeleting) {
                    deleteButton.setText("Delete");
                    isDeleting = false;
                    adapter.notifyDataSetChanged();
                }
                else {
                    deleteButton.setText("Done Deleting");
                    isDeleting = true;
                }
            }
        });
    }
}
