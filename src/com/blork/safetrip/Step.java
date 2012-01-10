package com.blork.safetrip;

import java.util.List;

import android.text.Html;
import android.text.Spanned;

import com.google.android.maps.GeoPoint;

public class Step {
	private List<GeoPoint> points;


	private String htmlDescription;
	
	public Step(List<GeoPoint> points, String htmlDescription) {
		super();
		this.points = points;
		this.htmlDescription = htmlDescription;
	}
	
	public List<GeoPoint> getPoints() {
		return points;
	}

	public Spanned getHtmlDescription() {
		return Html.fromHtml(htmlDescription.replace("<div style=\"font-size:0.9em\">", "<br />"));
	}
	
	public String toString() {
		return Html.fromHtml(htmlDescription.replace("<div style=\"font-size:0.9em\">", ". ")).toString();
	}
	
}
