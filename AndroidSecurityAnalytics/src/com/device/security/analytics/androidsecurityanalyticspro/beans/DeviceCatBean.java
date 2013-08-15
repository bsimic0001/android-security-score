package com.device.security.analytics.androidsecurityanalyticspro.beans;

import java.text.DecimalFormat;

public class DeviceCatBean {

	private String category;
	private float score;
	private String explanation;

	private DecimalFormat df = new DecimalFormat("#.##");

	public DeviceCatBean(String category, float score, String explanation) {
		this.category = category;
		this.score = score;
		this.explanation = explanation;
	}

	public DeviceCatBean() {
	}

	public String getScoreString() {
		return category + " score - " + df.format(score) + "%";
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public String getExplanation() {
		return explanation;
	}

	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}

}
