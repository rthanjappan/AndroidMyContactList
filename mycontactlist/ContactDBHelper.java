package com.example.mycontactlist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ContactDBHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "mycontacts.db";
        private static final int DATABASE_VERSION = 7;

        // Database creation sql statement
        private static final String CREATE_TABLE_CONTACT =
                "create table contact (_id integer primary key autoincrement, "
                        + "contactname text not null, streetaddress text, "
                        + "city text, state text, zipcode text, "
                        + "phonenumber text, cellnumber text, "
                        + "email text, birthday text);";

        public ContactDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(CREATE_TABLE_CONTACT);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(ContactDBHelper.class.getName(),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ", " + "which will keep old data " +
                            "and add a new column called bestFriendForever");
//            db.execSQL("DROP TABLE IF EXISTS contact");
//            onCreate(db);

            //db.execSQL("alter table Contact add column bestfriendforever int default 0");

            try {
                db.execSQL("ALTER TABLE contact ADD COLUMN contactphoto blob");
            }
            catch (Exception e) {
                //do nothing
            }

        }
}
