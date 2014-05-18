package com.aishang.app.data.dto;

import java.util.List;

import com.aishang.app.data.bean.AdPictureVO;

public class ScrollPictureDTO extends DTOMain {

	private List<AdPictureVO> ad;

	public List<AdPictureVO> getAd() {
		return ad;
	}

	public void setAd(List<AdPictureVO> ad) {
		this.ad = ad;
	}
}
