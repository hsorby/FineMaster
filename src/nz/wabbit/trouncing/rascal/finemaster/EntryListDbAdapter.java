package nz.wabbit.trouncing.rascal.finemaster;

import android.content.Context;
import android.database.Cursor;
//import android.util.Log;

public class EntryListDbAdapter extends BaseDbAdapter {

//	private static final String DEPOSIT_QUERY = "SELECT members._id, members.first_name, IFNULL(members.last_name, 'x') AS last_name, "
//			  + "IFNULL((SELECT finesummary.total FROM finesummary WHERE members._id = finesummary._id ORDER BY finesummary._id), '0') AS DUMBO"
//			  + " FROM members;";

	public EntryListDbAdapter(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

	public Cursor fetchDepositsForMemberOn(Long memberId, String date) {
		Cursor c = null;
		if (date.length() > 0) {
			c = _Db.query(DepositsDbAdapter.DEPOSITS_TABLE, new String[] {DepositsDbAdapter.KEY_ROWID, DepositsDbAdapter.KEY_VALUE}
			, DepositsDbAdapter.KEY_MEMBER_ID + '=' + memberId + " AND " + DepositsDbAdapter.KEY_DATE + "='" + date + "'", null, null, null, DepositsDbAdapter.KEY_ROWID, null); 
		} else {
			c = _Db.query(DepositsDbAdapter.DEPOSITS_TABLE, new String[] {DepositsDbAdapter.KEY_ROWID, DepositsDbAdapter.KEY_VALUE}
			, DepositsDbAdapter.KEY_MEMBER_ID + '=' + memberId, null, null, null, DepositsDbAdapter.KEY_ROWID, null); 
		}
		
		return  c;
	}

	public Cursor fetchFinesForMemberOn(Long memberId, String date) {
		Cursor c = null;
		if (date.length() > 0) {
			c = _Db.rawQuery("SELECT fines._id, fines.description, fines.value FROM fines, membersfines WHERE membersfines.fine_id = fines._id AND membersfines.member_id = " + memberId + " AND membersfines.date='" + date + "'", null); 
		} else {
			c = _Db.rawQuery("SELECT fines._id, fines.description, fines.value FROM fines, membersfines WHERE membersfines.fine_id = fines._id AND membersfines.member_id = " + memberId, null);
		}
		return c;
	}

}
