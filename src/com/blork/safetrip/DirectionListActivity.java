package com.blork.safetrip;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentMapActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItem;
import android.support.v4.view.ViewPager;

import com.viewpagerindicator.TitlePageIndicator;
import com.viewpagerindicator.TitleProvider;

public class DirectionListActivity extends FragmentMapActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.fragment_pager);
		PagerAdapter mAdapter = new PagerAdapter(getSupportFragmentManager());

		ViewPager mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		TitlePageIndicator mIndicator = (TitlePageIndicator)findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);


		//int index = getIntent().getExtras().getInt("index");
		mPager.setCurrentItem(0);
	}

	private static class PagerAdapter extends FragmentStatePagerAdapter implements TitleProvider {
		public PagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return SafeTripActivity.routes.size();
		}

		@Override
		public Fragment getItem(int position) {
			return StepListFragment.newInstance(position);
		}

		@Override
		public String getTitle(int position) {
			if (position == 0) {
				return "SafeRoute";
			} else {
				return "Alternative route " + position;
			}
		}
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
