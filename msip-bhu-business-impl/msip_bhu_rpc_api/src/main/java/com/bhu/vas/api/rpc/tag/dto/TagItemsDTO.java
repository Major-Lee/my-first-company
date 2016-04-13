package com.bhu.vas.api.rpc.tag.dto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TagItemsDTO implements Serializable {
	private String tag;

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
}
