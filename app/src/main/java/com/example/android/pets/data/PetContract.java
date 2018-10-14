package com.example.android.pets.data;

import android.provider.BaseColumns;

// "final" modifier because class only contains constants
public final class PetContract {

    public static final class PetEntry implements BaseColumns {

        // table name
        public static final String TABLE_NAME = "pets";

        // column names
        public static final String _ID = BaseColumns._ID; // _ID is inherent to Android framework
        public static final String COLUMN_PETS_NAME = "name";
        public static final String COLUMN_PETS_BREED = "breed";
        public static final String COLUMN_PETS_GENDER = "gender";
        public static final String COLUMN_PETS_WEIGHT = "weight";


        // gender names
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;

    }

}
