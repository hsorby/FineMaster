package nz.wabbit.trouncing.rascal.finemaster;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

public class DepositsDbAdapter extends BaseDbAdapter {

	public static final String KEY_DATE = "date";
    public static final String KEY_VALUE = "value";
    public static final String KEY_MEMBER_ID = "member_id";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_TOTAL = "total";

    public static final String DEPOSITS_TABLE = "deposits";
    public static final String DEPOSITSUMMARY_VIEW = "depositsummary";
    
    public static final String TABLE_CREATE =
            "CREATE TABLE " + DEPOSITS_TABLE + " (" + KEY_ROWID + " integer primary key autoincrement, "
            + KEY_VALUE + " integer not null, " + KEY_MEMBER_ID + " integer not null, " + KEY_DATE +" text not null);";
    public static final String VIEW_CREATE = 
    		"CREATE VIEW " + DEPOSITSUMMARY_VIEW + " AS SELECT " + DEPOSITS_TABLE + "." + KEY_MEMBER_ID + " AS " + KEY_ROWID + ", TOTAL(" + DEPOSITS_TABLE + "." + KEY_VALUE + ") AS " + KEY_TOTAL + 
    		" FROM " + DEPOSITS_TABLE + " GROUP BY " + DEPOSITS_TABLE + "." + KEY_MEMBER_ID;
    	
    public static final String TABLE_REMOVE = "DROP TABLE IF EXISTS " + DEPOSITS_TABLE;
    public static final String VIEW_REMOVE = "DROP VIEW IF EXISTS " + DEPOSITSUMMARY_VIEW;
    		
    public static final String TAG = "DepositsDbAdapter";

	public DepositsDbAdapter(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

	public long createDeposit(int value, long member_id, String date) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_VALUE, value);
        initialValues.put(KEY_MEMBER_ID, member_id);
        initialValues.put(KEY_DATE, date);

//        Log.i(TAG, " inserting entry in table: " + value);
        return _Db.insert(DEPOSITS_TABLE, null, initialValues);
    }

    public boolean deleteDeposit(long rowId) {

        return _Db.delete(DEPOSITS_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllDeposits() {

        return _Db.query(DEPOSITS_TABLE, new String[] {KEY_ROWID, KEY_VALUE, 
        		KEY_MEMBER_ID, KEY_DATE}, null, null, null, null, null);
    }

    public Cursor fetchDeposit(long rowId) throws SQLException {

        Cursor cursor =
            _Db.query(true, DEPOSITS_TABLE, new String[] {KEY_ROWID,
            		KEY_VALUE, KEY_MEMBER_ID, KEY_DATE}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;

    }

    public void list() {
    	Cursor c = fetchAllDeposits();
//		SimpleDateFormat format_in = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
//		SimpleDateFormat format_out = new SimpleDateFormat(BaseDbAdapter.date_format_out);
    	if (c.moveToFirst()) {
    		do {
    			Log.i("deposits: ", c.getString(0) + ", " + c.getString(1) + ", " + c.getString(2) + ", " + c.getString(3));
//    			Date date = null;
//				try {
//					date = format_in.parse(c.getString(3));
//				} catch (ParseException e) {
////					e.printStackTrace();
//				}
//				if (date != null) {
//					String date_str = format_out.format(date);
//	    			Log.i("deposits new: ", c.getString(0) + ", " + c.getString(1) + ", " + c.getString(2) + ", " + date_str);
//	    			updateDeposit(Integer.parseInt(c.getString(0)), Integer.parseInt(c.getString(1)), Integer.parseInt(c.getString(2)), date_str);
//				}
    		} while (c.moveToNext());
    	}
    	c.close();
    }

   public boolean updateDeposit(long rowId, int value, long member_id, String date) {
        ContentValues args = new ContentValues();
        args.put(KEY_VALUE, value);
        args.put(KEY_MEMBER_ID, member_id);
        args.put(KEY_DATE, date);

        return _Db.update(DEPOSITS_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

	public Cursor fetchDepositsForMember(Long memberId) {
	    Cursor cursor =
	            _Db.query(true, DEPOSITS_TABLE, new String[] {KEY_ROWID, KEY_VALUE,
	            		KEY_MEMBER_ID, KEY_DATE}, KEY_MEMBER_ID + "=" + memberId, null,
	                    null, null, null, null);
	    return cursor;
	}
}
