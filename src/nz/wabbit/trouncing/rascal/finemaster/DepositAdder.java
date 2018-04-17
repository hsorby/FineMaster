package nz.wabbit.trouncing.rascal.finemaster;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DepositAdder extends Activity {

    private EditText _value;
    private Button _saveButton;
    
    private DepositsDbAdapter _DbHelper;
    private Long _MemberId;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		_DbHelper = new DepositsDbAdapter(this);
		_DbHelper.open();
		
		setContentView(R.layout.deposit_adder);
		setTitle(R.string.deposit_activity);
		
		// Get handles to ui objects
		_value = (EditText) findViewById(R.id.depositValueEditText);
		_saveButton = (Button) findViewById(R.id.depositButton);
		
		_saveButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onSaveButtonClicked();
			}

		});
		
		_MemberId = (Long) null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	_MemberId = extras.getLong(MembersFinesDbAdapter.KEY_ROWID);
        }
	}

	protected void onSaveButtonClicked() {
        createDepositEntry();
        finish();
	}

	@SuppressLint("SimpleDateFormat")
	private void createDepositEntry() {
        int value = Integer.parseInt(_value.getText().toString());
		if (_MemberId != null) {
			SimpleDateFormat format_out = new SimpleDateFormat(BaseDbAdapter.date_format_out);
			String date_str = format_out.format(new Date());
	        _DbHelper.createDeposit(value, _MemberId, date_str);
		}		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		_DbHelper.list();
		_DbHelper.close();
	}

}
