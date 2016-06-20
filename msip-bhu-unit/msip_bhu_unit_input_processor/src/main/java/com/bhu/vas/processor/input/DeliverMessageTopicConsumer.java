package com.bhu.vas.processor.input;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.pure.kafka.business.observer.KafkaMsgObserverManager;
import com.bhu.pure.kafka.client.consumer.StringKafkaMessageConsumer;
import com.bhu.pure.kafka.client.consumer.callback.PollIteratorNotify;

public class DeliverMessageTopicConsumer extends StringKafkaMessageConsumer{
	private final Logger logger = LoggerFactory.getLogger(DeliverMessageTopicConsumer.class);
	private final String LoggerFormatTemplate = "Deliver Recv: topic[%s] partition[%s] key[%s] value[%s] offset[%s] consumerId[%s]";
	public void init(){
		super.doSubscribeTopics(new PollIteratorNotify<ConsumerRecords<String,String>>(){
			@Override
			public void notifyComming(String consumerId, ConsumerRecords<String, String> records) {
				for(ConsumerRecord<String, String> record : records){
					logger.info(String
							.format(LoggerFormatTemplate,
									record.topic(), record.partition(),
									record.key(), record.value(),
									record.offset(), consumerId));
					
					KafkaMsgObserverManager.DynaMsgCommingObserver.notifyMsgComming(record.topic(), record.partition(), record.key(), record.value(), record.offset(), consumerId);
				}
			}
		});
	}
}
