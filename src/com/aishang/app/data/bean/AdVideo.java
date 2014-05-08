package com.aishang.app.data.bean;

public class AdVideo {

	private int adVideo_id;
	private String adVideo_desc;
	private String adVideo_path;
	private int adVideo_count;
	private int users_id;

	private int adVideo_type;

	public int getAdVideo_type() {
		return adVideo_type;
	}

	public void setAdVideo_type(int adVideo_type) {
		this.adVideo_type = adVideo_type;
	}

	public String getAdVideo_desc() {
		return adVideo_desc;
	}

	public void setAdVideo_desc(String adVideo_desc) {
		this.adVideo_desc = adVideo_desc;
	}

	public int getAdVideo_id() {
		return adVideo_id;
	}

	public void setAdVideo_id(int adVideo_id) {
		this.adVideo_id = adVideo_id;
	}

	public String getAdVideo_path() {
		return adVideo_path;
	}

	public void setAdVideo_path(String adVideo_path) {
		this.adVideo_path = adVideo_path;
	}

	public int getAdVideo_count() {
		return adVideo_count;
	}

	public void setAdVideo_count(int adVideo_count) {
		this.adVideo_count = adVideo_count;
	}

	public int getUsers_id() {
		return users_id;
	}

	public void setUsers_id(int users_id) {
		this.users_id = users_id;
	}
}
