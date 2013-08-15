package com.device.security.analytics.androidsecurityanalytics.utils;

public class PermissionTracker {

	private float percentTop10 = 0;
	private float percentTop20 = 0;
	private float percentTop30 = 0;
	
	private int totalDevicePermissions = 0;
	private int totalNumberOfApps = 0;
	private int top10DeviceAmount = 0;
	private int top20DeviceAmount = 0;
	private int top30DeviceAmount = 0;
	
	public PermissionTracker(){}
	
	private void incrementTop10(){
		this.top10DeviceAmount += 1;
	}
	
	private void incrementTop20(){
		this.top20DeviceAmount += 1;
	}
	
	private void incrementTop30(){
		this.top30DeviceAmount += 1;
	}
	
	/**
	 * This method will iterate over an app's permissions and then 
	 * increment the correct values
	 * @param permissions
	 */
	public void handleAppPermissions(String[] permissions){
		
		if(permissions == null)
			return;
		
		totalNumberOfApps++;
		for (int i = 0; i < permissions.length; i++) {
			this.totalDevicePermissions++;
			int indexOfPermission = getPermissionIndex(permissions[i]);
			if(indexOfPermission < 10 && indexOfPermission >= 0)
				incrementTop10();
			if(indexOfPermission < 20 && indexOfPermission > 9)
				incrementTop20();
			if(indexOfPermission < 30 && indexOfPermission > 19)
				incrementTop30();
		}
	}
	
	private int getPermissionIndex(String permission){
		for (int i = 0; i < AnalyticsUtils.top30Permissions.length; i++) {
			if(permission.contains(AnalyticsUtils.top30Permissions[i])){
				return i;
			}
		}
		return -1;
	}

	public float getPercentTop10() {
		if(totalDevicePermissions > 0)
			this.percentTop10 = (top10DeviceAmount * 100.0f) / this.totalDevicePermissions;
		return percentTop10;
	}

	public float getPercentTop20() {
		if(totalDevicePermissions > 0)
			this.percentTop20 = (top20DeviceAmount * 100.0f) / this.totalDevicePermissions;
		return percentTop20;
	}

	public float getPercentTop30() {
		
		if(totalDevicePermissions > 0)
			this.percentTop30 = (top30DeviceAmount * 100.0f) / this.totalDevicePermissions;
		return percentTop30;
	}

	public int getTotalDevicePermissions() {
		return totalDevicePermissions;
	}

	public int getTotalNumberOfApps() {
		return totalNumberOfApps;
	}
	
	public int getTop10DeviceAmount() {
		return top10DeviceAmount;
	}

	public void setTop10DeviceAmount(int top10DeviceAmount) {
		this.top10DeviceAmount = top10DeviceAmount;
	}

	public int getTop20DeviceAmount() {
		return top20DeviceAmount;
	}

	public void setTop20DeviceAmount(int top20DeviceAmount) {
		this.top20DeviceAmount = top20DeviceAmount;
	}

	public int getTop30DeviceAmount() {
		return top30DeviceAmount;
	}

	public void setTop30DeviceAmount(int top30DeviceAmount) {
		this.top30DeviceAmount = top30DeviceAmount;
	}

	@Override
	public String toString() {
	    String NEW_LINE = System.getProperty("line.separator");
		
		StringBuilder value = new StringBuilder();
		
		value.append("Total Apps: " + totalNumberOfApps + NEW_LINE);
		value.append("Total Permmissons: " + totalDevicePermissions + NEW_LINE);
		value.append("Top 10 Percent: " + getPercentTop10() + NEW_LINE);
		value.append("Top 20 Percent: " + getPercentTop20() + NEW_LINE);
		value.append("Top 30 Percent: " + getPercentTop30() + NEW_LINE);
		value.append("Top 10 Perms: " + this.top10DeviceAmount + NEW_LINE);
		value.append("Top 20 Perms: " + this.top20DeviceAmount + NEW_LINE);
		value.append("Top 30 Perms: " + this.top30DeviceAmount + NEW_LINE);
		
		return value.toString();
	}

}
