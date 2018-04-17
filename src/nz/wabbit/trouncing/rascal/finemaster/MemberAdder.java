package nz.wabbit.trouncing.rascal.finemaster;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class MemberAdder extends Activity {
//    private static final String TAG = "MemberAdder";

    private static final int INVISIBLE = 4;
	private EditText _firstNameEditText;
    private EditText _lastNameEditText;
    private Button _saveButton;
    
    private MembersDbAdapter _dbHelper;
    private Long _rowId;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		System.out.println("=====================");
		_dbHelper = new MembersDbAdapter(this);
		_dbHelper.open();
		
		setContentView(R.layout.member_adder);
//		setTitle(R.string.memeber_activity_title);
		
		// Get handles to ui objects
		_firstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
		_lastNameEditText = (EditText) findViewById(R.id.lastnameEditText);
		_saveButton = (Button) findViewById(R.id.addNewPersonButton);
		
		_saveButton.setEnabled(false);
		_saveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onSaveButtonClicked();
			}	         
		});
		
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(_firstNameEditText, InputMethodManager.SHOW_IMPLICIT);
		_firstNameEditText.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				validateInput();
				return true;
			}
		});
		_lastNameEditText.setOnEditorActionListener(new OnEditorActionListener() {
	        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	        	validateInput();
	            if ((event != null && 
	            		((event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) &&
	            		_saveButton.isEnabled() ){
	            	onSaveButtonClicked();
	            	return true;
	            }    
	            return false;
	        }
	    });
		
		_rowId = (Long) null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            _rowId = extras.getLong(MembersDbAdapter.KEY_ROWID);
            _saveButton.setText(R.string.update_member);
            TextView header = (TextView) findViewById(R.id.textViewHeader);
            header.setText(R.string.update_member);
            header.setTextSize(32);
            populateFields();
        	
        } else {
        	TextView text = (TextView) findViewById(R.id.textView3);
        	text.setVisibility(INVISIBLE);
        	text = (TextView) findViewById(R.id.textView5);
        	text.setVisibility(INVISIBLE);
        	text = (TextView) findViewById(R.id.textView7);
        	text.setVisibility(INVISIBLE);
        }
	}

	protected void validateInput() {
		System.out.print("validate the output\n");
		if (_firstNameEditText.getText().length() > 0 && _lastNameEditText.getText().length() > 0){
			_saveButton.setEnabled(true);
		} else {
			_saveButton.setEnabled(false);
		}
	}

	private void populateFields() {
        Cursor member = _dbHelper.fetchMember(_rowId);
        _firstNameEditText.setText(member.getString(
                    member.getColumnIndexOrThrow(MembersDbAdapter.KEY_FIRST_NAME)));
        _lastNameEditText.setText(member.getString(
                member.getColumnIndexOrThrow(MembersDbAdapter.KEY_LAST_NAME)));
        member.close();
        FineMasterDbAdapter fineMasterDbHelper = new FineMasterDbAdapter(this);
        fineMasterDbHelper.open();
        Cursor c = null;
        TextView text = (TextView) findViewById(R.id.textView4);
        c = fineMasterDbHelper.fetchMemberFineCount(_rowId);
    	if (c.moveToFirst()) {
    		text.setText(c.getString(1));
    	}
    	c.close();
        text = (TextView) findViewById(R.id.textView6);
        c = fineMasterDbHelper.fetchMemberFineTotal(_rowId);
    	if (c.moveToFirst()) {
    		text.setText(c.getString(1));
    	}
    	c.close();
        text = (TextView) findViewById(R.id.textView8);
    	c = fineMasterDbHelper.fetchMemberDepositTotal(_rowId);
    	if (c.moveToFirst()) {
    		text.setText(c.getString(1));
    	}
    	c.close();
        fineMasterDbHelper.close();
	}

	protected void onSaveButtonClicked() {
        createMemberEntry();
        finish();
	}

	private void createMemberEntry() {
        String first_name = _firstNameEditText.getText().toString();
        String last_name = _lastNameEditText.getText().toString();
		if (_rowId == null) {
	        _dbHelper.createMember(first_name, last_name);
		} else {
			_dbHelper.updateMember(_rowId, first_name, last_name);
		}
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		_dbHelper.close();
	}

}
