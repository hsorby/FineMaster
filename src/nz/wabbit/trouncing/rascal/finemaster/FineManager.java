package nz.wabbit.trouncing.rascal.finemaster;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
//import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class FineManager extends ListActivity {

    private static final int ADD_FINE_MENU_ID = Menu.FIRST;
    private static final int EDIT_FINE_MENU_ID = Menu.FIRST + 1;

    private static final int ACTIVITY_ADD_FINE = 0;
    private static final int ACTIVITY_EDIT_FINE = 1;
    
	private FinesDbAdapter _DbHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fine_manager);
		
		_DbHelper = new FinesDbAdapter(this);
        _DbHelper.open();

        fillData();
        registerForContextMenu(getListView());
	}

	@SuppressWarnings("deprecation")
	private void fillData() {
        Cursor memberCursor = _DbHelper.fetchAllFines();
//        if (memberCursor.moveToFirst())
//        {
//        do{
//        	Log.i("fines manager:", memberCursor.getString(1) + ", " + memberCursor.getString(2));
////           todoItems.add(memberCursor.getString(0));
//
//           }while (memberCursor.moveToNext());
//        }

//        return;
        startManagingCursor(memberCursor);

        // Create an array to specify the fields we want to display in the list
        String[] from = new String[]{FinesDbAdapter.KEY_DESCRIPTION, FinesDbAdapter.KEY_VALUE};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.descriptionTextView, R.id.valueTextView};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter notes = 
            new SimpleCursorAdapter(this, R.layout.entry_row, memberCursor, from, to);
        setListAdapter(notes);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, ADD_FINE_MENU_ID, 0, R.string.add_fine);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case ADD_FINE_MENU_ID:
            	launchFineAdder();
            	return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, EDIT_FINE_MENU_ID, 0, R.string.edit_fine);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case EDIT_FINE_MENU_ID:
                Intent edit_intent = new Intent(this, FineAdder.class);
                edit_intent.putExtra(FinesDbAdapter.KEY_ROWID, info.id);
                startActivityForResult(edit_intent, ACTIVITY_EDIT_FINE );
                return true;
        }
        
        return false;
    }
    
    private void launchFineAdder() {
        Intent i = new Intent(this, FineAdder.class);
        startActivityForResult(i, ACTIVITY_ADD_FINE);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		_DbHelper.close();
	}

}
