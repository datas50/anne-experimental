package com.team980.thunderscout.signup_form.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.team980.thunderscout.signup_form.data.ServerDataContract.StudentDataTable;

public class ServerDataDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "ThunderScout_2016_CHAMPIONSHIPS.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String FLOAT_TYPE = " REAL";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String BLOB_TYPE = " BLOB";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + StudentDataTable.TABLE_NAME + " (" +
                    StudentDataTable._ID + " INTEGER PRIMARY KEY," +
                    StudentDataTable.COLUMN_NAME_STUDENT_NAME + TEXT_TYPE + COMMA_SEP +
                    StudentDataTable.COLUMN_NAME_STUDENT_EMAIL + TEXT_TYPE + COMMA_SEP +
                    StudentDataTable.COLUMN_NAME_STUDENT_PHONE_NUMBER + TEXT_TYPE + ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + StudentDataTable.TABLE_NAME;

    public ServerDataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for event data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
