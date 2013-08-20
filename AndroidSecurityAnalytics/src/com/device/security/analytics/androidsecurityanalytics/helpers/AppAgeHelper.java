package com.device.security.analytics.androidsecurityanalytics.helpers;

import android.content.pm.PackageManager;

import com.device.security.analytics.androidsecurityanalytics.utils.AppDateUtil;

public class AppAgeHelper {

	private float sixMonthsPercentage;
	private float twelveMonthsPercentage;

	private int appsSixMonthsOld;
	private int appsTwelveMonthsOld;

	private int totalApps;

	public AppAgeHelper() {
	}

	public void incrementAppsSixMonthsOld() {
		this.appsSixMonthsOld++;
	}

	public void incrementAppsTwelveMonthsOld() {
		this.appsTwelveMonthsOld++;
	}

	public void handleAppDate(PackageManager packageManager, String packageName) {
		totalApps++;

		int months = AppDateUtil.getMonthsSinceUpdate(packageManager, packageName);
		if(months >= 12)
			incrementAppsTwelveMonthsOld();
		if(months >= 5)
			incrementAppsSixMonthsOld();
	}

	public float getSixMonthsPercentage() {
		sixMonthsPercentage = (appsSixMonthsOld * 100.0f) / this.totalApps;
		return sixMonthsPercentage;
	}

	public float getTwelveMonthsPercentage() {
		twelveMonthsPercentage = (appsTwelveMonthsOld * 100.0f) / this.totalApps;
		return twelveMonthsPercentage;
	}

	public int getAppsSixMonthsOld() {
		return appsSixMonthsOld;
	}

	public int getAppsTwelveMonthsOld() {
		return appsTwelveMonthsOld;
	}

	public int getTotalApps() {
		return totalApps;
	}
	
	public void setTotalApps(int totalApps){
		this.totalApps = totalApps;
	}
	
	@Override
	public String toString() {
	    String NEW_LINE = System.getProperty("line.separator");
		
		StringBuilder value = new StringBuilder();
		
		value.append("Total Apps: " + totalApps + NEW_LINE);
		value.append("Apps 12 mos old: " + getAppsTwelveMonthsOld() + NEW_LINE);
		value.append("Apps 6 mos old: " + getAppsSixMonthsOld() + NEW_LINE);
		value.append("% 12 mos old: " + getTwelveMonthsPercentage() + NEW_LINE);
		value.append("% 6 mos old: " + getSixMonthsPercentage() + NEW_LINE);
		
		return value.toString();
	}
	

	
}
