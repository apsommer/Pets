package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

// content provider
public class PetProvider extends ContentProvider {

    // tag for log messages
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    // database helper object
    private PetDbHelper mDbHelper;

    // codes for URI matcher in the pets table
    private static final int PETS = 100;
    private static final int PET_ID = 101;

    // UriMatcher object matches a content URI to an integer code
    // the input passed to the constructor represents the code to return for the root URI
    // it is common to use the framework NO_MATCH constant for this initial case
    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // static initializer is run the first time anything is called from this class
    static {

        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // recognize these content URI patterns and return the specified integer codes
        // first line assigns code 100 to "content://com.example.android.pets/pets"
        mUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        mUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);

    }

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
