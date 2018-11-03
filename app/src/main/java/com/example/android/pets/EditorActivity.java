package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetContract.PetEntry;

// user creates a new pet or edits an existing one
public class EditorActivity extends AppCompatActivity {

    // field to enter the pet's name
    private EditText mNameEditText;

    // field to enter the pet's breed
    private EditText mBreedEditText;

    // field to enter the pet's weight
    private EditText mWeightEditText;

    // field to enter the pet's gender
    private Spinner mGenderSpinner;

    // gender of the pet: 0 for unknown gender, 1 for male, 2 for female
    private int mGender = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // defer to super class constructor for initialization
        super.onCreate(savedInstanceState);

        // set layout to activity_editor
        setContentView(R.layout.activity_editor);

        // get the intent which started this activity, always from catalog activity
        Intent intent = getIntent();

        // extract the URI included with the intent
        Uri selectedPetURI = intent.getData();

        // if the URI exists then the activity is in "edit mode" for a single pet
        if (selectedPetURI != null) {

            // change action bar title to reflect "edit mode"
            setTitle(R.string.editor_activity_title_edit_pet);

        }

        // else the URI is null because the FAB button was pressed and activity is in "new pet mode"
        else {

            // change action bar title to reflect "new pet mode"
            setTitle(R.string.editor_activity_title_add_a_pet);

        }

        // get references to all relevant views for user input
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        // helper function that defines spinner (dropdown menu)
        setupSpinner();
    }

    // setup the dropdown spinner to allow user to select the pet gender
    private void setupSpinner() {

        // create adapter for spinner, options from string array, default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // specify dropdown layout style as simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // set adapter on the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // set mGender to the user selected constant value
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // default state must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // gender unknown
            }
        });
    }

    private void insertPet() {

        // get user selections
        String nameString = mNameEditText.getText().toString().trim();
        String breedString = mBreedEditText.getText().toString().trim();
        // gender is already an int, assigned in the spinner listener
        int weightInt = Integer.parseInt(mWeightEditText.getText().toString().trim());

        // container for key : value pairs
        ContentValues values = new ContentValues();

        // add key : value pairs for each feature
        values.put(PetEntry.COLUMN_PETS_NAME, nameString);
        values.put(PetEntry.COLUMN_PETS_BREED, breedString);
        values.put(PetEntry.COLUMN_PETS_GENDER, mGender);
        values.put(PetEntry.COLUMN_PETS_WEIGHT, weightInt);

        // perform an insert on the provider using a content resolver
        // the correct content URI is defined as a constant in PetContract
        Uri uri = getContentResolver().insert(PetEntry.CONTENT_URI, values);

        // toast message about status of row insert
        String toastMessage;
        if (uri == null) { // row insert failed and therefore returned insert uri is null
            toastMessage = getString(R.string.pet_saved_error);
        }
        else { // row insert successful
            toastMessage = getString(R.string.pet_saved);
        }

        // display toast
        Toast toast = Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT);
        toast.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // add options menu to app bar by inflating menu_editor
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    // click listener for items in the options menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // get id of selected item
        switch (item.getItemId()) {

            // menu option "Save"
            case R.id.action_save:

                // insert new pet into sqlite database
                insertPet();

                // exit activity and return to catalog activity
                finish();
                return true;

            // menu option "Delete"
            case R.id.action_delete:

                // TODO implement delete functionality
                return true;

            // this is the arrow up button in top left of app bar
            case android.R.id.home:

                // navigate back to parent catalog activity
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        // defer to super class for correct data type to return
        return super.onOptionsItemSelected(item);
    }

}