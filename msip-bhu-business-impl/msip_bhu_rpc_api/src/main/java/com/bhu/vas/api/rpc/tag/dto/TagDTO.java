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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (TagItemsDTO itemsDto : this.getItems()) {
			sb.append(itemsDto.getTag()).append(" ");
		}
		return sb.toString();
	}
}
