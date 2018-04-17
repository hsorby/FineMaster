package nz.wabbit.trouncing.rascal.finemaster;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
//import android.util.Log;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class EntryList extends ListActivity {

	private Long _MemberId;
	private int _EntryType;
	private String _Date;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.entry_list);
//		setTitle(R.string.entry_list_activity);
		
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	_MemberId = extras.getLong(MembersDbAdapter.KEY_ROWID);
        	_EntryType = extras.getInt(FineMaster.ENTRY_TYPE);
        	_Date = extras.getString(CalendarView.DATE_STR);
        }
        
        TextView date_text = (TextView) findViewById(R.id.dateTextView);
        if (_Date.length() > 0) {
            date_text.setText(_Date);
        } else {
        	date_text.setText("All Dates");
        }
        
        populateName();
        populateList();
	}

	@SuppressWarnings("deprecation")
	private void populateList() {
		Cursor c = null;
		String[] from = null;
		EntryListDbAdapter helper = new EntryListDbAdapter(this);
		helper.open();
        if (_EntryType == FineMaster.SHOW_DEPOSITS_MENU_ID) {
			c = helper.fetchDepositsForMemberOn(_MemberId, _Date);
			from = new String[]{DepositsDbAdapter.KEY_ROWID, DepositsDbAdapter.KEY_VALUE};
        } else {
        	c = helper.fetchFinesForMemberOn(_MemberId, _Date);
			from = new String[]{FinesDbAdapter.KEY_DESCRIPTION, FinesDbAdapter.KEY_VALUE};
        }
		startManagingCursor(c);
        // Create an array to specify the fields we want to display in the list
        

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.descriptionTextView, R.id.valueTextView};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter entries = 
            new SimpleCursorAdapter(this, R.layout.entry_row, c, from, to);
        setListAdapter(entries);
		helper.close();
	}

	private void populateName() {
		MembersDbAdapter memberHelper = new MembersDbAdapter(this);
        memberHelper.open();
		TextView memberNameTextView = (TextView) findViewById(R.id.memberNameTextView);

		Cursor member = memberHelper.fetchMember(_MemberId);
		String name = member.getString(
	            member.getColumnIndexOrThrow(MembersDbAdapter.KEY_FIRST_NAME)) + ' ' +
	            member.getString(member.getColumnIndexOrThrow(MembersDbAdapter.KEY_LAST_NAME));
		memberNameTextView.setText(name);
		member.close();
		
        
        memberHelper.close();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
