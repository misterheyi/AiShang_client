package com.aishang.app.data.bean;

public class Users {
	private int users_id;
	private String users_name;
	private String users_email;
	private String users_password;
	private String users_IMEI;
	private int userGroup_id;
	private String users_face;

	public Users() {

	}

	public Users(String users_name, String users_email, String users_password, String users_IMEI, int userGroup_id) {
		this.users_name = users_name;
		this.users_email = users_email;
		this.users_password = users_password;
		this.users_IMEI = users_IMEI;
		this.userGroup_id = userGroup_id;
	}

	public String getUsers_face() {
		return users_face;
	}

	public void setUsers_face(String users_face) {
		this.users_face = users_face;
	}

	public int getUsers_id() {
		return users_id;
	}

	public String getUsers_email() {
		return users_email;
	}

	public void setUsers_email(String users_email) {
		this.users_email = users_email;
	}

	public void setUsers_id(int users_id) {
		this.users_id = users_id;
	}

	public String getUsers_name() {
		return users_name;
	}

	public void setUsers_name(String users_name) {
		this.users_name = users_name;
	}

	public String getUsers_password() {
		return users_password;
	}

	public void setUsers_password(String users_password) {
		this.users_password = users_password;
	}

	public String getUsers_IMEI() {
		return users_IMEI;
	}

	public void setUsers_IMEI(String users_IMEI) {
		this.users_IMEI = users_IMEI;
	}

	public int getUserGroup_id() {
		return userGroup_id;
	}

	public void setUserGroup_id(int userGroup_id) {
		this.userGroup_id = userGroup_id;
	}

}
