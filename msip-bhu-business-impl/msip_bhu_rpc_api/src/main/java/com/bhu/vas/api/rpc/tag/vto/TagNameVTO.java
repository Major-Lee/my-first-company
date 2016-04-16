package com.bhu.vas.api.rpc.tag.vto;

import java.io.Serializable;
import java.util.List;

import com.bhu.vas.api.rpc.tag.model.TagName;

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
