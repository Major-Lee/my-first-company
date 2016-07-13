package com.bhu.vas.api.dto.search.increment;

import java.util.ArrayList;
import java.util.List;

import com.bhu.vas.api.dto.search.increment.IncrementEnum.IncrementActionEnum;
import com.smartwork.msip.cores.helper.JsonHelper;



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
			//throw new RuntimeException("IncrementBulkDocumentDTO Builder Params Illegal");
			return null;
		}
		IncrementBulkDocumentDTO dto = new IncrementBulkDocumentDTO();
		dto.setIds(ids);
		dto.setAction(action.getKey());
		dto.setUniqueid(uniqueid);
		return dto;
	}
	
	public static void main(String [] args){
		List<String> macs = new ArrayList<String>();
		macs.add("84:82:f4:19:01:0c");
		macs.add("84:82:f4:28:7a:ec");
		
		IncrementBulkDocumentDTO dto = IncrementBulkDocumentDTO.builder(macs, 
				IncrementActionEnum.WD_OnlineStatus, 1);
		String incrementMessageWithoutPrefix = JsonHelper.getJSONString(dto);
		String incrementMessage = dto.getPrefix().concat(incrementMessageWithoutPrefix);
		System.out.println(incrementMessage);
	}
}
