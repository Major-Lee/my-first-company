package com.bhu.vas.processor.input;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.bhu.pure.kafka.business.observer.KafkaMsgObserverManager;
import com.bhu.pure.kafka.client.consumer.StringKafkaMessageConsumer;
import com.bhu.pure.kafka.client.consumer.callback.PollIteratorNotify;
import com.smartwork.msip.plugins.hook.observer.ExecObserverManager;

public class DeliverMessageTopicConsumer extends StringKafkaMessageConsumer{
	private final Logger logger = LoggerFactory.getLogger(DeliverMessageTopicConsumer.class);
	private final String LoggerFormatTemplate = "Deliver Recv: topic[%s] partition[%s] key[%s] value[%s] offset[%s] consumerId[%s]";
	public void init(){
		ScheduledExecutorService delay_exec = (ScheduledExecutorService) ExecObserverManager.
				buildNewScheduledThreadPool(this.getClass(), "DeliverMessageTopicConsumer Delay load", 1);
		delay_exec.schedule(new Runnable() {
			@Override
			public void run() {
				System.out.println("DeliverMessageTopicConsumer Init");
				
				doSubscribeTopics(new PollIteratorNotify<ConsumerRecords<String,String>>(){
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
				System.out.println("DeliverMessageTopicConsumer Init End");
			}}, 5, TimeUnit.SECONDS);
	}
}