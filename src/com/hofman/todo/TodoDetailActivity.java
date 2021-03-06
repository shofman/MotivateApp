package com.hofman.todo;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.hofman.R;
import com.hofman.todo.contentprovider.TodoContentProvider;
import com.hofman.todo.database.TodoTable;

public class TodoDetailActivity extends Activity {

	//TODO: Test with multiple entries => could have issues due to static
	//TODO: Add a cancel to the due date button and prevent users from going past previous date
	private Spinner mLabel;
	private Spinner mPriority;
	private EditText mTitleText;
	private EditText mBodyText;
	private CheckBox mDaily;
	private CheckBox mReminder;
	private static Button mDueDate;

	private static int dueDay = 0;
	private static int dueMonth = 0;
	private static int dueYear = 0;

	private int creationDay = 0;
	private int creationMonth = 0;
	private int creationYear = 0;

	private static boolean setDate = false;

	private Calendar mCalendar;

	private Uri todoUri;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todo_edit);

		setTodoCreationDateToToday();

		mPriority = (Spinner) findViewById(R.id.priority);
		mLabel = (Spinner) findViewById(R.id.label);
		mTitleText = (EditText) findViewById(R.id.todo_edit_summary);
		mBodyText = (EditText) findViewById(R.id.todo_edit_description);
		mDaily = (CheckBox) findViewById(R.id.dailyCheckBox);
		mReminder = (CheckBox) findViewById(R.id.reminderCheckBox);
		mDueDate = (Button) findViewById(R.id.setDueDateButton);

		Button confirmButton = (Button) findViewById(R.id.todo_edit_button);
		Bundle extras = getIntent().getExtras();

		todoUri = (savedInstanceState == null) ? null : (Uri) savedInstanceState.getParcelable(TodoContentProvider.CONTENT_ITEM_TYPE);
		if (extras != null) {
			todoUri = extras.getParcelable(TodoContentProvider.CONTENT_ITEM_TYPE);
			fillData(todoUri); 
			copyValuesForDateSuggestion();
		}


		mDueDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new DatePickerFragment();
				newFragment.show(getFragmentManager(), "datePicker");
			}
		});


		confirmButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(mTitleText.getText().toString())) {
					makeToast();
				} else {
					setResult(RESULT_OK);
					finish();
				}	
			}
		});
	}


	private void setTodoCreationDateToToday() {
		mCalendar = Calendar.getInstance();
		creationDay = mCalendar.get(Calendar.DAY_OF_WEEK);
		creationMonth = mCalendar.get(Calendar.MONTH);
		creationYear = mCalendar.get(Calendar.YEAR);
	}

	private void copyValuesForDateSuggestion() {
		dueDay = creationDay;
		dueMonth = creationMonth;
		dueYear = creationYear;
	}

	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, dueYear, dueMonth, dueDay);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			dueYear = year;
			dueMonth = month;
			dueDay = day;
			setDueDateButtonWithCurrentDueDate();
			setDate = true;
		}
	}


	private void fillData(Uri uri) {
		String[] projection = {TodoTable.COLUMN_SUMMARY,
				TodoTable.COLUMN_DESCRIPTION, TodoTable.COLUMN_LABEL,
				TodoTable.COLUMN_DAILY, TodoTable.COLUMN_DUEDAY, TodoTable.COLUMN_CREATIONDAY,
				TodoTable.COLUMN_CREATIONMONTH, TodoTable.COLUMN_CREATIONYEAR, TodoTable.COLUMN_DUEMONTH,
				TodoTable.COLUMN_DUEYEAR, TodoTable.COLUMN_REMINDER, TodoTable.COLUMN_PRIORITY};

		Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

		if (cursor != null) {
			populateData(cursor);
		}
	}

	private void populateData(Cursor c) {
		c.moveToFirst();
		String label = c.getString(c.getColumnIndexOrThrow(TodoTable.COLUMN_LABEL));
		setSpinnerData(mLabel, label);
		String priority = c.getString(c.getColumnIndexOrThrow(TodoTable.COLUMN_PRIORITY));
		setSpinnerData(mPriority, priority);

		int daily = c.getInt(c.getColumnIndexOrThrow(TodoTable.COLUMN_DAILY));
		mDaily.setChecked(daily == 1);
		int reminder = c.getInt(c.getColumnIndexOrThrow(TodoTable.COLUMN_REMINDER));
		mReminder.setChecked(reminder == 1);

		mTitleText.setText(c.getString(c.getColumnIndexOrThrow(TodoTable.COLUMN_SUMMARY)));
		mBodyText.setText(c.getString(c.getColumnIndexOrThrow(TodoTable.COLUMN_DESCRIPTION)));

		creationDay = Integer.parseInt(c.getString(c.getColumnIndexOrThrow(TodoTable.COLUMN_CREATIONDAY)));
		creationMonth = Integer.parseInt(c.getString(c.getColumnIndexOrThrow(TodoTable.COLUMN_CREATIONMONTH)));
		creationYear = Integer.parseInt(c.getString(c.getColumnIndexOrThrow(TodoTable.COLUMN_CREATIONYEAR)));

		String dueDayString = c.getString(c.getColumnIndexOrThrow(TodoTable.COLUMN_DUEDAY));
		String dueMonthString = c.getString(c.getColumnIndexOrThrow(TodoTable.COLUMN_DUEMONTH));
		String dueYearString = c.getString(c.getColumnIndexOrThrow(TodoTable.COLUMN_DUEYEAR));
		if (dueDayString != null && dueMonthString != null && dueYearString != null) {
			dueDay = Integer.parseInt(dueDayString);
			dueMonth = Integer.parseInt(dueMonthString);
			dueYear = Integer.parseInt(dueYearString);
			setDueDateButtonWithCurrentDueDate();
		}
		
		c.close();
	}

	private static void setDueDateButtonWithCurrentDueDate() {
		String dueMonthAsString = convertIntegerToMonth(dueMonth);
		mDueDate.setText(dueMonthAsString + " " + dueDay + ", " + dueYear);
	}

	private static String convertIntegerToMonth(int monthAsInt) {
		return new DateFormatSymbols().getMonths()[monthAsInt];
	}

	private void setSpinnerData(Spinner spinner, String toFind) {
		for (int i=0; i<spinner.getCount(); i++) {
			String s = (String) spinner.getItemAtPosition(i);
			if (s.equalsIgnoreCase(toFind)) {
				spinner.setSelection(i);
			}
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putParcelable(TodoContentProvider.CONTENT_ITEM_TYPE, todoUri);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	private void saveState() {
		String label = (String) mLabel.getSelectedItem();
		String summary = mTitleText.getText().toString();
		String description = mBodyText.getText().toString();
		String priority = (String) mPriority.getSelectedItem();

		int daily = (mDaily.isChecked()) ? 1 : 0;
		int reminder = (mReminder.isChecked()) ? 1 : 0;
		if (description.length() == 0 && summary.length() == 0) {
			return;
		}

		ContentValues values = new ContentValues();
		values.put(TodoTable.COLUMN_LABEL, label);
		values.put(TodoTable.COLUMN_SUMMARY, summary);
		values.put(TodoTable.COLUMN_DESCRIPTION, description);
		values.put(TodoTable.COLUMN_DAILY, daily);
		values.put(TodoTable.COLUMN_CREATIONDAY, "" + creationDay);
		values.put(TodoTable.COLUMN_CREATIONMONTH, "" + creationMonth);
		values.put(TodoTable.COLUMN_CREATIONYEAR, "" + creationYear);
		if (setDate) {
			values.put(TodoTable.COLUMN_DUEDAY, "" + dueDay);
			values.put(TodoTable.COLUMN_DUEMONTH, "" + dueMonth);
			values.put(TodoTable.COLUMN_DUEYEAR, "" + dueYear);
		}
		values.put(TodoTable.COLUMN_REMINDER, reminder);
		values.put(TodoTable.COLUMN_PRIORITY, priority);
		values.put(TodoTable.COLUMN_TIME, "10:00");

		if (todoUri == null) {
			todoUri = getContentResolver().insert(TodoContentProvider.CONTENT_URI, values);
		} else {
			getContentResolver().update(todoUri, values, null, null);
		}
	}

	private void makeToast() {
		Toast.makeText(TodoDetailActivity.this, "Please maintain a summary", Toast.LENGTH_LONG).show();
	}

}
