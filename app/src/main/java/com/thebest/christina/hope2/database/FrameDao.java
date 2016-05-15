package com.thebest.christina.hope2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.thebest.christina.hope2.model.Frame;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by christina on 15.03.16.
 */
public class FrameDao {
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    public FrameDao(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        db.close();
    }

    /**
     * Create new Frame object
     * @param frame
     */
    public void createFrame(Frame frame) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.SignalTable.COLUMN_LATITUDE, frame._latitude);
        contentValues.put(DatabaseHelper.SignalTable.COLUMN_LONGITUDE, frame._longitude);
        contentValues.put(DatabaseHelper.SignalTable.COLUMN_TIME, frame._time);
        contentValues.put(DatabaseHelper.SignalTable.COLUMN_ASU_SIGNAL_STRENGTH, frame._asuSignalStrength);
        contentValues.put(DatabaseHelper.SignalTable.COLUMN_BAR_SIGNAL_STRENGTH, frame._barSignalStrength);
        contentValues.put(DatabaseHelper.SignalTable.COLUMN_DBM_SIGNAL_STRENGTH, frame._dbmSignalStrength);
        contentValues.put(DatabaseHelper.SignalTable.COLUMN_CELL_INFO, frame._cellInfo);
        contentValues.put(DatabaseHelper.SignalTable.COLUMN_NETWORK_PROVIDER, frame._networkProvider);
        // Insert into DB
        db.insert(DatabaseHelper.SignalTable.TABLE_NAME, null, contentValues);
    }

    /**
     * Delete all Frame objects
     */
    public void deleteAllFrames() {
        // Delete from DB where id match
        db.delete(DatabaseHelper.SignalTable.TABLE_NAME, null, null);
    }

    /**
     * Get all Frames
     * @return List<Frame>
     */
    public List<Frame> getAllFrames() {
        List<Frame> frames = new ArrayList<>();

        String[] tableColumns = new String[] {
                DatabaseHelper.SignalTable.COLUMN_LATITUDE,
                DatabaseHelper.SignalTable.COLUMN_LONGITUDE,
                DatabaseHelper.SignalTable.COLUMN_TIME,
                DatabaseHelper.SignalTable.COLUMN_ASU_SIGNAL_STRENGTH,
                DatabaseHelper.SignalTable.COLUMN_BAR_SIGNAL_STRENGTH,
                DatabaseHelper.SignalTable.COLUMN_DBM_SIGNAL_STRENGTH,
                DatabaseHelper.SignalTable.COLUMN_CELL_INFO,
                DatabaseHelper.SignalTable.COLUMN_NETWORK_PROVIDER};

        Cursor cursor = db.query(DatabaseHelper.SignalTable.TABLE_NAME, tableColumns, null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            frames.add(new Frame(cursor.getDouble(0), cursor.getDouble(1), cursor.getLong(2), cursor.getInt(3), cursor.getInt(4), cursor.getInt(5), cursor.getString(6), cursor.getString(7)));
            cursor.moveToNext();
        }

        return frames;
    }
}
