package nz.wabbit.trouncing.rascal.finemaster;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

public class MembersFinesDbAdapter extends BaseDbAdapter {

	public static final String KEY_DATE = "date";
    public static final String KEY_FINE_ID = "fine_id";
    public static final String KEY_MEMBER_ID = "member_id";
    public static final String KEY_ROWID = "_id";

    public static final String MEMBERS_FINES_TABLE = "membersfines";
    
    public static final String TABLE_CREATE =
            "create table " + MEMBERS_FINES_TABLE + " (" + KEY_ROWID + " integer primary key autoincrement, "
             + KEY_MEMBER_ID + " integer not null, " + KEY_FINE_ID + " integer not null, "+ KEY_DATE + " text not null);";
    	
    public static final String TABLE_REMOVE = "DROP TABLE IF EXISTS " + MEMBERS_FINES_TABLE;
    		
    public static final String TAG = "DepositsDbAdapter";

	public MembersFinesDbAdapter(Context ctx) {
		super(ctx);
		// TODO Auto-generated constructor stub
	}

    public long createMembersFine(long member_id, long fine_id, String date) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_MEMBER_ID, member_id);
        initialValues.put(KEY_FINE_ID, fine_id);
        initialValues.put(KEY_DATE, date);

        return _Db.insert(MEMBERS_FINES_TABLE, null, initialValues);
    }

//    @SuppressLint("SimpleDateFormat")
	public void list() {
    	Cursor c = fetchAllMembersFines();
    	if (c.moveToFirst()) {
    		do {
    			Log.i("membersfines: ", c.getString(0) + ", " + c.getString(1) + ", " + c.getString(2) + ", " + c.getString(3));
    		} while (c.moveToNext());
    	}
    	c.close();
    }

    public boolean deleteMembersFine(long rowId) {

        return _Db.delete(MEMBERS_FINES_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllMembersFines() {

        return _Db.query(MEMBERS_FINES_TABLE, new String[] {KEY_ROWID, KEY_MEMBER_ID
        		, KEY_FINE_ID, KEY_DATE}, null, null, null, null, null);
    }

    public Cursor fetchMembersFine(long rowId) throws SQLException {

        Cursor cursor =
            _Db.query(true, MEMBERS_FINES_TABLE, new String[] {KEY_ROWID,
            		KEY_MEMBER_ID, KEY_FINE_ID, KEY_DATE}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;

    }

   public boolean updateMembersFine(long rowId, long member_id, long fine_id, String date) {
        ContentValues args = new ContentValues();
        args.put(KEY_MEMBER_ID, member_id);
        args.put(KEY_FINE_ID, fine_id);
        args.put(KEY_DATE, date);

        return _Db.update(MEMBERS_FINES_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

	public Cursor fetchFinesForMember(Long memberId) {
        Cursor cursor =
                _Db.query(true, MEMBERS_FINES_TABLE, new String[] {KEY_ROWID,
                		KEY_MEMBER_ID, KEY_FINE_ID, KEY_DATE}, KEY_MEMBER_ID + "=" + memberId, null,
                        null, null, null, null);
        return cursor;
	}

}
