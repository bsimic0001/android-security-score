package com.device.security.analytics.androidsecurityanalytics.beans;

import android.os.Parcel;
import android.os.Parcelable;

public class AppPermissionBean implements Parcelable {

	private int numberOfPermissions = 0;
	private int top10Permissions = 0;
	private int top20Permissions = 0;
	private int top30Permissions = 0;
	private String[] permissions;

	public AppPermissionBean(Parcel source) {
		this();
		permissions = source.createStringArray();
		numberOfPermissions = source.readInt();
		top10Permissions = source.readInt();
		top20Permissions = source.readInt();
		top30Permissions = source.readInt();
	}
	
	public int getPermissionsInTop30(){
		return top10Permissions + top20Permissions + top30Permissions;
	}

	public AppPermissionBean() {
	}

	public int getNumberOfPermissions() {
		return numberOfPermissions;
	}

	public void setNumberOfPermissions(int numberOfPermissions) {
		this.numberOfPermissions = numberOfPermissions;
	}

	public int getTop10Permissions() {
		return top10Permissions;
	}

	public void setTop10Permissions(int top10Permissions) {
		this.top10Permissions = top10Permissions;
	}

	public int getTop20Permissions() {
		return top20Permissions;
	}

	public void setTop20Permissions(int top20Permissions) {
		this.top20Permissions = top20Permissions;
	}

	public int getTop30Permissions() {
		return top30Permissions;
	}

	public void setTop30Permissions(int top30Permissions) {
		this.top30Permissions = top30Permissions;
	}

	public String[] getPermissions() {
		return permissions;
	}

	public void setPermissions(String[] permissions) {
		this.permissions = permissions;
	}

	@Override
	public int describeContents() {
		return 222;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(permissions);
		dest.writeInt(numberOfPermissions);
		dest.writeInt(top10Permissions);
		dest.writeInt(top20Permissions);
		dest.writeInt(top30Permissions);
	}

	public static final Parcelable.Creator<AppPermissionBean> CREATOR = new Parcelable.Creator<AppPermissionBean>() {
		@Override
		public AppPermissionBean createFromParcel(Parcel source) {
			return new AppPermissionBean(source);
		}

		@Override
		public AppPermissionBean[] newArray(int size) {
			return new AppPermissionBean[size];
		}
	};

}