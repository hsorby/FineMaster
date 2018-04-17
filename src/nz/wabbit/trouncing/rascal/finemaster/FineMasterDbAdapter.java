package nz.wabbit.trouncing.rascal.finemaster;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
//import android.util.Log;

public class FineMasterDbAdapter extends BaseDbAdapter {

	public static final String KEY_BALANCE = "balance";
	private static final String _ww = "SELECT members._id, members.first_name, members.last_name, "
			  + "IFNULL((SELECT finesummary.total FROM finesummary WHERE members._id = finesummary._id ORDER BY finesummary._id), '0') - " 
			  + "IFNULL((SELECT depositsummary.total FROM depositsummary WHERE members._id = depositsummary._id ORDER BY depositsummary._id), '0') AS " + KEY_BALANCE
			  + " FROM members;";
	private static final String _zz1 = "SELECT members._id, IFNULL((SELECT finesummary.total FROM finesummary WHERE ";
	private static final String _zz2 = " = finesummary._id), '0') AS FINE_TOTAL FROM members WHERE members._id = ";
	private static final String _yy1 = "SELECT members._id, IFNULL((SELECT depositsummary.total FROM depositsummary WHERE ";
	private static final String _yy2 = " = depositsummary._id), '0') AS DEPOSIT_TOTAL FROM members WHERE members._id = ";
//	private static final String rr = "SELECT members._id, members.first_name, IFNULL(members.last_name, 'x') AS last_name, "
//			  + "'$ ' || ROUND(IFNULL((SELECT finesummary.total FROM finesummary WHERE members._id = finesummary._id ORDER BY finesummary._id), '0')) AS " + KEY_BALANCE
//			  + " FROM members;";
	private static final String KEY_COUNT = "count";
	
    public FineMasterDbAdapter(Context ctx) {
		super(ctx);
	}

//	private static final String TAG = "FineMasterDbAdapter";

    /**
     * Open the notes database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
 	public Cursor fetchSummaryListing() {
// 		String mySql = " SELECT name FROM sqlite_master " + " WHERE type='view';";
// 		String mySql3 = " SELECT * FROM depositsummary";
// 		String mySql2 = " SELECT * FROM finesummary;";
//    	Cursor w = _Db.rawQuery(mySql2, null);
//    	if (w.moveToFirst()) {
//    		do {
//    			Log.i("finesummary: ", w.getString(0) + ", " + w.getString(1));
//    		} while (w.moveToNext());
//    	}
//    	w.close();
//    	w = _Db.rawQuery(mySql3, null);
//    	if (w.moveToFirst()) {
//    		do {
//    			Log.i("depositsummary: ", w.getString(0) + ", " + w.getString(1));
//    		} while (w.moveToNext());
//    	}
//    	w.close();
        Cursor c = _Db.rawQuery(_ww, null);
        return c;
// 		return _Db.rawQuery(ww, null);
//		return _Db.query(MembersDbAdapter.MEMBERS_TABLE, new String[] {MembersDbAdapter.KEY_ROWID, MembersDbAdapter.KEY_FIRST_NAME,
//				MembersDbAdapter.KEY_LAST_NAME}, null, null, null, null, null);
	}
 	
 	public Cursor fetchMemberFineTotal(Long id) {
 		String q = _zz1 + id + _zz2 + id + ";";
 		Cursor c = _Db.rawQuery(q, null);
 		return c;
 	}

 	public Cursor fetchMemberDepositTotal(Long id) {
 		String q = _yy1 + id + _yy2 + id + ";";
 		Cursor c = _Db.rawQuery(q, null);
 		return c;
 	}
 	
 	public Cursor fetchMemberFineCount(Long id) {
 		Cursor c = _Db.rawQuery("SELECT membersfines.member_id AS _id, COUNT(" + FinesDbAdapter.FINES_TABLE + "." + FinesDbAdapter.KEY_VALUE + ") AS " + KEY_COUNT + " FROM " + MembersFinesDbAdapter.MEMBERS_FINES_TABLE + " INNER JOIN " + FinesDbAdapter.FINES_TABLE + " ON " + MembersFinesDbAdapter.MEMBERS_FINES_TABLE + "." + MembersFinesDbAdapter.KEY_FINE_ID + " = " + FinesDbAdapter.FINES_TABLE + "." + FinesDbAdapter.KEY_ROWID +  " WHERE membersfines.member_id = " + id, null);
 		return c;
 	}


}
