package com.example.android.pets;

// framework packages
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.Locale;

// native packages
import com.example.android.pets.data.PetContract.PetEntry;

// user creates a new pet or edits an existing one
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // field to enter the pet's name
    private EditText mNameEditText;

    // field to enter the pet's breed
    private EditText mBreedEditText;

    // field to enter the pet's weight
    private EditText mWeightEditText;

    // field to enter the pet's gender
    private Spinner mGenderSpinner;

    // integer ID of cursor loader
    private static final int PET_LOADER = 0;

    // gender of the pet: 0 for unknown gender, 1 for male, 2 for female
    private int mGender = PetEntry.GENDER_UNKNOWN;

    // content URI for the selected existing pet, null if new pet
    private Uri mSelectedPetURI;

    // flag for unsaved user changes when navigating away from activity
    private boolean mPetHasChanged;

    // touch listener is set on a view, a touch implies the field has changed
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPetHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // defer to super class constructor for initialization
        super.onCreate(savedInstanceState);

        // set layout to activity_editor
        setContentView(R.layout.activity_editor);

        // get the intent which started this activity, always from catalog activity
        Intent intent = getIntent();

        // extract the URI included with the intent
        mSelectedPetURI = intent.getData();

        // if the URI is null, the FAB button was pressed and the activity is in "insert mode"
        if (mSelectedPetURI == null) {

            // update app bar title
            setTitle(R.string.editor_activity_title_add_a_pet);

            // since a pet is being added, the option to delete should be hidden from the menu
            // this invalidation causes the system to call onPrepareOptionsMenu
            invalidateOptionsMenu();

        // else the URI exists, and the activity is in "edit mode" for an existing single pet
        } else {

            // update app bar title
            setTitle(R.string.editor_activity_title_edit_pet);

            // initialize loader
            getLoaderManager().initLoader(PET_LOADER, null, this);
        }

        // get references to all relevant views for user input
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        // set a listener on each user input field
        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);

        // helper function that defines spinner (dropdown menu)
        setupSpinner();

    }

    // called by the system when in "insert pet" mode to hide the delete option from the overflow menu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        // defer to super class for initialization
        super.onPrepareOptionsMenu(menu);

        // hide the option to delete
        if (mSelectedPetURI == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }

        return true;
    }

    // create and show the "unsaved changes" dialog box
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        // alert dialog builder constructs the attributes of the message box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // primary message title
        builder.setMessage(R.string.unsaved_changes_dialog_msg);

        // positive button means to ignore the unsaved changes and continue with navigation
        builder.setPositiveButton(R.string.discard_changes, discardButtonClickListener);

        // negative button means cancel the navigation attempt and stay in the editor activity
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {

            // return back to editor activity
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // create and show the constructed dialog box
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    // create and show the "delete confirmation" dialog box
    private void showDeleteConfirmationDialog() {

        // alert dialog builder constructs the attributes of the message box
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // primary message title
        builder.setMessage(R.string.delete_dialog_msg_single_pet);

        // positive button is a confirmation to delete the pet from the database
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {

            // call helper method to perform the delete
            @Override
            public void onClick(DialogInterface dialog, int i) {
                deletePet();
            }
        });

        // negative button means cancel the navigation attempt and stay in the editor activity
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            // return back to editor activity
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // create and show the constructed dialog box
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // helper method called when delete button is pressed in the delete confirmation dialog
    private void deletePet() {

        // if the URI exists, then the activity is in "edit mode" for an existing single pet
        // if the URI is null, the FAB button was pressed and the activity is in "insert mode"
        if (mSelectedPetURI != null) {

            // perform a delete on the provider using a content resolver
            int deletedRows = getContentResolver().delete(mSelectedPetURI, null, null);

            // toast to display success (or failure) of delete action
            String toastMessage;

            // row delete failed and therefore the number of deleted rows is zero
            if (deletedRows == 0) {
                toastMessage = getString(R.string.editor_delete_pet_failed);

            // row delete successful
            } else {
                toastMessage = getString(R.string.editor_delete_pet_successful);
            }

            // display toast message
            Toast toast = Toast.makeText(getApplicationContext(), toastMessage, Toast.LENGTH_SHORT);
            toast.show();

        }

        // exit activity and return to catalog activity
        finish();

    }

    // if the user presses the back button with unsaved changes a warning dialog box appears
    @Override
    public void onBackPressed() {

        // if the user has not entered anything then proceed with normal back button behavior
        if (!mPetHasChanged) {
            super.onBackPressed();
        }

        // define a click listener for the "discard changes" button
        // this ends the editor activity
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                finish();
            }
        };

        // pass the discard button click listener to the alert dialog helper method
        showUnsavedChangesDialog(discardButtonClickListener);

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
                mGender = PetEntry.GENDER_UNKNOWN; // gender unknown
            }
        });
    }

    private void savePet() {

        // get raw state of user fields as strings
        String nameString = mNameEditText.getText().toString().trim();
        String breedString = mBreedEditText.getText().toString().trim();
        // mGender is assigned with the spinner selection, starting at default PetEntry.GENDER_UNKNOWN
        String weightString = mWeightEditText.getText().toString().trim();

        // if all fields are blank assume the user made a mistake and exit without saving
        if (TextUtils.isEmpty(nameString) && TextUtils.isEmpty(breedString) &&
                mGender == PetEntry.GENDER_UNKNOWN && TextUtils.isEmpty(weightString)) {
            return;
        }

        // if the user leaves the weight field blank use a default value of 0
        int weightInt = 0;

        // under normal conditions the user specifies a weight value
        if (!TextUtils.isEmpty(weightString)) {
            weightInt = Integer.parseInt(weightString);
        }

        // container for key : value pairs
        ContentValues values = new ContentValues();

        // add key : value pairs for each feature
        values.put(PetEntry.COLUMN_PETS_NAME, nameString);
        values.put(PetEntry.COLUMN_PETS_BREED, breedString);
        values.put(PetEntry.COLUMN_PETS_GENDER, mGender);
        values.put(PetEntry.COLUMN_PETS_WEIGHT, weightInt);

        // toast to display success (or failure) of save action
        String toastMessage;

        // if the URI is null, the FAB button was pressed and the activity is in "insert mode"
        if (mSelectedPetURI == null) {

            // perform an insert on the provider using a content resolver
            Uri newPetURI = getContentResolver().insert(PetEntry.CONTENT_URI, values);

            // row insert failed and therefore returned insert uri is null
            if (newPetURI == null) {
                toastMessage = getString(R.string.pet_saved_error);

            // row insert successful
            } else {
                toastMessage = getString(R.string.pet_saved);
            }

        // if the URI exists, then the activity is in "edit mode" for an existing single pet
        } else {

            // perform an insert on the provider using a content resolver
            int updatedRow = getContentResolver().update(mSelectedPetURI, values, null, null);

            // row insert failed and therefore the number of affected rows is zero
            if (updatedRow == 0) {
                toastMessage = getString(R.string.pet_saved_error);

            // row insert successful
            } else {
                toastMessage = getString(R.string.pet_saved);
            }

        }

        // display toast message
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

                // save existing pet data in sqlite database
                savePet();

                // exit activity and return to catalog activity
                finish();
                return true;

            // menu option "Delete"
            case R.id.action_delete:

                // ask the user for confirmation using a dialog box
                showDeleteConfirmationDialog();
                return true;

            // this is the up arrow button in top left of app bar
            // the first word in the signature android. means this is a framework component
            case android.R.id.home:

                // if the user has not entered anything then proceed with normal up button behavior
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                // define a click listener for the "discard changes" button
                // this ends the editor activity
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };

                // pass the discard button click listener to the alert dialog helper method
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

        }

        // defer to super class for correct data type to return
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        // define projection (column names) for query
        String[] projection = {PetEntry._ID, PetEntry.COLUMN_PETS_NAME, PetEntry.COLUMN_PETS_BREED, PetEntry.COLUMN_PETS_GENDER, PetEntry.COLUMN_PETS_WEIGHT};

        // CursorLoader requires that the column projection includes the _ID column
        return new CursorLoader(this, mSelectedPetURI, projection, null, null, null);

    }

    // the cursor is only a single row representing a single pet
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // exit method if finished cursor is null or empty
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // move cursor to the only row which is first position
        if (cursor.moveToFirst()) {

            // get column indices
            int nameIndex = cursor.getColumnIndex(PetEntry.COLUMN_PETS_NAME);
            int breedIndex = cursor.getColumnIndex(PetEntry.COLUMN_PETS_BREED);
            int weightIndex = cursor.getColumnIndex(PetEntry.COLUMN_PETS_WEIGHT);
            int genderIndex = cursor.getColumnIndex(PetEntry.COLUMN_PETS_GENDER);

            // get each value at each column index
            String name = cursor.getString(nameIndex);
            String breed = cursor.getString(breedIndex);
            int weight = cursor.getInt(weightIndex);
            int gender = cursor.getInt(genderIndex);

            // set the proper values in each user input field
            mNameEditText.setText(name);
            mBreedEditText.setText(breed);
            mWeightEditText.setText(String.format(Locale.getDefault(), "%d", weight));
            mGenderSpinner.setSelection(gender);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        // clear all selections
        mNameEditText.getText().clear();
        mBreedEditText.getText().clear();
        mWeightEditText.getText().clear();
        mGenderSpinner.setAdapter(null);

    }

}