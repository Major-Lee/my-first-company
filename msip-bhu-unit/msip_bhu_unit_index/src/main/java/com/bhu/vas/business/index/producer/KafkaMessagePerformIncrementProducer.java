package com.bhu.vas.business.index.producer;

import org.apache.commons.lang.StringUtils;

import com.bhu.pure.kafka.client.producer.StringKafkaMessageProducer;
import com.bhu.vas.api.dto.search.increment.IncrementEnum;
import com.bhu.vas.api.dto.search.increment.IncrementSingleDocumentDTO;
import com.smartwork.msip.cores.helper.JsonHelper;

public class KafkaMessagePerformIncrementProducer extends StringKafkaMessageProducer{
	
	public void incrementPerformDocument(IncrementSingleDocumentDTO incrementSingleDocumentDto){
		try{
			if(incrementSingleDocumentDto != null){
				String incrementMessageWithoutPrefix = JsonHelper.getJSONString(incrementSingleDocumentDto);
				String id = incrementSingleDocumentDto.getId();
				incrementPerformDocument(id, incrementMessageWithoutPrefix);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void incrementPerformDocument(String id, String incrementMessageWithoutPrefix){
		try{
			if(StringUtils.isNotEmpty(id) && StringUtils.isNotEmpty(incrementMessageWithoutPrefix)){
				super.send(IncrementEnum.KafkaPerformReceiveTopicName, id, incrementMessageWithoutPrefix);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
