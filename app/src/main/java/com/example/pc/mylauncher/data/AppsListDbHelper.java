package com.example.pc.mylauncher.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.pc.mylauncher.data.AppsListContract.AppsListEntry;

/**
 * Created by PC on 25.04.2017.
 */

public class AppsListDbHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "appsList.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 2;

    // Constructor
    public AppsListDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // Create a table to hold waitlist data
        final String SQL_CREATE_APPSLIST_TABLE =
                "CREATE TABLE " + AppsListEntry.TABLE_NAME + " (" +
                AppsListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                AppsListEntry.COLUMN_APP_PACKAGENAME + " TEXT NOT NULL," +
                AppsListEntry.COLUMN_APP_CLICKS + " INTEGER," +
                AppsListEntry.COLUMN_APP_IS_FAVOURITE + " INTEGER" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_APPSLIST_TABLE);

        final String SQL_CREATE_URLLIST_TABLE = "CREATE TABLE " + AppsListContract.URLEntry.TABLE_NAME + " (" +
                AppsListContract.URLEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                AppsListContract.URLEntry.COLUMN_URL + " TEXT UNIQUE, " +
                AppsListContract.URLEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";
        sqLiteDatabase.execSQL(SQL_CREATE_URLLIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For now simply drop the table and create a new one. This means if you change the
        // DATABASE_VERSION the table will be dropped.
        // In a production app, this method might be modified to ALTER the table
        // instead of dropping it, so that existing data is not deleted.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AppsListEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AppsListContract.URLEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}