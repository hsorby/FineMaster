package nz.wabbit.trouncing.rascal.finemaster;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class MemberFineAdder extends Activity {

//  private static final String TAG = "MemberFineAdder";

  private Spinner _fineDescription;
  private Button _fineButton;
  
  private MembersFinesDbAdapter _DbHelper;
  private Long _MemberId;
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		_MemberId = null;
		
		_DbHelper = new MembersFinesDbAdapter(this);
		_DbHelper.open();
		
		setContentView(R.layout.member_fine_adder);
//		setTitle(R.string.member_fine_title);
		
		// Get handles to ui objects
		_fineButton = (Button) findViewById(R.id.fineButton);
		_fineDescription = (Spinner) findViewById(R.id.fineSpinner);
		
		_fineButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onFineButtonClicked();
			}

		});
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			_MemberId = extras.getLong(MembersDbAdapter.KEY_ROWID);
			populateName();
		} else {
			TextView firstNameTextView = (TextView) findViewById(R.id.firstNameTextView);
			firstNameTextView.setText("All members");
		}
		
		populateFines();
	}

	@SuppressWarnings("deprecation")
	private void populateFines() {
		FinesDbAdapter finesHelper = new FinesDbAdapter(this);
		finesHelper.open();
			    
		Cursor fines = finesHelper.fetchAllFines();
	    startManagingCursor(fines);
		
	    String[] from = new String[] { FinesDbAdapter.KEY_DESCRIPTION };
	    int[] to = new int[] { android.R.id.text1 };

	    SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, fines, from, to);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    _fineDescription.setAdapter(adapter);
	    
	    finesHelper.close();
	}

//	@SuppressWarnings("deprecation")
	private void populateName() {
		MembersDbAdapter memberHelper = new MembersDbAdapter(this);
		memberHelper.open();
		
		TextView firstNameTextView = (TextView) findViewById(R.id.firstNameTextView);
		TextView lastNameTextView = (TextView) findViewById(R.id.lastNameTextView);

		Cursor member = memberHelper.fetchMember(_MemberId);
//		startManagingCursor(member);
		firstNameTextView.setText(member.getString(
		            member.getColumnIndexOrThrow(MembersDbAdapter.KEY_FIRST_NAME)));
		lastNameTextView.setText(member.getString(
		        member.getColumnIndexOrThrow(MembersDbAdapter.KEY_LAST_NAME)));
		member.close();
		memberHelper.close();
	}

	protected void onFineButtonClicked() {
		if (_MemberId != null) {
		    createMemberFineEntry(_MemberId);
		} else {
			fineAllMembers();
		}
		finish();
	}

	private void fineAllMembers() {
		MembersDbAdapter memberHelper = new MembersDbAdapter(this);
		memberHelper.open();
		
		Cursor c = memberHelper.fetchAllMembers();
    	if (c.moveToFirst()) {
    		do {
    			createMemberFineEntry(Long.parseLong(c.getString(0)));
    		} while (c.moveToNext());
    	}
    	c.close();
		
		memberHelper.close();
	}

	@SuppressLint("SimpleDateFormat")
	private void createMemberFineEntry(Long member_id) {
		int fine_id = _fineDescription.getSelectedItemPosition() + 1;
		
		SimpleDateFormat format_out = new SimpleDateFormat(BaseDbAdapter.date_format_out);
		String date_str = format_out.format(new Date());
	    _DbHelper.createMembersFine(member_id, fine_id, date_str);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		_DbHelper.list();
		_DbHelper.close();
	}

}
