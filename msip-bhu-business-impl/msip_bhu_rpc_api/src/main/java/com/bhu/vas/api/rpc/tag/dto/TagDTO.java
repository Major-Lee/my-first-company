package com.bhu.vas.api.rpc.tag.dto;

import java.io.Serializable;
import java.util.List;


@SuppressWarnings("serial")
public class TagDTO implements Serializable {
	private List<TagItemsDTO> items;

	public List<TagItemsDTO> getItems() {
		return items;
	}

	public void setItems(List<TagItemsDTO> items) {
		this.items = items;
	}

	
}
