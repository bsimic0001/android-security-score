package com.device.security.analytics.androidsecurityanalytics.beans;

public class PermBean {

	private int id;
	private String name;
	private String displayName;
	private int rank;
	private String info;

	public PermBean(int id, String name, String displayName, int rank,
			String info) {
		this.id = id;
		this.name = name;
		this.displayName = displayName;
		this.rank = rank;
		this.info = info;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

}
