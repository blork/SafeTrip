package com.blork.safetrip;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class StepAdapter extends ArrayAdapter<Step> {

	private List<Step> steps;
	private Context context;
	private int textViewResourceId;

	public StepAdapter(Context context, int textViewResourceId, List<Step> items) {
		super(context, textViewResourceId, items);
		this.steps = items;
		this.context = context;
		this.textViewResourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(textViewResourceId, null);
		}
		Step step = steps.get(position);
		if (step != null) {
			TextView title = (TextView) v.findViewById(android.R.id.title);

			if (title != null) {
				title.setText(step.getHtmlDescription());                           
			}

		}
		return v;
	}
}