package com.hofman.calendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.hofman.R;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.TextView;

/*
 * Fragment displaying a single month's calendar. Created by adapter.
 */
public class ScreenSlideCalFragment extends android.support.v4.app.Fragment {
	public static final String ARG_PAGE = "page";
	
	private int mPageNumber;
	private static int mainPage;
	/*
	 * Calendar: mCalendar is the current month to display (initially set to the current date.
	 * 				Value adjusted relative to pages in setupCalendar)
	 * 			 previousMonth is set to the value of mCalendar - 1 month, and used to determine days in previous month
	 */
	private Calendar mCalendar = Calendar.getInstance();
	private Calendar previousMonth = Calendar.getInstance();
	private Locale english = new Locale("EN");
	private static int showYear, showMonth;					//ShowYear and showMonth are the values shown
	private static int currYear, currDay;					//CurrYear and currDay are the current day's values, used to tell when 'today' is
	private static String showMonthName = "", currMonthName = "";			//One to display, one to compare
	
	
	/*
	 * Creates a new fragment whenever called, and passes the page number for recall later
	 */
	public static ScreenSlideCalFragment create(int pageNumber) {
		ScreenSlideCalFragment fragment = new ScreenSlideCalFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public ScreenSlideCalFragment() {
	}
	
	/*
	 * By choice, we assume that the user will likely go backward and forward
	 * (with more chance to go backward to see previous days). We place today's month in the middle
	 * of the total amount of pages, to lessen the load
	 */
	public static void setTotalPages(int pages) {
		mainPage = pages/2;
	}
	
	@Override 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageNumber = getArguments().getInt(ARG_PAGE);
	}
	
	/*
	 * Create view for both the calendar (as a gridview) and set the adapter to respond to the grid
	 * Also register and start listening for clicks on the individual days.
	 * Here, all the values are passed into a dayView (month name, whether or not it is today, etc)
	 * before the dayView is drawn. 
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.calendar, container, false);
		setupCalendar();
		GridView gv = (GridView) rootView.findViewById(R.id.gridview);
		gv.setBackgroundColor(Color.LTGRAY);
		DayAdapter dAdapter = new DayAdapter(getActivity().getApplicationContext());
		gv.setAdapter(dAdapter);
		registerForContextMenu(gv);
		gv.setOnItemLongClickListener(new OnItemLongClickListener() {

	        @Override
	        public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
				Log.d("Press", "Long was here");
	        	getActivity().openContextMenu(v.getRootView());
	            return false;
	        }
	    });
	    gv.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				Log.d("Press", "Short was here");
	            //Toast.makeText(getActivity().getApplicationContext(), "" + position, Toast.LENGTH_SHORT).show();
	        	DayView myDView = (DayView) v;
	        	myDView.setRedX();
	        	myDView.invalidate();
	        }
	    });
	    
	    int maxDays = mCalendar.getActualMaximum(Calendar.DATE);
		int startDay = mCalendar.get(Calendar.DAY_OF_WEEK);
		int previousMonthEndDate = previousMonth.getActualMaximum(Calendar.DATE);
		dAdapter.setShownValues(showMonthName, startDay, maxDays, previousMonthEndDate, showYear);
		dAdapter.setCurrentDate(currMonthName, currYear, currDay);
	    
		TextView tv = (TextView) rootView.findViewById(R.id.monthName);
		tv.setText(showMonthName + ", " + showYear);
		
		return rootView;
	}
	
	public static int[] getMonthAndYearBasedOnCalendar(int pageNumber) {
		Calendar c = Calendar.getInstance();
		int todayMonth = c.get(Calendar.MONTH);
		int todayYear = c.get(Calendar.YEAR);
		
		int[] adjustedMonthAndYear = adjustMonthAndYearBasedOnPageNumber(pageNumber, todayMonth, todayYear);
		todayMonth = adjustedMonthAndYear[0];
		todayYear = adjustedMonthAndYear[1];
		
		return new int[] {todayMonth, todayYear};
	}
	
	private void setupCalendar() {
		//Gets the current month, at today's date. currYear and currDay do not change (used to determine if today)
		showYear = mCalendar.get(Calendar.YEAR);
		showMonth = mCalendar.get(Calendar.MONTH);
		currYear = mCalendar.get(Calendar.YEAR);
		currDay = mCalendar.get(Calendar.DATE);

		int[] adjustedMonthAndYear = adjustMonthAndYearBasedOnPageNumber(mPageNumber, showMonth, showYear);
		showMonth = adjustedMonthAndYear[0];
		showYear = adjustedMonthAndYear[1];
		//Find the current date, before changing mCalendar to reflect the shown date. Find previous month from new Month (uses wraparound)
		currDay = mCalendar.get(Calendar.DATE);
		currMonthName = new SimpleDateFormat("MMMM", english).format(mCalendar.getTime());
		mCalendar.set(showYear, showMonth, 1);
		showMonthName = new SimpleDateFormat("MMMM", english).format(mCalendar.getTime());
		previousMonth.set(showYear, showMonth-1, 1);
	}
	
	public static int[] adjustMonthAndYearBasedOnPageNumber(int pageNumber, int currentMonth, int currentYear) {
		/*
		 * Find the difference between today's month value and the main page
		 * This tells us how much we've shifted the results to center the main page and what we need
		 * to change for the next value
		 */
		int shiftedValue = currentMonth - mainPage;
		
		/*
		 * Adjust the month by adding the currentPage together with the shift, 
		 * and % 12 to keep it within month values
		 * Example: mPageNumber = 3
		 * 			mainPage = 2
		 * 			showMonth = 10 (November)
		 * 			shiftedValue = 8 (month shown on the first page)
		 * 			(new) showMonth = 3+8 = 11 % 12 = 11 (December)
		 */
		currentMonth = (pageNumber + shiftedValue) % 12;
		
		/*
		 * If showMonth is positive (we are looking to the right), we add 1 for every time we pass a 12
		 * If showMonth is negative (looking left), we subtract 12 before removing one from each 12
		 * (mCalendar tries to assist us if negative by changing the year, but this doesn't work with positive values)
		 */
		int changeYearValue;
		if (currentMonth >= 0) {
			changeYearValue = (int) ((float) (pageNumber + shiftedValue)) / 12;
		} else {
			changeYearValue = (int) ((float) (pageNumber + shiftedValue) - 12) / 12;
			currentMonth += 12;
		}
		currentYear += changeYearValue;
		return new int[] {currentMonth, currentYear};
	}
	

	
}
