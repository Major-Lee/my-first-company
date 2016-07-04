package com.bhu.vas.api.dto.search.increment;

import org.springframework.util.StringUtils;



@SuppressWarnings("serial")
public class IncrementSingleDocumentDTO extends IncrementDocumentDTO{
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String getPrefix() {
		return IncrementConstant.IncrementDtoSinglePrefix;
	}
	
	public static IncrementSingleDocumentDTO builder(String id, IncrementActionEnum action, int uniqueid){
		if(StringUtils.isEmpty(id) || action == null){
			throw new RuntimeException("IncrementBulkDocumentDTO Builder Params Illegal");
		}
		IncrementSingleDocumentDTO dto = new IncrementSingleDocumentDTO();
		dto.setId(id);
		dto.setAction(action.getKey());
		dto.setUniqueid(uniqueid);
		return dto;
	}
}
