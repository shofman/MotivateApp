package com.hofman.home;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.hofman.R;
import com.hofman.calendar.CalendarMain;
import com.hofman.todo.TodosOverviewActivity;
/*
 * TODO: Replace this with a proper home screen
 * 
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_navigation);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void startCalendar(View v) {
		Intent i = new Intent(this, CalendarMain.class);
		startActivity(i);
	}
	
	public void startTodo(View v) {
		Intent i = new Intent(this, TodosOverviewActivity.class);
		startActivity(i);
	}

}
