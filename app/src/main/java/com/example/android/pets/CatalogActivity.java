package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.pets.data.PetContract.PetEntry;

// displays list of pets that were entered and stored in the app
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // integer ID of cursor loader
    private static final int PET_LOADER = 0;

    // reference to cursor adapter that populates list view in activity_catalog
    PetCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // defer to superclass constructor for initialization
        super.onCreate(savedInstanceState);

        // set the view as activity_catalog
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

        getLoaderManager().initLoader(PET_LOADER, null, this);

        // get reference to list view in activity_catalog
        ListView listView = (ListView) findViewById(R.id.list_view);

        // get view reference for empty state and set it on the list view
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        // create new cursor adapter and set it on the list view
        // the cursor input parameter is null because a loader supplies cursors to the adapter
        mAdapter = new PetCursorAdapter(this, null);
        listView.setAdapter(mAdapter);

        // setup click listener for items in the list view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            // clicking an item in the list view opens the editor activity in "edit mode" for that pet
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // explicit intent to open editor activity
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                // add content URI for the selected pet to the intent
                Uri selectedPetURI = ContentUris.withAppendedId(PetEntry.CONTENT_URI, id);
                intent.setData(selectedPetURI);

                // start editor activity in "edit mode"
                startActivity(intent);

            }

        });


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
                return true;

            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:

                // Do nothing for now
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // define projection (column names) for query
        String[] projection = {PetEntry._ID, PetEntry.COLUMN_PETS_NAME, PetEntry.COLUMN_PETS_BREED};

        // CursorLoader requires that the column projection includes the _ID column
        return new CursorLoader(this, PetEntry.CONTENT_URI, projection, null, null, null);

    }

    // called by system when a new cursor si finished being created by the loader
    // the adapter must now refresh with this new cursor
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    // called when a previously created loader is being reset
    // therefore its data is no longer valid and the cursor currently in the adapter is removed
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
