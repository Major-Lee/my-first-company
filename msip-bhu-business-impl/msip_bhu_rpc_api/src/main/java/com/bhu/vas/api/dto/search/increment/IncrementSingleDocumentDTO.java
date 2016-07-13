package com.bhu.vas.api.dto.search.increment;

import org.springframework.util.StringUtils;

import com.bhu.vas.api.dto.search.increment.IncrementEnum.IncrementActionEnum;
import com.smartwork.msip.cores.helper.JsonHelper;



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
			//throw new RuntimeException("IncrementBulkDocumentDTO Builder Params Illegal");
			return null;
		}
		return builder(id, incrementActionEnum.getKey(), uniqueid);
	}
	
	public static IncrementSingleDocumentDTO builder(String id, String action, int uniqueid){
		if(StringUtils.isEmpty(id) || StringUtils.isEmpty(action)){
			//throw new RuntimeException("IncrementBulkDocumentDTO Builder Params Illegal");
			return null;
		}
		IncrementSingleDocumentDTO dto = new IncrementSingleDocumentDTO();
		dto.setId(id);
		dto.setAction(action);
		dto.setUniqueid(uniqueid);
		return dto;
	}
	
	public static void main(String [] args){
		IncrementSingleDocumentDTO dto = IncrementSingleDocumentDTO.builder("84:82:f4:19:01:0c", 
				IncrementActionEnum.WD_OnlineStatus, 1);
		String incrementMessageWithoutPrefix = JsonHelper.getJSONString(dto);
		String incrementMessage = dto.getPrefix().concat(incrementMessageWithoutPrefix);
		System.out.println(incrementMessage);
	}
}
