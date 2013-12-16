package com.hofman.calendar;

import com.hofman.R;
import com.hofman.todo.DailyTodosOverviewActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.GridView;
import android.widget.Toast;


public class CalendarMain extends FragmentActivity {

	private static int TOTAL_CAL_PAGES = 32;
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;
	
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.view_pager);

	    mPager = (ViewPager) findViewById(R.id.pager);
	    mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					if (position == TOTAL_CAL_PAGES -1 || position == 0) {
						//Reached end or beginning
						int amountOfPagesToAdd = 16;
						TOTAL_CAL_PAGES += amountOfPagesToAdd;
						mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
						mPager.setAdapter(mPagerAdapter);
						mPager.setCurrentItem((position + (amountOfPagesToAdd/2)), true);
					}
					invalidateOptionsMenu();
				}
		});
		mPager.setCurrentItem(TOTAL_CAL_PAGES/2);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.calendar_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			return true;
		case R.id.action_today:
			mPager.setCurrentItem(TOTAL_CAL_PAGES/2);
			return true;
		case R.id.action_previous:
			mPager.setCurrentItem(mPager.getCurrentItem() - 1);
			return true;
		case R.id.action_next:
			mPager.setCurrentItem(mPager.getCurrentItem() + 1);
			return true;	
		}		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	                                ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    GridView.AdapterContextMenuInfo grid = (GridView.AdapterContextMenuInfo) menuInfo;
	    Toast.makeText(getApplicationContext(), grid.position + "", Toast.LENGTH_SHORT).show();
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.set_done:
	        	Toast.makeText(getApplicationContext(), "Set done", Toast.LENGTH_SHORT).show();
	            return true;
	        case R.id.view_todo:
	        	int[] currentMonthAndYear = ScreenSlideCalFragment.getMonthAndYearBasedOnCalendar(mPager.getCurrentItem());
	        	Toast.makeText(getApplicationContext(), "" + info.position + " " + currentMonthAndYear[0] + " " + currentMonthAndYear[1], Toast.LENGTH_SHORT).show();
	        	//Toast.makeText(getApplicationContext(), "Viewing list", Toast.LENGTH_SHORT).show();
	    		Intent i = new Intent(this, DailyTodosOverviewActivity.class);
	    		startActivity(i);
	        	return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
	class ScreenSlidePagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {

		public ScreenSlidePagerAdapter(android.support.v4.app.FragmentManager fm) {
			super(fm);	
		}

		@Override
		public ScreenSlideCalFragment getItem(int position) {
			ScreenSlideCalFragment.setTotalPages(TOTAL_CAL_PAGES);
			return ScreenSlideCalFragment.create(position);
		}

		@Override
		public int getCount() {
			return TOTAL_CAL_PAGES;
		}
		
	}
}
