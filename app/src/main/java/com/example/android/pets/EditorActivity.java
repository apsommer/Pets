package com.example.android.pets;

import android.content.ContentValues;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        setupSpinner();
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
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
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {

            // Respond to a click on the "Save" menu option
            case R.id.action_save:

                // insert new pet into sqlite database
                insertPet();

                // exit activity and return to catalog activity
                finish();
                return true;

            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:

                // Do nothing for now
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:

                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}