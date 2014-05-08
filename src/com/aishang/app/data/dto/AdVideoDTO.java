package com.aishang.app.data.dto;

import java.util.ArrayList;
import java.util.List;

import com.aishang.app.data.bean.AdVideo;

public class AdVideoDTO extends DTOMain {
	private List<AdVideo> videos;

	public List<AdVideo> getVideos() {
		if(videos == null){
			return new ArrayList<AdVideo>();
		}
		return videos;
	}

	public void setVideos(List<AdVideo> videos) {
		this.videos = videos;
	}

}
