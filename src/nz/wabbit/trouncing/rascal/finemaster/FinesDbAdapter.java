package nz.wabbit.trouncing.rascal.finemaster;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

public class FinesDbAdapter extends BaseDbAdapter {

    public static final String KEY_ROWID = "_id";
	public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_VALUE = "value";

    public static final String FINES_TABLE = "fines";
    
    public static final String TABLE_CREATE =
            "CREATE TABLE " + FINES_TABLE + " (" + KEY_ROWID + " integer primary key autoincrement, "
            + KEY_DESCRIPTION +" text not null, " + KEY_VALUE + " integer not null);";
    	
    public static final String TABLE_REMOVE = "DROP TABLE IF EXISTS " + FINES_TABLE;
    		
    public static final String TAG = "MembersDbAdapter";


	public FinesDbAdapter(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

    public long createFine(String description, int value) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_DESCRIPTION, description);
        initialValues.put(KEY_VALUE, value);

        return _Db.insert(FINES_TABLE, null, initialValues);
    }

    public boolean deleteFine(long rowId) {

        return _Db.delete(FINES_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public void list() {
    	Cursor c = fetchAllFines();
    	if (c.moveToFirst()) {
    		do {
    			Log.i("fines: ", c.getString(0) + ", " + c.getString(1) + ", " + c.getString(2));
    		} while (c.moveToNext());
    	}
    	c.close();
    }

    public Cursor fetchAllFines() {

        return _Db.query(FINES_TABLE, new String[] {KEY_ROWID, KEY_DESCRIPTION,
        		KEY_VALUE}, null, null, null, null, null);
    }

    public Cursor fetchFine(long rowId) throws SQLException {

        Cursor mCursor =
            _Db.query(true, FINES_TABLE, new String[] {KEY_ROWID,
            		KEY_DESCRIPTION, KEY_VALUE}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

   public boolean updateFine(long rowId, String description, int value) {
        ContentValues args = new ContentValues();
        args.put(KEY_DESCRIPTION, description);
        args.put(KEY_VALUE, value);

        return _Db.update(FINES_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

}
