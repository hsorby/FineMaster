package nz.wabbit.trouncing.rascal.finemaster;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
//import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class FineAdder extends Activity {
//	private static final String TAG = "FineAdder";

    private EditText _description;
    private EditText _value;
    private Button _saveButton;
    
    private FinesDbAdapter _DbHelper;
    private Long _RowId;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		_DbHelper = new FinesDbAdapter(this);
		_DbHelper.open();
		
		setContentView(R.layout.fine_adder);
		setTitle(R.string.fine_activity_title);
		
		// Get handles to ui objects
		_description = (EditText) findViewById(R.id.fineAdderDescriptionText);
		_value = (EditText) findViewById(R.id.fineAdderValueText);
		_saveButton = (Button) findViewById(R.id.fineAdderButton);
		
		_saveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onSaveButtonClicked();
			}

		});
		
		_value.setOnEditorActionListener(new OnEditorActionListener() {
	        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
	            	onSaveButtonClicked();
	            }    
	            return false;
	        }
	    });
		
		_RowId = (Long) null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            _RowId = extras.getLong(FinesDbAdapter.KEY_ROWID);
            _saveButton.setText(R.string.update_fine);
            TextView header = (TextView) findViewById(R.id.textViewHeader);
            header.setText(R.string.update_fine);
            populateFields();
        	
        }
        
	}

	private void populateFields() {
        Cursor member = _DbHelper.fetchFine(_RowId);
        _description.setText(member.getString(
                    member.getColumnIndexOrThrow(FinesDbAdapter.KEY_DESCRIPTION)));
        _value.setText(member.getString(
                member.getColumnIndexOrThrow(FinesDbAdapter.KEY_VALUE)));
        member.close();
	}

	protected void onSaveButtonClicked() {
        createFineEntry();
        finish();
	}

	private void createFineEntry() {
        String description = _description.getText().toString();
        int value = Integer.parseInt(_value.getText().toString());
		if (_RowId == null) {
	        _DbHelper.createFine(description, value);
		} else {
			_DbHelper.updateFine(_RowId, description, value);
		}
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		_DbHelper.list();
		_DbHelper.close();
	}

}
