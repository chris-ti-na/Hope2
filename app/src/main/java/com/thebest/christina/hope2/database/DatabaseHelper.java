package com.thebest.christina.hope2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SignalMap.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";
    private static final String BLOB_TYPE = " BLOB";
    private static final String COMMA_SEP = ",";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + SignalTable.TABLE_NAME + " (" +
//                SignalTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                SignalTable.COLUMN_LATITUDE + REAL_TYPE + COMMA_SEP +
                SignalTable.COLUMN_LONGITUDE + REAL_TYPE + COMMA_SEP +
                SignalTable.COLUMN_TIME + INTEGER_TYPE + COMMA_SEP +
                SignalTable.COLUMN_SIGNAL_STRENGTH + INTEGER_TYPE + COMMA_SEP +
                SignalTable.COLUMN_CELL_INFO + TEXT_TYPE + COMMA_SEP +
                SignalTable.COLUMN_NETWORK_PROVIDER + TEXT_TYPE +
                " )");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES + SignalTable.TABLE_NAME);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static abstract class SignalTable implements BaseColumns {
        public static final String TABLE_NAME = "signalMap";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_SIGNAL_STRENGTH = "signalStrength";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_CELL_INFO = "cellInfo";
        public static final String COLUMN_NETWORK_PROVIDER = "networkProvider";
    }
}