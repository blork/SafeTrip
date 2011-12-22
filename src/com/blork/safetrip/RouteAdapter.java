package com.blork.safetrip;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;


public class RouteAdapter extends ArrayAdapter<Route> implements SpinnerAdapter {

	private List<Route> routes;
	private Context context;

	public RouteAdapter(Context context, int textViewResourceId, List<Route> items) {
		super(context, textViewResourceId, items);
		this.routes = items;
		this.context = context;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(android.R.layout.simple_spinner_dropdown_item, null);
		}
		Route route = routes.get(position);
		if (route != null) {
			TextView title = (TextView) v.findViewById(android.R.id.text1);

			if (title != null) {
				String titleText;
				if (position == 0) {
					titleText = "Your safest route";
				} else {
					titleText = "Alternative route #" + position;
				}
				title.setText(titleText);                           
			}

		}

		return v;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.abs__simple_spinner_item, null);
		}
		Route route = routes.get(position);
		if (route != null) {
			TextView title = (TextView) v.findViewById(android.R.id.text1);

			if (title != null) {
				String titleText;
				if (position == 0) {
					titleText = "Your safest route";
				} else {
					titleText = "Alternative route #" + position;
				}
				title.setText(titleText);                           
			}

		}

		return v;
	}
}

