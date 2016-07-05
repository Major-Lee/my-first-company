package com.bhu.vas.business.index.processor.service;

import com.bhu.vas.api.dto.search.increment.IncrementSingleDocumentDTO;
import com.bhu.vas.api.dto.search.increment.IncrementEnum.IncrementActionEnum;

public abstract class IncrementPerformActionService {
	
	public void incrementDocumentAction(IncrementSingleDocumentDTO incrementSingleDocumentDto){
		String increment_action = incrementSingleDocumentDto.getAction();
		IncrementActionEnum actionEnum = IncrementActionEnum.getIncrementActionFromKey(increment_action);
		if(actionEnum != null){
			String id = incrementSingleDocumentDto.getId();
			incrementDocumentActionHandle(id, actionEnum);
		}
	}
	
	public abstract void incrementDocumentActionHandle(String id, IncrementActionEnum actionEnum);
}
