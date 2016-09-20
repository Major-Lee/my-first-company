package com.bhu.vas.api.rpc.tag.vto;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TagNameVTO implements Serializable {

	private String tagName;

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
}
