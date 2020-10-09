package doitgames.soundrecorder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;
import android.util.Log;

public class AppDatabase extends SQLiteOpenHelper {
    private static final String TAG = "AppDatabase";

    public interface OnDatabaseChangedListener{
        void onDatabaseEntryAdded();
        void onDatabaseEntryRenamed();
    }
    private OnDatabaseChangedListener onDatabaseChangedListener = null;

    public void setOnDatabaseChangedListener(OnDatabaseChangedListener listener){
        onDatabaseChangedListener = listener;
    }



    public static final String DATABASE_NAME = "Recordings.db";
    public static final int DATABASE_VERSION = 1;

    private static AppDatabase instance = null;

    private AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static AppDatabase getInstance(Context context){
        if(instance == null){
            synchronized (AppDatabase.class){
                if(instance == null){
                    instance = new AppDatabase(context);
                }
            }
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // CREATE TABLE table_name (_id INTEGER PRIMARY KEY, recording_name TEXT, file_path TEXT, length INTEGER, time_added INTEGER)
        String sql = "CREATE TABLE " + RecordingContract.TABLE_NAME + " ("
                + RecordingContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + RecordingContract.Columns.RECORDING_NAME + " TEXT, "
                + RecordingContract.Columns.RECORDING_FILE_PATH + " TEXT, "
                + RecordingContract.Columns.RECORDING_LENGTH + " INTEGER, "
                + RecordingContract.Columns.RECORDING_TIME_ADDED + " INTEGER)";
        sqLiteDatabase.execSQL(sql);
        Log.d(TAG, "onCreate: Database Created! sql = " + sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Hmm?
    }

    public RecordItem getItemAt(int position){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {RecordingContract.Columns._ID,
                RecordingContract.Columns.RECORDING_NAME,
                RecordingContract.Columns.RECORDING_FILE_PATH,
                RecordingContract.Columns.RECORDING_LENGTH,
                RecordingContract.Columns.RECORDING_TIME_ADDED
        };
        Cursor c = db.query(RecordingContract.TABLE_NAME, projection, null, null, null, null, null);
        if(c.moveToPosition(position)){
            RecordItem item = new RecordItem();
            item.setmId(c.getInt(c.getColumnIndex(RecordingContract.Columns._ID)));
            item.setmName(c.getString(c.getColumnIndex(RecordingContract.Columns.RECORDING_NAME)));
            item.setmFilePath(c.getString(c.getColumnIndex(RecordingContract.Columns.RECORDING_FILE_PATH)));
            item.setmLength(c.getInt(c.getColumnIndex(RecordingContract.Columns.RECORDING_LENGTH)));
            item.setmTime(c.getLong(c.getColumnIndex(RecordingContract.Columns.RECORDING_TIME_ADDED)));
            c.close();
            return item;
        }
        return null;
    }

    public boolean removeItemWithId(int itemId){
        SQLiteDatabase db = getWritableDatabase();
        String[] whereArgs = {String.valueOf(itemId)};
        if(db.delete(RecordingContract.TABLE_NAME, "_ID = ?", whereArgs) > 0){
            return true;
        }
        return false;
    }

    public int getCount(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {RecordingContract.Columns._ID};
        Cursor c = db.query(RecordingContract.TABLE_NAME, projection, null, null, null, null, null);
        int count = c.getCount();
        c.close();
        return count;
    }

    public long addRecording(String recordingName, String filePath, long length){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(RecordingContract.Columns.RECORDING_NAME, recordingName);
        cv.put(RecordingContract.Columns.RECORDING_FILE_PATH, filePath);
        cv.put(RecordingContract.Columns.RECORDING_LENGTH, length);
        cv.put(RecordingContract.Columns.RECORDING_TIME_ADDED, System.currentTimeMillis());
        long rowId = db.insert(RecordingContract.TABLE_NAME, null, cv);
        if(rowId == -1){
            throw new SQLException("Error inserting new recording!");
        }

        if(onDatabaseChangedListener != null){
            onDatabaseChangedListener.onDatabaseEntryAdded();
        }

        return rowId;
    }

    public boolean renameItem(RecordItem item, String recordingName, String filePath){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(RecordingContract.Columns.RECORDING_NAME, recordingName);
        cv.put(RecordingContract.Columns.RECORDING_FILE_PATH, filePath);
        db.update(RecordingContract.TABLE_NAME, cv, RecordingContract.Columns._ID + " = " + item.getmId(), null);

        if(onDatabaseChangedListener != null){
            onDatabaseChangedListener.onDatabaseEntryRenamed();
        }

        return false;
    }

//    public long restoreRecording(){
//
//    }


}
