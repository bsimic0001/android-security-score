package com.device.security.analytics.androidsecurityanalytics.beans;

public class TrustedApp {

	private String packageName;
	private boolean trusted;

	public TrustedApp() {

	}

	public TrustedApp(String packageName, boolean trusted) {
		this.packageName = packageName;
		this.trusted = trusted;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public boolean isTrusted() {
		return trusted;
	}

	public void setTrusted(boolean trusted) {
		this.trusted = trusted;
	}

}
