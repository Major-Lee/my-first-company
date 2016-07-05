package com.bhu.vas.business.index.processor;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;




/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.bhu.pure.kafka.business.observer.KafkaMsgIndexManager;
import com.bhu.pure.kafka.business.observer.KafkaMsgObserverManager;
import com.bhu.pure.kafka.business.observer.listener.DynaMessageListener;
import com.bhu.vas.api.dto.search.increment.IncrementBulkDocumentDTO;
import com.bhu.vas.api.dto.search.increment.IncrementEnum;
import com.bhu.vas.api.dto.search.increment.IncrementSingleDocumentDTO;
import com.bhu.vas.api.dto.search.increment.IncrementEnum.IncrementPrefixEnum;
import com.bhu.vas.business.index.producer.KafkaMessagePerformIncrementProducer;
import com.smartwork.msip.cores.helper.JsonHelper;
import com.smartwork.msip.exception.BusinessI18nCodeException;
import com.smartwork.msip.jdo.ResponseErrorCode;
import com.smartwork.msip.plugins.hook.observer.ExecObserverManager;

/**
 * 此类加载必须保证lazy=false，正常加入消息监听列表，才能收到消息
 * @author Edmond
 *
 */
@Service
public class BusinessIncrementReceiveMessageProcessor implements DynaMessageListener{
	private final Logger logger = LoggerFactory.getLogger(BusinessIncrementReceiveMessageProcessor.class);
	private ThreadPoolExecutor exec_processes = null;
	
	@Resource
	private KafkaMessagePerformIncrementProducer kafkaMessagePerformIncrementProducer;
	
	@PostConstruct
	public void initialize(){
		//System.out.println("BusinessDynaMsgProcessor initialize...");
		System.out.println("BusinessIncrementReceiveMessageProcessor Init");
		logger.info("BusinessIncrementReceiveMessageProcessor initialize...");
		
		exec_processes = (ThreadPoolExecutor)ExecObserverManager.buildExecutorService(this.getClass(),"BusinessIncrementReceiveMessageProcessor processes消息处理", 20);
		KafkaMsgIndexManager.IncrementMsgCommingObserver.addMsgCommingListener(this);
	}

	@Override
	public void onMessage(final String topic,final int partition,final String key,final String message,final long offset,final String consumerId) {
		validateStep1(message);
		exec_processes.submit((new Runnable() {
			@Override
			public void run() {
				try{
					logger.info(String.format("BusinessIncrementReceiveMessageProcessor message: topic[%s] partition[%s] key[%s] message[%s] offset[%s] consumerId[%s]",
							topic, partition,
							key, message,
							offset, consumerId));					
					String prefix = message.substring(0,3);
					IncrementPrefixEnum prefixEnum = IncrementEnum.IncrementPrefixEnum.getIncrementPrefixFromKey(prefix);
					if(prefixEnum != null){
						String incrementMessageWithoutPrefix = message.substring(3);
						if(StringUtils.isNotEmpty(incrementMessageWithoutPrefix)){
							separateMessage(key, prefixEnum, incrementMessageWithoutPrefix);
						}
					}
				}catch(Exception ex){
					ex.printStackTrace(System.out);
					logger.error("BusinessIncrementReceiveMessageProcessor", ex);
				}
			}
		}));
	}
	
	public void separateMessage(String key, IncrementPrefixEnum prefixEnum, String incrementMessageWithoutPrefix){
		switch(prefixEnum){
			case SinglePrefix:
				kafkaMessagePerformIncrementProducer.incrementPerformDocument(key, incrementMessageWithoutPrefix);
				break;
			case BulkPrefix:
				//如果是批量方式需要拆分
				IncrementBulkDocumentDTO incrementBulkDocumentDto = JsonHelper.getDTO(incrementMessageWithoutPrefix, IncrementBulkDocumentDTO.class);
				List<String> ids = incrementBulkDocumentDto.getIds();
				if(ids != null){
					for(String id : ids){
						IncrementSingleDocumentDTO incrementSingleDocumentDto = IncrementSingleDocumentDTO.builder(id, 
								incrementBulkDocumentDto.getAction(), incrementBulkDocumentDto.getUniqueid());
						kafkaMessagePerformIncrementProducer.incrementPerformDocument(incrementSingleDocumentDto);
					}
				}
				break;
			default:
				break;
		}
	}
	
	public static void validateStep1(String msg){
		if(StringUtils.isEmpty(msg) || msg.length() < 3) 
			throw new BusinessI18nCodeException(ResponseErrorCode.COMMON_DATA_VALIDATE_ILEGAL);
	}
	
}
