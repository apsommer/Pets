package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

// content provider
public class PetProvider extends ContentProvider {

    // tag for log messages
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    // database helper object
    private PetDbHelper mDbHelper;

    // initialize provider and database helper
    @Override
    public boolean onCreate() {

        // create and initialize a PetDbHelper object to gain access to the pets database
        mDbHelper = new PetDbHelper(getContext());

        return true;
    }


    // perform a query on the given URI
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    // get MIMI data type at the content URI
    @Override
    public String getType(Uri uri) {
        return null;
    }

    // insert new data into provider
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    // delete data at the given selection
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    // update data at the given selection
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }
}
