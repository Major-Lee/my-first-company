package com.bhu.vas.api.rpc.tag.vto;

import java.io.Serializable;
import java.util.List;

import com.bhu.vas.api.rpc.tag.model.TagName;

@SuppressWarnings("serial")
public class TagItemsVTO implements Serializable {
	
	private List<TagName> items;

	public List<TagName> getItems() {
		return items;
	}

	public void setItems(List<TagName> items) {
		this.items = items;
	}
	
	
}
