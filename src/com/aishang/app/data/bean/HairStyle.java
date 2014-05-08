package com.aishang.app.data.bean;

public class HairStyle {
	private int hairStyle_id;
	private String hairStyle_path;
	private String hairStyle_sex; // 性别
	private String hairStyle_area; // 地区
	private String hairStyle_height; // 长短
	private String hairStyle_desc; // 描述
	private int users_id;

	public int getHairStyle_id() {
		return hairStyle_id;
	}

	public void setHairStyle_id(int hairStyle_id) {
		this.hairStyle_id = hairStyle_id;
	}

	public String getHairStyle_path() {
		return hairStyle_path;
	}

	public void setHairStyle_path(String hairStyle_path) {
		this.hairStyle_path = hairStyle_path;
	}

	public String getHairStyle_sex() {
		return hairStyle_sex;
	}

	public void setHairStyle_sex(String hairStyle_sex) {
		this.hairStyle_sex = hairStyle_sex;
	}

	public String getHairStyle_area() {
		return hairStyle_area;
	}

	public void setHairStyle_area(String hairStyle_area) {
		this.hairStyle_area = hairStyle_area;
	}

	public String getHairStyle_height() {
		return hairStyle_height;
	}

	public void setHairStyle_height(String hairStyle_height) {
		this.hairStyle_height = hairStyle_height;
	}

	public String getHairStyle_desc() {
		return hairStyle_desc;
	}

	public void setHairStyle_desc(String hairStyle_desc) {
		this.hairStyle_desc = hairStyle_desc;
	}

	public int getUsers_id() {
		return users_id;
	}

	public void setUsers_id(int users_id) {
		this.users_id = users_id;
	}

}
