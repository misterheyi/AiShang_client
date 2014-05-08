package com.aishang.app.data.dto;

import com.aishang.app.data.bean.Users;

public class LoginDTO extends DTOMain {
	// 发型师
	private Users hairstylist;
	// 发型店
	private Users store;
	// 代理商
	private Users agent;

	public Users getHairstylist() {
		return hairstylist;
	}

	public void setHairstylist(Users hairstylist) {
		this.hairstylist = hairstylist;
	}

	public Users getStore() {
		return store;
	}

	public void setStore(Users store) {
		this.store = store;
	}

	public Users getAgent() {
		return agent;
	}

	public void setAgent(Users agent) {
		this.agent = agent;
	}

}
