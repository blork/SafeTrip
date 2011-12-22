package com.blork.safetrip;

import java.util.ArrayList;

import android.graphics.Color;

public class Route {
	private String name;
	private String description;
	private ArrayList<Step> steps;
	private int safeScore;
	
	public Route(String name, String description, int safeScore, ArrayList<Step> steps) {
		super();
		this.name = name;
		this.description = description;;
		this.steps = steps;
		this.safeScore = safeScore;
	}
	
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}

	public ArrayList<Step> getSteps() {
		return steps;
	}
	
	public int getSafeScore() {
		return safeScore;
	}

}