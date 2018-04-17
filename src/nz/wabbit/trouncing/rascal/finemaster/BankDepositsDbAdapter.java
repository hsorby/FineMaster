package nz.wabbit.trouncing.rascal.finemaster;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

public class BankDepositsDbAdapter extends BaseDbAdapter {

	public static final String KEY_DATE = "date";
    public static final String KEY_VALUE = "value";
    public static final String KEY_ROWID = "_id";

    public static final String BANK_DEPOSITS_TABLE = "bankdeposits";
    
    public static final String TABLE_CREATE =
            "CREATE TABLE " + BANK_DEPOSITS_TABLE + " (" + KEY_ROWID + " integer primary key autoincrement, "
            + KEY_VALUE + " integer not null, " + KEY_DATE +" text not null);";
    	
    public static final String TABLE_REMOVE = "DROP TABLE IF EXISTS " + BANK_DEPOSITS_TABLE;
    		
    public static final String TAG = "BankDepositsDbAdapter";

	public BankDepositsDbAdapter(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

	public long createBankDeposit(int value, long member_id, String date) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_VALUE, value);
        initialValues.put(KEY_DATE, date);

//        Log.i(TAG, " inserting entry in table: " + value);
        return _Db.insert(BANK_DEPOSITS_TABLE, null, initialValues);
    }

    public boolean deleteBankDeposit(long rowId) {

        return _Db.delete(BANK_DEPOSITS_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllBankDeposits() {

        return _Db.query(BANK_DEPOSITS_TABLE, new String[] {KEY_ROWID, KEY_VALUE, 
        		KEY_DATE}, null, null, null, null, null);
    }

    public Cursor fetchBankDeposit(long rowId) throws SQLException {

        Cursor cursor =
            _Db.query(true, BANK_DEPOSITS_TABLE, new String[] {KEY_ROWID,
            		KEY_VALUE, KEY_DATE}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;

    }

    public void list() {
    	Cursor c = fetchAllBankDeposits();
//		SimpleDateFormat format_in = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
//		SimpleDateFormat format_out = new SimpleDateFormat(BaseDbAdapter.date_format_out);
    	if (c.moveToFirst()) {
    		do {
    			Log.i("bank deposits: ", c.getString(0) + ", " + c.getString(1) + ", " + c.getString(2));
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

   public boolean updateBankDeposit(long rowId, int value, long member_id, String date) {
        ContentValues args = new ContentValues();
        args.put(KEY_VALUE, value);
        args.put(KEY_DATE, date);

        return _Db.update(BANK_DEPOSITS_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

}
