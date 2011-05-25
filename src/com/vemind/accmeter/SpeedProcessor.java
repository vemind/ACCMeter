package com.vemind.accmeter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class SpeedProcessor implements Parcelable {
	private static SpeedProcessor instance;
	
	public static final String NAME = "SpeedProcessor";
	public static final String DATABASE_NAME = "speed_processor";
	public static final int DATABASE_VERSION = 1;
	public static final String DATABASE_CREATE = "create table speeds (_id integer primary key autoincrement, "
        + "tstamp integer not null, value integer not null);";
	public static final String TAG = "SpeedProcessorAdapter";
	public static final String KEY_DATE = "tstamp";
	public static final String KEY_VALUE = "value";
	private static final String KEY_ROWID = "_id";
	private static final String DATABASE_TABLE = "speeds";
	
	private float currentSpeed;
	private boolean hasSpeed;
	private boolean loggingState;
	
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    private Context mCtx;
    
    private SpeedStatistic speedStat;
	private int mData;
	
	private SpeedProcessor() {
		currentSpeed = 0.0f;
		hasSpeed = false;
		loggingState = true;
		speedStat = new SpeedStatistic();
	}
	
	public static synchronized SpeedProcessor getInstance() {
		if (instance == null) {
			instance = new SpeedProcessor();
		}
		return instance;
	}
	
	public Float addSpeedValue(float newSpeed) {
		if (Math.abs(newSpeed - currentSpeed) < 50 || !hasSpeed) { //filter all the difference more than 50 m/s
			currentSpeed = newSpeed;
			if (loggingState) logData (currentSpeed);
		}
		hasSpeed = true;
		return currentSpeed;
	}
	
	public Float getSpeed(){ // return speed in km/h
		int speedCutter = (int) (currentSpeed * 36); // *10*3600/1000 - convert to kmh and 
		Float retSpeed = (float) speedCutter;
		return retSpeed/10;
	}
	
	public void logData(float speedValue) {
			int tempSpeed = (int) (speedValue * 10); // Speed values stored in DB in m/s * 10
			addLogValue (System.currentTimeMillis(), tempSpeed);
	}
	
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS speeds");
            onCreate(db);
        }
    }
    
    public SpeedProcessor open(Context ctx) throws SQLException {
    	mCtx = ctx;
    	if (mCtx != null) {
    		mDbHelper = new DatabaseHelper(mCtx);
            mDb = mDbHelper.getWritableDatabase();
            }
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }
    
    public long addLogValue (long date, int value) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_DATE, date);
        cv.put(KEY_VALUE, value);

        return mDb.insert(DATABASE_TABLE, null, cv);
    }
    
    public boolean changeLogValue (long rowId, long date, int value) {
    	 ContentValues cv = new ContentValues();
         cv.put(KEY_DATE, date);
         cv.put(KEY_VALUE, value);

         return mDb.update(DATABASE_TABLE, cv, KEY_ROWID + "=" + rowId, null) > 0;	
    }
    
    public boolean deleteLogValue (long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;    	
    }
    
    public Cursor getLogValue (long rowId) throws SQLException {
        Cursor mCursor =
            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_DATE, KEY_VALUE}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public Cursor getAllLogs () {
        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_DATE,
                KEY_VALUE}, null, null, null, null, null);
    }
    
    public boolean clearAllLogs () {
    	return mDb.delete(DATABASE_TABLE, null, null) > 0;
    }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mData);
		
	}
    // this is used to regenerate object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<SpeedProcessor> CREATOR = new Parcelable.Creator<SpeedProcessor>() {
        public SpeedProcessor createFromParcel(Parcel in) {
            return new SpeedProcessor(in);
        }

        public SpeedProcessor[] newArray(int size) {
            return new SpeedProcessor[size];
        }
    };
		
	private SpeedProcessor (Parcel in) {
        mData = in.readInt();
        speedStat = new SpeedStatistic();
    }
	
	public SpeedStatistic getStats () { 
		speedStat.recalculate(getAllLogs());
		return speedStat;
	}
	
	public void saveLogs (boolean saveLog) {
		loggingState = saveLog;
	}
	
	public boolean isLogging () {
		return loggingState;
	}
	
	public void setStatus (boolean newState) {
		hasSpeed = newState;
	}
	
	public boolean getStatus() {
		return hasSpeed;
	}
}
