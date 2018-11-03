package com.example.android.pets;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.pets.data.PetContract.PetEntry;

// displays list of pets that were entered and stored in the app
public class CatalogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // explicit intent to open editor activity
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        displayDatabaseInfo();

    }

    // called when returning to this activity from editor activity
    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    // temporary helper method to display information in the onscreen TextView about the state of the pets database
    private void displayDatabaseInfo() {

        // define projection (column names) for query
        String[] projection = {PetEntry._ID, PetEntry.COLUMN_PETS_NAME,
                PetEntry.COLUMN_PETS_BREED, PetEntry.COLUMN_PETS_GENDER, PetEntry.COLUMN_PETS_WEIGHT};

        // perform a query on the provider using a content resolver
        // the correct content URI is defined as a constant in PetContract
        Cursor cursor = getContentResolver().query(PetEntry.CONTENT_URI, projection, null, null, null);

        // get reference to list view in activity_catalog
        ListView listView = (ListView) findViewById(R.id.list_view);

        // create new cursor adapter and set it on the list view
        PetCursorAdapter adapter = new PetCursorAdapter(this, cursor);
        listView.setAdapter(adapter);

//        // try-finally block ensures that the cursor is always closed
//        try {
//
//            // display the number of rows in the Cursor (= number of rows in pets table)
//            displayView.setText("The pets table contains " + cursor.getCount() + " pets.\n\n");
//            displayView.append(PetEntry._ID + " - " + PetEntry.COLUMN_PETS_NAME +
//                    " - " + PetEntry.COLUMN_PETS_BREED + " - " + PetEntry.COLUMN_PETS_GENDER +
//                    " - " + PetEntry.COLUMN_PETS_WEIGHT + "\n");
//
//            // get index position (int) for each column
//            int idColumnIndex = cursor.getColumnIndex(PetEntry._ID);
//            int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PETS_NAME);
//            int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PETS_BREED);
//            int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PETS_GENDER);
//            int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PETS_WEIGHT);
//
//            // iterate through all rows, Cursor starts at row -1 (column titles)
//            // therefore, first moveToNext() puts Cursor at row 0
//            while (cursor.moveToNext()) {
//
//                // use indices to extract table values
//                int currentID = cursor.getInt(idColumnIndex);
//                String currentName = cursor.getString(nameColumnIndex);
//                String currentBreed = cursor.getString(breedColumnIndex);
//                int currentGender = cursor.getInt(genderColumnIndex);
//                int currentWeight = cursor.getInt(weightColumnIndex);
//
//                displayView.append("\n" + currentID + " - " + currentName + " - " + currentBreed
//                    + " - " + currentGender + " - " + currentWeight);
//
//            }
//
//        // always close Cursor to prevent memory leaks
//        } finally {
//            cursor.close();
//        }
    }

    // insert dummy data for a single pet (new row in sqlite table)
    private void insertPet() {

        // container for key : value pairs
        ContentValues values = new ContentValues();

        // add key : value pairs for each feature
        values.put(PetEntry.COLUMN_PETS_NAME, "Toto");
        values.put(PetEntry.COLUMN_PETS_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PETS_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PETS_WEIGHT, 7);

        // perform an insert on the provider using a content resolver
        // the correct content URI is defined as a constant in PetContract
        Uri uri = getContentResolver().insert(PetEntry.CONTENT_URI, values);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:

                insertPet();
                displayDatabaseInfo();
                return true;

            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:

                // Do nothing for now
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
