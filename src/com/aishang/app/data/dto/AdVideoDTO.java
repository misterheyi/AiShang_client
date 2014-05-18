package com.aishang.app.data.dto;

import java.util.List;

import com.aishang.app.data.bean.AdVideoVO;

public class AdVideoDTO extends DTOMain {
	
	private List<AdVideoVO> videos;

	public List<AdVideoVO> getVideos() {
		return videos;
	}

	public void setVideos(List<AdVideoVO> videos) {
		this.videos = videos;
	}



}
