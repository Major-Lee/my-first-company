package com.bhu.vas.api.dto.search.increment;

import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.search.increment.IncrementEnum.IncrementActionEnum;



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
		return IncrementEnum.IncrementPrefixEnum.SinglePrefix.getKey();
	}
	
	public static IncrementSingleDocumentDTO builder(String id, IncrementActionEnum incrementActionEnum, int uniqueid){
		if(incrementActionEnum == null){
			throw new RuntimeException("IncrementBulkDocumentDTO Builder Params Illegal");
		}
		return builder(id, incrementActionEnum.getKey(), uniqueid);
	}
	
	public static IncrementSingleDocumentDTO builder(String id, String action, int uniqueid){
		if(StringUtils.isEmpty(id) || StringUtils.isEmpty(action)){
			throw new RuntimeException("IncrementBulkDocumentDTO Builder Params Illegal");
		}
		IncrementSingleDocumentDTO dto = new IncrementSingleDocumentDTO();
		dto.setId(id);
		dto.setAction(action);
		dto.setUniqueid(uniqueid);
		return dto;
	}
}
