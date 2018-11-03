package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.PetContract.PetEntry;

// adapter for ListView or GridView that uses a Cursor from an sqlite database as its source.
public class PetCursorAdapter extends CursorAdapter {

    // defer to super class constructor for initialization
    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    // returns a new blank View from list_item
    // the Cursor is already on the correct row position
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    // sets the Cursor row data on the inflated blank view from newView method
    // the Cursor is already in the correct row position
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // get references to view entities
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView breedTextView = (TextView) view.findViewById(R.id.breed);

        // get index position for each column
        int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PETS_NAME);
        int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PETS_BREED);

        // extract data from cursor
        String nameString = cursor.getString(nameColumnIndex);
        String breedString = cursor.getString(breedColumnIndex);

        // set cursor data on views
        nameTextView.setText(nameString);
        breedTextView.setText(breedString);

    }
}
