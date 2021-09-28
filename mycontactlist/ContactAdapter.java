package com.example.mycontactlist;

import android.content.Context;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class ContactAdapter extends ArrayAdapter<Contact> {

    private ArrayList<Contact> items;
    private Context adapterContext;
    private int colorsList[]={Color.RED,Color.BLUE};

    public ContactAdapter(Context context, ArrayList<Contact> items) {
        super(context, R.layout.list_item, items);
        adapterContext = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        try {
            Contact contact = items.get(position);

            if (v == null) {
                LayoutInflater vi = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }

            TextView contactName = (TextView) v.findViewById(R.id.textContactName);
            contactName.setTextColor(colorsList[position%2]);
            ImageView imageBff = (ImageView) v.findViewById(R.id.imageBff);
            TextView streetAddress = (TextView) v.findViewById(R.id.textStreetAddress);
            TextView line2 = (TextView) v.findViewById(R.id.textLine2);
            TextView contactPhoneNumber = (TextView) v.findViewById(R.id.textPhoneNumber);
            Button b = (Button) v.findViewById(R.id.buttonDeleteContact);

            contactName.setText(contact.getContactName());
            imageBff.setVisibility(contact.getBff()==0?View.INVISIBLE:View.VISIBLE);
            streetAddress.setText(contact.getStreetAddress());
            line2.setText(""+contact.getCity()+
                    ", "+contact.getState()+", "+contact.getZipCode());
            contactPhoneNumber.setText("Home:"+contact.getPhoneNumber()+
                    " Cell:"+contact.getCellNumber());

            b.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            e.getCause();
        }
        return v;
    }
    //Listing 6.15 Code to delete from list
    public void showDelete(final int position, final View convertView,final Context context, final Contact contact) {
        View v = convertView;
        final Button b = (Button) v.findViewById(R.id.buttonDeleteContact);

        if (b.getVisibility()==View.INVISIBLE) {
            b.setVisibility(View.VISIBLE);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideDelete(position, convertView, context);
                    items.remove(contact);
                    deleteOption(contact.getContactID(), context);
                }
            });
        }
        else {
            hideDelete(position, convertView, context);
        }
    }

    private void deleteOption(int contactToDelete, Context context) {
        ContactDataSource db = new ContactDataSource(context);
        try {
            db.open();
            db.deleteContact(contactToDelete);
            db.close();
        }
        catch (Exception e) {
            Toast.makeText(adapterContext, "Delete Contact Failed", Toast.LENGTH_LONG).show();
        }
        this.notifyDataSetChanged();
    }

    public void hideDelete(int position, View convertView, Context context) {
        View v = convertView;
        final Button b = (Button) v.findViewById(R.id.buttonDeleteContact);
        b.setVisibility(View.INVISIBLE);
        b.setOnClickListener(null);
    }
}