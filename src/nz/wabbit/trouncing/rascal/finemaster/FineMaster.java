package nz.wabbit.trouncing.rascal.finemaster;

import nz.wabbit.trouncing.rascal.finemaster.util.SystemUiHider;

//import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
//import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
//import android.widget.Button;
//import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.app.ListActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FineMaster extends ListActivity {
    public static final String TAG = "FineMaster";

	private FineMasterDbAdapter _DbHelper;
	
	public static final String ENTRY_TYPE = "entry_type";
	
	private static final int ACTIVITY_CREATE_MEMBER = 0;
    private static final int ACTIVITY_EDIT_MEMBER = 1;
//    private static final int ACTIVITY_MANAGE_FINES = 2;
    private static final int ACTIVITY_CREATE_MEMBER_FINE = 3;
    private static final int ACTIVITY_CREATE_MEMBER_DEPOSIT = 4;
    private static final int ACTIVITY_SHOW_MEMBER_ENTRIES = 5;
    
    private static final int ADD_MEMBER_ID = Menu.FIRST;
    private static final int MANAGE_FINES_ID = Menu.FIRST + 1;
    private static final int PAY_DEPOSIT_MENU_ID = Menu.FIRST + 2;
    public static final int SHOW_FINES_MENU_ID = Menu.FIRST + 3;
    public static final int SHOW_DEPOSITS_MENU_ID = Menu.FIRST + 4;
    private static final int VIEW_MEMBER_MENU_ID = Menu.FIRST + 5;
    private static final int FINE_ALL_MENU_ID = Menu.FIRST + 6;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fine_master);
		setTitle(R.string.app_name);

		_DbHelper = new FineMasterDbAdapter(this);
        _DbHelper.open();
        fillData();
        registerForContextMenu(getListView());
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, ADD_MEMBER_ID, 0, R.string.add_member);
        menu.add(0, MANAGE_FINES_ID, 0, R.string.manage_fines);
        menu.add(0, FINE_ALL_MENU_ID, 0, R.string.fine_all_members);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case ADD_MEMBER_ID:
                launchMemberAdder();
                return true;
            case MANAGE_FINES_ID:
            	launchFineManager();
            	return true;
            case FINE_ALL_MENU_ID:
            	launchFineAdder();
            	return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    private void launchFineAdder() {
    	Intent i = new Intent(this, MemberFineAdder.class);
    	startActivityForResult(i, ACTIVITY_CREATE_MEMBER_FINE );
	}

	@Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, PAY_DEPOSIT_MENU_ID, 0, R.string.context_menu_pay_deposit);
        menu.add(0, SHOW_FINES_MENU_ID, 0, R.string.show_fines);
        menu.add(0, SHOW_DEPOSITS_MENU_ID, 0, R.string.show_deposits);
        menu.add(0, VIEW_MEMBER_MENU_ID, 0, R.string.context_menu_view_member);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case VIEW_MEMBER_MENU_ID:
                Intent edit_intent = new Intent(this, MemberAdder.class);
                edit_intent.putExtra(MembersDbAdapter.KEY_ROWID, info.id);
                startActivityForResult(edit_intent, ACTIVITY_EDIT_MEMBER );
                return true;
            case PAY_DEPOSIT_MENU_ID:
                Intent deposit_intent = new Intent(this, DepositAdder.class);
                deposit_intent.putExtra(MembersDbAdapter.KEY_ROWID, info.id);
                startActivityForResult(deposit_intent, ACTIVITY_CREATE_MEMBER_DEPOSIT );
            	return true;
            case SHOW_FINES_MENU_ID:
            case SHOW_DEPOSITS_MENU_ID:
            	Intent calendar_intent = new Intent(this, CalendarView.class);
            	calendar_intent.putExtra(MembersDbAdapter.KEY_ROWID, info.id);
            	calendar_intent.putExtra(FineMaster.ENTRY_TYPE, item.getItemId());
            	startActivityForResult(calendar_intent, ACTIVITY_SHOW_MEMBER_ENTRIES );
            	return true;
        }
        return super.onContextItemSelected(item);
    }

	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, MemberFineAdder.class);
        i.putExtra(MembersDbAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_CREATE_MEMBER_FINE );
    }

	@SuppressWarnings("deprecation")
	private void fillData() {
        // Get all of the rows from the database and create the item list
		
        Cursor memberCursor = _DbHelper.fetchSummaryListing();
//        if (memberCursor.moveToFirst())
//        {
//        do{
//        	Log.i(TAG, memberCursor.getString(0) + ", " + memberCursor.getString(3));
////           todoItems.add(memberCursor.getString(0));
//
//           }while (memberCursor.moveToNext());
//        }

//        return;
        startManagingCursor(memberCursor);

        // Create an array to specify the fields we want to display in the list
        String[] from = new String[]{MembersDbAdapter.KEY_FIRST_NAME, MembersDbAdapter.KEY_LAST_NAME, FineMasterDbAdapter.KEY_BALANCE};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.textView1, R.id.textView2, R.id.textView3};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter members = 
            new SimpleCursorAdapter(this, R.layout.member_summary, memberCursor, from, to);
        setListAdapter(members);
	}

    /**
     * Launches the MemberAdder activity to add a new member to the squad.
     */
    protected void launchMemberAdder() {
        Intent i = new Intent(this, MemberAdder.class);
        startActivityForResult(i, ACTIVITY_CREATE_MEMBER);
    }
    
    private void launchFineManager() {
        Intent i = new Intent(this, FineManager.class);
//      startActivityForResult(i, ACTIVITY_MANAGE_FINES);
        startActivity(i);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		_DbHelper.close();
	}
	
}
