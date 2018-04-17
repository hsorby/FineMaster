package nz.wabbit.trouncing.rascal.finemaster;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BaseDbAdapter {
	
	public static final String date_format_out = "yyyy MMM dd";

	private static final String TAG = "BaseDbAdapter";
    private static final String DATABASE_NAME = "finemaster";
    private static final int DATABASE_VERSION = 11;
    
    public static final String FINESUMMARY_VIEW = "finesummary";
    public static final String KEY_TOTAL = "total";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_MEMBER_ID = "member_id";
    
    public String dd = ", " + FinesDbAdapter.FINES_TABLE + "." + FinesDbAdapter.KEY_VALUE + ", ";
    private static final String VIEW_CREATE =
    		"create view " + FINESUMMARY_VIEW + " AS SELECT " + MembersFinesDbAdapter.MEMBERS_FINES_TABLE + "." + MembersFinesDbAdapter.KEY_MEMBER_ID + 
    		" AS " + KEY_ROWID + ", TOTAL(" + FinesDbAdapter.FINES_TABLE + "." + FinesDbAdapter.KEY_VALUE + ") AS " + KEY_TOTAL + 
    		" FROM " + MembersFinesDbAdapter.MEMBERS_FINES_TABLE + " INNER JOIN " + FinesDbAdapter.FINES_TABLE + 
    		" ON " + MembersFinesDbAdapter.MEMBERS_FINES_TABLE + "." + MembersFinesDbAdapter.KEY_FINE_ID + " = " + FinesDbAdapter.FINES_TABLE + "." + FinesDbAdapter.KEY_ROWID + 
    		" GROUP BY " + MembersFinesDbAdapter.MEMBERS_FINES_TABLE + "." + MembersFinesDbAdapter.KEY_MEMBER_ID;
    private static final String VIEW_REMOVE =
    		"DROP VIEW IF EXISTS " + FINESUMMARY_VIEW;
    
    private final Context _Ctx;
    
    private DatabaseHelper _DbHelper;
	protected SQLiteDatabase _Db;

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        	db.execSQL(MembersDbAdapter.TABLE_CREATE);
        	db.execSQL(DepositsDbAdapter.TABLE_CREATE);
        	db.execSQL(FinesDbAdapter.TABLE_CREATE);
        	db.execSQL(MembersFinesDbAdapter.TABLE_CREATE);
        	db.execSQL(BankDepositsDbAdapter.TABLE_CREATE);
        	db.execSQL(DepositsDbAdapter.VIEW_CREATE);
        	db.execSQL(BaseDbAdapter.VIEW_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL(MembersDbAdapter.TABLE_REMOVE);
            db.execSQL(DepositsDbAdapter.TABLE_REMOVE);
            db.execSQL(FinesDbAdapter.TABLE_REMOVE);
            db.execSQL(MembersFinesDbAdapter.TABLE_REMOVE);
            db.execSQL(BankDepositsDbAdapter.TABLE_REMOVE);
            db.execSQL(DepositsDbAdapter.VIEW_REMOVE);
            db.execSQL(BaseDbAdapter.VIEW_REMOVE);
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public BaseDbAdapter(Context ctx) {
        this._Ctx = ctx;
    }

	public BaseDbAdapter open() throws SQLException {
        _DbHelper = new DatabaseHelper(_Ctx);
        _Db = _DbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        _DbHelper.close();
    }

}
