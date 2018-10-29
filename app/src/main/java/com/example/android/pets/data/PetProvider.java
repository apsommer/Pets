package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.pets.data.PetContract.PetEntry;

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

        // get reference to readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // cursor holds the result of the query
        Cursor cursor;

        // get pattern match code for URI
        final int match = mUriMatcher.match(uri);

        switch (match) {

            // full pets table
            case PETS:

                // perform a query on the entire pets table
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            // specific row in pets table
            case PET_ID:

                // the ? and array pattern protects against SQL injection hacker attacks
                // number of ? in selection must match number of elements in selectionArgs[]
                // equivalent to string "_id=?"
                selection = PetEntry._ID + "=?";

                // parseId extracts only the integer id from the content URI
                // equivalent to string "_id=#" where # is any integer
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // perform a query on the pets table where _id equals #, returning a single row Cursor
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);

                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI: " + uri);
        }

        return cursor;
    }

    // get MIMI data type at the content URI
    @Override
    public String getType(Uri uri) {
        return null;
    }

    // insert new data into provider
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        // get pattern match code for URI
        final int match = mUriMatcher.match(uri);

        switch (match) {

            // full pets table
            case PETS:

                // helper method returns content URI for this new row
                return insertPet(uri, contentValues);

            // specific row in pets table
            // case PET_ID: this case will never happen as insert is always at the end of the table

            // only the full table case is matches, everything else throws exception
            default:
                throw new IllegalArgumentException("Cannot query unknown URI: " + uri);
        }
    }

    // insert pet into database with given content values, return content URI for this new row
    private Uri insertPet(Uri uri, ContentValues values) {

        // check validity of name value
        // extract the value from the key : value pair
        String name = values.getAsString(PetEntry.COLUMN_PETS_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name!");
        }

        // the breed value can be null, no need to check

        // check validity of gender value
        // use a capital Integer rather than int since we are checking for nullity
        Integer gender = values.getAsInteger(PetEntry.COLUMN_PETS_GENDER);
        if (gender == null || !PetEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Pet requires a valid gender!");
        }

        // check validity of weight value
        // use a capital Integer rather than int since we are checking for nullity
        Integer weight = values.getAsInteger(PetEntry.COLUMN_PETS_WEIGHT);
        if (weight != null && weight < 0) { // null is acceptable as the sqlite database will default to 0
            throw new IllegalArgumentException("Pet requires a valid weight!");
        }

        // get reference to writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // insert new row into the pets table and get the new row id
        long newRowId = database.insert(PetEntry.TABLE_NAME, null, values);

        // if the insertion failed then newRowId = -1 and return null
        if (newRowId == -1) {
            Log.e(LOG_TAG, "Insertion failed for: " + uri);
            return null;
        }

        // return the new URI with the new ID appended to it
        return ContentUris.withAppendedId(uri, newRowId);
    }

    // update data at the given selection
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        // get pattern match code for URI
        final int match = mUriMatcher.match(uri);

        switch (match) {

            // full pets table
            case PETS:

                // helper method returns integer for number of rows updated
                return updatePet(uri, contentValues, selection, selectionArgs);

            // specific row in pets table
            case PET_ID:

                // the ? and array pattern protects against SQL injection hacker attacks
                // number of ? in selection must match number of elements in selectionArgs[]
                // equivalent to string "_id=?"
                selection = PetEntry._ID + "=?";

                // parseId extracts only the integer id from the content URI
                // equivalent to string "_id=#" where # is any integer
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // helper method returns integer for number of rows updated
                return updatePet(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update failed for: " + uri);
        }
    }

    // update pet(s) in database with given content values, return integer for number of rows updated
    // an update is an edit of existing data and affect any number of table values
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // check validity of name value, if it exists
        if (values.containsKey(PetEntry.COLUMN_PETS_NAME)) {

            // extract the value from the key : value pair
            String name = values.getAsString(PetEntry.COLUMN_PETS_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name!");
            }
        }

        // the breed value can be null, no need to check

        // check validity of gender value, if it exists
        if (values.containsKey(PetEntry.COLUMN_PETS_GENDER)) {

            // use a capital Integer rather than int since we are checking for nullity
            Integer gender = values.getAsInteger(PetEntry.COLUMN_PETS_GENDER);
            if (gender == null || !PetEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet requires a valid gender!");
            }
        }

        // check validity of weight value, if it exists
        if (values.containsKey(PetEntry.COLUMN_PETS_WEIGHT)) {

            // use a capital Integer rather than int since we are checking for nullity
            Integer weight = values.getAsInteger(PetEntry.COLUMN_PETS_WEIGHT);
            if (weight != null && weight < 0) { // null is acceptable as the sqlite database will default to 0
                throw new IllegalArgumentException("Pet requires a valid weight!");
            }
        }

        // a final check that there is actually something to update
        if (values.size() == 0) {
            return 0;
        }

        // get reference to writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // update row(s) in pets table, and get the number of total rows affected
        return database.update(PetEntry.TABLE_NAME, values, selection, selectionArgs);

    }

    // delete data at the given selection
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // get reference to writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // get pattern match code for URI
        final int match = mUriMatcher.match(uri);

        switch (match) {

            // full pets table
            case PETS:

                // delete all rows at the selection and selection arguments
                return database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);

            // specific row in pets table
            case PET_ID:

                // the ? and array pattern protects against SQL injection hacker attacks
                // number of ? in selection must match number of elements in selectionArgs[]
                // equivalent to string "_id=?"
                selection = PetEntry._ID + "=?";

                // parseId extracts only the integer id from the content URI
                // equivalent to string "_id=#" where # is any integer
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // delete a single row given by the ID in the URI
                return database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

}
