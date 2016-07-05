package com.bhu.vas.api.dto.search.increment;

import java.util.List;

import com.bhu.vas.api.dto.search.increment.IncrementEnum.IncrementActionEnum;



@SuppressWarnings("serial")
public class IncrementBulkDocumentDTO extends IncrementDocumentDTO{
	private List<String> ids;

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}
	
	@Override
	public String getPrefix() {
		return IncrementEnum.IncrementPrefixEnum.BulkPrefix.getKey();
	}
	
	public static IncrementBulkDocumentDTO builder(List<String> ids, IncrementActionEnum action, int uniqueid){
		if(ids == null || ids.isEmpty() || action == null){
			throw new RuntimeException("IncrementBulkDocumentDTO Builder Params Illegal");
		}
		IncrementBulkDocumentDTO dto = new IncrementBulkDocumentDTO();
		dto.setIds(ids);
		dto.setAction(action.getKey());
		dto.setUniqueid(uniqueid);
		return dto;
	}
}
