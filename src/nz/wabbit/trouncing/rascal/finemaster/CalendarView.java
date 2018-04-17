/*
* Copyright 2011 Lauri Nevala.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package nz.wabbit.trouncing.rascal.finemaster;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;


public class CalendarView extends Activity implements OnGestureListener {

    private static final int SHOW_ALL_MENU_ID = Menu.FIRST;

    public static final String DATE_STR = "date";
	private GestureDetector _GestureDetector;
	private MembersFinesDbAdapter _MembersFinesHelper;
	private DepositsDbAdapter _DepositHelper;
	private Long _MemberId;
	private int _EntryType;
	
	public Calendar _Month;
	public CalendarAdapter _Adapter;
	public Handler _Handler;
	public ArrayList<String> _Items; // container to store some random calendar items
	
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.calendar);

		_MemberId = (Long) null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
        	_MemberId = extras.getLong(MembersDbAdapter.KEY_ROWID);
        	_EntryType = extras.getInt(FineMaster.ENTRY_TYPE);
        }
        	    
	    _Month = Calendar.getInstance();
	    
	    _GestureDetector = new GestureDetector(this);
	    if (_EntryType == FineMaster.SHOW_DEPOSITS_MENU_ID) {
	    	_DepositHelper = new DepositsDbAdapter(this);
	    	_DepositHelper.open();
	    }
	    else {
		    _MembersFinesHelper = new MembersFinesDbAdapter(this);
		    _MembersFinesHelper.open();
	    }
	    
	    _Items = new ArrayList<String>();
	    _Adapter = new CalendarAdapter(this, _Month);
	    
	    GridView gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter(_Adapter);
	    
	    _Handler = new Handler();
	    _Handler.post(calendarUpdater);
	    
	    TextView title  = (TextView) findViewById(R.id.title);
	    title.setText(android.text.format.DateFormat.format("MMMM yyyy", _Month));

	    TextView previous  = (TextView) findViewById(R.id.previous);
	    previous.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				moveToPreviousMonth();
			}
		});
	    
	    TextView next  = (TextView) findViewById(R.id.next);
	    next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				moveToNextMonth();
				
			}
		});
	    
	    gridview.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return _GestureDetector.onTouchEvent(event);
			}
		});
	    
		gridview.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		    	TextView date = (TextView)v.findViewById(R.id.date);
//		    	Log.i("CalendarView", "onItemClickListener()");
		        if(date instanceof TextView && !date.getText().equals("")) {
		        	
		    		String day = date.getText().toString();
		        	onShowEntryList(day);
//		        	setResult(RESULT_OK, intent);
//		        	finish();
		        }
		        
		    }
		});
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, SHOW_ALL_MENU_ID, 0, R.string.show_all);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case SHOW_ALL_MENU_ID:
            	onShowEntryList("");
//                launchMemberAdder();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (_EntryType == FineMaster.SHOW_DEPOSITS_MENU_ID) {
			_DepositHelper.close();
		}
		else {
			_MembersFinesHelper.close();
		}
	}

	public void refreshCalendar()
	{
		TextView title  = (TextView) findViewById(R.id.title);
		
		_Adapter.refreshDays();
		_Adapter.notifyDataSetChanged();				
		_Handler.post(calendarUpdater); // generate some random calendar items				
		
		title.setText(android.text.format.DateFormat.format("MMMM yyyy", _Month));
	}
	
	public void onNewIntent(Intent intent) {
		String date = intent.getStringExtra("date");
		String[] dateArr = date.split("-"); // date format is yyyy-mm-dd
		_Month.set(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[2]));
	}
	
	public Runnable calendarUpdater = new Runnable() {
		
		@SuppressLint("SimpleDateFormat")
		@Override
		public void run() {
			_Items.clear();
//			SimpleDateFormat format_in = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
			//SimpleDateFormat forma/t_in = new SimpleDateFormat();
//			SimpleDateFormat format_out = new SimpleDateFormat(BaseDbAdapter.date_format_out);
//			DateFormat df = DateFormat.g.getDateFormat();
			Cursor c = null;
			if (_EntryType == FineMaster.SHOW_DEPOSITS_MENU_ID) {
				c = _DepositHelper.fetchDepositsForMember(_MemberId);
			}
			else {
				c = _MembersFinesHelper.fetchFinesForMember(_MemberId);
			}

	    	if (c.moveToFirst()) {
	    		do {
//	    			Date date = null;
//					try {
//						date = format_in.parse(c.getString(3));
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
//					if (date != null) {
//						String date_str = format_out.format(date);
	    			String date_str = c.getString(3);
//	    			
					if (!_Items.contains(date_str)) {
		    			_Items.add(date_str);
//			    			int cc = c.getColumnCount();
//			    			if (cc == 4) {
//			    				Log.i("membersfines: ", c.getString(0) + ", " + c.getString(1) + ", " + c.getString(2) + ", " + c.getString(3));
//			    			}
//			    			if (cc == 3) {
//			    				Log.i("membersdeposits: ", c.getString(0) + ", " + c.getString(1) + ", " + c.getString(2));
//			    			}
//			    			Log.i("++++ string date:", date_str);
//						}
					}
	    		} while (c.moveToNext());
	    	}
	    	c.close();

			_Adapter.setItems(_Items);
			_Adapter.notifyDataSetChanged();
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		return _GestureDetector.onTouchEvent(me);
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent start, MotionEvent finish, float velocityX,
			float velocityY) {
		if (start.getRawX() < finish.getRawX()) {
            moveToPreviousMonth();
		} else {
		    moveToNextMonth();
		}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	private void moveToPreviousMonth() {
		if(_Month.get(Calendar.MONTH)== _Month.getActualMinimum(Calendar.MONTH)) {				
			_Month.set((_Month.get(Calendar.YEAR)-1),_Month.getActualMaximum(Calendar.MONTH),1);
		} else {
			_Month.set(Calendar.MONTH,_Month.get(Calendar.MONTH)-1);
		}
		refreshCalendar();
	}

	private void moveToNextMonth() {
		if(_Month.get(Calendar.MONTH)== _Month.getActualMaximum(Calendar.MONTH)) {				
			_Month.set((_Month.get(Calendar.YEAR)+1),_Month.getActualMinimum(Calendar.MONTH),1);
		} else {
			_Month.set(Calendar.MONTH,_Month.get(Calendar.MONTH)+1);
		}
		refreshCalendar();
	}

	private void onShowEntryList(String day) {
		Intent intent = new Intent(this, EntryList.class);
		if (day.length() > 0) {
			_Month.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
			intent.putExtra(DATE_STR, android.text.format.DateFormat.format("yyyy MMM dd", _Month));
		} else {
			intent.putExtra(DATE_STR, "");
		}
		intent.putExtra(MembersDbAdapter.KEY_ROWID, _MemberId);
		intent.putExtra(FineMaster.ENTRY_TYPE, _EntryType);

		startActivity(intent);
	}
}
