package com.blork.safetrip;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blork.safetrip.util.Debug;


public class StepAdapter extends ArrayAdapter<Step> {

	private List<Step> steps;
	private Context context;

	public StepAdapter(Context context, int textViewResourceId, List<Step> items) {
		super(context, textViewResourceId, items);
		this.steps = items;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(android.R.layout.simple_list_item_1, null);
		}
		Step step = steps.get(position);
		if (step != null) {
			TextView title = (TextView) v.findViewById(android.R.id.text1);

			if (title != null) {
				title.setText(step.getHtmlDescription());                           
			}

		}
		return v;
	}
}