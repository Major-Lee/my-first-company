package com.bhu.vas.business.index.processor.service;

import com.bhu.vas.api.dto.search.increment.IncrementEnum.IncrementActionEnum;
import com.bhu.vas.api.dto.search.increment.IncrementSingleDocumentDTO;

public abstract class IncrementPerformActionService {
	//private final Logger logger = LoggerFactory.getLogger(IncrementPerformActionService.class);
	
	public void incrementDocumentAction(IncrementSingleDocumentDTO incrementSingleDocumentDto){
		String increment_action = incrementSingleDocumentDto.getAction();
		IncrementActionEnum actionEnum = IncrementActionEnum.getIncrementActionFromKey(increment_action);
		//logger.info(String.format("IncrementPerformActionService actionEnum: [%s]", actionEnum));
		if(actionEnum != null){
			String id = incrementSingleDocumentDto.getId();
			incrementDocumentActionHandle(id, actionEnum);
		}
	}
	
	public abstract void incrementDocumentActionHandle(String id, IncrementActionEnum actionEnum);
}
