package com.device.security.analytics.androidsecurityanalyticspro.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class AppDetailBean implements Parcelable {

	private String appName;
	private String appDetail;
	private String packageName;
	private AppPermissionBean permissionBean;

	public AppDetailBean(Parcel source) {
		this();

		appName = source.readString();
		appDetail = source.readString();
		packageName = source.readString();
		permissionBean = source.readParcelable(AppPermissionBean.class
				.getClassLoader());
	}

	public AppDetailBean() {
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppDetail() {
		return appDetail;
	}

	public void setAppDetail(String appDetail) {
		this.appDetail = appDetail;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public AppPermissionBean getPermissionBean() {
		return permissionBean;
	}

	public void setPermissionBean(AppPermissionBean permissionBean) {
		this.permissionBean = permissionBean;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(appName);
		dest.writeString(appDetail);
		dest.writeString(packageName);
		dest.writeParcelable(permissionBean, flags);
	}

	public static final Parcelable.Creator<AppDetailBean> CREATOR = new Parcelable.Creator<AppDetailBean>() {
		@Override
		public AppDetailBean createFromParcel(Parcel source) {
			return new AppDetailBean(source);
		}

		@Override
		public AppDetailBean[] newArray(int size) {
			return new AppDetailBean[size];
		}
	};
}
