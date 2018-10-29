package com.example.android.pets.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

// "final" modifier because class only contains constants
public final class PetContract {

    // empty constructor as no objects of this class will ever be created
    private PetContract() {}

    // content authority is the name for the entire content provider
    // a convenient string is the package name as it is guaranteed unique on the device
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";

    // portion of the content URI that is common to all tables
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // possible content URI endpoint, appended to common base URI in PetEntry
    public static final String PATH_PETS = "pets";

    public static final class PetEntry implements BaseColumns {

        // table
        public static final String TABLE_NAME = "pets";

        // columns
        public static final String _ID = BaseColumns._ID; // _ID is inherent to Android framework
        public static final String COLUMN_PETS_NAME = "name";
        public static final String COLUMN_PETS_BREED = "breed";
        public static final String COLUMN_PETS_GENDER = "gender";
        public static final String COLUMN_PETS_WEIGHT = "weight";

        // genders
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;

        // returns true if the gender is either 0, 1, or 2
        public static boolean isValidGender(int gender) {
            if (gender == GENDER_UNKNOWN || gender == GENDER_MALE || gender == GENDER_FEMALE) {
                return true;
            }
            return false;
        }

        // content URI for the pets table
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);

        // MIME type for a list of pets
        // equivalent to "vnd.android.cursor.dir/com.example.android.pets/pets"
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

        // MIME type for a single pet
        // equivalent to "vnd.android.cursor.item/com.example.android.pets/pets"
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + CONTENT_AUTHORITY + "/" + PATH_PETS;

    }

}
