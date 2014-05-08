package com.aishang.app.data.dto;

import java.util.ArrayList;
import java.util.List;

import com.aishang.app.data.bean.AdPicture;
import com.aishang.app.data.bean.Users;

public class PriceListDTO extends DTOMain {
	private List<AdPicture> ad1;
	private List<AdPicture> ad2;
	private List<AdPicture> ad3;
	private List<AdPicture> ad4;
	private List<Users> hairstylist;

	public List<Users> getHairstylist() {
		if(hairstylist == null){
			return new ArrayList<Users>();
		}
		return hairstylist;
	}

	public void setHairstylist(List<Users> hairstylist) {
		this.hairstylist = hairstylist;
	}

	public List<AdPicture> getAd1() {
		if(ad1 == null){
			return new ArrayList<AdPicture>();
		}
		return ad1;
	}

	public void setAd1(List<AdPicture> ad1) {
		this.ad1 = ad1;
	}

	public List<AdPicture> getAd2() {
		if(ad2 == null){
			return new ArrayList<AdPicture>();
		}
		return ad2;
	}

	public void setAd2(List<AdPicture> ad2) {
		this.ad2 = ad2;
	}

	public List<AdPicture> getAd3() {
		if(ad3 == null){
			return new ArrayList<AdPicture>();
		}
		return ad3;
	}

	public void setAd3(List<AdPicture> ad3) {
		this.ad3 = ad3;
	}

	public List<AdPicture> getAd4() {
		if(ad4 == null){
			return new ArrayList<AdPicture>();
		}
		return ad4;
	}

	public void setAd4(List<AdPicture> ad4) {
		this.ad4 = ad4;
	}
}
