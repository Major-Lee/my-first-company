package com.bhu.vas.business.search.increment;

import com.bhu.pure.kafka.client.producer.StringKafkaMessageProducer;
import com.bhu.vas.api.dto.search.increment.IncrementBulkDocumentDTO;
import com.bhu.vas.api.dto.search.increment.IncrementConstant;
import com.bhu.vas.api.dto.search.increment.IncrementDocumentDTO;
import com.bhu.vas.api.dto.search.increment.IncrementSingleDocumentDTO;
import com.smartwork.msip.cores.helper.JsonHelper;

public class KafkaMessageIncrementProducer extends StringKafkaMessageProducer{
	
	public void incrementDocument(IncrementDocumentDTO incrementDocumentDto){
		try{
			if(incrementDocumentDto != null){
				String incrementMessageWithoutPrefix = JsonHelper.getJSONString(incrementDocumentDto);
				String incrementMessage = incrementDocumentDto.getPrefix().concat(incrementMessageWithoutPrefix);
				if(incrementDocumentDto instanceof IncrementSingleDocumentDTO){
					String kafkaKey = ((IncrementSingleDocumentDTO)incrementDocumentDto).getId();
					super.send(IncrementConstant.KafkaReceiveTopicName, kafkaKey, incrementMessage);
				}else if(incrementDocumentDto instanceof IncrementBulkDocumentDTO){
					super.send(IncrementConstant.KafkaReceiveTopicName, null, incrementMessage);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
