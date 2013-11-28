package com.hofman.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

/*
 * View that represents each individual day within the grid view (single day in calendar)
 * TODO: Center paint text or replace with textview
 */
public class DayView extends View {
	Paint mPaint = new Paint();
	private int day = 0;
	private boolean hasX = false;				//Paint an X for the day
	private boolean isToday = false;			//Detect if it is the current day
	private boolean greyed = false;				//If displayed on calendar, but not in current month, grey out
	//private int circleRadius = this.getWidth()/2;				//Circle around today's radius

	public DayView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DayView(Context mContext) {
		super(mContext);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mPaint.setColor(Color.WHITE);
		mPaint.setStyle(Style.FILL);
		canvas.drawPaint(mPaint);
		
		//Determine color depending on whether number is within month or outside
		if (greyed) {
			mPaint.setColor(Color.LTGRAY);
		} else {
			mPaint.setColor(Color.BLACK);
		}
		
		//Determine whether to draw text or an X to indicate completion
		if (hasX) {
			drawX(canvas);
		} else {
			mPaint.setTextSize(50);
			canvas.drawText("" + (day), this.getWidth()/3, this.getHeight()/1.5f, mPaint);
		}
		
		//Draw a circle around today
		if (isToday) {
			drawCircle(canvas);
		}
	}

	/*
	 * Sets the day integer to be painted, and whether or not it should be grey or not
	 */
	protected void setDay(int day, boolean grey) {
		this.day = day;
		if (grey) {
			greyed = true;
		}
	}

	/*
	 * Draws red circle on current canvas center (used for drawing todays date)
	 */
	private void drawCircle(Canvas canvas) {
		int circleRadius = 0;
		if (this.getWidth() > this.getHeight())
			circleRadius = this.getHeight()/2 - 3;
		else 
			circleRadius = this.getWidth()/2 - 3;
		mPaint.setColor(Color.RED);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(10);
		canvas.drawCircle(this.getWidth()/2, this.getHeight()/2f, circleRadius, mPaint);
		mPaint.setColor(Color.BLACK);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setStrokeWidth(0);
	}

	/*
	 * Draws a red cross on current canvas center (used for indicating completion)
	 */
	private void drawX(Canvas canvas) {
		mPaint.setColor(Color.RED);
		mPaint.setStrokeWidth(10);
		canvas.drawLine(this.getWidth(), 0, 0, this.getHeight(), mPaint);
		canvas.drawLine(0, 0, this.getWidth(), this.getHeight(), mPaint);
		mPaint.setColor(Color.BLACK);
		mPaint.setStrokeWidth(0);
	}

	//Tell view that the day is today
	protected void setToday() {
		isToday = true;
	}

	//Set a red X for this day
	protected void setRedX() {
		hasX = true;
	}

}
