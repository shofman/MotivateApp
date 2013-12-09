package com.hofman.calendar;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
//TODO: Fix bug: Replicate by tilting phone horizontal, scrolling down, and tilting back. First sunday is greyed
//TODO: Test on small phone sizes
//TODO: Add Sun-Sat tags along the top
public class DayAdapter extends BaseAdapter {
	private Context mContext;
	private int width, height;
	private int maximumDays = 0;
	private int previousMonthMax = 0;
	private int displayYear = 2013;
	private int currYear = 2013;
	private int nextMonthDay = 1;
	int dayToStart = 0, today = 0;
	private String displayMonthName = "", currMonthName = "";
	
	public DayAdapter(Context c) {
		mContext = c;
		//Find the window height to setup the size of the calendar days
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
	}
	
	/*
	 * Meaning of life? And/or number of days when displaying 6 weeks with 7 days 
	 */
	public int getCount() {
		return 42;
	}
	
	//Default
	public Object getItem(int position) {
		return null;
	}
	
	//Default
	public long getItemId(int position) {
		return 0;
	}
	
	public View getView(int position, View convertView, ViewGroup parent ) {
		DayView dView;
		int displayDate = 1;
		boolean grey = false;

		if (dayToStart == 1) {
			dayToStart += 7;
		}
		
		/*
		 * Adjust display date, depending on three things
		 * 	1) Before month has started (31 - (3 (Tuesday) - 2) + 0 (position will be low)
		 * 		(adjusted by 2 for zero index of position, and 1 index of dayToStart)
		 * 		Shows 30 for Sunday (pos 0) and 31 for Monday (pos 1) 
		 *  2) After the month has finished, increments a counter until done ( 1,2,3...)
		 *  3) Otherwise, displays the number of the day, relative to the position and when the month starts
		 */
		if(position + 1 < dayToStart) {
			displayDate = previousMonthMax - (dayToStart - 2) + position;
			grey = true;
		} else if (position + 1> maximumDays + dayToStart - 1)  {
			displayDate = (position + 2 - dayToStart) % maximumDays;
			grey = true;
			//nextMonthDay++;
		} else {
			displayDate = position + 2 - dayToStart;
		}

		//Create view if not there, or use previously created one
		if (convertView == null) {
			dView = new DayView(mContext);
			dView.setPadding(8,8,8,8);
			
			//Set the height and width to fill the screen without gaps, based on screen width and height
			if (width > height) {
				dView.setLayoutParams(new GridView.LayoutParams(width/7,height/7));
			} else {
				dView.setLayoutParams(new GridView.LayoutParams(width/7,width/7));
			}
		} else {
			dView = (DayView) convertView;			
		}
		
		//Set the displayDate to the adjusted value, and whether to grey it out or not
		dView.setDay(displayDate, grey);
		
		//If it is today, draw a circle around it
		if (displayMonthName.equals(currMonthName) && today == displayDate && currYear == displayYear && !grey) {
			dView.setToday();
		}
		
		//Add redX code here
		if (displayDate == 32) {
			dView.setRedX();
		}
		
		return dView;
	}
	
	//Pass in the values for displaying on the calendar
	protected void setShownValues(String monShownName, int startDay, int maxDays, int prevMonth, int currShowYear) {
		displayMonthName = monShownName;
		dayToStart = startDay;
		maximumDays = maxDays;
		previousMonthMax = prevMonth;
		displayYear = currShowYear;
	}

	//Pass in the information to set today's date
	protected void setCurrentDate(String currMonName, int currYear, int currDate) {
		this.today = currDate;
		this.currYear = currYear;
		this.currMonthName = currMonName;
	}

}
