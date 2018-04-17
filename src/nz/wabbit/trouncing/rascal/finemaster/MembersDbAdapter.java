package nz.wabbit.trouncing.rascal.finemaster;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

public class MembersDbAdapter extends BaseDbAdapter {
    
	public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_ROWID = "_id";

    public static final String MEMBERS_TABLE = "members";
    public static final String MEMBERS_TABLE_TEMP = "members_temp";
    
    public static final String TABLE_CREATE =
            "create table " + MEMBERS_TABLE + " (_id integer primary key autoincrement, "
            + KEY_FIRST_NAME +" text not null, " + KEY_LAST_NAME + " text not null);";
    public static final String TABLE_CREATE_TEMP =
            "create table " + MEMBERS_TABLE + " (_id integer primary key autoincrement, "
            + KEY_FIRST_NAME +" text not null, " + KEY_LAST_NAME + " text not null);";
    	
    public static final String TABLE_REMOVE = "DROP TABLE IF EXISTS " + MEMBERS_TABLE;
    public static final String TABLE_REMOVE_TEMP = "DROP TABLE IF EXISTS " + MEMBERS_TABLE_TEMP;
    		
    public static final String TAG = "MembersDbAdapter";


	public MembersDbAdapter(Context ctx) {
		super(ctx);
	}

    /**
     * Create a new note using the title and body provided. If the note is
     * successfully created return the new rowId for that note, otherwise return
     * a -1 to indicate failure.
     * 
     * @param title the title of the note
     * @param body the body of the note
     * @return rowId or -1 if failed
     */
    public long createMember(String first_name, String last_name) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_FIRST_NAME, first_name);
        initialValues.put(KEY_LAST_NAME, last_name);

        return _Db.insert(MEMBERS_TABLE, null, initialValues);
    }

    /**
     * Delete the note with the given rowId
     * 
     * @param rowId id of note to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteMember(long rowId) {

        return _Db.delete(MEMBERS_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all notes in the database
     * 
     * @return Cursor over all notes
     */
    public Cursor fetchAllMembers() {

        return _Db.query(MEMBERS_TABLE, new String[] {KEY_ROWID, KEY_FIRST_NAME,
                KEY_LAST_NAME}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the note that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching note, if found
     * @throws SQLException if note could not be found/retrieved
     */
    public Cursor fetchMember(long rowId) throws SQLException {

        Cursor mCursor =

            _Db.query(true, MEMBERS_TABLE, new String[] {KEY_ROWID,
                    KEY_FIRST_NAME, KEY_LAST_NAME}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }

    /**
     * Update the note using the details provided. The note to be updated is
     * specified using the rowId, and it is altered to use the title and body
     * values passed in
     * 
     * @param rowId id of note to update
     * @param first_name value to set note title to
     * @param last_name value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateMember(long rowId, String first_name, String last_name) {
        ContentValues args = new ContentValues();
        args.put(KEY_FIRST_NAME, first_name);
        args.put(KEY_LAST_NAME, last_name);

        return _Db.update(MEMBERS_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

}
