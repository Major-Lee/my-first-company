package com.bhu.vas.business.index.processor.service;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.bhu.vas.api.dto.search.increment.IncrementSingleDocumentDTO;
import com.bhu.vas.business.search.BusinessIndexDefine;
import com.smartwork.msip.cores.helper.JsonHelper;

@Service
public class IncrementPerformService {
	//private final Logger logger = LoggerFactory.getLogger(IncrementPerformService.class);
	
	@Resource
	private IncrementPerformActionService wifiDeviceIncrementPerformService;
	
	public void incrementDocument(final String message){
		try{
			if(StringUtils.isNotEmpty(message)){
				//logger.info(String.format("IncrementPerformService message: [%s]",message));
				IncrementSingleDocumentDTO incrementSingleDocumentDto = JsonHelper.getDTO(message, IncrementSingleDocumentDTO.class);
				if(incrementSingleDocumentDto != null){
					int documentDBUniqueid = incrementSingleDocumentDto.getUniqueid();
					switch(documentDBUniqueid){
						case BusinessIndexDefine.WifiDevice.IndexUniqueId:
							wifiDeviceIncrementPerformService.incrementDocumentAction(incrementSingleDocumentDto);
							break;
						default:
							break;
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
