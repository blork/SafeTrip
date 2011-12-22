package com.blork.safetrip;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.blork.safetrip.util.Debug;

public class StepListFragment extends ListFragment {
	boolean mDualPane;
	private int mCurCheckPosition = 0;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Populate list with our static array of titles.
		int index = getSupportActivity().getSupportActionBar().getSelectedNavigationIndex();

		setListAdapter(new StepAdapter(getActivity(), android.R.layout.simple_list_item_1, 
				SafeTripActivity.routes.get(getShownRoute()).getSteps()));

		
		// Check to see if we have a frame in which to embed the details
		// fragment directly in the containing UI.
		View detailsFrame = getActivity().findViewById(R.id.details);
		mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;


		if (mDualPane) {
			// In dual-pane mode, the list view highlights the selected item.
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			// Make sure our UI is in the correct state.
			// showDetails(mCurCheckPosition);
		}
	}
	
	public static StepListFragment newInstance(int route) {
		Debug.log("The int passed to StepListFragment newInstance is " + route);
		StepListFragment f = new StepListFragment();

		// Supply index input as an argument.
		Bundle args = new Bundle();
		args.putInt("route", route);
		f.setArguments(args);

		return f;
	}

	public int getShownRoute() {
		return getArguments().getInt("route", 0);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Debug.log("The clicked on step is " + position);
		showDetails(position);
	}

	/**
	 * Helper function to show the details of a selected item, either by
	 * displaying a fragment in-place in the current UI, or starting a
	 * whole new activity in which it is displayed.
	 */
	void showDetails(int step) {
		mCurCheckPosition  = step;

		if (mDualPane) {
			// We can display everything in-place with fragments, so update
			// the list to highlight the selected item and show the data.
			getListView().setItemChecked(step, true);

			// Check what fragment is currently shown, replace if needed.
			MapRouteFragment details = (MapRouteFragment) getFragmentManager().findFragmentById(R.id.details);
			if (details == null || details.getShownStep() != step) {
				// Make new fragment to show this selection.
				details = MapRouteFragment.newInstance(getShownRoute(), step);

				// Execute a transaction, replacing any existing fragment
				// with this one inside the frame.
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.details, details);
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commit();
			}

		} else {
			// Otherwise we need to launch a new activity to display
			// the dialog fragment with selected text.
			Intent intent = new Intent(getActivity(), MappingActivity.MapRouteActivity.class);
			intent.putExtra("route", getShownRoute());
			intent.putExtra("step", step);

			startActivity(intent);
		}
	}
}
