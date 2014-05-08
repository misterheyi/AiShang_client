package com.aishang.app.data.dto;

import java.util.ArrayList;
import java.util.List;

import com.aishang.app.data.bean.HairStyle;

public class HairStyleDTO extends DTOMain {
	private List<HairStyle> hairStyles;

	public List<HairStyle> getHairStyles() {
		if(hairStyles == null){
			return new ArrayList<HairStyle>();
		}
		return hairStyles;
	}

	public void setHairStyles(List<HairStyle> hairStyles) {
		this.hairStyles = hairStyles;
	}

}
