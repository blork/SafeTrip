package com.blork.safetrip;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBar;
import android.support.v4.app.ActionBar.OnNavigationListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentMapActivity;
import android.support.v4.view.MenuItem;
import android.view.View;

public class MappingActivity extends FragmentMapActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fragment_layout);
		getSupportFragmentManager()
		.beginTransaction()
		.add(R.id.titles, new Fragment())
		.commit();

		ensureSupportActionBarAttached();

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		RouteAdapter items = new RouteAdapter(this, android.R.layout.simple_spinner_item, SafeTripActivity.routes);
		items.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		getSupportActionBar().setListNavigationCallbacks(items, new OnNavigationListener() {
			public boolean onNavigationItemSelected(int route, long itemId) {
				getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.titles, StepListFragment.newInstance(route))
				.commit();

				View detailsFrame = findViewById(R.id.details);
				boolean mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
				if (mDualPane) {
					MapRouteFragment map = new MapRouteFragment();
					map.setArguments(getIntent().getExtras());

					getSupportFragmentManager().beginTransaction()
					.replace(R.id.details, map)
					.commit();
				}


				return true;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, SafeTripActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return true;
	}

	/**
	 * This is a secondary activity, to show what the user has selected
	 * when the screen is not large enough to show it all in one activity.
	 */

	public static class MapRouteActivity extends FragmentMapActivity {

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			if (getResources().getConfiguration().orientation
					== Configuration.ORIENTATION_LANDSCAPE) {
				// If the screen is now in landscape mode, we can show the
				// dialog in-line with the list so we don't need this activity.
				finish();
				return;
			}
			
			ensureSupportActionBarAttached();

			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			

			MapRouteFragment map = new MapRouteFragment();
			map.setArguments(getIntent().getExtras());

			getSupportFragmentManager().beginTransaction()
			.add(android.R.id.content, map)
			.commit();

		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case android.R.id.home:
				Intent intent = new Intent(this, SafeTripActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
			}
		}

		@Override
		protected boolean isRouteDisplayed() {
			// TODO Auto-generated method stub
			return true;
		}
	}
}
